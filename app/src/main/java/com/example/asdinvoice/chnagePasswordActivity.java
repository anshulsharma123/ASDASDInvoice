package com.example.asdinvoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
//import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;

public class chnagePasswordActivity extends AppCompatActivity {
    private MaterialEditText oldPsw, newPsw, confirmPsw;
    private Button changePsw;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chnage_password);
        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(chnagePasswordActivity.this,MainActivity.class));
            }
        });

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        oldPsw=findViewById(R.id.oldPassword);
        newPsw=findViewById(R.id.newPassword);
        confirmPsw=findViewById(R.id.confirmPassword);
        changePsw=findViewById(R.id.changePassword);
        progressBar=findViewById(R.id.progressBar);
        changePsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtOldPsw=oldPsw.getText().toString();
                String txtNewPsw=newPsw.getText().toString();
                String txtConfirmPsw=confirmPsw.getText().toString();
                if(txtConfirmPsw.isEmpty()||txtNewPsw.isEmpty()||txtOldPsw.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"All fiels are necessary",Toast.LENGTH_LONG).show();
                }
                else if(txtNewPsw.length()<6)
                {
                    Toast.makeText(getApplicationContext(),"Password must be greater then 6 digits", Toast.LENGTH_LONG).show();
                }
                else if(!txtConfirmPsw.equals(txtNewPsw))
                {
                    Toast.makeText(getApplicationContext(),"Confirm dont match with new password",Toast.LENGTH_LONG).show();
                }
                else
                {
                    changePassword(txtOldPsw, txtNewPsw);

                }
            }
        });
    }

    private void changePassword(String txtOldPsw, final String txtNewPsw) {
        progressBar.setVisibility(View.VISIBLE);
        AuthCredential credential= EmailAuthProvider.getCredential(firebaseUser.getEmail(),txtOldPsw);
        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    firebaseUser.updatePassword(txtNewPsw).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                firebaseAuth.signOut();
                                Intent intent=new Intent(chnagePasswordActivity.this,MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}