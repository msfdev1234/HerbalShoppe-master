package com.madmobiledevs.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmobiledevs.ecommerce.Model.Users;
import com.madmobiledevs.ecommerce.Prevalent.Prevalent;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button signUpNowButton,loginButton;
    private ProgressDialog loadingBar;

    private Fragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container_Profile,new LoginFragment()).commit();

        signUpNowButton = (Button) findViewById(R.id.chs_SignUp_Btn);
        loginButton = (Button) findViewById(R.id.chs_login_Btn);

        loadingBar = new ProgressDialog(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpNowButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                loginButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                signUpNowButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                loginButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container_Profile,new LoginFragment()).commit();

            }
        });
        signUpNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                signUpNowButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                loginButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                signUpNowButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_Container_Profile,new SignUpFragment()).commit();
            }
        });

        Paper.init(this);

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

//        if (UserPhoneKey != "" && UserPasswordKey != "") {
//            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)) {
//                loadingBar.setTitle("Already Logged In");
//                loadingBar.setMessage("Please wait....");
//                loadingBar.setCanceledOnTouchOutside(false);
//                loadingBar.show();
//
//                AllowAccess(UserPhoneKey, UserPasswordKey);
//            }
//        }
    }

    private void AllowAccess(final String phoneNumber, final String password) {

        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(phoneNumber).exists()){
                    Users userData=dataSnapshot.child("Users").child(phoneNumber).getValue(Users.class);


                    assert userData != null;
                    if (userData.getPhoneNumber().equals(phoneNumber)){
                        if (userData.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this, "Login successfull", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                            intent.putExtra("ischkRememberme",1);
                            finish();
                            startActivity(intent);

                        }
                        else {
                            Toast.makeText(MainActivity.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }

                }else {
                    Toast.makeText(MainActivity.this, "Account with this "+phoneNumber+" do not exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
