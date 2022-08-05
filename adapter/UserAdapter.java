package com.anonim.blueben.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anonim.blueben.R;
import com.anonim.blueben.databinding.RecyclerMainBinding;
import com.anonim.blueben.model.User;
import com.anonim.blueben.ui.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<User> userArrayList;
    private Context context;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    public UserAdapter(ArrayList<User> userArrayList,Context context) {
        this.userArrayList = userArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerMainBinding recyclerMainBinding = RecyclerMainBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(recyclerMainBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.recyclerMainBinding.userName.setText(userArrayList.get(position).getName());

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        final String[] username = new String[1];

        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(Objects.equals(dataSnapshot.child("email").getValue(), firebaseUser.getEmail())){
                        username[0] = dataSnapshot.child("name").getValue(String.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("otherUsername",userArrayList.get(position).getName());
                intent.putExtra("username",username[0]);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public void filteredList(ArrayList<User> filteredList){
        userArrayList = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerMainBinding recyclerMainBinding;

        public ViewHolder(@NonNull RecyclerMainBinding recyclerMainBinding) {
            super(recyclerMainBinding.getRoot());

            this.recyclerMainBinding = recyclerMainBinding;
        }
    }
}
