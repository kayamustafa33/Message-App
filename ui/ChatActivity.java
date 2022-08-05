package com.anonim.blueben.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.anonim.blueben.R;
import com.anonim.blueben.adapter.ChatAdapter;
import com.anonim.blueben.databinding.ActivityChatBinding;
import com.anonim.blueben.model.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    String otherName,username;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    ArrayList<Message> messageArrayList;
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        messageArrayList = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            otherName = extras.getString("otherUsername");
            username = extras.getString("username");
            getSupportActionBar().setTitle(otherName);
        }


        getData();

        binding.recyclerChatActivity.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(messageArrayList,username,otherName);
        binding.recyclerChatActivity.setAdapter(chatAdapter);

    }

    public void sendMessage(String text){

        String key = databaseReference.child("Messages").child(username).child(otherName).push().getKey();

        Map messageMap = new HashMap();
        messageMap.put("text",text);
        messageMap.put("from",username);

        databaseReference.child("Messages").child(username).child(otherName).child(key).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    databaseReference.child("Messages").child(otherName).child(username).child(key).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }
            }
        });
    }

    public void getData(){
        databaseReference.child("Messages").child(username).child(otherName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                messageArrayList.add(message);
                chatAdapter.notifyDataSetChanged();
                binding.recyclerChatActivity.scrollToPosition(messageArrayList.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sendMessageBtn(View view){
        String text = binding.messageEditText.getText().toString();
        binding.messageEditText.setText("");
        sendMessage(text);
    }

    public void attachFileClicked(View view){
        Toast.makeText(this, "Yapım aşamasında!", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_item,menu);
        MenuItem menuItem = menu.findItem(R.id.search);

        //searcview initialize edildi
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Bir şeyler ara...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    //listedeki veriler küçük harfe çevrilip listeye eklendi
    private void filter(String newText) {
        ArrayList<Message> filteredList = new ArrayList<>();
        for(Message item : messageArrayList){
            if(item.getText().toLowerCase().contains(newText)){
                filteredList.add(item);
            }
        }
        chatAdapter.filteredList(filteredList);
    }
    
    public void cameraClicked(View view){
        Toast.makeText(this, "Yapım aşamasında!", Toast.LENGTH_SHORT).show();
    }
}