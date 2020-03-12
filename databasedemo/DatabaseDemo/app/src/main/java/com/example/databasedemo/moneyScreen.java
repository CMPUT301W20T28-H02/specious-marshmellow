package com.example.databasedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class moneyScreen extends AppCompatActivity {
    Button btn5, btn20, btn25, makeReq,add_money;
    Float Money,moneyFetched;
    TextView addMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_screen);
        btn5 = findViewById(R.id.add_5);
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Money = 0.0f;
                Money+=5.0f;
            }
        });
        btn20 =  findViewById(R.id.add_20);
        btn20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Money+=20.0f;
            }
        });
        btn25 = findViewById(R.id.add_25);
        btn25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Money+=25.0f;
            }
        });
        makeReq =  findViewById(R.id.back_home);
        makeReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), RiderDriverInitialActivity.class);
                intent.putExtra("driver", false);
                startActivity(intent);
            }
        });
        addMoney = findViewById(R.id.addMoney);
        add_money = findViewById(R.id.add_money);
        add_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getmoney = addMoney.getText().toString();
                moneyFetched = Float.valueOf(getmoney);




            }
        });




    }
}
