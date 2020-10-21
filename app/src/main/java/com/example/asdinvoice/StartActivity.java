package com.example.asdinvoice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

//import android.widget.Toolbar;

public class StartActivity extends AppCompatActivity {
    private TextView userName;
    private CircleImageView circleImageView;
    private FirebaseUser firebaseUser;
    private CardView cardView1, cardView2 , cardView3, cardView4;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ImagesRecyclerAdapter imagesRecyclerAdapter;
    private List<ImagesList> imagesList;
    private static final int IMAGES_REQUEST=1;
    private UsersData usersData;
    private Uri imageUri;
    private StorageTask storageTask;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        imagesList =new ArrayList<>();
        userName=findViewById(R.id.username);
        circleImageView=findViewById(R.id.profileImage);
        cardView1=findViewById(R.id.card1);
        cardView2=findViewById(R.id.card2);
        cardView3=findViewById(R.id.card3);
        cardView4=findViewById(R.id.card4);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        storageReference= FirebaseStorage.getInstance().getReference("profile_images");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 usersData=dataSnapshot.getValue(UsersData.class);
                assert usersData!=null;
                userName.setText(usersData.getUsername());
                if(usersData.getImageURL().equals("default"))
                {
                    circleImageView.setImageResource(R.drawable.ic_launcher_background);

                }
                else
                {
                    Glide.with(getApplicationContext()).load(usersData.getImageURL()).into(circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(StartActivity.this);
                builder.setCancelable(true);
                View mView = LayoutInflater.from(StartActivity.this).inflate(R.layout.select_image_layout,null);
                RecyclerView recyclerView= mView.findViewById(R.id.recyclerView);
                collectOldImages();
                recyclerView.setLayoutManager(new GridLayoutManager(StartActivity.this,3));
                recyclerView.setHasFixedSize(true);
                imagesRecyclerAdapter =new ImagesRecyclerAdapter(imagesList,StartActivity.this);
                recyclerView.setAdapter(imagesRecyclerAdapter);
                imagesRecyclerAdapter.notifyDataSetChanged();
                Button openImage= mView.findViewById(R.id.openImages);
                openImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openImage();
                    }
                });
                builder.setView(mView);
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
            }
        });

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(StartActivity.this, additems.class);
                startActivity(intent);
            }
        });
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(StartActivity.this, bill.class);
                startActivity(intent);
            }
        });
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(StartActivity.this, generatedbiils.class);
                startActivity(intent);
            }
        });
        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(StartActivity.this, info.class);
                startActivity(intent);
            }
        });
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("images/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGES_REQUEST);
    }
   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
   {
       super.onActivityResult(requestCode,resultCode,data);
       if(requestCode==IMAGES_REQUEST&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null)
       {
           imageUri=data.getData();
           if(storageTask!=null&&storageTask.isInProgress())
           {
               Toast.makeText(this,"Uploading is in progress", Toast.LENGTH_LONG).show();
           }
           else
           {
               uploadImage();
           }
       }

   }

    private void uploadImage() {
        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("uploading Image");
        progressDialog.show();
        if(imageUri!=null)
        {
            Bitmap bitmap=null;
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);

            }catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
            assert bitmap!=null;
            bitmap.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);
            byte[] imageFileToByte= byteArrayOutputStream.toByteArray();
            final StorageReference imageReferences=storageReference.child(usersData.getUsername()+System.currentTimeMillis()+".jpg");
            storageTask=imageReferences.putBytes(imageFileToByte);
            storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>()
            {

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                   if(!task.isSuccessful())
                   {
                       throw task.getException();
                   }
                   return imageReferences.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUri=task.getResult();
                        String sDownloadUri=downloadUri.toString();
                        Map<String,Object> hashMap=new HashMap<>();
                        hashMap.put("imageUrl",sDownloadUri);
                        databaseReference.updateChildren(hashMap);
                        final DatabaseReference profileImagesReferences=FirebaseDatabase.getInstance().getReference("profile_images").child(firebaseUser.getUid());
                        profileImagesReferences.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    progressDialog.dismiss();
                                }
                                else
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                   Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                   progressDialog.dismiss();
                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.profile_menu,menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       int id=item.getItemId();
       if(id==R.id.changePsw)
       {
           startActivity(new Intent(StartActivity.this,chnagePasswordActivity.class));
       }else if(id==R.id.logout)
       {
           firebaseAuth.signOut();
           startActivity(new Intent(StartActivity.this,MainActivity.class));
           finish();
       }
           return true;
    }

    private void collectOldImages() {
        DatabaseReference imagesListReference= FirebaseDatabase.getInstance().getReference("profile_images").child(firebaseUser.getUid());
        imagesListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imagesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                 imagesList.add(snapshot.getValue(ImagesList.class));
                }
                imagesRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
             Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

//    private void setSupportActionBar(Toolbar toolbar) {
//    }
}