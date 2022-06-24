package com.idialogics.autofobuser.Activities.Main;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.idialogics.autofobuser.Model.Cart;
import com.idialogics.autofobuser.Model.Product;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.CircleAnimationUtil;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.DigitTextView;
import com.idialogics.autofobuser.Utils.Functions;
import com.idialogics.autofobuser.Utils.SharedPref;

public class ProductDetailActivity extends AppCompatActivity {

    Context context;
    DatabaseReference dataRef;
    SharedPref sh;
    Product productIntent;
    String manufacturer, model, year, productName, price, availability, fcc, battery, productDetail, productImage;
    TextView tvProductName, tvPrice, tvProductDetail, tvBattery, tvFCC, tvAvailability, tvCartCount;
    ImageView ivProduct, ivPlus, ivMinus;
    DigitTextView tvCount;
    int count = 1, cartCount = 0;
    Button btnAddToCart;
    CheckBox ckMatch, ckFitment;
    String priceText;
    ConstraintLayout clProduct;
    float finalPrice;
    Toolbar toolbar;
    CardView cvBadge;
    RelativeLayout destView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        initUI();

        tvCount.setValue(count,false);

        ivPlus.setOnClickListener(v -> {

            count++;
            finalPrice = Float.parseFloat(price) * count;
            tvCount.setValue(count,true);
            String price = "$ " + finalPrice;
            tvPrice.setText(price);

        });

        destView.setOnClickListener(v -> {

            startActivity(new Intent(context,CartListActivity.class));
            //

        });

        ivMinus.setOnClickListener(v -> {

            if (count == 1) {

                Functions.showSnackBar(context, "Minimum Item is 1");

            } else {

                count--;
                finalPrice = Float.parseFloat(price) * count;
                tvCount.setValue(count,true);
                String price = "$ " + finalPrice;
                tvPrice.setText(price);

            }

        });

        btnAddToCart.setOnClickListener(v -> {

            if (ckMatch.isChecked() && ckFitment.isChecked()) {

                makeFlyAnimation(ivProduct);



            } else {

                Functions.showSnackBar(context, "Please Check your agreement");

            }

        });


        clProduct.setOnClickListener(v -> Functions.showImageDialog(context,clProduct, productImage));


    }

    private void makeFlyAnimation(ImageView targetView) {



        new CircleAnimationUtil().attachActivity(this).setTargetView(targetView).setMoveDuration(500).setDestView(destView).setAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                addToCart();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).startAnimation();


    }

    private void initUI() {

        context = ProductDetailActivity.this;
        dataRef = FirebaseDatabase.getInstance().getReference();
        sh = new SharedPref(context);
        tvProductName = findViewById(R.id.tv_name);
        tvAvailability = findViewById(R.id.tv_availability);
        tvPrice = findViewById(R.id.tv_price);
        tvFCC = findViewById(R.id.tv_fcc);
        tvBattery = findViewById(R.id.tv_battery);
        tvProductDetail = findViewById(R.id.tv_product_detail);
        ivProduct = findViewById(R.id.iv_product);
        ivMinus = findViewById(R.id.iv_minus);
        ivPlus = findViewById(R.id.iv_plus);
        tvCount = findViewById(R.id.tv_count);
        btnAddToCart = findViewById(R.id.btn_add_cart);
        ckMatch = findViewById(R.id.ck_match);
        ckFitment = findViewById(R.id.ck_fitment);
        toolbar = findViewById(R.id.toolbar);
        clProduct = findViewById(R.id.constraintLayout2);
        cvBadge = findViewById(R.id.cv_notification2);
        tvCartCount = findViewById(R.id.tv_cart_count);
        destView = findViewById(R.id.cartRelativeLayout);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Functions.pushDownButton(btnAddToCart);
        Functions.pushDownIV(ivPlus);
        Functions.pushDownIV(ivMinus);

        productIntent = (Product) getIntent().getSerializableExtra(Constants.PRODUCT_DETAIL_INTENT);
        manufacturer = getIntent().getStringExtra(Constants.MANUFECTURER).trim();
        model = getIntent().getStringExtra(Constants.MODEL).trim();
        year = getIntent().getStringExtra(Constants.YEAR).trim();


        productName = productIntent.getName().trim();

        productImage = productIntent.getImage().trim();

        Glide.with(context).load(productImage).into(ivProduct);

        productDetail = manufacturer + " " + model + " " + year;
        price = productIntent.getPrice().trim();
        availability = productIntent.getAvailability().trim();
        battery = productIntent.getBattery().trim();
        fcc = productIntent.getFcc().trim();

        tvProductDetail.setText(productDetail);
        tvProductName.setText(productName);
        priceText = "$ " + price;
        tvPrice.setText(priceText);
        tvFCC.setText(fcc);
        tvAvailability.setText(availability);
        tvBattery.setText(battery);


        finalPrice = Float.parseFloat(productIntent.getPrice().trim());
    }


    private void addToCart() {

        Functions.loadingDialog(context, "Adding", true);

        String key = dataRef.push().getKey();

        String date = Functions.getCurrentDate();

        Cart cart = new Cart(key, manufacturer, model, year, productIntent.getId(), productIntent.getImage().trim()
                , String.valueOf(count), productName, price, battery, fcc, availability, date);


        assert key != null;
        dataRef.child(Constants.USER).child(sh.getString(Constants.ID))
                .child(Constants.CART).child(key)
                .setValue(cart)
                .addOnCompleteListener(task -> {


                    ckFitment.setChecked(false);
                    ckMatch.setChecked(false);
                    count = 1;
                    tvCount.setValue(count,false);
                    priceText = "$ " + price;
                    tvPrice.setText(priceText);
                    Functions.loadingDialog(context, "Adding", false);
                    Functions.showSnackBar(context, "Added to Cart");

                }).addOnFailureListener(e -> {


            Functions.loadingDialog(context, "Adding", false);
            Functions.showSnackBar(context, e.getMessage());


        });

    }

    private void getCartList() {

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

                        Functions.pushDownCard(cvBadge);

                        if (cartCount > 0) {

                            cvBadge.setVisibility(View.VISIBLE);


                            if (cartCount > 9) {


                                String text = "9+";

                                tvCartCount.setText(text);


                            } else {

                                String text1 = String.valueOf(cartCount);
                                tvCartCount.setText(text1);

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

    @Override
    protected void onResume() {
        super.onResume();
        getCartList();
    }
}