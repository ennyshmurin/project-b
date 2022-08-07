package com.example.checkme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private DatabaseReference reference;
    com.google.android.material.button.MaterialButton addItem;

    private RecyclerView recyclerView;
    ItemListAdapter adapter;
    DatabaseReference mbase;
    GoogleSignInAccount account;

    private GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        reference = FirebaseDatabase.getInstance().getReference("Users");

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null)
        {

            String Name = account.getDisplayName();
            String Email = account.getEmail();
        }



        mbase= FirebaseDatabase.getInstance().getReference("Items");
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linear = new LinearLayoutManager(this);
        linear.setReverseLayout(true);
        recyclerView.setLayoutManager(
                linear);


        FirebaseRecyclerOptions<Item> options = new FirebaseRecyclerOptions.Builder<Item>()
                .setQuery(mbase, Item.class).build();
        adapter = new ItemListAdapter(options,account);
        recyclerView.setAdapter(adapter);



        addItem = (com.google.android.material.button.MaterialButton)findViewById(R.id.additem);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog mydialog = new Dialog(MainActivity.this);

                WindowManager.LayoutParams lp1 = new WindowManager.LayoutParams();
                lp1.copyFrom(mydialog.getWindow().getAttributes());
                lp1.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp1.height = WindowManager.LayoutParams.WRAP_CONTENT;

                mydialog.setTitle("ADD ITEM");
                LayoutInflater layoutInflater3 = getLayoutInflater();
                View additem_view = layoutInflater3.inflate(R.layout.item_box,null);
                mydialog.setContentView(additem_view);
                mydialog.getWindow().setAttributes(lp1);
                mydialog.show();

                final MaterialButton add_view_btn = (MaterialButton) additem_view.findViewById(R.id.add_item_btn);
                final EditText item_text = (EditText) additem_view.findViewById(R.id.item_name);

                add_view_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String data = item_text.getText().toString();
                        if(data.trim().length() > 0)
                        {

                            if(LoginActivity.isNetworkAvailable(MainActivity.this)) {
                                Item item = new Item();
                                item.setName(data);

                                if(account == null) {
                                    item.setUserid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                }else
                                {
                                    item.setUserid(account.getId());
                                }

                                mbase.push().setValue(item);
                                Toast.makeText(MainActivity.this, "Item Added Success!", Toast.LENGTH_SHORT).show();
                                item_text.setText("");
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, "Please check your internet connection !", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Please enter the data to add item...!", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return  true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                finish();
                break;
            case R.id.Logout :

                try{

                    if(account == null)
                        FirebaseAuth.getInstance().signOut();
                    else
                    {
                        mGoogleSignInClient.signOut();
                    }


                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                    Toast.makeText(this, "You are logged out!", Toast.LENGTH_SHORT).show();

                }catch (Exception ex){
                    //  Toast.makeText(this, ""+ex.getMessage(), Toast.LENGTH_LONG).show();
                }

                break;
            default:
                break;
        }
        return  true;
    }


}