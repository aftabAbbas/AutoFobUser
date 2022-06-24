package com.idialogics.autofobuser.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.idialogics.autofobuser.Adapter.NotificationsRecycler;
import com.idialogics.autofobuser.Model.Notifications;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.Functions;
import com.idialogics.autofobuser.Utils.SharedPref;

import java.util.ArrayList;


public class NotificationsFragment extends Fragment {

    Context context;
    DatabaseReference dataRef;
    SharedPref sh;
    RecyclerView rvNotifications;
    SwipeRefreshLayout swNotifications;
    ArrayList<Notifications> notificationsArrayList;
    NotificationsRecycler notificationsRecycler;
    LottieAnimationView animationView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        initUI(view);

        return view;
    }

    private void initUI(View view) {
        context = view.getContext();
        dataRef = FirebaseDatabase.getInstance().getReference();
        sh = new SharedPref(context);
        rvNotifications = view.findViewById(R.id.rv_notifications);
        swNotifications = view.findViewById(R.id.sw_notifications);
        animationView = view.findViewById(R.id.animationView);

        notificationsArrayList = new ArrayList<>();


        getNotifications();

        swNotifications.setOnRefreshListener(this::getNotifications);
    }

    private void getNotifications() {

        swNotifications.setRefreshing(true);


        dataRef.child(Constants.KEY_COLLECTION_USERS)
                .child(sh.getString(Constants.ID)).child(Constants.NOTIFICATION)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        notificationsArrayList.clear();
                        swNotifications.setRefreshing(false);

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            Notifications notifications = dataSnapshot.getValue(Notifications.class);
                            if (notifications != null) {

                                notificationsArrayList.add(notifications);

                            }

                        }
                        if (notificationsArrayList.size() > 0) {

                            animationView.setVisibility(View.GONE);
                            notificationsArrayList.sort((o1, o2) -> {
                                if (o2.getTime() == null || o1.getTime() == null)
                                    return 0;
                                return o1.getTime().compareTo(o2.getTime());
                            });


                            notificationsRecycler = new NotificationsRecycler(context, notificationsArrayList);
                            rvNotifications.setAdapter(notificationsRecycler);

                        } else {

                            animationView.setVisibility(View.VISIBLE);


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        swNotifications.setRefreshing(false);
                        Functions.showSnackBar(context, error.getMessage());

                    }
                });

    }
}
