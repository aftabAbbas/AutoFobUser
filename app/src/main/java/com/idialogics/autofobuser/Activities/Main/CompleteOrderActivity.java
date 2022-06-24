package com.idialogics.autofobuser.Activities.Main;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.idialogics.autofobuser.Model.Buy;
import com.idialogics.autofobuser.Model.Notifications;
import com.idialogics.autofobuser.Network.ApiClient;
import com.idialogics.autofobuser.Network.ApiService;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.Functions;
import com.idialogics.autofobuser.Utils.SharedPref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompleteOrderActivity extends AppCompatActivity {

    Context context;
    DatabaseReference dataRef;
    SharedPref sh;
    Notifications notificationsIntent;
    String id, manufacturer, model, year, productName, price, availability, fcc, battery, productDetail, status, productImage, trackingNumber;
    TextView tvProductName, tvPrice, tvProductDetail, tvBattery, tvFCC, tvAvailability, tvCount, tvTrackingNumber;
    ImageView ivProduct;
    Button btnCompleteOrder;
    FirebaseFirestore fireStore;
    ArrayList<String> adminList = new ArrayList<>();
    ConstraintLayout clProduct;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_order);
        initUI();


        btnCompleteOrder.setOnClickListener(v -> changeStatusInNotification());

        clProduct.setOnClickListener(v -> Functions.showImageDialog(context,clProduct, productImage));


    }

    private void changeStatusInNotification() {

        Functions.loadingDialog(context, "Processing", true);


        HashMap<String, Object> statusMap = new HashMap<>();
        statusMap.put(Constants.STATUS, Constants.COMPLETED);

        dataRef.child(Constants.KEY_COLLECTION_USERS)
                .child(sh.getString(Constants.ID)).child(Constants.NOTIFICATION)
                .child(notificationsIntent.getId())
                .updateChildren(statusMap)
                .addOnCompleteListener(task -> {


                    changeStatus();
                    btnCompleteOrder.setVisibility(View.GONE);


                }).addOnFailureListener(e -> {

            Functions.showSnackBar(context, e.getMessage());
            Functions.loadingDialog(context, "Processing", true);


        });
    }

    private void initUI() {
        context = CompleteOrderActivity.this;
        dataRef = FirebaseDatabase.getInstance().getReference();
        fireStore = FirebaseFirestore.getInstance();
        sh = new SharedPref(context);
        tvProductName = findViewById(R.id.tv_name);
        tvAvailability = findViewById(R.id.tv_availability);
        tvPrice = findViewById(R.id.tv_price);
        tvFCC = findViewById(R.id.tv_fcc);
        tvBattery = findViewById(R.id.tv_battery);
        tvTrackingNumber = findViewById(R.id.tv_tracking_number);
        tvProductDetail = findViewById(R.id.tv_product_detail);
        ivProduct = findViewById(R.id.iv_product);
        tvCount = findViewById(R.id.tv_count);
        btnCompleteOrder = findViewById(R.id.btn_cancel_order);
        clProduct = findViewById(R.id.constraintLayout2);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        notificationsIntent = (Notifications) getIntent().getSerializableExtra(Constants.PRODUCT_DETAIL_INTENT);

        status = notificationsIntent.getStatus();

        getProductDetail(notificationsIntent.getId());


        if (status.equals(Constants.PENDING)) {

            btnCompleteOrder.setVisibility(View.VISIBLE);

        } else {

            btnCompleteOrder.setVisibility(View.GONE);


        }

    }

    private void getProductDetail(String id) {

        Functions.loadingDialog(context, "Loading", true);

        dataRef.child(Constants.ORDERS)
                .child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Buy buy = snapshot.getValue(Buy.class);

                        if (buy != null) {

                            setData(buy);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Functions.loadingDialog(context, "Loading", false);
                        Functions.showSnackBar(context, error.getMessage());


                    }
                });

    }

    private void setData(Buy buy) {

        Functions.loadingDialog(context, "Loading", false);

        id = buy.getId();
        price = buy.getPrice();
        availability = buy.getAvailability();
        battery = buy.getBattery();
        fcc = buy.getFcc();
        manufacturer = buy.getManufacturer();
        model = buy.getModel();
        year = buy.getYear();
        productImage = buy.getProductImage();
        productName = buy.getProductName();
        trackingNumber = buy.getTrackingNumber();
        productDetail = manufacturer + " " + model + " " + year;
        int count = Integer.parseInt(buy.getCount());
        float intPrice = Float.parseFloat(price);

        float finalPrice2 = count * intPrice;
        productDetail = manufacturer + " " + model + " " + year;

        String priceText = "$ " + finalPrice2;

        tvProductDetail.setText(productDetail);
        tvProductName.setText(productName);
        tvPrice.setText(priceText);
        tvFCC.setText(fcc);
        tvAvailability.setText(availability);
        tvBattery.setText(battery);
        tvTrackingNumber.setText(trackingNumber);


        tvCount.setText(String.valueOf(count));
    }

    private void changeStatus() {


        HashMap<String, Object> statusMap = new HashMap<>();
        statusMap.put(Constants.STATUS, Constants.COMPLETED);

        dataRef.child(Constants.ORDERS)
                .child(id)
                .updateChildren(statusMap)
                .addOnCompleteListener(task -> {

                    getAdminFCM();
                    //

                }).addOnFailureListener(e -> {

            Functions.loadingDialog(context, "Processing", false);
            Functions.showSnackBar(context, e.getMessage());

        });

    }

    @SuppressWarnings("deprecation")
    private void getAdminFCM() {

        fireStore.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.TYPE, Constants.ADMIN).get()
                .addOnCompleteListener(task -> {


                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {

                        adminList.clear();


                        for (int i = 0; i < task.getResult().size(); i++) {

                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(i);


                            String fcm = documentSnapshot.getString(Constants.KEY_FCM_TOKEN);

                            adminList.add(fcm);


                        }


                        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful() && task2.getResult() != null) {
                                String inviterToken = task2.getResult().getToken();

                                initiateNotification(adminList, inviterToken);

                            }

                        });


                    }


                }).addOnFailureListener(e -> {

            Functions.loadingDialog(context, "Adding", true);
            Functions.showSnackBar(context, e.getMessage());

        });
    }


    private void initiateNotification(List<String> receiversList, String inviterToken) {
        try {

            JSONArray token = new JSONArray();


            if (receiversList != null && receiversList.size() > 0) {
                for (int i = 0; i < receiversList.size(); i++) {
                    token.put(receiversList.get(i));

                }
            }
            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            String bodyText = "Completed Order for " + manufacturer + " " + model + " " + year + " " + productName + "      x" + tvCount.getText().toString();
            String titleText = sh.getString(Constants.KEY_FIRST_NAME).trim() + " " + sh.getString(Constants.KEY_LAST_NAME);

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken);
            data.put(Constants.KEY_TITLE, titleText);
            data.put(Constants.KEY_BODY, bodyText);


            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, token);

            Functions.saveNotiToDB2(titleText, bodyText);

            sendRemoteMessage(body.toString());

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendRemoteMessage(String remoteInvitation) {

        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(), remoteInvitation
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {


                    Functions.loadingDialog(context, "Adding", false);
                    Toast.makeText(context, "Notification Sent Successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    finish();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                Functions.loadingDialog(context, "Adding", false);
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    public void back(View view) {
        onBackPressed();
    }
}