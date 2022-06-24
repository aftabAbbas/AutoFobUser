package com.idialogics.autofobuser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idialogics.autofobuser.Activities.Main.CompleteOrderActivity;
import com.idialogics.autofobuser.Model.Notifications;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.Functions;

import java.util.ArrayList;

public class NotificationsRecycler extends RecyclerView.Adapter<NotificationsRecycler.MyOrderVH> {

    Context context;
    ArrayList<Notifications> notificationsArrayList;

    public NotificationsRecycler(Context context, ArrayList<Notifications> notificationsArrayList) {
        this.context = context;
        this.notificationsArrayList = notificationsArrayList;
    }

    @NonNull
    @Override
    public MyOrderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notifications_item, parent, false);

        return new MyOrderVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderVH holder, int position) {

        holder.setData(notificationsArrayList.get(position));

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, CompleteOrderActivity.class);
            intent.putExtra(Constants.PRODUCT_DETAIL_INTENT, notificationsArrayList.get(position));
            context.startActivity(intent);


        });


    }

    @Override
    public int getItemCount() {
        return notificationsArrayList.size();
    }

    public static class MyOrderVH extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDate, tvBody;

        public MyOrderVH(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvBody = itemView.findViewById(R.id.tv_body);

        }

        void setData(Notifications notifications) {

            String title = notifications.getTitle();
            String body = notifications.getBody();

            String finalTime = Functions.timeManager(notifications.getTime());

            tvTitle.setText(title);
            tvBody.setText(body);
            tvDate.setText(finalTime);

        }


    }
}
