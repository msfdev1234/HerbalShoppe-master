package com.madmobiledevs.ecommerce;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.madmobiledevs.ecommerce.LoadingDialougs.LoadingDialog;
import com.madmobiledevs.ecommerce.Model.orders;
import com.madmobiledevs.ecommerce.Prevalent.Prevalent;
import com.madmobiledevs.ecommerce.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class tab1ActiveOrders extends Fragment {

    private RecyclerView active_Orders_RecyclerView;
    RecyclerView.LayoutManager layoutManager;

    private DatabaseReference order_Ref;

    private int item_Count;
    private ImageView no_Orders_ImgVw;
    private LoadingDialog loadingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.tab1_fragment_actve_orders, container, false);
        loadingDialog = new LoadingDialog(getActivity());

        order_Ref = FirebaseDatabase.getInstance().getReference().child("orders1").child(Paper.book().read(Prevalent.UserPhoneKey).toString());

        active_Orders_RecyclerView = view.findViewById(R.id.recyclerView_Active_Orders);
        no_Orders_ImgVw = view.findViewById(R.id.no_Orders_Icon_Active);

        layoutManager = new LinearLayoutManager(getContext());
        active_Orders_RecyclerView.setLayoutManager(layoutManager);

        return view;
    }

    private void checkAvailabilityItems() {

        loadingDialog.startLoadingDialog();

        DatabaseReference CartRef_ForCheck = FirebaseDatabase.getInstance().getReference().child("orders1").child(Paper.book().read(Prevalent.UserPhoneKey).toString());
        CartRef_ForCheck.orderByChild("status_Order").equalTo("Active").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {

                    loadingDialog.dismissDialog();
                    showAllOrders();

                } else {

                    loadingDialog.dismissDialog();
                    no_Orders_ImgVw.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {

        super.onStart();
        checkAvailabilityItems();

    }



    private void showAllOrders() {


        FirebaseRecyclerOptions<orders> options =
                new FirebaseRecyclerOptions.Builder<orders>()
                        .setQuery(order_Ref.orderByChild("status_Order").equalTo("Active"), orders.class)
                        .build();

        FirebaseRecyclerAdapter<orders, OrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<orders, OrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final OrderViewHolder orderViewHolder, int i, @NonNull final orders orders) {


                        DatabaseReference items_Count_Ref = FirebaseDatabase.getInstance().getReference().child("orderProducts").child(orders.getOrderId());



                        items_Count_Ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                item_Count = (int) dataSnapshot.getChildrenCount();

                                orderViewHolder.items_TxtVw.setText(Integer.toString(item_Count));

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        if (orders.getPaymentStatus().equals("null")){
                            orderViewHolder.payment_Status_TxtVw.setText("Cash On Delivery");
                        } else{
                            orderViewHolder.payment_Status_TxtVw.setText("Payment Done");
                        }

                        orderViewHolder.order_Id_TxtVw.setText("HS-"+orders.getOrderId());
                        orderViewHolder.amount_TxtVw.setText("Rs "+orders.getGrandTotal());
                        orderViewHolder.day_Date_TxtVw.setText(orders.getFullDay());

                        if (orders.getUser_status_Order().equals("waiting")){

                            orderViewHolder.dot_Vw.setBackgroundResource(R.drawable.shape_waiting);
                            orderViewHolder.order_Status_Dot_TxtVw.setTextColor(Color.parseColor("#e7b416"));
                            orderViewHolder.order_Status_Dot_TxtVw.setText("Waiting to confirm");

                        } else if (orders.getUser_status_Order().equals("Processing")){

                            orderViewHolder.dot_Vw.setBackgroundResource(R.drawable.shape_processing);
                            orderViewHolder.order_Status_Dot_TxtVw.setTextColor(Color.parseColor("#689f39"));
                            orderViewHolder.order_Status_Dot_TxtVw.setText("Order processing");

                        }

                        orderViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent=new Intent(getActivity(),Order_ProductsActivity.class);
                                intent.putExtra("order_Id", orders.getOrderId());
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_order_layout, parent, false);

                        OrderViewHolder orderViewHolder = new OrderViewHolder(view);
                        return orderViewHolder;
                    }
                };
        active_Orders_RecyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    private int getItemsCount(String orderId) {
        return item_Count;
    }
}
