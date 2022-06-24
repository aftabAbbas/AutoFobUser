package com.idialogics.autofobuser;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.idialogics.autofobuser.Activities.Main.MyOrdersActivity;
import com.idialogics.autofobuser.Activities.Main.SettingsActivity;
import com.idialogics.autofobuser.Activities.StartUp.LoginActivity;
import com.idialogics.autofobuser.Adapter.MainPagerAdapter;
import com.idialogics.autofobuser.Model.Cart;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.CustomViewPager;
import com.idialogics.autofobuser.Utils.Functions;
import com.idialogics.autofobuser.Utils.SetBusinessName;
import com.idialogics.autofobuser.Utils.SharedPref;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.idialogics.autofobuser.Fragments.CartFragment.snackbar;
import static com.idialogics.autofobuser.Utils.Functions.changeStatusBarColor;

@SuppressLint({"StaticFieldLeak"})
@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {
    public static TextView tvAppName, tvSelectionCount;
    Context context;
    SharedPref sh;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    CircleImageView civUser;
    BottomNavigationView btmNav;
    CustomViewPager vpMain;
    MainPagerAdapter pagerAdapter;
    Dialog logoutDialog;
    int cartCount = 0;
    ConstraintLayout clSelection;
    DatabaseReference dataRef;
    ImageView ivCancelSelection;
    BottomNavigationMenuView bottomNavigationMenuView;
    View v;
    BottomNavigationItemView itemView;
    View badge;
    TextView tvCount;
    CardView cvBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

        bottomNavigationListener();
        pagerListener();

        civUser.setOnClickListener(v -> Functions.showImageDialog(context, clSelection, sh.getString(Constants.KEY_DP)));

    }


    private void initUI() {
        context = MainActivity.this;
        sh = new SharedPref(context);
        dataRef = FirebaseDatabase.getInstance().getReference();
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        civUser = findViewById(R.id.civ_user);
        btmNav = findViewById(R.id.bottomNavigationView);
        vpMain = findViewById(R.id.vp_main);
        appBarLayout = findViewById(R.id.appBarLayout);
        tvAppName = findViewById(R.id.tv_app_name);
        clSelection = findViewById(R.id.cl_selection);
        tvSelectionCount = findViewById(R.id.tv_selection_count);
        ivCancelSelection = findViewById(R.id.iv_cancel);


        navigationView.setItemIconTintList(null);
        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        vpMain.setAdapter(pagerAdapter);


        bottomNavigationMenuView =
                (BottomNavigationMenuView) btmNav.getChildAt(0);
        v = bottomNavigationMenuView.getChildAt(1);
        itemView = (BottomNavigationItemView) v;

        badge = LayoutInflater.from(context)
                .inflate(R.layout.count_badge, itemView, true);

        cvBadge = badge.findViewById(R.id.cv_notification2);

        tvCount = badge.findViewById(R.id.count);


        String businessName = sh.getString(Constants.BUSINESS_NAME);

        tvAppName.setText(businessName);

    }


    private void navigationSet() {

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_home) {

                drawerLayout.closeDrawer(GravityCompat.START);
                changeStatusBarColor(context, context.getResources().getColor(R.color.white));
                btmNav.getMenu().findItem(R.id.menu_home).setChecked(true);
                vpMain.setCurrentItem(0);
                vpMain.setPageTransformer(true, new CardTransformer(0.70f));// Animation.


            } else if (item.getItemId() == R.id.menu_my_orders) {

                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(context, MyOrdersActivity.class));


            } else if (item.getItemId() == R.id.menu_settings) {

                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(context, SettingsActivity.class));


            } else if (item.getItemId() == R.id.menu_logout) {

                drawerLayout.closeDrawer(GravityCompat.START);
                showLogoutDialog();

            }
            return false;
        });

    }

    public void headerSetting() {

        View headerView = navigationView.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.tv_name);
        TextView tvEmail = headerView.findViewById(R.id.tv_email);
        CircleImageView cvUser = headerView.findViewById(R.id.civ_user);


        String name = sh.getString(Constants.KEY_FIRST_NAME) + " " + sh.getString(Constants.KEY_LAST_NAME);
        String email = sh.getString(Constants.KEY_EMAIL);

        tvName.setText(name);
        tvEmail.setText(email);
        tvEmail.setSelected(true);

        Glide.with(context).load(sh.getString(Constants.KEY_DP)).placeholder(R.drawable.place_holder).into(cvUser);
        Glide.with(context).load(sh.getString(Constants.KEY_DP)).placeholder(R.drawable.place_holder).into(civUser);


    }


    private void bottomNavigationListener() {

        btmNav.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_home) {
                dismissBuySnackBar();
                changeStatusBarColor(context, context.getResources().getColor(R.color.white));
                btmNav.getMenu().findItem(R.id.menu_home).setChecked(true);
                vpMain.setCurrentItem(0);
                vpMain.setPageTransformer(true, new CardTransformer(0.70f));// Animation.


            } else if (item.getItemId() == R.id.menu_cart) {

                dismissBuySnackBar();
                changeStatusBarColor(context, context.getResources().getColor(R.color.white));
                btmNav.getMenu().findItem(R.id.menu_cart).setChecked(true);
                vpMain.setCurrentItem(1);
                vpMain.setPageTransformer(true, new CardTransformer(0.70f));// Animation.

            } else if (item.getItemId() == R.id.menu_notifications) {

                dismissBuySnackBar();
                changeStatusBarColor(context, context.getResources().getColor(R.color.white));
                btmNav.getMenu().findItem(R.id.menu_notifications).setChecked(true);
                vpMain.setCurrentItem(2);
                vpMain.setPageTransformer(true, new CardTransformer(0.70f));// Animation.

            } else if (item.getItemId() == R.id.menu_profile) {

                dismissBuySnackBar();
                changeStatusBarColor(context, context.getResources().getColor(R.color.purple_500));
                btmNav.getMenu().findItem(R.id.menu_profile).setChecked(true);
                vpMain.setCurrentItem(3);
                vpMain.setPageTransformer(true, new CardTransformer(0.70f));// Animation.

            }


            return false;
        });

    }


    private void pagerListener() {

        vpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {

                    showToolbar();

                    dismissBuySnackBar();
                    changeStatusBarColor(context, context.getResources().getColor(R.color.white));
                    btmNav.getMenu().findItem(R.id.menu_home).setChecked(true);
                    vpMain.setCurrentItem(0);
                    vpMain.setPageTransformer(true, new CardTransformer(0.70f));// Animation.


                } else if (position == 1) {

                    showToolbar();

                    dismissBuySnackBar();
                    changeStatusBarColor(context, context.getResources().getColor(R.color.white));
                    btmNav.getMenu().findItem(R.id.menu_cart).setChecked(true);
                    vpMain.setCurrentItem(1);
                    vpMain.setPageTransformer(true, new CardTransformer(0.70f));// Animation.


                } else if (position == 2) {

                    showToolbar();

                    dismissBuySnackBar();
                    changeStatusBarColor(context, context.getResources().getColor(R.color.white));
                    btmNav.getMenu().findItem(R.id.menu_notifications).setChecked(true);
                    vpMain.setCurrentItem(2);
                    vpMain.setPageTransformer(true, new CardTransformer(0.70f));// Animation.

                } else if (position == 3) {
                    dismissBuySnackBar();

                    hideToolbar();
                    changeStatusBarColor(context, context.getResources().getColor(R.color.purple_500));
                    btmNav.getMenu().findItem(R.id.menu_profile).setChecked(true);
                    vpMain.setCurrentItem(3);
                    vpMain.setPageTransformer(true, new CardTransformer(0.70f));// Animation.

                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        navigationSet();
        headerSetting();
        getCartCount();

        SetBusinessName.with(context, tvAppName);

        manageVPSwipe(true);

    }

    public void manageVPSwipe(boolean enable) {

        vpMain.setPageScrollEnabled(enable);

    }

    private void hideToolbar() {

        tvAppName.setVisibility(View.GONE);
        appBarLayout.setVisibility(View.GONE);
        civUser.setVisibility(View.GONE);

    }

    private void showToolbar() {

        tvAppName.setVisibility(View.VISIBLE);
        appBarLayout.setVisibility(View.VISIBLE);
        civUser.setVisibility(View.VISIBLE);

    }

    private void showLogoutDialog() {

        logoutDialog = new Dialog(context);
        logoutDialog.setContentView(R.layout.logout_confirm_dialog);
        Objects.requireNonNull(logoutDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        logoutDialog.setCancelable(false);

        Button btnYes = logoutDialog.findViewById(R.id.btn_yes);
        Button btnNo = logoutDialog.findViewById(R.id.btn_no);


        btnYes.setOnClickListener(v -> {

            logoutDialog.dismiss();

            logout();

        });

        btnNo.setOnClickListener(v -> logoutDialog.dismiss());

        logoutDialog.show();
        logoutDialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

    }

    private void logout() {

        FirebaseAuth.getInstance().signOut();
        sh.clearPrefrence();


        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);


    }

    private void getCartCount() {

        dataRef.child(Constants.USER).child(sh.getString(Constants.ID)).child(Constants.CART)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        cartCount = 0;

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            Cart cart = dataSnapshot.getValue(Cart.class);

                            if (cart != null) {
                                cartCount++;
                            }

                        }

                        if (cartCount > 0) {

                            cvBadge.setVisibility(View.VISIBLE);


                            if (cartCount > 9) {


                                String text = "9+";

                                tvCount.setText(text);


                            } else {

                                String text1 = String.valueOf(cartCount);
                                tvCount.setText(text1);

                            }
                        } else {

                            cvBadge.setVisibility(View.INVISIBLE);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                        Functions.showSnackBar(context, error.getMessage());

                    }
                });

    }

    private void dismissBuySnackBar() {

        if (snackbar != null) {

            snackbar.dismiss();
        }


    }

    @Override
    public void onBackPressed() {
        if (vpMain.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            vpMain.setCurrentItem(vpMain.getCurrentItem() - 1);
        }
    }

    public static class CardTransformer implements ViewPager.PageTransformer {

        private final float scalingStart;

        public CardTransformer(float scalingStart) {
            super();
            this.scalingStart = 1 - scalingStart;
        }

        @Override
        public void transformPage(@NonNull View page, float position) {

            if (position >= 0) {
                final int w = page.getWidth();
                float scaleFactor = 1 - scalingStart * position;

                page.setAlpha(1 - position);
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setTranslationX(w * (1 - position) - w);
            }
        }
    }
}