package com.madmobiledevs.ecommerce.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.madmobiledevs.ecommerce.Interface.ItemClickListener;
import com.madmobiledevs.ecommerce.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductQuantity, txtProductQuantityUnits, txtProductPrice;
    private ItemClickListener itemClickListener;

    public ImageView product_Image, delete_button_cart;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        product_Image = itemView.findViewById(R.id.product_Image_Cart);
        delete_button_cart = itemView.findViewById(R.id.delete_button_cart);

        txtProductName= itemView.findViewById(R.id.cart_product_Name);
        txtProductPrice= itemView.findViewById(R.id.product_Rate_TxtVw_Cart);
        txtProductQuantity= itemView.findViewById(R.id.cart_product_quantity);
        txtProductQuantityUnits = itemView.findViewById(R.id.product_Quantity_Units_TxtVw_Cart);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
