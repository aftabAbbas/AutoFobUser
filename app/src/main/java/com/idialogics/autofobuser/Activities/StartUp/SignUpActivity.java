package com.idialogics.autofobuser.Activities.StartUp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.idialogics.autofobuser.MainActivity;
import com.idialogics.autofobuser.Model.Users;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.Functions;
import com.idialogics.autofobuser.Utils.SharedPref;

import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class SignUpActivity extends AppCompatActivity {

    Context context;
    SharedPref sh;
    EditText etFName, etLName, etEmail, etCPassword, etPassword;
    TextView tvPasswordValidator, tvCPasswordValidator;
    AppCompatButton btnSignUp;
    String id, firstName, lastName, email, CPassword, password, token = "";
    boolean valid = false;
    FirebaseAuth mAuth;
    FirebaseFirestore fireStore;
    DatabaseReference dataRef;
    ArrayList<String> headingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initUI();

        addPasswordPatternWatcher();


        btnSignUp.setOnClickListener(v -> {

            firstName = etFName.getText().toString().trim();
            lastName = etLName.getText().toString().trim();
            email = etEmail.getText().toString().trim();
            CPassword = etCPassword.getText().toString().trim();
            password = etPassword.getText().toString().trim();


            valid = fieldsValidation();


            boolean bothPasswordMatch = password.equals(CPassword);


            if (valid && bothPasswordMatch) {

                allowSignUp();


            }

        });

    }


    private void initUI() {
        context = SignUpActivity.this;
        sh = new SharedPref(context);
        mAuth = FirebaseAuth.getInstance();
        headingData = new ArrayList<>();
        fireStore = FirebaseFirestore.getInstance();
        dataRef = FirebaseDatabase.getInstance().getReference();
        etFName = findViewById(R.id.et_fName);
        etLName = findViewById(R.id.et_lName);
        etEmail = findViewById(R.id.et_email);
        etCPassword = findViewById(R.id.et_cPassword);
        etPassword = findViewById(R.id.et_password);
        btnSignUp = findViewById(R.id.btn_signUp);
        tvPasswordValidator = findViewById(R.id.tv_password_validator);
        tvCPasswordValidator = findViewById(R.id.tv_cPassword_validator);


        Functions.pushDownButton(btnSignUp);
    }

    public void login(View view) {

        startActivity(new Intent(context, LoginActivity.class));
        finish();

    }

    private Boolean fieldsValidation() {


        if (firstName.equals("")) {

            etFName.setError(Constants.EMPTY_ERROR);

        } else if (lastName.equals("")) {

            etLName.setError(Constants.EMPTY_ERROR);

        } else if (email.equals("")) {

            etEmail.setError(Constants.EMPTY_ERROR);

        } else if (!email.matches(Constants.EMAIL_VALID_REGEX)) {

            etEmail.setError(Constants.NOT_VALID_ADDRESS);

        } else if (password.equals("")) {

            etPassword.setError(Constants.EMPTY_ERROR);

        } else if (CPassword.equals("")) {

            etCPassword.setError(Constants.EMPTY_ERROR);

        } else {

            valid = true;
        }


        return valid;

    }


    private void addPasswordPatternWatcher() {

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                tvPasswordValidator.setVisibility(View.VISIBLE);

                if (s.length() == 0) {

                    tvPasswordValidator.setText(R.string.not_entered);
                    tvPasswordValidator.setTextColor(getResources().getColor(R.color.red));

                } else if (s.length() < 6) {

                    tvPasswordValidator.setText(R.string.easy);
                    tvPasswordValidator.setTextColor(getResources().getColor(R.color.school_bus_yellow));

                } else if (s.length() < 10) {

                    tvPasswordValidator.setText(R.string.medium);
                    tvPasswordValidator.setTextColor(getResources().getColor(R.color.green));

                } else if (s.length() < 15) {

                    tvPasswordValidator.setText(R.string.strong);
                    tvPasswordValidator.setTextColor(getResources().getColor(R.color.teal_200));

                } else {

                    tvPasswordValidator.setText(R.string.password_max_length);
                    tvPasswordValidator.setTextColor(getResources().getColor(R.color.red));


                }

                if (s.length() == 15) {


                    etPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
                    Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake_layout);
                    etPassword.startAnimation(shake);

                    Functions.Vibrate(context);


                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        etCPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                tvCPasswordValidator.setVisibility(View.VISIBLE);
                tvCPasswordValidator.setTextColor(getResources().getColor(R.color.red));

                if (s.length() == 0) {

                    tvCPasswordValidator.setText(R.string.not_entered);
                    tvCPasswordValidator.setTextColor(getResources().getColor(R.color.red));

                }

                if (etPassword.getText().toString().length() == s.length() && !etPassword.getText().toString().trim().isEmpty()) {

                    if (etPassword.getText().toString().contentEquals(s)) {

                        tvCPasswordValidator.setText(R.string.match);
                        tvCPasswordValidator.setTextColor(getResources().getColor(R.color.green));

                    } else {

                        tvCPasswordValidator.setText(R.string.not_match);
                        tvCPasswordValidator.setTextColor(getResources().getColor(R.color.red));

                    }


                } else if (s.length() > 0) {

                    tvCPasswordValidator.setText(R.string.not_match);
                    tvCPasswordValidator.setTextColor(getResources().getColor(R.color.red));

                }

                if (s.length() == 15) {


                    etCPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
                    Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake_layout);
                    etCPassword.startAnimation(shake);

                    Functions.Vibrate(context);


                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    private void allowSignUp() {
        Functions.loadingDialog(context, "Loading", true);

        mAuth.createUserWithEmailAndPassword(email, CPassword)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {


                        id = mAuth.getCurrentUser().getUid();
                        saveToFireStore();

                    }

                }).addOnFailureListener(e -> {

            Functions.loadingDialog(context, "Loading", false);
            Functions.showSnackBar(context, e.getMessage());


        });

    }

    @SuppressWarnings("deprecation")
    private void saveToFireStore() {

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {

                token = task.getResult().getToken();


            }

        });

        Users users = new Users(id, firstName, lastName, email, "", token, Constants.USER, "");

        fireStore.collection(Constants.KEY_COLLECTION_USERS)
                .document(id)
                .set(users)
                .addOnCompleteListener(task -> {

                    sh.putBoolean(Constants.IS_LOGGED_IN, true);
                    sh.putString(Constants.KEY_FIRST_NAME, users.getFirstName());
                    sh.putString(Constants.KEY_LAST_NAME, users.getLastName());
                    sh.putString(Constants.KEY_EMAIL, users.getEmail());
                    sh.putString(Constants.ID, users.getId());
                    sh.putString(Constants.USER_TYPE, Constants.USER);
                    sh.putString(Constants.VERSION, Constants.APP_VERSION_CODE);
                    sh.putBoolean(Constants.CHECK_IS_NOTIFICATION_ON, true);

                    Functions.loadingDialog(context, "Loading", false);


                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


                }).addOnFailureListener(e -> {

            Functions.loadingDialog(context, "Loading", false);
            Functions.showSnackBar(context, e.getMessage());


        });


    }


}