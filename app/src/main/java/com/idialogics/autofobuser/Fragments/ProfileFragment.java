package com.idialogics.autofobuser.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.idialogics.autofobuser.Activities.StartUp.ResetPasswordSuccessActivity;
import com.idialogics.autofobuser.MainActivity;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.Functions;
import com.idialogics.autofobuser.Utils.SharedPref;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("deprecation")
public class ProfileFragment extends Fragment {

    Context context;
    SharedPref sh;
    TextView tvName;
    EditText etFName, etLName, etEmail;
    ImageView ivEdit, ivChoseImage;
    CircleImageView civUser;
    Button btnUpdate, btnUploadDp;
    FirebaseFirestore fireStore;
    StorageReference userDpStorageRef;
    Uri selectedFileURI = null;
    String dpUrl = "";
    ConstraintLayout clResetPwd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initUI(view);

        ivEdit.setOnClickListener(v -> {

            if (btnUploadDp.getVisibility() == View.GONE) {

                etFName.setFocusableInTouchMode(true);
                etLName.setFocusableInTouchMode(true);
                etFName.setEnabled(true);
                etLName.setEnabled(true);

                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                etFName.requestFocus();

                btnUpdate.setVisibility(View.VISIBLE);

            } else {

                Functions.showSnackBar(context, "Upload Profile Picture First");

            }
        });


        btnUpdate.setOnClickListener(v -> {

            String fName = etFName.getText().toString();
            String lName = etLName.getText().toString();

            if (fName.equals("")) {

                Functions.showSnackBar(context, Constants.EMPTY_ERROR.toString());

            }
            if (lName.equals("")) {

                Functions.showSnackBar(context, Constants.EMPTY_ERROR.toString());

            } else {

                updateName(fName, lName);

            }

        });

        ivChoseImage.setOnClickListener(v -> {

            if (btnUpdate.getVisibility() == View.GONE) {

                choseImage();

            } else {

                Functions.showSnackBar(context, "Please Update information first");

            }

        });

        btnUploadDp.setOnClickListener(v -> uploadDpToDatabase());

        civUser.setOnClickListener(v -> Functions.showImageDialog(context,civUser, sh.getString(Constants.KEY_DP)));


        clResetPwd.setOnClickListener(v -> {

            resetPassword();
            //

        });

        return view;
    }


    private void initUI(View view) {

        context = view.getContext();
        sh = new SharedPref(context);
        fireStore = FirebaseFirestore.getInstance();
        userDpStorageRef = FirebaseStorage.getInstance().getReference().child("Profile Pictures");
        etFName = view.findViewById(R.id.et_first_name);
        etLName = view.findViewById(R.id.et_last_name);
        etEmail = view.findViewById(R.id.et_email);
        tvName = view.findViewById(R.id.tv_user_name);
        ivEdit = view.findViewById(R.id.iv_edit);
        ivChoseImage = view.findViewById(R.id.iv_camera);
        civUser = view.findViewById(R.id.civ_user);
        btnUpdate = view.findViewById(R.id.btn_update);
        btnUploadDp = view.findViewById(R.id.btn_upload_dp);
        clResetPwd = view.findViewById(R.id.reset_pwd_layout);

        etFName.setFocusableInTouchMode(false);
        etLName.setFocusableInTouchMode(false);
        etEmail.setFocusableInTouchMode(false);

        btnUpdate.setVisibility(View.GONE);

        Functions.pushDownButton(btnUpdate);
        Functions.pushDownButton(btnUploadDp);

        setData();
    }

    private void setData() {

        String fName = sh.getString(Constants.KEY_FIRST_NAME).trim();
        String lName = sh.getString(Constants.KEY_LAST_NAME).trim();
        String name = fName + " " + lName;
        String email = sh.getString(Constants.KEY_EMAIL);


        tvName.setText(name);
        etFName.setText(fName);
        etLName.setText(lName);
        etEmail.setText(email);
        Glide.with(context).load(sh.getString(Constants.KEY_DP)).placeholder(R.drawable.place_holder).into(civUser);
    }

    private void updateName(String fName, String lName) {

        Functions.loadingDialog(context, "Updating", true);

        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put(Constants.KEY_FIRST_NAME, fName);
        userInfo.put(Constants.KEY_LAST_NAME, lName);

        fireStore.collection(Constants.KEY_COLLECTION_USERS)
                .document(sh.getString(Constants.ID))
                .update(userInfo)
                .addOnCompleteListener(task -> {


                    sh.putString(Constants.KEY_FIRST_NAME, fName);
                    sh.putString(Constants.KEY_LAST_NAME, lName);

                    setData();
                    btnUpdate.setVisibility(View.GONE);
                    etFName.setFocusableInTouchMode(false);
                    etLName.setFocusableInTouchMode(false);
                    etFName.setEnabled(false);
                    etLName.setEnabled(false);

                    Functions.loadingDialog(context, "Updating", false);
                    Functions.showSnackBar(context, "Updated");

                    ((MainActivity) context).headerSetting();

                }).addOnFailureListener(e -> {

            Functions.showSnackBar(context, e.getMessage());
            Functions.loadingDialog(context, "Updating", false);

        });

    }

    private void choseImage() {
        ImagePicker.Companion.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)
                .start();//Final image resolution will be less than 1080 x 1080(Optional)

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (data != null) {
                selectedFileURI = data.getData();

                if (selectedFileURI != null && !Objects.requireNonNull(selectedFileURI.getPath()).isEmpty()) {

                    btnUpdate.setVisibility(View.GONE);
                    Glide.with(context).load(selectedFileURI).into(civUser);
                    btnUploadDp.setVisibility(View.VISIBLE);

                } else {

                    Functions.showSnackBar(context, "Cannot Get this Image");

                }
            }

        }
    }


    private void uploadDpToDatabase() {

        Functions.loadingDialog(context, "Uploading", true);

        StorageReference filePath = userDpStorageRef.child(sh.getString(Constants.ID) + ".jpg");

        filePath.putFile(selectedFileURI).addOnSuccessListener(taskSnapshot -> {

            final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();

            firebaseUri.addOnSuccessListener(uri -> {

                dpUrl = uri.toString();
                upDateUserInfo();

            }).addOnFailureListener(e -> {

                Functions.loadingDialog(context, "Uploading", false);
                Functions.showSnackBar(context, e.getMessage());

            });

        }).addOnFailureListener(e -> {

            Functions.loadingDialog(context, "Uploading", false);
            Functions.showSnackBar(context, e.getMessage());

        });


    }

    private void upDateUserInfo() {

        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put(Constants.KEY_DP, dpUrl);

        fireStore.collection(Constants.KEY_COLLECTION_USERS)
                .document(sh.getString(Constants.ID))
                .update(userInfo)
                .addOnCompleteListener(task -> {


                    Functions.showSnackBar(context, "Uploaded");
                    Functions.loadingDialog(context, "Uploading", false);
                    sh.putString(Constants.KEY_DP, dpUrl);
                    btnUploadDp.setVisibility(View.GONE);

                    ((MainActivity) context).headerSetting();

                }).addOnFailureListener(e -> {

            Functions.loadingDialog(context, "Uploading", false);
            Functions.showSnackBar(context, e.getMessage());

        });

    }

    public void resetPassword() {

        Dialog resetPasswordDialog = new Dialog(context);
        resetPasswordDialog.setContentView(R.layout.reset_password_dialog);
        Objects.requireNonNull(resetPasswordDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        resetPasswordDialog.setCancelable(false);

        Button btnYes = resetPasswordDialog.findViewById(R.id.btn_yes);
        Button btnNo = resetPasswordDialog.findViewById(R.id.btn_no);
        TextView tvPasswordText = resetPasswordDialog.findViewById(R.id.tv_reset_password_text);

        String text = "We'll send an email with instruction to reset password on this email "
                + "<b>" + sh.getString(Constants.KEY_EMAIL) + "</b> ";

        tvPasswordText.setText(Html.fromHtml(text));

        btnYes.setOnClickListener(v -> {

            Functions.loadingDialog(context, "Resetting", true);
            resetPasswordDialog.dismiss();
            restPasswordSend();


        });

        btnNo.setOnClickListener(v -> resetPasswordDialog.dismiss());

        resetPasswordDialog.show();
        resetPasswordDialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);


    }

    private void restPasswordSend() {

        FirebaseAuth.getInstance().sendPasswordResetEmail(sh.getString(Constants.KEY_EMAIL))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        //hide progress
                        FirebaseAuth.getInstance().signOut();
                        sh.clearPrefrence();


                        Functions.loadingDialog(context, "Resetting", false);

                        Intent intent = new Intent(context, ResetPasswordSuccessActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                }).addOnFailureListener(e -> {

            Functions.loadingDialog(context, "Resetting", false);
            Functions.showSnackBar(context, e.getMessage());
            //hide progress


        });

    }
}