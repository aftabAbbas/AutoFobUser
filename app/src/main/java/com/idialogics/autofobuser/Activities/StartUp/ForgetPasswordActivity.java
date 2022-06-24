package com.idialogics.autofobuser.Activities.StartUp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.Functions;
import com.idialogics.autofobuser.Utils.SharedPref;

public class ForgetPasswordActivity extends AppCompatActivity {

    Context context;
    EditText etEmail;
    Button btnContinue;
    String email;
    boolean validate;
    SharedPref sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        initUI();

        btnContinue.setOnClickListener(v -> {

            email = etEmail.getText().toString().trim();

            validate = validateFields();

            if (validate) {

                allowSendEmail();

            } else {

                Functions.showSnackBar(context, getString(R.string.unknow));

            }


        });
    }

    private void allowSendEmail() {

        Functions.loadingDialog(context, "Resetting", true);

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        //hide progress
                        FirebaseAuth.getInstance().signOut();
                        sh.clearPrefrence();


                        Functions.loadingDialog(context, "Resetting", false);

                        Intent intent = new Intent(context,ResetPasswordSuccessActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                }).addOnFailureListener(e -> {

            Functions.loadingDialog(context, "Resetting", false);
            Functions.showSnackBar(context, e.getMessage());
            //hide progress


        });

    }


    private boolean validateFields() {

        if (email.equals("")) {

            etEmail.setError(Constants.EMPTY_ERROR);

        } else if (!email.matches(Constants.EMAIL_VALID_REGEX)) {

            etEmail.setError(Constants.NOT_VALID_ADDRESS);

        } else {

            validate = true;
        }

        return validate;
    }

    private void initUI() {
        context =ForgetPasswordActivity.this;
        sh = new SharedPref(context);
        etEmail = findViewById(R.id.et_email);
        btnContinue = findViewById(R.id.btn_continue);
    }

    public void back(View view) {

        onBackPressed();

    }
}