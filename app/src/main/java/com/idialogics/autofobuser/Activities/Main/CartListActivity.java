package com.idialogics.autofobuser.Activities.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.idialogics.autofobuser.Adapter.CartRecycler;
import com.idialogics.autofobuser.Model.Cart;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.Functions;
import com.idialogics.autofobuser.Utils.SharedPref;

import java.util.ArrayList;

public class CartListActivity extends AppCompatActivity {

    Context context;
    DatabaseReference dataRef;
    SharedPref sh;
    ArrayList<Cart> cartArrayList;
    CartRecycler cartRecycler;
    RecyclerView rvCart;
    TextView tvNoCartItem;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);
        initUI();

    }

    private void initUI() {
        context = CartListActivity.this;
        dataRef = FirebaseDatabase.getInstance().getReference().child(Constants.USER);
        sh = new SharedPref(context);
        rvCart = findViewById(R.id.rv_cart);
        tvNoCartItem = findViewById(R.id.tv_no_cart);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        cartArrayList = new ArrayList<>();
        getData();
    }

    private void getData() {

        Functions.loadingDialog(context,"Loading",true);

        dataRef.child(sh.getString(Constants.ID)).child(Constants.CART)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        cartArrayList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            Cart cart = dataSnapshot.getValue(Cart.class);

                            cartArrayList.add(cart);
                        }


                        if (cartArrayList.size() > 0) {

                            tvNoCartItem.setVisibility(View.GONE);

                            cartArrayList.sort((o1, o2) -> {
                                if (o1.getDate() == null || o2.getDate() == null)
                                    return 0;
                                return o2.getDate().compareTo(o1.getDate());
                            });


                        } else {

                            tvNoCartItem.setVisibility(View.VISIBLE);

                        }

                        cartRecycler = new CartRecycler(context, cartArrayList, false);
                        rvCart.setAdapter(cartRecycler);

                        Functions.loadingDialog(context,"Loading",false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Functions.loadingDialog(context,"Loading",false);
                        Functions.showSnackBar(context, error.getMessage());

                    }
                });

    }

}