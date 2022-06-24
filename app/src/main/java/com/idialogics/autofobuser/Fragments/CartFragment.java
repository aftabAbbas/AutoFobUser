package com.idialogics.autofobuser.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.idialogics.autofobuser.Activities.Main.BillingInfo2Activity;
import com.idialogics.autofobuser.Adapter.CartRecycler;
import com.idialogics.autofobuser.MainActivity;
import com.idialogics.autofobuser.Model.Cart;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.Functions;
import com.idialogics.autofobuser.Utils.SharedPref;

import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class CartFragment extends Fragment {

    public static Snackbar snackbar;
    public static ArrayList<Cart> selectedList;
    Context context;
    DatabaseReference dataRef;
    SharedPref sh;
    ArrayList<Cart> cartArrayList;
    CartRecycler cartRecycler;
    RecyclerView rvCart;
    SwipeRefreshLayout swCart;
    TextView tvNoCartItem;
    Button btnBuy;
    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        initUI(view);


        return view;
    }

    private void initUI(View view) {

        context = view.getContext();
        activity = (Activity) context;
        dataRef = FirebaseDatabase.getInstance().getReference().child(Constants.USER);
        sh = new SharedPref(context);
        rvCart = view.findViewById(R.id.rv_cart);
        swCart = view.findViewById(R.id.sw_cart);
        tvNoCartItem = view.findViewById(R.id.tv_no_cart);
        btnBuy = view.findViewById(R.id.btn_buy);
        selectedList = new ArrayList<>();

        cartArrayList = new ArrayList<>();
        getData();


        if (snackbar == null) {

            swCart.setOnRefreshListener(this::getData);


        } else {


            swCart.setEnabled(false);
            swCart.setRefreshing(false);

        }


        btnBuy.setOnClickListener(v -> {

            if (cartArrayList.size() > 0) {
                if (snackbar.isShown()) {

                    ((MainActivity) context).manageVPSwipe(true);


                    btnBuy.setText(getString(R.string.proceed_to_checkout));

                    snackbar.dismiss();

                    cartRecycler = new CartRecycler(context, cartArrayList, false);


                } else {

                    ((MainActivity) context).manageVPSwipe(false);

                    swCart.setEnabled(false);
                    swCart.setRefreshing(false);

                    btnBuy.setText(getString(R.string.cancel));

                    snackbar.show();

                    cartRecycler = new CartRecycler(context, cartArrayList, true);

                }
                rvCart.setAdapter(cartRecycler);
            } else {

                Functions.showSnackBar(context, "Please add some products in cart");

            }

        });


        snackbar = Snackbar.make(activity.findViewById(android.R.id.content), selectedList.size() + " Selected", Snackbar.LENGTH_INDEFINITE)
                .setAction("Buy", view1 -> {

                    if (CartRecycler.selectedProductList.size() > 0) {

                        Intent intent = new Intent(context, BillingInfo2Activity.class);
                        startActivity(intent);

                    } else {

                        Toast.makeText(context, "Please select product", Toast.LENGTH_SHORT).show();

                    }

                })
                .setActionTextColor(context.getResources().getColor(android.R.color.holo_red_light));

    }


    private void getData() {

        swCart.setRefreshing(true);

        if (snackbar != null) {

            snackbar.dismiss();
        }

        dataRef.child(sh.getString(Constants.ID)).child(Constants.CART)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        cartArrayList.clear();
                        swCart.setRefreshing(false);
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        swCart.setRefreshing(false);
                        Functions.showSnackBar(context, error.getMessage());

                    }
                });

    }


    @Override
    public void onResume() {
        super.onResume();
        getData();

        cartRecycler = new CartRecycler(context, cartArrayList, false);
        rvCart.setAdapter(cartRecycler);

        snackbar.dismiss();

        btnBuy.setText(getString(R.string.proceed_to_checkout));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {

            clearSelection();
            snackbar.dismiss();

            btnBuy.setText(getString(R.string.proceed_to_checkout));
        }

    }

    public void clearSelection() {

        if (cartArrayList.size() > 0) {

            cartRecycler = new CartRecycler(context, cartArrayList, false);
            rvCart.setAdapter(cartRecycler);

        }
    }


}