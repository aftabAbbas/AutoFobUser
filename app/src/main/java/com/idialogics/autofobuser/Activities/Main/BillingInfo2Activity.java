package com.idialogics.autofobuser.Activities.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idialogics.autofobuser.Adapter.CartRecycler;
import com.idialogics.autofobuser.Model.BillingInfo;
import com.idialogics.autofobuser.Model.Cart;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Services.NetworkChangeReceiver;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.Functions;
import com.idialogics.autofobuser.Utils.SharedPref;

import java.util.ArrayList;

public class BillingInfo2Activity extends AppCompatActivity {
    Context context;
    SharedPref sh;
    DatabaseReference dataRef;
    Button btnPlaceOrder;
    boolean isValidFields = false;
    EditText etFName, etLName, etCName, etStreetAddress, etCity, etState, etZipCode, etEmail;
    String fName, lName, cName, streetAddress, city, state, zipCode, email;
    double finalPrice;
    FirebaseFirestore fireStore;
    ArrayList<Cart> productList = new ArrayList<>();
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_info2);

        initUI();


        btnPlaceOrder.setOnClickListener(v -> {

            fName = etFName.getText().toString().trim();
            lName = etLName.getText().toString().trim();
            cName = etCName.getText().toString().trim();
            city = etCity.getText().toString().trim();
            state = etState.getText().toString().trim();
            zipCode = etZipCode.getText().toString().trim();
            email = etEmail.getText().toString().trim();
            streetAddress = etStreetAddress.getText().toString().trim();


            isValidFields = false;
            isValidFields = validateFields();

            if (isValidFields) {

                if (!NetworkChangeReceiver.isOnline(context)) {

                    Functions.noInternetConnectDialog(context);

                } else {

                    buyProduct();


                }

            }

        });
    }

    private boolean validateFields() {

        if (fName.equals("")) {

            etFName.setError(Constants.EMPTY_ERROR);

        } else if (lName.equals("")) {

            etLName.setError(Constants.EMPTY_ERROR);

        } else if (streetAddress.equals("")) {

            etStreetAddress.setError(Constants.EMPTY_ERROR);

        } else if (city.equals("")) {

            etCity.setError(Constants.EMPTY_ERROR);

        } else if (state.equals("")) {

            etState.setError(Constants.EMPTY_ERROR);

        } else if (zipCode.equals("")) {

            etZipCode.setError(Constants.EMPTY_ERROR);

        } else if (email.equals("")) {

            etEmail.setError(Constants.EMPTY_ERROR);

        } else {

            isValidFields = true;
        }


        return isValidFields;
    }

    private void initUI() {

        context = BillingInfo2Activity.this;
        sh = new SharedPref(context);
        dataRef = FirebaseDatabase.getInstance().getReference();
        fireStore = FirebaseFirestore.getInstance();

        btnPlaceOrder = findViewById(R.id.btn_place_order);
        etFName = findViewById(R.id.et_first_name);
        etLName = findViewById(R.id.et_last_name);
        etCName = findViewById(R.id.et_company_name);
        etStreetAddress = findViewById(R.id.et_street_address);
        etCity = findViewById(R.id.et_city);
        etState = findViewById(R.id.et_state);
        etZipCode = findViewById(R.id.et_zip_code);
        etEmail = findViewById(R.id.et_email);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        //  getAdminFCM();

        productList.clear();
        productList.addAll(CartRecycler.selectedProductList);


    }

    private void buyProduct() {

        finalPrice = 0;

        for (int i = 0; i < productList.size(); i++) {

            double price = Double.parseDouble(productList.get(i).getPrice());
            double count = Double.parseDouble(productList.get(i).getCount());

            double tempPrice = price * count;
            tempPrice = Math.round(tempPrice * 100D) / 100D;


            finalPrice = finalPrice + tempPrice;

        }


        BillingInfo billingInfo = new BillingInfo(fName, lName, cName, streetAddress, city, state, zipCode, email, finalPrice + "");

        Intent intent = new Intent(context, CheckOut2Activity.class);
        intent.putExtra(Constants.BILLING_INFO, billingInfo);
        startActivity(intent);
    }


   /* private void buyProduct() {

        if (counter < productList.size()) {

            String key = dataRef.push().getKey();

            String date = Functions.getCurrentDate();


            dataRef.child(Constants.USER)
                    .child(sh.getString(Constants.ID))
                    .child(Constants.CART)
                    .child(productList.get(counter).getId())
                    .removeValue();


            if (key != null) {

                Buy buy = new Buy(key, productList.get(counter).getManufacturer(), productList.get(counter).getModel(),
                        productList.get(counter).getYear(), productList.get(counter).getProductId()
                        , productList.get(counter).getProductImage()
                        , productList.get(counter).getCount(), productList.get(counter).getProductName()
                        , productList.get(counter).getPrice(), productList.get(counter).getBattery()
                        , productList.get(counter).getFcc(), productList.get(counter).getAvailability()
                        , date, sh.getString(Constants.ID), Constants.PENDING, fName, lName, cName, streetAddress
                        , city, state, zipCode, email, key, "");


                dataRef.child(Constants.ORDERS)
                        .child(key)
                        .setValue(buy)
                        .addOnCompleteListener(task -> {


                            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful() && task2.getResult() != null) {
                                    String inviterToken = task2.getResult().getToken();

                                    try {

                                        JSONArray token = new JSONArray();


                                        if (adminList != null && adminList.size() > 0) {
                                            for (int i = 0; i < adminList.size(); i++) {
                                                token.put(adminList.get(i));

                                            }
                                        }
                                        JSONObject body = new JSONObject();
                                        JSONObject data = new JSONObject();

                                        String bodyText = "Order for " +
                                                productList.get(counter).getManufacturer() + " " +
                                                productList.get(counter).getModel() + " " +
                                                productList.get(counter).getYear() + " " +
                                                productList.get(counter).getProductName() + "      x" +
                                                productList.get(counter).getCount();

                                        String titleText = sh.getString(Constants.KEY_FIRST_NAME).trim() + " " + sh.getString(Constants.KEY_LAST_NAME);

                                        data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
                                        data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken);
                                        data.put(Constants.KEY_TITLE, titleText);
                                        data.put(Constants.KEY_BODY, bodyText);


                                        body.put(Constants.REMOTE_MSG_DATA, data);
                                        body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, token);

                                        Functions.saveNotiToDB(titleText, bodyText, key);

                                        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                                                Constants.getRemoteMessageHeaders(), body.toString()
                                        ).enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                                if (response.isSuccessful()) {


                                                    counter++;
                                                    buyProduct();


                                                }

                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                                                Functions.loadingDialog(context, "Adding", false);
                                                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                    } catch (Exception e) {
                                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });

                            //

                        }).addOnFailureListener(e -> {

                    Functions.loadingDialog(context, "Adding", false);
                    Functions.showSnackBar(context, e.getMessage());

                });
            }

        } else {

            Functions.loadingDialog(context, "Adding", false);
            Toast.makeText(context, "Notification Sent Successfully", Toast.LENGTH_SHORT).show();
           // openBottomSheet();
            startActivity(new Intent(context,CheckOutActivity.class));
        }

    }

    @SuppressLint("InflateParams")
    private void openBottomSheet() {


        View view1 = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        dialog.setContentView(view1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnDone = dialog.findViewById(R.id.btn_done);


        assert btnDone != null;
        btnDone.setOnClickListener(v -> {

            dialog.dismiss();


            onBackPressed();
            finish();

        });

        dialog.show();

    }

    public void back(View view) {
        onBackPressed();
    }

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

                    }


                }).addOnFailureListener(e -> Functions.showSnackBar(context, e.getMessage()));
    }*/
}