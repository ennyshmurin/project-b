package com.example.checkme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import Model.Users;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String u_profile_id;
    private CircleImageView u_profile_img;
    private TextView username, u_status,u_email,u_dob;
    private DatabaseReference reference;

    private GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    static GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);


        reference = FirebaseDatabase.getInstance().getReference("Users");

        u_status = (TextView) findViewById(R.id.u_status);
        u_profile_img = (CircleImageView) findViewById(R.id.u_profile_img);
        username = (TextView) findViewById(R.id.u_name);
        u_email = (TextView) findViewById(R.id.u_email);
        u_dob = (TextView) findViewById(R.id.u_dateofbirth);


        if(account != null)
        {

            String Name = account.getDisplayName();
            String Email = account.getEmail();
            username.setText(""+Name);
            u_email.setText(""+Email);
            u_status.setVisibility(View.GONE);
            u_dob.setVisibility(View.GONE);
            Picasso.get().load(account.getPhotoUrl());

        }
        else {
            u_profile_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            reference.child(u_profile_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Users u = dataSnapshot.getValue(Users.class);
                    username.setText("" + u.getUsername());
                    Picasso.get().load(u.getImageUrl()).into(u_profile_img);
                    u_status.setText(u.getStatus());
                    u_email.setText(u.getEmail());
                    u_dob.setText(u.getDob());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
                break;
            default:
                break;
        }
        return  true;
    }


}