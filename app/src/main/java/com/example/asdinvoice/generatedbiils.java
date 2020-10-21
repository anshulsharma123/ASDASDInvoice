package com.example.asdinvoice;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asdinvoice.Adapter.RecyclerViewAdapter;
import com.example.asdinvoice.Data.MyDbHandler;
import com.example.asdinvoice.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class generatedbiils extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<Contact> contactArrayList;
    private ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generatedbiils);

        //Recyclerview initialization
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        MyDbHandler db = new MyDbHandler(generatedbiils.this);



        contactArrayList = new ArrayList<>();


        // Get all contacts
        List<Contact> contactList = db.getAllContacts();
        for(Contact contact: contactList){

            Log.d("dbharry", "\nId: " + contact.getId() + "\n" +
                    "Name: " + contact.getName() + "\n"+
                    "Phone Number: " + contact.getRupees() + "\n" );

            contactArrayList.add(contact);
        }

//        Use your recyclerView
        recyclerViewAdapter = new RecyclerViewAdapter(generatedbiils.this, contactArrayList);
        recyclerView.setAdapter(recyclerViewAdapter);

        Log.d("dbharry", "Bro you have "+ db.getCount()+ " contacts in your database");


    }
}