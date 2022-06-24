package com.idialogics.autofobuser.Activities.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.idialogics.autofobuser.Model.Buy;
import com.idialogics.autofobuser.Model.Cart;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.DigitTextView;
import com.idialogics.autofobuser.Utils.Functions;
import com.idialogics.autofobuser.Utils.SharedPref;

public class ProductBuyActivity extends AppCompatActivity {

    Context context;
    DatabaseReference dataRef;
    SharedPref sh;
    Cart productIntent;
    String manufacturer, model, year, productName, price, availability, fcc, battery, productDetail, productImage;
    TextView tvProductName, tvPrice, tvProductDetail, tvBattery, tvFCC, tvAvailability;
    ImageView ivProduct, ivPlus, ivMinus;
    int count;
    Button btnRemoveFromCart, btnBuy;
    CheckBox ckMatch, ckFitment;
    DigitTextView tvCount;
    ConstraintLayout clProduct;
    double finalPrice;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_buy);
        initUI();

        //tvCount.setText(String.valueOf(count));

        ivPlus.setOnClickListener(v -> {

            count++;
            finalPrice = Double.parseDouble(price) * count;
            finalPrice = Math.round(finalPrice * 100D) / 100D;
            tvCount.setValue(count, true);
            String price = "$ " + finalPrice;
            tvPrice.setText(price);

        });

        ivMinus.setOnClickListener(v -> {

            if (count == 1) {

                Functions.showSnackBar(context, "Minimum Item is 1");

            } else {

                count--;
                finalPrice = Double.parseDouble(price) * count;
                finalPrice = Math.round(finalPrice * 100D) / 100D;
                tvCount.setValue(count, true);
                String price = "$ " + finalPrice;
                tvPrice.setText(price);

            }

        });

        btnBuy.setOnClickListener(v -> {

            if (ckMatch.isChecked() && ckFitment.isChecked()) {

                sendToNextActivity();


            } else {

                Functions.showSnackBar(context, "Please Check your agreement");

            }

        });

        btnRemoveFromCart.setOnClickListener(v -> {

            removeFromCart();
            //

        });

        clProduct.setOnClickListener(v -> Functions.showImageDialog(context, clProduct, productImage));


    }


    private void sendToNextActivity() {

        String key = dataRef.push().getKey();
        String date = Functions.getCurrentDate();
        Buy buy = null;
        if (key != null) {

            buy = new Buy(key, manufacturer, model, year, productIntent.getProductId(),
                    productIntent.getProductImage(), String.valueOf(count), productName, productIntent.getPrice()
                    , battery, fcc, availability, date, sh.getString(Constants.ID), Constants.PENDING, "", "", "", "", "", "", "", "", productIntent.getId(), "");


        }

        Intent intent = new Intent(context, BillingInfoActivity.class);
        intent.putExtra(Constants.PRODUCT_DETAIL_INTENT, buy);
        intent.putExtra(Constants.IS_MULTIPLE, false);
        startActivity(intent);
        finish();
        //buyProduct();
    }


    private void initUI() {

        context = ProductBuyActivity.this;
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
        btnRemoveFromCart = findViewById(R.id.btn_remove_from_cart);
        btnBuy = findViewById(R.id.btn_buy);
        ckMatch = findViewById(R.id.ck_match);
        ckFitment = findViewById(R.id.ck_fitment);
        clProduct = findViewById(R.id.constraintLayout2);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Functions.pushDownButton(btnBuy);
        Functions.pushDownButton(btnRemoveFromCart);

        productIntent = (Cart) getIntent().getSerializableExtra(Constants.PRODUCT_DETAIL_INTENT);


        productName = productIntent.getProductName();

        productImage = productIntent.getProductImage();


        Glide.with(context).load(productImage).into(ivProduct);

        productDetail = manufacturer + " " + model + " " + year;
        price = productIntent.getPrice();
        availability = productIntent.getAvailability();
        battery = productIntent.getBattery();
        fcc = productIntent.getFcc();
        manufacturer = productIntent.getManufacturer();
        model = productIntent.getModel();
        year = productIntent.getYear();

        count = Integer.parseInt(productIntent.getCount());
        float intPrice = Float.parseFloat(price);

        float finalPrice2 = count * intPrice;

        String priceText = "$ " + finalPrice2;

        tvProductDetail.setText(productDetail);
        tvProductName.setText(productName);
        tvPrice.setText(priceText);
        tvFCC.setText(fcc);
        tvAvailability.setText(availability);
        tvBattery.setText(battery);

        tvCount.setValue(count, false);

        finalPrice = Float.parseFloat(productIntent.getPrice());
    }


    private void removeFromCart() {

        Functions.loadingDialog(context, "Deleting", true);

        dataRef.child(Constants.USER)
                .child(sh.getString(Constants.ID))
                .child(Constants.CART)
                .child(productIntent.getId())
                .removeValue()
                .addOnCompleteListener(task -> {

                    Functions.loadingDialog(context, "Deleting", false);
                    Functions.showSnackBar(context, "Deleted");


                    onBackPressed();
                    finish();


                }).addOnFailureListener(e -> {

            Functions.loadingDialog(context, "Deleting", false);
            Functions.showSnackBar(context, e.getMessage());

        });

    }


    public void back(View view) {
        onBackPressed();
    }
}