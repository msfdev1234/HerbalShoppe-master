package com.madmobiledevs.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmobiledevs.ecommerce.LoadingDialougs.LoadingDialog;
import com.madmobiledevs.ecommerce.Prevalent.Prevalent;

import io.paperdb.Paper;

public class Profile_Activity extends AppCompatActivity {

    LoadingDialog loadingDialog;

    TextView name_TxtVw, phone_TxtVw;
    String userPhoneKey;


    String name;

    RelativeLayout logout_Btn, orders_Btn, contactUs_Btn;

    ImageView back_Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Paper.init(this);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();



        name_TxtVw = findViewById(R.id.name_Profile);
        phone_TxtVw = findViewById(R.id.phoneNumber_Profile);
        logout_Btn = findViewById(R.id.logOut_Btn);

        contactUs_Btn = findViewById(R.id.contactUs_Btn);

        orders_Btn = findViewById(R.id.orders_Btn);

        back_Image = findViewById(R.id.back_Btn_ImgView_Profile);

        userPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);

        if (userPhoneKey!=""){
            geAndSetName();
        }

        back_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        contactUs_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Profile_Activity.this,supportActivity.class);
                startActivity(intent);
            }
        });

        logout_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Paper.book().destroy();
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(Profile_Activity.this,HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        orders_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Profile_Activity.this,OrdersActivity.class);
                startActivity(intent);
            }
        });
    }

    private void geAndSetName() {
        DatabaseReference NameRef;

        NameRef = FirebaseDatabase.getInstance().getReference().child("Users1").child(Paper.book().read(Prevalent.UserPhoneKey).toString()).child("name");

        NameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userNm = dataSnapshot.getValue().toString();
                name_TxtVw.setText(userNm);
                phone_TxtVw.setText("+"+Paper.book().read(Prevalent.UserPhoneKey).toString());
                loadingDialog.dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}