package com.idialogics.autofobuser.Utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idialogics.autofobuser.Model.Notifications;
import com.idialogics.autofobuser.R;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class Functions {
    public static Dialog dialog;


    public static void loadingDialog(Context context, String text, boolean showHide) {


        if (showHide) {

            dialog = new Dialog(context);
            dialog.setContentView(R.layout.loading_dialog);
            dialog.setCancelable(false);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            dialog.getWindow().setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            TextView tv = dialog.findViewById(R.id.textView);
            tv.setText(text);

            dialog.show();

        } else {

            dialog.dismiss();

        }


    }



    public static void noInternetConnectDialog(Context context) {


        Dialog noInternetDialog = new Dialog(context);
        noInternetDialog.setContentView(R.layout.no_internet_dialog);
        Objects.requireNonNull(noInternetDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        noInternetDialog.setCancelable(false);
        Button btnConnect = noInternetDialog.findViewById(R.id.btn_connect);


        btnConnect.setOnClickListener(v -> {

            noInternetDialog.dismiss();
            Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
            context.startActivity(intent);

        });
        noInternetDialog.show();
        noInternetDialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);


    }

    public static String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.getDefault());

        return df.format(c);
    }


    public static void changeStatusBarColor(Context context, int color) {

        Activity activity = (Activity) context;
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);

    }

    public static void hideSystemUI(Context context) {


        Activity activity = (Activity) context;
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    public static void Vibrate(Context context) {

        Vibrator vibrate = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrate.vibrate(50);

    }

    @SuppressWarnings("deprecation")
    public static void showSnackBar(Context context, String text) {

        Activity activity = (Activity) context;

        Snackbar.make(activity.findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
                .setAction("Ok", view -> {

                })
                .setActionTextColor(context.getResources().getColor(android.R.color.holo_red_light))
                .show();

    }

    public static void uploadToken(Context context, String token) {

        SharedPref sh = new SharedPref(context);

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        HashMap<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put(Constants.KEY_FCM_TOKEN, token);

        database.collection(Constants.KEY_COLLECTION_USERS).
                document(sh.getString(Constants.ID))
                .update(tokenInfo)
                .addOnSuccessListener(aVoid -> {

                    sh.putString(Constants.KEY_FCM_TOKEN, token);
                    //Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e ->

                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());


    }

    public static void pushDownButton(Button button) {

        PushDownAnim.setPushDownAnimTo(button)
                .setScale(PushDownAnim.MODE_SCALE, 0.95f);

    }


    public static void pushDownTextView(TextView textView) {

        PushDownAnim.setPushDownAnimTo(textView)
                .setScale(PushDownAnim.MODE_SCALE, 0.95f);
    }

    public static void saveNotiToDB(String titleText, String bodyText, String key) {


        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();

        String date = getCurrentDate();

        Notifications notifications = new Notifications(key, titleText, bodyText, date, Constants.PENDING);

        dataRef.child(Constants.NOTIFICATION)
                .child(key)
                .setValue(notifications);


    }


    public static void saveNotiToDB2(String titleText, String bodyText) {


        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();

        String date = getCurrentDate();

        String key = dataRef.push().getKey();

        Notifications notifications = new Notifications(key, titleText, bodyText, date, Constants.PENDING);

        assert key != null;
        dataRef.child(Constants.NOTIFICATION)
                .child(key)
                .setValue(notifications);


    }

    public static String getMilliSeconds(String time) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH);
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        long millis = date.getTime();

        return String.valueOf(millis);
    }

    /*public static String timeManger(String time) {
        String strEndDate = getCurrentDate();
        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH);
        Date startDate = null;   // initialize start date
        try {
            startDate = myFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date endDate = null; // initialize  end date
        try {
            endDate = myFormat.parse(strEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert endDate != null;
        assert startDate != null;
        long difference = endDate.getTime() - startDate.getTime();

        int days = (int) (difference / (1000 * 60 * 60 * 24));
        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
        hours = (hours < 0 ? -hours : hours);
        if (min < 1) {
            return "now";
        }
        if (hours < 1) {
            return min + " mints ago";
        }
        if (days < 1) {
            return hours + " hours ago";
        }
        if (days < 365) {
            return ChangeDateFormat(time);
        }
        return ChangeDateFormat2(time);

    }*/

    public static String timeManager(String time) {
        String endDate1 = getCurrentDate();
        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH);
        Date startDate = null;   // initialize start date

        try {
            startDate = myFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date endDate = null; // initialize  end date
        try {
            endDate = myFormat.parse(endDate1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert endDate != null;
        assert startDate != null;
        long difference = endDate.getTime() - startDate.getTime();

        int days = (int) (difference / (1000 * 60 * 60 * 24));
        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
        hours = (hours < 0 ? -hours : hours);

        if (min < 1) {
            return "now";
        }

        if (hours < 1) {
            if (min <= 1)
                return min + " minute ago";
            else
                return min + " minutes ago";
        }

        if (days < 1) {
            if (hours <= 1)
                return hours + " hour ago";
            else
                return hours + " hours ago";
        }

        if (days < 365) {
            return ChangeDateFormat(time);
        }
        return ChangeDateFormat2(time);
    }

    public static String ChangeDateFormat(String date) {
        DateFormat originalFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
        Date date1 = null;
        try {
            date1 = originalFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date1 != null;
        return targetFormat.format(date1);
    }

    public static String ChangeDateFormat2(String date) {
        DateFormat originalFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
        Date date1 = null;
        try {
            date1 = originalFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date1 != null;
        return targetFormat.format(date1);
    }

    public static void pushDownItem(View itemView) {

        PushDownAnim.setPushDownAnimTo(itemView)
                .setScale(PushDownAnim.MODE_SCALE, 0.95f);
    }

    public static void pushDownCard(CardView cvMain) {

        PushDownAnim.setPushDownAnimTo(cvMain)
                .setScale(PushDownAnim.MODE_SCALE, 0.95f);
    }

    public static void pushDownIV(ImageView iv) {
        PushDownAnim.setPushDownAnimTo(iv)
                .setScale(PushDownAnim.MODE_SCALE, 0.95f);
    }

  /*  public static void showImageDialog(Context context, String url) {
        Dialog dialog = new Dialog(context);
        dialog.getWindow().getAttributes().windowAnimations = R.style.profileDialogAnim;
        dialog.setContentView(R.layout.profile_image_dialog);

        ImageView ivUser = dialog.findViewById(R.id.iv_user);


        Glide.with(context).load(url).placeholder(R.drawable.place_holder).into(ivUser);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }*/

    public static void showImageDialog(Context context, View itemView, String url) {

        final View dialogView = View.inflate(context, R.layout.profile_image_dialog, null);
        Dialog dialog = new Dialog(context, R.style.MyAlertDialogStyle);
        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        ImageView ivUser = dialog.findViewById(R.id.iv_user);
        ImageView ivCancel = dialog.findViewById(R.id.iv_cancel);


        Glide.with(context).load(url).dontAnimate().into(ivUser);


        dialog.setOnShowListener(dialogInterface -> revealShow(itemView, dialogView, true, null));


        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {

                revealShow(itemView, dialogView, false, dialog);
                return true;
            }

            return false;
        });

        ivCancel.setOnClickListener(v -> revealShow(itemView, dialogView, false, dialog));

        dialog.show();
        dialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);


    }

    private static void revealShow(View itemView, View dialogView, boolean b, final Dialog dialog) {


        final View view = dialogView.findViewById(R.id.view_profile);

        int w = view.getWidth();
        int h = view.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        int cx = (int) (itemView.getX() + (itemView.getWidth() / 2));
        int cy = (int) (itemView.getY()) + itemView.getHeight() + 56;


        if (b) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, endRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(500);
            revealAnimator.start();

        } else {

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(500);
            anim.start();
        }


    }

}
