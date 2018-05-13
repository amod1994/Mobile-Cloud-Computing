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

import java.util.ArrayList;

public class PrePayment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_payment);

        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#010000")));

        Intent intent = getIntent();

        final Long totalTime = ((intent.getLongExtra("TotalTime", 0)/60000)*10);
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
                startActivity(new Intent(PrePayment.this, MainActivity.class));
                Toast.makeText(PrePayment.this,"See You There!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
