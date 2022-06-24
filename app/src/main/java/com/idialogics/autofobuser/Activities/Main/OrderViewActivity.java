package com.idialogics.autofobuser.Activities.Main;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.idialogics.autofobuser.Model.Buy;
import com.idialogics.autofobuser.Network.ApiClient;
import com.idialogics.autofobuser.Network.ApiService;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.Functions;
import com.idialogics.autofobuser.Utils.SharedPref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderViewActivity extends AppCompatActivity {

    Context context;
    DatabaseReference dataRef;
    SharedPref sh;
    Buy buyIntent;
    String manufacturer, model, year, productName, price, availability, fcc, battery, productDetail;
    TextView tvProductName, tvPrice, tvProductDetail, tvBattery, tvFCC, tvAvailability, tvCount;
    ImageView ivProduct;
    Button btnCancelOrder;
    FirebaseFirestore fireStore;
    ArrayList<String> adminList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);
        initUI();


        if (buyIntent.getStatus().equals(Constants.PENDING)) {

            btnCancelOrder.setVisibility(View.VISIBLE);

        } else {

            btnCancelOrder.setVisibility(View.GONE);


        }
        btnCancelOrder.setOnClickListener(v -> {

            showConfirmationDialog();
            //


        });
    }


    private void initUI() {

        context = OrderViewActivity.this;
        dataRef = FirebaseDatabase.getInstance().getReference();
        fireStore = FirebaseFirestore.getInstance();
        sh = new SharedPref(context);
        tvProductName = findViewById(R.id.tv_name);
        tvAvailability = findViewById(R.id.tv_availability);
        tvPrice = findViewById(R.id.tv_price);
        tvFCC = findViewById(R.id.tv_fcc);
        tvBattery = findViewById(R.id.tv_battery);
        tvProductDetail = findViewById(R.id.tv_product_detail);
        ivProduct = findViewById(R.id.iv_product);
        tvCount = findViewById(R.id.tv_count);
        btnCancelOrder = findViewById(R.id.btn_cancel_order);

        buyIntent = (Buy) getIntent().getSerializableExtra(Constants.PRODUCT_DETAIL_INTENT);


        productName = buyIntent.getProductName();


        Glide.with(context).load(buyIntent.getProductImage()).into(ivProduct);

        productDetail = manufacturer + " " + model + " " + year;
        price = buyIntent.getPrice();
        availability = buyIntent.getAvailability();
        battery = buyIntent.getBattery();
        fcc = buyIntent.getFcc();
        manufacturer = buyIntent.getManufacturer();
        model = buyIntent.getModel();
        year = buyIntent.getYear();

        int count = Integer.parseInt(buyIntent.getCount());
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


        tvCount.setText(String.valueOf(count));

    }


    private void showConfirmationDialog() {


        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.cancel_order_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tvName = dialog.findViewById(R.id.tv_name);
        ImageView civUser = dialog.findViewById(R.id.iv_product);
        Button btnDelete = dialog.findViewById(R.id.btn_cancel_order);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        Glide.with(context).load(buyIntent.getProductImage()).placeholder(R.drawable.place_holder).into(civUser);

        tvName.setText(productName);

        String date = Functions.getCurrentDate();

        btnDelete.setOnClickListener(v -> {

            dialog.dismiss();

            Buy buy = new Buy(buyIntent.getId(), manufacturer, model, year, buyIntent.getProductId(),
                    buyIntent.getProductImage(), tvCount.getText().toString(), productName, price
                    , battery, fcc, availability, date, sh.getString(Constants.ID), Constants.CANCELED,"","","","","","","","",buyIntent.getCartId(),"");


            Functions.loadingDialog(context, "Removing", true);

            dataRef.child(Constants.ORDERS)
                    .child(buy.getId())
                    .setValue(buy).addOnCompleteListener(task -> {

                getAdminFCM(buyIntent.getId());
                //


            }).addOnFailureListener(e -> {

                dialog.dismiss();
                Functions.loadingDialog(context, "Removing", false);
                Functions.showSnackBar(context, e.getMessage());

            });

        });

        btnCancel.setOnClickListener(v -> {

            dialog.dismiss();
            //

        });
        dialog.show();
        dialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);


    }

    @SuppressWarnings("deprecation")
    private void getAdminFCM(String key) {

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

                                initiateNotification(adminList, inviterToken, key);

                            }

                        });


                    }


                }).addOnFailureListener(e -> {

            Functions.loadingDialog(context, "Adding", true);
            Functions.showSnackBar(context, e.getMessage());

        });
    }


    private void initiateNotification(List<String> receiversList, String inviterToken, String key) {
        try {

            JSONArray token = new JSONArray();


            if (receiversList != null && receiversList.size() > 0) {
                for (int i = 0; i < receiversList.size(); i++) {
                    token.put(receiversList.get(i));

                }
            }
            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            String bodyText = "Canceled Order for " + manufacturer + " " + model + " " + year + " " + productName + "      x" + buyIntent.getCount();
            String titleText = sh.getString(Constants.KEY_FIRST_NAME).trim() + " " + sh.getString(Constants.KEY_LAST_NAME);

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken);
            data.put(Constants.KEY_TITLE, titleText);
            data.put(Constants.KEY_BODY, bodyText);


            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, token);

            Functions.saveNotiToDB(titleText, bodyText, key);

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
