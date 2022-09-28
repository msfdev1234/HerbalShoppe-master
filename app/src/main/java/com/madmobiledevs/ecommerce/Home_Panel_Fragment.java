package com.madmobiledevs.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madmobiledevs.ecommerce.LoadingDialougs.LoadingDialog;
import com.madmobiledevs.ecommerce.Model.Products;
import com.madmobiledevs.ecommerce.Prevalent.Prevalent;
import com.madmobiledevs.ecommerce.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

import io.paperdb.Paper;

public class Home_Panel_Fragment extends Fragment {

    private String ptype;

    private DatabaseReference ProductRef;

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    LoadingDialog loadingDialog;

    EditText searchViewEdtTxt;
    ImageView P_Search_Bttn;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Menu menu;

    Toolbar toolBar;

    ActionBarDrawerToggle toggle;
    private ProgressDialog loadingBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_panel_fragment,container,false);

        loadingDialog = new LoadingDialog(getActivity());
        loadingBar=new ProgressDialog(getActivity());

        drawerLayout=v.findViewById(R.id.drawerLayout);
        navigationView=v.findViewById(R.id.nav_view);
        searchViewEdtTxt = (EditText) v.findViewById(R.id.searchBox_Main);
        recyclerView = v.findViewById(R.id.recycler_menu);

        toolBar = v.findViewById(R.id.Toolbar_Main);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolBar);

        toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolBar, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();



        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products");
        Paper.init(getActivity());
        if (!Paper.book().contains(Prevalent.UserPhoneKey)) {
            Paper.book().write(Prevalent.UserPhoneKey, "");
        }

        

        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.nav_home:
                        Toast.makeText(getActivity(), "a", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_Profile:
                        Toast.makeText(getActivity(), "b", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }

                return true;
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        showAllProducts();



        searchViewEdtTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!searchViewEdtTxt.getText().toString().equals("")) {
                    showFilteredProducts(editable.toString());
                }
                else {
                    showAllProducts();
                }
            }
        });
    }

    private void showFilteredProducts(String typedString){

        FirebaseRecyclerOptions<Products> options=
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductRef.orderByChild("pname")
                                .startAt(typedString.substring(0, 1).toUpperCase() + typedString.substring(1))
                                .endAt(typedString.substring(0, 1).toUpperCase() + typedString.substring(1)+"\uf8ff"),Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter=
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ProductViewHolder productViewHolder, int i, @NonNull final Products products) {

                        ptype=products.getPtype();

                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.units,android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        int mrp = (int)(Integer.parseInt(products.getPrice())*(50.0f/100.0f));
                        mrp = mrp+Integer.parseInt(products.getPrice());
                        productViewHolder.txtMrpPrice.setPaintFlags( productViewHolder.txtMrpPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        productViewHolder.txtMrpPrice.setText(Integer.toString(mrp));

                        productViewHolder.spinner.setAdapter(adapter);

                        if (ptype.equals("raw")){
                            productViewHolder.txtProductPrice.setText(products.getPrice()+" / Gram");
                        } else if (ptype.equals("pack")){
                            productViewHolder.txtProductPrice.setText("Price "+products.getPrice()+"₹");
                        }

                        productViewHolder.txtProductName.setText(products.getPname());
                        productViewHolder.txtProductDescription.setText(products.getShortDescription());

                        if ( Pattern.compile(Pattern.quote("ras"), Pattern.CASE_INSENSITIVE).matcher(products.getPname()).find()){
                            productViewHolder.imageView.setImageResource(R.drawable.liquid_image);

                        } else {
                            productViewHolder.imageView.setImageResource(R.drawable.raw_image);

                        }

                      //  Picasso.get().load(products.getImage()).into(productViewHolder.imageView);

                        productViewHolder.add_Button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (TextUtils.isEmpty(productViewHolder.quantity_input.getText())){
                                    Toast.makeText(getActivity(), "Please input quantity", Toast.LENGTH_SHORT).show();
                                    productViewHolder.quantity_input.setFocusableInTouchMode(true);
                                    productViewHolder.quantity_input.requestFocus();

                                } else {
                                    Toast.makeText(getActivity(), "yes", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_items_layout,parent, false);
                        ProductViewHolder holder= new ProductViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void showAllProducts(){
        loadingDialog.startLoadingDialog();


        FirebaseRecyclerOptions<Products> options=
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductRef,Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter=
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ProductViewHolder productViewHolder, int i, @NonNull final Products products) {

                        ptype=products.getPtype();

                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.units,android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        int mrp = (int)(Integer.parseInt(products.getPrice())*(50.0f/100.0f));
                        mrp = mrp+Integer.parseInt(products.getPrice());
                        productViewHolder.txtMrpPrice.setPaintFlags( productViewHolder.txtMrpPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        productViewHolder.txtMrpPrice.setText(Integer.toString(mrp));

                        productViewHolder.spinner.setAdapter(adapter);


                        if (ptype.equals("raw")){
                            productViewHolder.txtProductPrice.setText(products.getPrice()+" / Gram");
                        } else if (ptype.equals("pack")){
                            productViewHolder.txtProductPrice.setText("Price "+products.getPrice()+"₹");
                        }



                        productViewHolder.txtProductName.setText(products.getPname());
                        productViewHolder.txtProductDescription.setText(products.getShortDescription());

                        if ( Pattern.compile(Pattern.quote("ras"), Pattern.CASE_INSENSITIVE).matcher(products.getPname()).find()){
                            productViewHolder.imageView.setImageResource(R.drawable.liquid_image);
                            loadingDialog.dismissDialog();
                        } else {
                            productViewHolder.imageView.setImageResource(R.drawable.raw_image);
                            loadingDialog.dismissDialog();
                        }

//                        Picasso.get().load(products.getImage()).into(productViewHolder.imageView, new Callback() {
//                            @Override
//                            public void onSuccess() {
//                                loadingDialog.dismissDialog();
//                            }
//
//                            @Override
//                            public void onError(Exception e) {
//
//                            }
//                        });

                        productViewHolder.add_Button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if(!(Paper.book().read(Prevalent.UserPhoneKey).equals(""))){
                                    if (TextUtils.isEmpty(productViewHolder.quantity_input.getText())){
                                        Toast.makeText(getActivity(), "Please input quantity", Toast.LENGTH_SHORT).show();
                                        productViewHolder.quantity_input.setFocusableInTouchMode(true);
                                        productViewHolder.quantity_input.requestFocus();

                                    } else {
                                        loadingBar.setTitle("Please wait..");
                                        loadingBar.setMessage("Processing Your request");
                                        loadingBar.setCanceledOnTouchOutside(false);
                                        loadingBar.show();

                                        String quantity_grams;

                                        if(productViewHolder.spinner.getSelectedItem().toString().equals("gm")){
                                            quantity_grams = productViewHolder.quantity_input.getText().toString();
                                        } else {


                                        quantity_grams = Integer.toString(Integer.parseInt(productViewHolder.quantity_input.getText().toString())*1000);

                                        }

                                        addToBasket(Paper.book().read(Prevalent.UserPhoneKey).toString()
                                        ,products.getPid()
                                        ,products.getPname()
                                        ,products.getPrice()
                                        ,Integer.toString(Integer.parseInt(products.getPrice())*Integer.parseInt(quantity_grams))
                                        ,quantity_grams
                                        ,productViewHolder.txtMrpPrice.getText().toString()
                                        ,products.getImage()
                                        ,productViewHolder.spinner.getSelectedItem().toString()
                                        ,productViewHolder.quantity_input.getText().toString());

                                    }
                                } else {
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                }


                            }
                        });

//                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                Intent intent= new Intent(HomeActivity.this,Product_Details_Activity.class);
//                                intent.putExtra("pid",products.getPid());
//                                startActivity(intent);
//                            }
//                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_items_layout,parent, false);
                        ProductViewHolder holder= new ProductViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    private void addToBasket(final String UserPhoneKey, final String pid, String pname, String rate, String price, String quantity, String mrp, String pImage, String units, String unit_quantity) {

        String saveCurrentDate, saveCurrentsTime;


        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate= currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss a");
        saveCurrentsTime= currentDate.format(calForDate.getTime());

        final DatabaseReference cartListRef =FirebaseDatabase.getInstance().getReference().child("Cart");

        final HashMap<String, Object> cartMap= new HashMap<>();

        cartMap.put("pid",pid);
        cartMap.put("pname",pname);
        cartMap.put("price",price);
        cartMap.put("rate",rate);
        cartMap.put("image",pImage);
        cartMap.put("ptype",ptype);
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrentsTime);
        cartMap.put("quantity",quantity);
        cartMap.put("mrp",mrp);
        cartMap.put("units",units);
        cartMap.put("unit_quantity",unit_quantity);


        cartListRef.child(UserPhoneKey).child(pid)
                .updateChildren(cartMap).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(getActivity(), "Added to CartList", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
