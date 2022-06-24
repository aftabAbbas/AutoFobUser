package com.idialogics.autofobuser.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.idialogics.autofobuser.Activities.Main.ProductBuyActivity;
import com.idialogics.autofobuser.Model.Cart;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.Functions;

import java.util.ArrayList;

import static com.idialogics.autofobuser.Fragments.CartFragment.selectedList;
import static com.idialogics.autofobuser.Fragments.CartFragment.snackbar;
import static com.idialogics.autofobuser.MainActivity.tvSelectionCount;

public class CartRecycler extends RecyclerView.Adapter<CartRecycler.CartVH> {

    public static ArrayList<Cart> productArrayList, selectedProductList;
    Context context;
    boolean check;
    double price = 0.0;

    public CartRecycler(Context context, ArrayList<Cart> productArrayList, boolean check) {
        this.context = context;
        CartRecycler.productArrayList = productArrayList;
        selectedProductList = new ArrayList<>();
        selectedProductList.addAll(productArrayList);
        selectedList.addAll(productArrayList);
        this.check = check;
    }

    @NonNull
    @Override
    public CartVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);

        return new CartVH(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CartVH holder, int position) {

        holder.setData(context, productArrayList.get(position));

        Functions.pushDownItem(holder.itemView);

        if (holder.cbCheck.getVisibility() == View.GONE) {

            holder.itemView.setOnClickListener(v -> {

                if (!check) {

                    Intent intent = new Intent(context, ProductBuyActivity.class);
                    intent.putExtra(Constants.PRODUCT_DETAIL_INTENT, productArrayList.get(position));
                    context.startActivity(intent);
                } else {

                    holder.cbCheck.performClick();

                }

            });
        }

        if (check) {

            holder.cbCheck.setVisibility(View.VISIBLE);

        } else {

            holder.cbCheck.setVisibility(View.GONE);

        }

       /* price = 0;
        for (Cart cart : selectedProductList) {

            int price2 = Integer.parseInt(cart.getPrice());
            price += price2;

        }*/

        /*price = selectedProductList.stream()
                .mapToInt(a -> Integer.parseInt(a.getPrice()) * Integer.parseInt(a.getCount()))
                .sum();*/


        price = selectedProductList.stream()
                .mapToDouble(value -> {
                    double p = Double.parseDouble(value.getPrice());
                    double c = Double.parseDouble(value.getCount());

                    return p * c;
                }).sum();


        snackbar.setText(selectedProductList.size() + " Item $" + price);

        tvSelectionCount.setText(selectedProductList.size() + "");

        holder.cbCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            price = 0;


            if (isChecked) {

                selectedProductList.add(productArrayList.get(position));


            } else {

                selectedProductList.remove(productArrayList.get(position));
                // selectedList.remove(productArrayList.get(position));


            }

            price = selectedProductList.stream()
                    .mapToDouble(value -> {
                        double p = Double.parseDouble(value.getPrice());
                        double c = Double.parseDouble(value.getCount());

                        return p * c;
                    }).sum();

            snackbar.setText(selectedProductList.size() + " Item $" + price);


            tvSelectionCount.setText(selectedProductList.size() + "");

        });


    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public static class CartVH extends RecyclerView.ViewHolder {

        TextView tvProductName, tvPrice, tvDate, tvProductDetail, tvCount;
        ImageView ivProductImage;
        CheckBox cbCheck;

        public CartVH(@NonNull View itemView) {
            super(itemView);

            tvProductName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            ivProductImage = itemView.findViewById(R.id.iv_product);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvProductDetail = itemView.findViewById(R.id.tv_product_detail);
            tvCount = itemView.findViewById(R.id.tv_count);
            cbCheck = itemView.findViewById(R.id.iv_selector);

        }

        void setData(Context context, Cart cart) {

            String name = cart.getProductName();
            String productDetail = cart.getManufacturer() + " " + cart.getModel() + " " + cart.getYear();

            String finalTime = Functions.timeManager(cart.getDate());

            int count = Integer.parseInt(cart.getCount());
            float price = Float.parseFloat(cart.getPrice());

            float finalPrice = count * price;

            String priceText = "$ " + finalPrice;


            Glide.with(context).load(cart.getProductImage()).into(ivProductImage);

            tvPrice.setText(priceText);
            tvProductName.setText(name);
            String countText = "x " + count;
            tvCount.setText(countText);
            tvDate.setText(finalTime);
            tvProductDetail.setText(productDetail);
        }


    }
}
