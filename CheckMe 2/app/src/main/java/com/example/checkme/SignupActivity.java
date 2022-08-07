package com.example.checkme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import Model.Users;

public class SignupActivity extends AppCompatActivity {

    private com.google.android.material.button.MaterialButton login, signup;
    private EditText username,email,password,dob;
    private Users users;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, myusers;
    private StorageReference str;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        myusers = FirebaseDatabase.getInstance().getReference("Users");


        str = FirebaseStorage.getInstance().getReference().child("userlogo.png");

        login = (com.google.android.material.button.MaterialButton)findViewById(R.id.login);
        signup = (com.google.android.material.button.MaterialButton)findViewById(R.id.signup);

        username = (EditText)findViewById(R.id.username);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        dob = (EditText)findViewById(R.id.dob);



        users = new Users();
        //ref =  FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(email.length() == 0 || password.length() == 0 || dob.length() == 0|| username.length() == 0){

                    Toast.makeText(SignupActivity.this, "All fields are required to signup!", Toast.LENGTH_SHORT).show();
                }else{
                    final String myname = username.getText().toString().trim();
                    final String myemail = email.getText().toString().trim();
                    String mydob = dob.getText().toString().trim();
                    final String mypasswrod = password.getText().toString().trim();

                    /*users = new Users(myname,myemail,mydob);*/
                    users.setUsername(myname);
                    users.setEmail(myemail);
                    users.setDob(mydob);
                    users.setIsonline("Active");
                    users.setGender("");
                    users.setStatus("");


                    str.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            users.setImageUrl(uri.toString());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            Toast.makeText(SignupActivity.this, "ERROR:"+ exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    dialog = ProgressDialog.show(SignupActivity.this, "",
                            "PLEASE WAIT...", true);


                    if(myemail != null && myemail.length() != 0 && mypasswrod != null && mypasswrod.length() != 0) {


                        mAuth.createUserWithEmailAndPassword(myemail, mypasswrod)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {


                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            FirebaseUser user = mAuth.getCurrentUser();

                                            dialog.dismiss();

                                            Toast.makeText(SignupActivity.this, "Account Creation Success ! ",
                                                    Toast.LENGTH_SHORT).show();

                                            try {


                                                users.setId(FirebaseAuth.getInstance().getUid());
                                                mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(users);
                                                username.setText("");
                                                email.setText("");
                                                password.setText("");
                                                dob.setText("");

                                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                                finish();

                                            }catch (Exception exception)
                                            {
                                                Toast.makeText(SignupActivity.this, "ERROR : "+ exception.getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }


                                        } else {
                                            Toast.makeText(SignupActivity.this, "User already exists, or something went wrong...!",
                                                    Toast.LENGTH_LONG).show();
                                        }

                                    }

                                });




                    }else{
                        Toast.makeText(SignupActivity.this, "data is null!", Toast.LENGTH_SHORT).show();
                    }

                }


            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
                break;
            default:
                break;
        }
        return  true;
    }

}