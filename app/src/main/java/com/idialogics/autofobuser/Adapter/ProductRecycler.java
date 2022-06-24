package com.idialogics.autofobuser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.idialogics.autofobuser.Activities.Main.ProductDetailActivity;
import com.idialogics.autofobuser.Model.Product;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.Functions;

import java.util.ArrayList;

public class ProductRecycler extends RecyclerView.Adapter<ProductRecycler.ProductVH> {

    Context context;
    ArrayList<Product> productArrayList;
    String manufacturer, model, year;

    public ProductRecycler(Context context, ArrayList<Product> productArrayList, String manufacturer, String model, String year) {
        this.context = context;
        this.productArrayList = productArrayList;
        this.manufacturer = manufacturer;
        this.model = model;
        this.year = year;
    }

    @NonNull
    @Override
    public ProductVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);

        return new ProductVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductVH holder, int position) {


        holder.setData(context, productArrayList.get(position));

        Functions.pushDownItem(holder.itemView);

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra(Constants.PRODUCT_DETAIL_INTENT, productArrayList.get(position));
            intent.putExtra(Constants.MANUFECTURER, manufacturer);
            intent.putExtra(Constants.MODEL, model);
            intent.putExtra(Constants.YEAR, year);
            context.startActivity(intent);


        });


    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public static class ProductVH extends RecyclerView.ViewHolder {

        TextView tvProductName, tvPrice;
        ImageView ivProductImage;
        LottieAnimationView animationView;

        public ProductVH(@NonNull View itemView) {
            super(itemView);

            tvProductName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            ivProductImage = itemView.findViewById(R.id.iv_product);
            animationView = itemView.findViewById(R.id.animationView);

        }

        void setData(Context context, Product product) {

            String name = product.getName().trim();
            String price = "$ " + product.getPrice().trim();
            String image = product.getImage().trim();


            tvPrice.setText(price);
            tvProductName.setText(name);
            tvProductName.setSelected(true);


            Glide.with(context).load(image).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                    animationView.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                    animationView.setVisibility(View.GONE);
                    return false;
                }
            }).into(ivProductImage);

        }


    }
}
