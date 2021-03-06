package com.example.amodgandhe.smartparking;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.craftman.cardform.Card;
import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnPayBtnClickListner;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Payment extends AppCompatActivity {

    ArrayList<String> temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();

        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#010000")));

        temp = new ArrayList<>();
        temp = intent.getStringArrayListExtra("slotDetails");

        final LatLng latLng = new LatLng(Double.parseDouble(temp.get(0)), Double.parseDouble(temp.get(1)));
        final String key = temp.get(2);

        final Long totalTime = (((Long.parseLong(temp.get(4)) - Long.parseLong(temp.get(3)))/60000)*2);

        CardForm cardFrom  = (CardForm)findViewById(R.id.cardform);

        TextView txtDes = (TextView) findViewById(R.id.payment_amount);
        Button btnpay = (Button) findViewById(R.id.btn_pay);
        if (totalTime < 10){
            txtDes.setText("10");
        }else
            txtDes.setText(totalTime.toString());
        btnpay.setText(String.format("Payable Amount %s",txtDes.getText()));
        cardFrom.setPayBtnClickListner(new OnPayBtnClickListner() {
            @Override
            public void onClick(Card card) {

                if(totalTime!=0) {
                    History.addToHistory(latLng, "$" + totalTime.toString());
                }else
                    History.addToHistory(latLng, "$" + "10");
                startActivity(new Intent(Payment.this, MainActivity.class));
                BookSlot.freeSlot(latLng, key);
                Toast.makeText(Payment.this,"Thank You for Visiting us!",Toast.LENGTH_LONG).show();
            }
        });
    }
}
