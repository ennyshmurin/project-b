package com.example.checkme;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private static final int REQ_ONE_TAP = 2;
    BeginSignInRequest signInRequest;

    private com.google.android.material.button.MaterialButton login, signup;
    private FirebaseAuth mAuth;
    private EditText email,password;
    private DatabaseReference admin;
    private DatabaseReference mDatabase;
    ImageView loginWithGoogle;

    static GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    static GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();
        admin = FirebaseDatabase.getInstance().getReference("Admin");
        mDatabase = FirebaseDatabase.getInstance().getReference();


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null)
        {

            //String Name = account.getDisplayName();
            //String Email = account.getEmail();
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }



        loginWithGoogle = (ImageView) findViewById(R.id.loginWithGoogle);

        login = (com.google.android.material.button.MaterialButton)findViewById(R.id.login);
        signup = (com.google.android.material.button.MaterialButton)findViewById(R.id.signup);

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);

        loginWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signInUsingGoogle();


            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(email.length() == 0 || password.length() == 0) {

                    Toast.makeText(LoginActivity.this, "Enter your valid email and password to login!",
                            Toast.LENGTH_SHORT).show();

                } else{

                    final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "",
                            "Verifying credentials...Please wait...", true);

                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();

                                    } else{
                                        Toast.makeText(LoginActivity.this, "Please enter your valid email and password to login!",
                                                Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }

                                }
                            });
                }

            }
        });



    }

    private void signInUsingGoogle() {

    Intent intent = mGoogleSignInClient.getSignInIntent();
    startActivityForResult(intent,REQ_ONE_TAP);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQ_ONE_TAP)
        {

            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                task.getResult(ApiException.class);

                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();


            } catch (ApiException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!isNetworkAvailable(LoginActivity.this)){
            Toast.makeText(this, "Please check your Internet Connection!", Toast.LENGTH_LONG).show();
        }

        if(account == null) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }


    }

    public static boolean isNetworkAvailable(Context con) {
        try {
            ConnectivityManager cm = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}