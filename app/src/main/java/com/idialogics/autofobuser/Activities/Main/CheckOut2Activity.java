package com.idialogics.autofobuser.Activities.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.idialogics.autofobuser.Adapter.CartRecycler;
import com.idialogics.autofobuser.Model.BillingInfo;
import com.idialogics.autofobuser.Model.Buy;
import com.idialogics.autofobuser.Model.Cart;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.Functions;
import com.idialogics.autofobuser.Utils.SharedPref;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CheckOut2Activity extends AppCompatActivity {
    private final OkHttpClient httpClient = new OkHttpClient();
    SharedPref sh;
    Context context;
    CardInputWidget cardInputWidget;
    Button btnSubmit;
    TextView tvPrice;
    String sentid = "azxa23";
    String transactionId = "";
    String pub_key = "pk_test_51HFlnZFfxe7mIxEOIuVqs2e343yi3r5UmsOxzQyM9PAK8QBavgNtaWcghTQnvewlK5JiaUj8sZoGXFSKre3daexZ00PiLPxuse";
    String stripe_Url = "https://apis.appistaan.com/AutoFob/stripeSendPayment/";
    double totalprice = 0;
    BillingInfo billingInfoIntent;
    DatabaseReference dataRef;
    FirebaseFirestore fireStore;
    int counter = 0;
    ArrayList<Cart> productList = new ArrayList<>();
    private String paymentIntentClientSecret;
    private Stripe stripe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out2);
        initUI();


        // Configure the SDK with your Stripe publishable key so it can make requests to Stripe

    }

    private void startPaymentScreen() {
        // Create a PaymentIntent by calling the server's endpoint.
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        String json = "{"
                + "\"currency\":\"usd\","
                + "\"money\":" + totalprice + ","
                + "\"items\":["
                + "{\"id\":\"" + sentid + "\"}"
                + "]"
                + "}";

        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(stripe_Url + "paymoney.php")
                .post(body)
                .build();
        httpClient.newCall(request)
                .enqueue(new CheckOut2Activity.PayCallback(this));
        // Hook up the pay button to the card widget and stripe instance
        btnSubmit.setOnClickListener((View view) ->
        {
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
            if (params != null) {

                Functions.loadingDialog(context, "Adding", true);

                btnSubmit.setVisibility(View.GONE);
                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                stripe.confirmPayment(this, confirmParams);
            }
        });
    }

    private void displayAlert(@Nullable String message) {

        btnSubmit.setVisibility(View.VISIBLE);
        try {
            JSONObject jsonObject = new JSONObject(message);
            String status = String.valueOf(jsonObject.get("status"));
            if (status.equals("Succeeded")) {
                transactionId = String.valueOf(jsonObject.get("id"));

                // now we add the data
                addToDb();

            } else {

                Functions.loadingDialog(context, "Adding", false);
                Functions.showSnackBar(context, "Transaction failed");

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Functions.loadingDialog(context, "Adding", false);
            Functions.showSnackBar(context, e.getMessage());

        }


    }


    private void initUI() {
        context = CheckOut2Activity.this;
        sh = new SharedPref(context);
        dataRef = FirebaseDatabase.getInstance().getReference();
        fireStore = FirebaseFirestore.getInstance();
        btnSubmit = findViewById(R.id.payButton);
        cardInputWidget = findViewById(R.id.cardInputWidget);
        tvPrice = findViewById(R.id.tv_total_price);

        productList.clear();
        productList.addAll(CartRecycler.selectedProductList);


        billingInfoIntent = (BillingInfo) getIntent().getSerializableExtra(Constants.BILLING_INFO);

        totalprice = 0;

        totalprice = Double.parseDouble(billingInfoIntent.getFinalPrice());


        tvPrice.setText("$ " + totalprice);

        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull(pub_key)
        );
        startPaymentScreen();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new CheckOut2Activity.PaymentResultCallback(this));
    }

    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );
        paymentIntentClientSecret = responseMap.get("clientSecret");
    }

    private void addToDb() {


        if (counter < productList.size()) {

            Toast.makeText(context, "" + counter, Toast.LENGTH_SHORT).show();

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
                        , date, sh.getString(Constants.ID), Constants.PENDING, billingInfoIntent.getfName(), billingInfoIntent.getlName()
                        , billingInfoIntent.getcName(), billingInfoIntent.getStreetAddress()
                        , billingInfoIntent.getCity(), billingInfoIntent.getState(), billingInfoIntent.getZipCode(), billingInfoIntent.getEmail(), productList.get(counter).getId(), "");


                dataRef.child(Constants.ORDERS)
                        .child(key)
                        .setValue(buy).addOnCompleteListener(task -> {

                    counter++;
                    addToDb();

                }).addOnFailureListener(e -> {
                    Functions.loadingDialog(context, "Adding", false);
                    Functions.showSnackBar(context, e.getMessage());
                });

            }
        } else {

            Functions.loadingDialog(context, "Adding", false);


            String bodyText = "Order for " + productList.size() + " keys" + " in " + totalprice + " dollars";
            String titleText = sh.getString(Constants.KEY_FIRST_NAME).trim() + " " + sh.getString(Constants.KEY_LAST_NAME);

            finish();
            Intent intent = new Intent(context, PaymentConfirmActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Constants.ID, productList.get(0).getId());
            intent.putExtra(Constants.KEY_BODY, bodyText);
            intent.putExtra(Constants.KEY_TITLE, titleText);
            startActivity(intent);
        }
    }

    private static final class PayCallback implements Callback {
        @NonNull
        private final WeakReference<CheckOut2Activity> activityRef;

        PayCallback(@NonNull CheckOut2Activity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            final CheckOut2Activity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(() ->
                    Toast.makeText(
                            activity, "Error: " + e.toString(), Toast.LENGTH_LONG
                    ).show()
            );
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull final Response response)
                throws IOException {
            final CheckOut2Activity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            if (!response.isSuccessful()) {
                activity.runOnUiThread(() ->
                        Toast.makeText(
                                activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                activity.onPaymentSuccess(response);
            }
        }
    }

    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull
        private final WeakReference<CheckOut2Activity> activityRef;

        PaymentResultCallback(@NonNull CheckOut2Activity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final CheckOut2Activity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                activity.displayAlert(
                        gson.toJson(paymentIntent)
                );
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                activity.displayAlert(
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
                );
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final CheckOut2Activity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert(e.toString());
        }
    }


}