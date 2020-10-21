package com.example.asdinvoice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.asdinvoice.Data.MyDbHandler;
import com.example.asdinvoice.model.Contact;

public class additems extends AppCompatActivity {
    Button bt, bt1;
    EditText itemName, itemPrice, delete;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additems);
        bt=findViewById(R.id.ADD);
        bt1=findViewById(R.id.deleteItem);
        itemName=findViewById(R.id.itemName);
        itemPrice=findViewById(R.id.itemPrice);
        delete=findViewById(R.id.itemID);
        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ADD ITEMS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(additems.this,StartActivity.class));
            }
        });
        final MyDbHandler db = new MyDbHandler(additems.this);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Contact anshul = new Contact();
               //Log.d("annn","string"+text.toString());
                Log.d("jdod","data inserted");
                anshul.setRupees(itemPrice.getText().toString());
                anshul.setName(itemName.getText().toString());
                db.addContact(anshul);
               Toast.makeText(getApplicationContext(),"data added", Toast.LENGTH_LONG).show();
            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               int id=Integer.parseInt(delete.getText().toString());
               db.deleteContactById(id);
                Log.d("hiii","deleted");
               Toast.makeText(getApplicationContext(),"DATA DELETED", Toast.LENGTH_SHORT).show();
            }
        });

    }
}