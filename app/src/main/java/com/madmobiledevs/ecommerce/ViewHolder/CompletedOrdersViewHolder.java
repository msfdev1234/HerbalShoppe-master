package com.madmobiledevs.ecommerce.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.madmobiledevs.ecommerce.Interface.ItemClickListener;
import com.madmobiledevs.ecommerce.R;

public class CompletedOrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView order_Id_TxtVw, amount_TxtVw , items_TxtVw, order_Status_TxtVw, day_Date_TxtVw;
    public View dot_Vw;

    private ItemClickListener listener;


    public CompletedOrdersViewHolder(@NonNull View itemView) {
        super(itemView);
        day_Date_TxtVw = itemView.findViewById(R.id.date_Day_TxtVw_Compltd);
//        timeSlot_TxtVw = itemView.findViewById(R.id.time_Slot_Compltd_Orders);
        order_Id_TxtVw = itemView.findViewById(R.id.order_Id_Compltd_Orders);
        amount_TxtVw = itemView.findViewById(R.id.amount_Compltd_Orders);
        items_TxtVw = itemView.findViewById(R.id.items_Total_Compltd_Orders);
        order_Status_TxtVw = itemView.findViewById(R.id.order_Status_Compltd_Orders);

    }

    @Override
    public void onClick(View view) {
        listener.onClick(view,getAdapterPosition(),false);
    }


    public void setItemClickListener(com.madmobiledevs.ecommerce.Interface.ItemClickListener listener){
        this.listener = listener;
    }


}
