package com.madmobiledevs.ecommerce.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.madmobiledevs.ecommerce.Interface.ItemClickListener;
import com.madmobiledevs.ecommerce.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductDescription, txtProductPrice, txtMrpPrice;

    public TextInputLayout Quantity_field;
    public TextInputEditText quantity_input;

    public ImageView imageView;
    public ItemClickListener listener;
    public Spinner spinner;

    public Button add_Button;


    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView= (ImageView) itemView.findViewById(R.id.product_Image);
        txtProductName= (TextView) itemView.findViewById(R.id.product_Name);
        txtProductDescription= (TextView) itemView.findViewById(R.id.product_Description);
        txtProductPrice= (TextView) itemView.findViewById(R.id.product_Rate_TxtVw);
        txtMrpPrice=(TextView) itemView.findViewById(R.id.product_Mrp_TxtVw);
        spinner = (Spinner) itemView.findViewById(R.id.units_spinner);
        Quantity_field = (TextInputLayout) itemView.findViewById(R.id.product_Display_Quantity_EdtTxt_Layout);
        quantity_input = (TextInputEditText) itemView.findViewById(R.id.product_Display_Quantity_EdtTxt);
        add_Button = (Button) itemView.findViewById(R.id.Add_To_Cart_Btn);

    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;

    }

    @Override
    public void onClick(View v) {
        listener.onClick(v,getAdapterPosition(),false);
    }
}
