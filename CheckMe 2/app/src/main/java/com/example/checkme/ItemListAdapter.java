package com.example.checkme;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

class ItemListAdapter extends FirebaseRecyclerAdapter<Item, ItemListAdapter.ListViewHolder> {

    GoogleSignInAccount account;


    public ItemListAdapter(@NonNull FirebaseRecyclerOptions<Item> options, GoogleSignInAccount account) {
        super(options);
        this.account = account;
    }

    @Override
    protected void onBindViewHolder(@NonNull ListViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Item model) {



       if(account == null)
       {
           if(model.getUserid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
           {
               holder.cardView.setVisibility(View.VISIBLE);
               holder.item_name.setText(model.getName());
           }
           else
           {
               holder.cardView.setVisibility(View.GONE);
           }

       }
       else
       {
           if(model.getUserid().equals(account.getId()))
           {
               holder.cardView.setVisibility(View.VISIBLE);
               holder.item_name.setText(model.getName());
           }
           else
           {
               holder.cardView.setVisibility(View.GONE);
           }
       }




    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_layout, viewGroup,false);

        return new ListViewHolder(view);
    }


    public  class ListViewHolder extends RecyclerView.ViewHolder {
        CheckBox item_name;
        CardView cardView;
        public ListViewHolder(@NonNull final View itemView) {
            super(itemView);
            item_name = (CheckBox) itemView.findViewById(R.id.item_check_box);
            cardView = (CardView) itemView.findViewById(R.id.myCard);
        }
    }



}
