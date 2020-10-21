package com.example.asdinvoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.rengwuxian.materialedittext.MaterialEditText;

//import android.widget.Toolbar;
public class ForgetPasswordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView resetState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgetPasswordActivity.this,MainActivity.class));
            }
        });
        mAuth=FirebaseAuth.getInstance();
        final MaterialEditText emailAddress=findViewById(R.id.email);
        resetState= findViewById(R.id.resetText);
        final Button resetPasswordButton=findViewById(R.id.sendMessage);
        progressBar= findViewById(R.id.progressBar);
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                progressBar.setVisibility(view.VISIBLE);
                mAuth.fetchSignInMethodsForEmail(emailAddress.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if(task.getResult().getSignInMethods().isEmpty())
                        {
                            progressBar.setVisibility(view.GONE);
                            resetState.setText("This is not an registered Email");
                        }
                        else
                        {
                            mAuth.sendPasswordResetEmail(emailAddress.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(view.GONE);
                                    if(task.isSuccessful())
                                    {
                                        resetState.setText("Reset email sent to your mail");
                                    }
                                    else
                                    {
                                        resetState.setText(task.getException().getMessage());
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}