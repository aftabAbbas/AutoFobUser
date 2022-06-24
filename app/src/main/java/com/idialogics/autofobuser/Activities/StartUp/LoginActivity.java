package com.idialogics.autofobuser.Activities.StartUp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idialogics.autofobuser.MainActivity;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.Functions;
import com.idialogics.autofobuser.Utils.SharedPref;

public class LoginActivity extends AppCompatActivity {

    Context context;
    SharedPref sh;
    EditText etEmail, etPassword;
    AppCompatButton btnLogin;
    String email, password, id;
    boolean validate = false;
    FirebaseAuth mAuth;
    FirebaseFirestore fireStore;
    TextView tvForgetPassword, tvSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();

        btnLogin.setOnClickListener(v -> {

            validate = validateFields();

            if (validate) {

                allowLogin();

            }

        });


        tvForgetPassword.setOnClickListener(this::resetPassword);

        tvSignUp.setOnClickListener(this::signUp);

    }


    private void initUI() {
        context = LoginActivity.this;
        sh = new SharedPref(context);
        mAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvForgetPassword = findViewById(R.id.textView4);
        tvSignUp = findViewById(R.id.tv_signUp);


        Functions.pushDownButton(btnLogin);
        Functions.pushDownTextView(tvForgetPassword);
        Functions.pushDownTextView(tvSignUp);

    }

    public void signUp(View view) {

        startActivity(new Intent(context, SignUpActivity.class));

    }


    private boolean validateFields() {

        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();

        if (email.equals("")) {

            etEmail.setError(Constants.EMPTY_ERROR);

        } else if (!email.matches(Constants.EMAIL_VALID_REGEX)) {

            etEmail.setError(Constants.NOT_VALID_ADDRESS);

        } else if (password.equals("")) {

            etPassword.setError(Constants.EMPTY_ERROR);

        } else {

            validate = true;
        }


        return validate;
    }

    private void allowLogin() {

        Functions.loadingDialog(context, "Loading", true);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        if (mAuth.getCurrentUser() != null) {

                            id = mAuth.getCurrentUser().getUid();
                            getDataFromFireStore();
                        }


                    } else {

                        if (task.getException() != null) {

                            Functions.showSnackBar(context, task.getException().getMessage());
                            Functions.loadingDialog(context, "Loading", false);

                        }
                    }


                }).addOnFailureListener(e -> {

            Functions.showSnackBar(context, e.getMessage());
            Functions.loadingDialog(context, "Loading", false);

        });

    }

    private void getDataFromFireStore() {

        fireStore.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.ID, id).get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {


                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                        sh.putBoolean(Constants.IS_LOGGED_IN, true);
                        sh.putString(Constants.ID, id);
                        sh.putString(Constants.KEY_EMAIL, email);
                        sh.putString(Constants.KEY_FIRST_NAME, documentSnapshot.getString(Constants.KEY_FIRST_NAME));
                        sh.putString(Constants.KEY_LAST_NAME, documentSnapshot.getString(Constants.KEY_LAST_NAME));
                        sh.putString(Constants.PHONE, documentSnapshot.getString(Constants.PHONE));
                        sh.putString(Constants.KEY_DP, documentSnapshot.getString(Constants.KEY_DP));
                        sh.putString(Constants.VERSION, Constants.APP_VERSION_CODE);
                        sh.putString(Constants.USER_TYPE, Constants.ADMIN);
                        sh.putBoolean(Constants.CHECK_IS_NOTIFICATION_ON, true);
                        Functions.loadingDialog(context, "Loading", false);
                        Functions.showSnackBar(context, "Logged in");

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);


                    }else {

                        Functions.loadingDialog(context, "Loading", false);
                        Functions.showSnackBar(context, "User not found");

                    }

                }).addOnFailureListener(e -> {

            Functions.showSnackBar(context, e.getMessage());
            Functions.loadingDialog(context, "Loading", false);
        });


    }

    public void resetPassword(View view) {

        startActivity(new Intent(context, ForgetPasswordActivity.class));


    }
}