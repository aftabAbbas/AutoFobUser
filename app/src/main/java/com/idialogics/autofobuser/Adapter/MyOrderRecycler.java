package com.idialogics.autofobuser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.idialogics.autofobuser.Activities.Main.OrderViewActivity;
import com.idialogics.autofobuser.Model.Buy;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.Functions;

import java.util.ArrayList;

public class MyOrderRecycler extends RecyclerView.Adapter<MyOrderRecycler.MyOrderVH> {

    Context context;
    ArrayList<Buy> myOrderList;

    public MyOrderRecycler(Context context, ArrayList<Buy> myOrderList) {
        this.context = context;
        this.myOrderList = myOrderList;
    }

    @NonNull
    @Override
    public MyOrderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_order_item, parent, false);

        return new MyOrderVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderVH holder, int position) {

        holder.setData(context, myOrderList.get(position));

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, OrderViewActivity.class);
            intent.putExtra(Constants.PRODUCT_DETAIL_INTENT, myOrderList.get(position));
            context.startActivity(intent);


        });


    }

    @Override
    public int getItemCount() {
        return myOrderList.size();
    }

    public static class MyOrderVH extends RecyclerView.ViewHolder {

        TextView tvProductName, tvPrice, tvDate, tvProductDetail, tvStatus, tvCount;
        ImageView ivProductImage;

        public MyOrderVH(@NonNull View itemView) {
            super(itemView);

            tvProductName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            ivProductImage = itemView.findViewById(R.id.iv_product);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvProductDetail = itemView.findViewById(R.id.tv_product_detail);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvCount = itemView.findViewById(R.id.tv_count);

        }

        void setData(Context context, Buy buy) {

            String name = buy.getProductName();
            String status = buy.getStatus();
            String date = Functions.timeManager(buy.getDate());
            String count = buy.getCount();
            String productDetail = buy.getManufacturer() + " " + buy.getModel() + " " + buy.getYear();

            Glide.with(context).load(buy.getProductImage()).into(ivProductImage);


            tvProductName.setText(name);
            tvProductDetail.setText(productDetail);
            tvProductName.setSelected(true);
            tvDate.setText(date);
            tvCount.setText(count);

            switch (status) {
                case Constants.CANCELED:

                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
                    break;

                case Constants.PENDING:

                    tvStatus.setTextColor(ContextCompat.getColor(context,R.color.school_bus_yellow));
                    break;

                case Constants.SHIPPED:

                    tvStatus.setTextColor(ContextCompat.getColor(context,R.color.blue));
                    break;

                case Constants.COMPLETED:

                    tvStatus.setTextColor(ContextCompat.getColor(context,R.color.green));
                    break;

            }


            tvStatus.setText(status);

        }


    }
}
