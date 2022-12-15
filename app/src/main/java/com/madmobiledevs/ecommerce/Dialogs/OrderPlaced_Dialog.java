package com.madmobiledevs.ecommerce.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.madmobiledevs.ecommerce.HomeActivity;
import com.madmobiledevs.ecommerce.MainActivity;
import com.madmobiledevs.ecommerce.R;

public class OrderPlaced_Dialog extends Dialog implements View.OnClickListener{

    private Button okayBtn;
    public Activity activity;
    String orderID;
    TextView orderID_TvtVw;

    public OrderPlaced_Dialog(@NonNull Activity a, String orderId) {
        super(a, R.style.full_screen_dialog);

        this.activity = a;
        this.orderID = orderId;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_placed_dialog_layout);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        orderID_TvtVw = findViewById(R.id.IdOrder_orderPlaced_TxtVw_test);
        okayBtn = findViewById(R.id.okayBtn_orderPlaced_TxtVw_test);

        orderID_TvtVw.setText("#"+orderID);

        okayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "order Placed Successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(activity, HomeActivity.class);
                activity.finish();
                intent.putExtra("isFrom", "login");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                activity.startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Toast.makeText(activity, "order Placed Successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(activity, HomeActivity.class);
        activity.finish();
        intent.putExtra("isFrom", "login");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        activity.startActivity(intent);
    }

    @Override
    public void onClick(View view) {


    }
}
