package com.idialogics.autofobuser.Activities.StartUp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.idialogics.autofobuser.Activities.Main.CheckOutActivity;
import com.idialogics.autofobuser.MainActivity;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.Functions;
import com.idialogics.autofobuser.Utils.SharedPref;

public class SplashActivity extends AppCompatActivity {

    Context context;
    SharedPref sh;
    String token;
    DatabaseReference dataRef;


    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initUi();


        new Handler().postDelayed(() -> {

            if (sh.getBoolean(Constants.IS_LOGGED_IN)) {

                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        token = task.getResult().getToken();

                        Functions.uploadToken(context, token);

                    }

                });


                startActivity(new Intent(context, MainActivity.class));

            } else if (!sh.getBoolean(Constants.IS_LOGGED_IN)) {

                startActivity(new Intent(context, LoginActivity.class));

            }
            finish();


        }, Constants.SPLASH_TIME_OUT);

    }


    private void initUi() {
        context = SplashActivity.this;
        Functions.hideSystemUI(context);
        sh = new SharedPref(context);
        dataRef = FirebaseDatabase.getInstance().getReference();
    }
}