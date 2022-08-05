package com.anonim.blueben.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.anonim.blueben.R;
import com.anonim.blueben.databinding.RecyclerChatBinding;
import com.anonim.blueben.model.Message;
import com.anonim.blueben.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private ArrayList<Message> messageArrayList;
    String username,otherName;
    static Boolean state = false;
    int view_Send=1,view_received=2;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;


    public ChatAdapter(ArrayList<Message> messageArrayList,String username,String otherName) {
        this.messageArrayList = messageArrayList;
        this.username = username;
        this.otherName = otherName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if(viewType == view_Send){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_chat,parent,false);
            return new ViewHolder(view);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_otherchat,parent,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.textView.setText(messageArrayList.get(position).getText());

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(holder.itemView.getContext(),v);
                popupMenu.getMenuInflater().inflate(R.menu.longclick_menu,popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.delete_option:
                                Toast.makeText(v.getContext(), "Yapım aşamasında.", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            if(state){
                textView = itemView.findViewById(R.id.userTextView);
            }else{
                textView = itemView.findViewById(R.id.otherTextView);
            }
            
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messageArrayList.get(position).getFrom().equals(username)){
            state = true;
            return view_Send;
        }else{
            state = false;
            return view_received;
        }
    }

    public void filteredList(ArrayList<Message> filteredList){
        messageArrayList = filteredList;
        notifyDataSetChanged();
    }
}
