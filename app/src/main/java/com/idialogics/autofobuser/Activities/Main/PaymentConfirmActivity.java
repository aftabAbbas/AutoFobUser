package com.idialogics.autofobuser.Activities.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.idialogics.autofobuser.MainActivity;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PaymentConfirmActivity extends AppCompatActivity {

    SharedPref sh;
    Context context;
    FirebaseFirestore fireStore;
    ArrayList<String> adminList = new ArrayList<>();
    String bodyText, titleText, productId;
    Button btnDone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirm);
        initUI();
    }

    private void initUI() {
        context = PaymentConfirmActivity.this;
        sh = new SharedPref(context);
        fireStore = FirebaseFirestore.getInstance();
        btnDone = findViewById(R.id.btn_done);


        bodyText = getIntent().getStringExtra(Constants.KEY_BODY);
        titleText = getIntent().getStringExtra(Constants.KEY_TITLE);
        productId = getIntent().getStringExtra(Constants.ID);

        Functions.loadingDialog(context, "Adding", true);

        getAdminFCM(productId);


        btnDone.setOnClickListener(v -> {

            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);


        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

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

            Functions.loadingDialog(context, "Adding", false);
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
                    Functions.showSnackBar(context, "Add Successfully");


                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                Functions.loadingDialog(context, "Adding", false);
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}