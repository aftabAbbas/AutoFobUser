package com.idialogics.autofobuser.Activities.Main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.idialogics.autofobuser.Activities.StartUp.LoginActivity;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.Functions;
import com.idialogics.autofobuser.Utils.SharedPref;

@SuppressLint("InflateParams")
public class SettingsActivity extends AppCompatActivity {

    Context context;
    Toolbar toolbar;
    ConstraintLayout deleteAccount;
    SharedPref sharedPref;
    SwitchCompat notificationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        setContentView(R.layout.activity_settings);

        init();

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        deleteAccount.setOnClickListener(v -> {

            showAccountDeleteDialog();
            //

        });

    }


    private void init() {
        context = SettingsActivity.this;
        sharedPref = new SharedPref(context);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        deleteAccount = findViewById(R.id.delete_account);
        notificationSwitch = findViewById(R.id.notifications_switch);

        boolean isChecked = sharedPref.getBoolean(Constants.CHECK_IS_NOTIFICATION_ON);

        notificationSwitch.setChecked(isChecked);


        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked1) ->
                sharedPref.putBoolean(Constants.CHECK_IS_NOTIFICATION_ON, isChecked1));
    }


    private void showAccountDeleteDialog() {

        View view1 = getLayoutInflater().inflate(R.layout.delete_account_bottom_sheet, null);
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        dialog.setContentView(view1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnDone = dialog.findViewById(R.id.btn_delete);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        EditText etPwd = dialog.findViewById(R.id.et_password);


        assert btnDone != null;
        btnDone.setOnClickListener(v -> {

            assert etPwd != null;
            String password = etPwd.getText().toString().trim();
            if (!password.equals("")) {


                dialog.dismiss();
                deleteAccount(password);

            } else {

                etPwd.setError(Constants.EMPTY_ERROR);

            }

        });


        assert btnCancel != null;
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }

    private void deleteAccount(String password) {

        Functions.loadingDialog(context, "Deleting", true);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider.getCredential(sharedPref.getString(Constants.KEY_EMAIL), password);

        assert user != null;
        user.reauthenticate(credential)
                .addOnCompleteListener(task ->
                        user.delete().addOnCompleteListener(task1 -> {

                            if (task1.isSuccessful()) {
                                sharedPref.clearPrefrence();
                                Intent intent = new Intent(context, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }));
    }

}