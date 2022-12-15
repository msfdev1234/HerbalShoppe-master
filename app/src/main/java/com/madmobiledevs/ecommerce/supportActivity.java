package com.madmobiledevs.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmobiledevs.ecommerce.LoadingDialougs.LoadingDialog;

public class supportActivity extends AppCompatActivity {
    LoadingDialog loadingDialog;

    RelativeLayout call_Btn, whatsApp_Btn, email_Btn;

    private static final int REQUEST_CALL = 1;

    String support_Number;
    String support_Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        call_Btn = findViewById(R.id.call_Btn_ContctUs);
        whatsApp_Btn = findViewById(R.id.whatsApp_Btn_ContctUs);
        email_Btn = findViewById(R.id.email_Btn_ContctUs);

        getSupportDetails();

        call_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phn_Number = support_Number;
                makePhoneCall(phn_Number);
            }
        });

        whatsApp_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://api.whatsapp.com/send?phone="+"+91"+support_Number;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        email_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + support_Email));
                intent.putExtra(Intent.EXTRA_SUBJECT, "your_subject");
                intent.putExtra(Intent.EXTRA_TEXT, "your_text");
                startActivity(intent);
            }
        });


    }

    private void makePhoneCall(String number) {

        if (ContextCompat.checkSelfPermission(supportActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(supportActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }

    }

    private void getSupportDetails(){

        DatabaseReference numRef = FirebaseDatabase.getInstance().getReference().child("Support");

        numRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (int i=0; i<1; i++){
                    support_Number = dataSnapshot.child("phone").getValue().toString();
                    support_Email =  dataSnapshot.child("email").getValue().toString();
                }

                loadingDialog.dismissDialog();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}