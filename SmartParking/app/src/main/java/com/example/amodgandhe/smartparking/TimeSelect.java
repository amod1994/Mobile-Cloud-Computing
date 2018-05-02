package com.example.amodgandhe.smartparking;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimeSelect extends AppCompatActivity {

    TextView tv;
    Calendar currentTime;
    int hour,minute;
    String Format;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_select);

        tv = (TextView) findViewById(R.id.textView4);
        currentTime = Calendar.getInstance();
        hour = currentTime.get(Calendar.HOUR_OF_DAY);
        minute = currentTime.get(Calendar.MINUTE);
        selectedTimeFormat(hour);
        tv.setText(hour + " : " + minute );
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(TimeSelect.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedTimeFormat(hourOfDay);
                        tv.setText(hourOfDay + ":" + minute + " " + Format);

                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });
    }




    public void selectedTimeFormat(int hour){
        if(hour==0){
            hour += 12;
            Format = "AM";
        }
        else if (hour ==12){
            Format= "PM";

        }
        else if (hour > 12){
            hour -=12;
            Format = "PM";

        }
        else{
            Format ="AM";
        }
    }

    public void payment(View view){
        startActivity(new Intent(getApplicationContext(), Payment.class));
    }

}
