package com.madmobiledevs.ecommerce;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.madmobiledevs.ecommerce.LoadingDialougs.LoadingDialog;
import com.madmobiledevs.ecommerce.Model.Address;
import com.madmobiledevs.ecommerce.Prevalent.Prevalent;
import com.madmobiledevs.ecommerce.ViewHolder.Adresses_ViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class AddressActivity extends AppCompatActivity {

    TextView addAddress_Btn;
    RecyclerView recyclerVwAddresses;
    RecyclerView.LayoutManager layoutManager;

    private String name;
    private ImageView empty_Image, back_Image;

    private LoadingDialog loadingDialog;
    private  FirebaseRecyclerAdapter<Address, Adresses_ViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        Paper.init(this);


        empty_Image = findViewById(R.id.empty_Addresses_Icon);
        recyclerVwAddresses = findViewById(R.id.recyclerView_Addresses);
        layoutManager = new LinearLayoutManager(this);
        recyclerVwAddresses.setLayoutManager(layoutManager);

        loadingDialog = new LoadingDialog(AddressActivity.this);

        addAddress_Btn = findViewById(R.id.Add_Address_Btn);

        back_Image = findViewById(R.id.back_Btn_ImgView_Address);

        back_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        addAddress_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddressActivity.this,AddNewAddressActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        checkAvailability();
    }

    private void getName() {

        DatabaseReference Name_Ref = FirebaseDatabase.getInstance().getReference().child("Users1")
                .child(Paper.book().read(Prevalent.UserPhoneKey).toString())
                .child("name");

        Name_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for (int i = 1; i<2 ; i++){
                   name = dataSnapshot.getValue().toString();
               }

               loadingDialog.dismissDialog();
               showAllAddresses();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void checkAvailability(){
        loadingDialog.startLoadingDialog();
        DatabaseReference addresses_Ref = FirebaseDatabase.getInstance().getReference().child("Addresses")
                .child(Paper.book().read(Prevalent.UserPhoneKey).toString());

        addresses_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               int size = (int) dataSnapshot.getChildrenCount();
               if (size < 1){
                   loadingDialog.dismissDialog();
                   empty_Image.setVisibility(View.VISIBLE);
               } else {
                   empty_Image.setVisibility(View.INVISIBLE);
                   getName();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void showAllAddresses() {

        DatabaseReference addresses_Ref = FirebaseDatabase.getInstance().getReference().child("Addresses")
                .child(Paper.book().read(Prevalent.UserPhoneKey).toString());



        FirebaseRecyclerOptions<Address> options =
                new FirebaseRecyclerOptions.Builder<Address>()
                        .setQuery(addresses_Ref , Address.class)
                        .build();

         adapter = new FirebaseRecyclerAdapter<Address, Adresses_ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final Adresses_ViewHolder adresses_viewHolder, int i, @NonNull final Address address) {

                        adresses_viewHolder.name_TxtVw.setText(name);
                        adresses_viewHolder.hNo_TxtVw.setText(address.getHno());
                        adresses_viewHolder.type_address_Txtw.setText(address.getType());
                        if (!address.getFlatNo().toString().equals(" ")){
                            adresses_viewHolder.flatNo_TxtVw.setVisibility(View.VISIBLE);
                            adresses_viewHolder.flatNo_TxtVw.setText("Flat no : "+address.getFlatNo());
                        }
                        adresses_viewHolder.address_TxtVw.setText(address.getAddress());
                        adresses_viewHolder.phone_TxtVw.setText("ph : "+Paper.book().read(Prevalent.UserPhoneKey).toString());

                        adresses_viewHolder.view_Whole.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(AddressActivity.this, PaymentAndOrderPlaceActivity.class);
                                // TODO Add extras or a data URI to this intent as appropriate.

                                intent.putExtra("Type", address.getType());
                                intent.putExtra("Lat", address.getLat());
                                intent.putExtra("Longt", address.getLongt());
                                intent.putExtra("Address", address.getAddress());
                                intent.putExtra("Hno", address.getHno());
                                intent.putExtra("FlatNo", address.getFlatNo());
                                intent.putExtra("Apartment", address.getApartment());

                                startActivity(intent);

                           //     Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();

//                                setResult(Activity.RESULT_OK, intent);
//                                finish();

                            }
                        });
                        adresses_viewHolder.delete_Btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(AddressActivity.this);

                                builder.setTitle("Confirm");
                                builder.setMessage("Are you sure?");

                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        deleteAddress(address.getType());
                                    }
                                });

                                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public Adresses_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_addresses, parent, false);

                        Adresses_ViewHolder adresses_viewHolder = new Adresses_ViewHolder(view);

                        return adresses_viewHolder;
                    }
                };

        recyclerVwAddresses.setAdapter(adapter);
        adapter.startListening();


    }

    private void deleteAddress(String type) {
        loadingDialog.startLoadingDialog();
        DatabaseReference addresses_Ref = FirebaseDatabase.getInstance().getReference().child("Addresses")
                .child(Paper.book().read(Prevalent.UserPhoneKey).toString());

        addresses_Ref.child(type).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingDialog.dismissDialog();
                checkAvailability();
            }
        });


    }

}