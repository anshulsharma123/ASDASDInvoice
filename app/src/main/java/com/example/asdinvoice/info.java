package com.example.asdinvoice;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.asdinvoice.Data.MyDbHandler;
import com.example.asdinvoice.model.Contact;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class info extends AppCompatActivity {
    private ArrayList<String> Item,rps,dummy;
    private ArrayList<Integer> dummyRate,quantity;
    private Button btn,done;
    String username,phno,email,gsts;
    EditText customer, phone, gst, invoice,qty;
    Spinner spinner;
    Date date =new Date();
    String Invoices="";
    String customers="";
    String phones="";
    private UsersData usersData;
    private Uri imageUri;
    private StorageTask storageTask;
    private StorageReference storageReference;
    private ArrayAdapter<String> arrayAdapter;
    private  ArrayList<Contact> contactArrayList;
    private FirebaseUser firebaseUser;
    private CardView cardView1, cardView2 , cardView3, cardView4;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ImagesRecyclerAdapter imagesRecyclerAdapter;
    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        customer=findViewById(R.id.CustomerName);
        phone=findViewById(R.id.PhoneNo);
        gst=findViewById(R.id.GST);
        invoice=findViewById(R.id.InvoiceNo);
        qty=findViewById(R.id.Quantity);
        done=findViewById(R.id.Done);
        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("INFO");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(info.this,StartActivity.class));
            }
        });
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        storageReference= FirebaseStorage.getInstance().getReference("profile_images");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersData=dataSnapshot.getValue(UsersData.class);
                assert usersData!=null;
                username=(usersData.getUsername());
                phno=(usersData.getMobile());
                email=(usersData.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        spinner=findViewById(R.id.spinner);
        MyDbHandler db = new MyDbHandler(info.this);
        Item=new ArrayList<>();
        rps=new ArrayList<>();
        dummy=new ArrayList<>();
        dummyRate=new ArrayList<>();
        quantity=new ArrayList<>();
        contactArrayList = new ArrayList<>();



        // Get all contacts
        List<Contact> contactList = db.getAllContacts();
        for(Contact contact: contactList){

            Log.d("dbharry", "\nId: " + contact.getId() + "\n" +
                    "Name: " + contact.getName() + "\n"+
                    "Phone Number: " + contact.getRupees() + "\n" );
            Item.add(contact.getName() );
            rps.add(contact.getRupees());
            contactArrayList.add(contact);
        }
        arrayAdapter= new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,Item);
        spinner.setAdapter(arrayAdapter);
        btn=findViewById(R.id.AddItem);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qtys=qty.getText().toString();
                qty.setText("");
                quantity.add(Integer.parseInt(qtys));
                dummy.add(spinner.getSelectedItem().toString());
                dummyRate.add(Integer.parseInt(rps.get(spinner.getSelectedItemPosition())));
                //Toast.makeText(getApplicationContext(),"name "+customers,Toast.LENGTH_SHORT).show();
               // Toast.makeText(getApplicationContext(),""+dummy.size()+" "+rps.get(spinner.getSelectedItemPosition()),Toast.LENGTH_SHORT).show();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                printInvoice();
                Toast.makeText(getApplicationContext(),"PDF GENERATED",Toast.LENGTH_SHORT).show();
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void printInvoice()
    {

        PdfDocument myPdfDocument= new PdfDocument();
        Paint myPaint=new Paint();
        customers=customer.getText().toString();
        phones=phone.getText().toString();
        gsts=gst.getText().toString();
        if(gsts.length()==0)
        {
            gsts="NONE";
        }
        if(phones.length()<10)
        {
           Toast.makeText(getApplicationContext(),"PHONE NO SHOULD BE OF 10 DIGITS",Toast.LENGTH_SHORT).show();
        }
        else if(customers.length()==0)
        {
            Toast.makeText(getApplicationContext(),"NAME CANT BE EMPTY", Toast.LENGTH_SHORT).show();
        }
        else if(dummy.size()==0)
        {
            Toast.makeText(getApplicationContext(),"BILL CANT BE EMPTY", Toast.LENGTH_SHORT).show();
        }
        else {
            Invoices = invoice.getText().toString();
            PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(1000, 1900, 1).create();
            PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
            Canvas canvas = myPage.getCanvas();
            myPaint.setTextSize(80);
            canvas.drawText((username.substring(0, Math.min(username.length(), 16)).toUpperCase()), 30, 80, myPaint);
            myPaint.setTextSize(30);
            canvas.drawText(phno, 30, 120, myPaint);
            canvas.drawText(email, 250, 120, myPaint);
            myPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("INVOICE NO:", canvas.getWidth() - 40, 40, myPaint);
            canvas.drawText(String.valueOf(Invoices), canvas.getWidth() - 40, 80, myPaint);
            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setColor(Color.rgb(150, 150, 150));
            canvas.drawRect(30, 150, canvas.getWidth() - 30, 160, myPaint);
            myPaint.setColor(Color.BLACK);
            canvas.drawText("Date:", 50, 200, myPaint);
            canvas.drawText(String.valueOf(currentDate), 150, 200, myPaint);
            canvas.drawText("Time:", 620, 200, myPaint);
            myPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(String.valueOf(currentTime), canvas.getWidth() - 40, 200, myPaint);
            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setColor(Color.rgb(150, 150, 150));
            canvas.drawRect(30, 250, 250, 300, myPaint);
            myPaint.setColor(Color.WHITE);
            canvas.drawText("Bill To:", 50, 285, myPaint);
            myPaint.setColor(Color.BLACK);
            canvas.drawText("GST NO:", 620, 285, myPaint);
            myPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(String.valueOf(gsts.toUpperCase()), canvas.getWidth() - 40, 285, myPaint);
            myPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Customer Name:", 30, 350, myPaint);
            canvas.drawText(String.valueOf(customers.toUpperCase()), 280, 350, myPaint);
            canvas.drawText("Phone:", 620, 350, myPaint);
            myPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(String.valueOf(phones), canvas.getWidth() - 40, 350, myPaint);
            myPaint.setTextAlign(Paint.Align.RIGHT);
            myPaint.setColor(Color.rgb(150, 50, 150));
            canvas.drawRect(30, 400, canvas.getWidth() - 30, 450, myPaint);
            myPaint.setColor(Color.WHITE);
            canvas.drawText("ITEM", 120, 435, myPaint);
            canvas.drawText("QTY", 550, 435, myPaint);
            myPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("AMOUNT", canvas.getWidth() - 40, 435, myPaint);
            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setColor(Color.BLACK);
            int i = 0;
            int total = 0;
            for (int j = 0; j < dummy.size(); j++) {
                myPaint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(String.valueOf(dummy.get(j)), 30, 480 + i, myPaint);
                canvas.drawText(quantity.get(j).toString(), 550, 480 + i, myPaint);
                myPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(String.valueOf(dummyRate.get(j) * quantity.get(j)), canvas.getWidth() - 40, 480 + i, myPaint);
                myPaint.setTextAlign(Paint.Align.RIGHT);
                i += 40;
                total += dummyRate.get(j) * quantity.get(j);
            }


            myPaint.setColor(Color.rgb(150, 150, 150));
            canvas.drawRect(30, 550 + i, canvas.getWidth() - 30, 550 + i, myPaint);
            myPaint.setColor(Color.BLACK);

            canvas.drawText("SUBTOTAL:", 550, 600 + i, myPaint);
            canvas.drawText("TAX 18%:", 550, 640 + i, myPaint);
            myPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("TOTAL:", 550, 680 + i, myPaint);
            myPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

            myPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(String.valueOf(total), 970, 600 + i, myPaint);
            canvas.drawText(String.valueOf(total * 18 / 100), 970, 640 + i, myPaint);
            myPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText(String.valueOf((total + total * 18 / 100)), 970, 680 + i, myPaint);

            myPdfDocument.finishPage(myPage);
            String directory_path = Environment.getExternalStorageDirectory().getPath() + "/ASDINVOICE/";
            File file = new File(directory_path);
            if (!file.exists()) {
                file.mkdirs();
            }
            String targetPdf = directory_path + Invoices + ".pdf";
            File filePath = new File(targetPdf);
            try {
                myPdfDocument.writeTo(new FileOutputStream(filePath));
                Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.e("main", "error " + e.toString());
                Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
            }
            myPdfDocument.close();
        }
    }
}