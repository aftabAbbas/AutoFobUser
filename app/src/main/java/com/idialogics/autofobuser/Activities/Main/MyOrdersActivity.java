package com.idialogics.autofobuser.Activities.Main;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.idialogics.autofobuser.Adapter.MyOrderRecycler;
import com.idialogics.autofobuser.Model.Buy;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.Functions;
import com.idialogics.autofobuser.Utils.SharedPref;

import java.util.ArrayList;
import java.util.Objects;

public class MyOrdersActivity extends AppCompatActivity {

    Context context;
    DatabaseReference dataRef;
    SharedPref sh;
    RecyclerView rvMyOrder;
    SwipeRefreshLayout swMyOrder;
    ArrayList<Buy> myOrdersList;
    ArrayList<Buy> pendingList;
    ArrayList<Buy> completedList;
    ArrayList<Buy> canceledList;
    MyOrderRecycler myOrderRecycler;
    TabLayout tlFilter;
    LottieAnimationView animationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        initUI();

        tlFilterListener();

    }

    private void initUI() {
        context = MyOrdersActivity.this;
        dataRef = FirebaseDatabase.getInstance().getReference();
        sh = new SharedPref(context);
        tlFilter = findViewById(R.id.tl_filter);
        rvMyOrder = findViewById(R.id.rv_my_orders);
        swMyOrder = findViewById(R.id.sw_my_orders);
        animationView = findViewById(R.id.animationView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        myOrdersList = new ArrayList<>();
        canceledList = new ArrayList<>();
        pendingList = new ArrayList<>();
        completedList = new ArrayList<>();

        getMyOrders();

        swMyOrder.setOnRefreshListener(this::getMyOrders);
    }

    private void getMyOrders() {
        Objects.requireNonNull(tlFilter.getTabAt(0)).select();
        swMyOrder.setRefreshing(true);

        String userId = sh.getString(Constants.ID);

        Query query = dataRef.child(Constants.ORDERS).orderByChild(Constants.USER_ID).equalTo(userId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                swMyOrder.setRefreshing(false);

                myOrdersList.clear();
                pendingList.clear();
                completedList.clear();
                canceledList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Buy buy = dataSnapshot.getValue(Buy.class);
                    if (buy != null) {

                        myOrdersList.add(buy);

                        if (buy.getStatus().equals(Constants.SHIPPED) || buy.getStatus().equals(Constants.COMPLETED)) {

                            completedList.add(buy);

                        } else if (buy.getStatus().equals(Constants.CANCELED)) {

                            canceledList.add(buy);

                        } else {

                            pendingList.add(buy);

                        }

                    }

                }
                myOrdersList.sort((o1, o2) -> {
                    if (o1.getDate() == null || o2.getDate() == null) {
                        return 0;
                    }
                    String t1 = Functions.getMilliSeconds(o1.getDate());
                    String t2 = Functions.getMilliSeconds(o2.getDate());

                    return t2.compareTo(t1);
                });


                pendingList.sort((o1, o2) -> {
                    if (o1.getDate() == null || o2.getDate() == null) {
                        return 0;
                    }
                    String t1 = Functions.getMilliSeconds(o1.getDate());
                    String t2 = Functions.getMilliSeconds(o2.getDate());

                    return t2.compareTo(t1);
                });


                completedList.sort((o1, o2) -> {
                    if (o1.getDate() == null || o2.getDate() == null) {
                        return 0;
                    }
                    String t1 = Functions.getMilliSeconds(o1.getDate());
                    String t2 = Functions.getMilliSeconds(o2.getDate());

                    return t2.compareTo(t1);
                });

                canceledList.sort((o1, o2) -> {
                    if (o1.getDate() == null || o2.getDate() == null) {
                        return 0;
                    }
                    String t1 = Functions.getMilliSeconds(o1.getDate());
                    String t2 = Functions.getMilliSeconds(o2.getDate());

                    return t2.compareTo(t1);
                });


                showAnimation(pendingList);

                myOrderRecycler = new MyOrderRecycler(context, pendingList);
                rvMyOrder.setAdapter(myOrderRecycler);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                swMyOrder.setRefreshing(false);
                Functions.showSnackBar(context, error.getMessage());

            }
        });

    }


    private void tlFilterListener() {


        tlFilter.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                if (tab.getPosition() == 0) {

                    showAnimation(pendingList);

                    tab.setText(context.getResources().getString(R.string.pending));
                    myOrderRecycler = new MyOrderRecycler(context, pendingList);
                    myOrderRecycler.notifyDataSetChanged();
                    rvMyOrder.setAdapter(myOrderRecycler);


                } else if (tab.getPosition() == 1) {

                    showAnimation(completedList);

                    tab.setText(context.getResources().getString(R.string.completed));
                    myOrderRecycler = new MyOrderRecycler(context, completedList);
                    myOrderRecycler.notifyDataSetChanged();
                    rvMyOrder.setAdapter(myOrderRecycler);


                } else if (tab.getPosition() == 2) {

                    showAnimation(canceledList);

                    tab.setText(context.getResources().getString(R.string.canceled));
                    myOrderRecycler = new MyOrderRecycler(context, canceledList);
                    myOrderRecycler.notifyDataSetChanged();
                    rvMyOrder.setAdapter(myOrderRecycler);


                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                if (tab.getPosition() == 0) {

                    tab.setText("");

                } else if (tab.getPosition() == 1) {

                    tab.setText("");


                } else if (tab.getPosition() == 2) {

                    tab.setText("");


                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    public void back(View view) {

        onBackPressed();
    }

    private void showAnimation(ArrayList<Buy> list) {

        if (list.isEmpty()) {

            animationView.setVisibility(View.VISIBLE);

        } else {

            animationView.setVisibility(View.GONE);

        }
    }
}