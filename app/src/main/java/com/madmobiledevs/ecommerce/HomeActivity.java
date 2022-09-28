package com.madmobiledevs.ecommerce;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Parcelable;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmobiledevs.ecommerce.LoadingDialougs.LoadingDialog;
import com.madmobiledevs.ecommerce.Model.Products;
import com.madmobiledevs.ecommerce.Model.Users;
import com.madmobiledevs.ecommerce.Prevalent.Prevalent;
import com.madmobiledevs.ecommerce.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    String UserPhoneKey;
    LoadingDialog loadingDialog;

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Paper.init(this);
        if (!Paper.book().contains(Prevalent.UserPhoneKey)) {
            Paper.book().write(Prevalent.UserPhoneKey, "");
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_Home_Layout,new Home_Panel_Fragment()).commit();

        loadingDialog = new LoadingDialog(this);

        UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);

        bottomNavigationView = findViewById(R.id.Bottom_NavigationView);

        Paper.init(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;
                Activity selectedActivity = null;

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        selectedFragment = new Home_Panel_Fragment();
                        break;
                    case R.id.nav_Basket:
                        if(!(Paper.book().read(Prevalent.UserPhoneKey).equals(""))){
                            selectedActivity = new CartActivity();
                        }
                        else {

                            if (Paper.book().read(Prevalent.UserPhoneKey)!=""){
                                selectedActivity = new Profile_Activity();
                            } else {
                                selectedActivity = new MainActivity();
                            }
                        }
                        break;
                    case R.id.nav_Profile:
                        if (Paper.book().read(Prevalent.UserPhoneKey)!=""){
                            selectedActivity = new Profile_Activity();
                        } else {
                            selectedActivity = new MainActivity();
                        }
                        break;
                }

                if (selectedFragment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_Home_Layout,selectedFragment).commit();
                } else if (selectedActivity!=null){
                    Intent intent = new Intent(HomeActivity.this, selectedActivity.getClass());
                    startActivity(intent);
                }
                return true;
            }
        });


    }
}
