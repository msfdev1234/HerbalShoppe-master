package com.madmobiledevs.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmobiledevs.ecommerce.LoadingDialougs.LoadingDialog;
import com.madmobiledevs.ecommerce.Model.Cart;
import com.madmobiledevs.ecommerce.Prevalent.Prevalent;
import com.madmobiledevs.ecommerce.ViewHolder.CartViewHolder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import io.paperdb.Paper;

public class CartActivity extends AppCompatActivity {


    private LoadingDialog loadingDialog;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextProcessBtn;
    private TextView total_Amount_TxtVw_1;

    private int overAllTotalPrice=0;

    private String ptype;

    private List<String> productIds;
    private List<String> productQuantitys;
    private List<String> productNames;
    private List<String> productPrices;
    private List<String> productPtypes;

    private ProgressDialog loadingBar;

    private RelativeLayout bottom_Cart_Layout;
    ImageView empty_Cart_ImgVw;

    private int TotalAmount ;

    private ImageView back_Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Paper.init(this);
        loadingDialog = new LoadingDialog(this);

        productIds = new ArrayList<String>();
        productQuantitys = new ArrayList<String>();
        productNames = new ArrayList<String>();
        productPrices = new ArrayList<String>();
        productPtypes = new ArrayList<String>();

        empty_Cart_ImgVw = findViewById(R.id.empty_Cart_Icon);
        bottom_Cart_Layout = findViewById(R.id.bottom_Cart_Layout);


        recyclerView= (RecyclerView) findViewById(R.id.cart_List);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextProcessBtn=(Button) findViewById(R.id.checkOut_Btn_cart);
        total_Amount_TxtVw_1=(TextView) findViewById(R.id.total_Amount_TxtVw);

        loadingBar=new ProgressDialog(this);

        back_Image = findViewById(R.id.back_Btn_ImgView_Cart);

        back_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        NextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this,AddressActivity.class);
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        Paper.init(this);
        if (!Paper.book().contains(Prevalent.UserPhoneKey)) {
            Paper.book().write(Prevalent.UserPhoneKey, "");
        }

        checkAvailabilityCartItems();

    }

    private void checkAvailabilityCartItems() {

        if (!loadingDialog.isShowing()){
            loadingDialog.startLoadingDialog();
        }

        DatabaseReference CartRef_ForCheck = FirebaseDatabase.getInstance().getReference().child("Cart");
        CartRef_ForCheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(Paper.book().read(Prevalent.UserPhoneKey).toString())) {

                    empty_Cart_ImgVw.setVisibility(View.INVISIBLE);
                    bottom_Cart_Layout.setVisibility(View.VISIBLE);

                    update_Cart_Total();
                    showCartItems();


                } else {
                    empty_Cart_ImgVw.setVisibility(View.VISIBLE);
                    bottom_Cart_Layout.setVisibility(View.INVISIBLE);
                    loadingDialog.dismissDialog();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showCartItems(){

        final String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);

        final DatabaseReference cartListRef= FirebaseDatabase.getInstance().getReference().child("Cart").child(UserPhoneKey);
        FirebaseRecyclerOptions<Cart> options;

        options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef, Cart.class)
                .build();


        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter= new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull final Cart cart) {

                ptype = cart.getPtype();

                cartViewHolder.txtProductName.setText(cart.getPname());

                cartViewHolder.txtProductPrice.setText(cart.getPrice());

                cartViewHolder.txtProductQuantity.setText(cart.getUnit_quantity());

                cartViewHolder.txtProductQuantityUnits.setText(cart.getUnits());


                if ( Pattern.compile(Pattern.quote("ras"), Pattern.CASE_INSENSITIVE).matcher(cart.getPname()).find()){
                    cartViewHolder.product_Image.setImageResource(R.drawable.liquid_image);

                } else {
                    cartViewHolder.product_Image.setImageResource(R.drawable.raw_image);
                }

                loadingDialog.dismissDialog();

                cartViewHolder.delete_button_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        remove_From_Cart(cart.getPid(), UserPhoneKey);
                    }
                });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void update_Cart_Total(){
        DatabaseReference cartTotal_Ref = FirebaseDatabase.getInstance().getReference().child("Cart").child(Paper.book().read(Prevalent.UserPhoneKey).toString());
        TotalAmount = 0;
        cartTotal_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot ) {

                int TotalAmount_Loop = 0;

                for (DataSnapshot child : dataSnapshot.getChildren()){
                    int item_Amount = Integer.parseInt(child.child("price").getValue().toString());
                    TotalAmount_Loop += item_Amount;
                }

                setTotalAmount_Text(TotalAmount_Loop);


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setTotalAmount_Text(int totalAmount_Loop) {

        DecimalFormat formatter = new DecimalFormat("#,###,###");
        String formatted_Total = formatter.format(totalAmount_Loop);

        if (totalAmount_Loop == 0){
            total_Amount_TxtVw_1.setText(Integer.toString(totalAmount_Loop));
            lastCheck();
        } else {
            total_Amount_TxtVw_1.setText(formatted_Total);
        }
    }

    private void lastCheck(){

        loadingDialog.startLoadingDialog();

        DatabaseReference CartRef_ForCheck = FirebaseDatabase.getInstance().getReference().child("Cart");
        CartRef_ForCheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(Paper.book().read(Prevalent.UserPhoneKey).toString())) {

                    empty_Cart_ImgVw.setVisibility(View.VISIBLE);
                    bottom_Cart_Layout.setVisibility(View.INVISIBLE);
                    loadingDialog.dismissDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void remove_From_Cart(String pid, String UserPhoneKey) {


        DatabaseReference elegantNum_Ref = FirebaseDatabase.getInstance().getReference().child("Cart").child(UserPhoneKey).child(pid);

        elegantNum_Ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                update_Cart_Total();

                Toast.makeText(getApplicationContext(), "Item removed from cart", Toast.LENGTH_SHORT).show();

            }
        });

    }

}
