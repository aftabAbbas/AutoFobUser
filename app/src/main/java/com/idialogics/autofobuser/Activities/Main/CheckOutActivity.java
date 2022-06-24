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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.idialogics.autofobuser.Model.Buy;
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
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CheckOutActivity extends AppCompatActivity {

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
    int count = 0;
    Buy buyIntnet;
    DatabaseReference dataRef;
    FirebaseFirestore fireStore;
    private OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret;
    private Stripe stripe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        initUI();

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
                .enqueue(new PayCallback(this));
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

    private void displayAlert(@NonNull String title,
                              @Nullable String message) {

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
        context = CheckOutActivity.this;
        sh = new SharedPref(context);
        dataRef = FirebaseDatabase.getInstance().getReference();
        fireStore = FirebaseFirestore.getInstance();
        btnSubmit = findViewById(R.id.payButton);
        cardInputWidget = findViewById(R.id.cardInputWidget);
        tvPrice = findViewById(R.id.tv_total_price);

        buyIntnet = (Buy) getIntent().getSerializableExtra(Constants.BUY);

        count = Integer.parseInt(buyIntnet.getCount());
        totalprice = Double.parseDouble(buyIntnet.getPrice());


        totalprice = totalprice * count;


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
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
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

        dataRef.child(Constants.USER)
                .child(sh.getString(Constants.ID))
                .child(Constants.CART)
                .child(buyIntnet.getCartId())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        dataRef.child(Constants.ORDERS)
                                .child(buyIntnet.getId())
                                .setValue(buyIntnet)
                                .addOnCompleteListener(task1 -> {

                                    String bodyText = "Order for " + buyIntnet.getManufacturer() + " " + buyIntnet.getModel() + " " + buyIntnet.getYear() + " " + buyIntnet.getProductName() + "      x" + buyIntnet.getCount();
                                    String titleText = sh.getString(Constants.KEY_FIRST_NAME).trim() + " " + sh.getString(Constants.KEY_LAST_NAME);

                                    Functions.loadingDialog(context, "Adding", false);

                                    finish();
                                    Intent intent = new Intent(context, PaymentConfirmActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra(Constants.ID, buyIntnet.getId());
                                    intent.putExtra(Constants.KEY_BODY, bodyText);
                                    intent.putExtra(Constants.KEY_TITLE, titleText);
                                    startActivity(intent);


                                    //

                                }).addOnFailureListener(e -> {

                            Functions.loadingDialog(context, "Adding", false);
                            Functions.showSnackBar(context, e.getMessage());

                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Functions.loadingDialog(context, "Adding", false);
                Functions.showSnackBar(context, e.getMessage());
            }
        });


    }


    private static final class PayCallback implements Callback {
        @NonNull
        private final WeakReference<CheckOutActivity> activityRef;

        PayCallback(@NonNull CheckOutActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            final CheckOutActivity activity = activityRef.get();
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
            final CheckOutActivity activity = activityRef.get();
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
        private final WeakReference<CheckOutActivity> activityRef;

        PaymentResultCallback(@NonNull CheckOutActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final CheckOutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                activity.displayAlert(
                        "Payment completed",
                        gson.toJson(paymentIntent)
                );
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
                );
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final CheckOutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert("Error", e.toString());
        }
    }


}