package com.anonim.blueben.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.anonim.blueben.R;
import com.anonim.blueben.adapter.UserAdapter;
import com.anonim.blueben.databinding.ActivityMainBinding;
import com.anonim.blueben.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ArrayList<User> userArrayList;
    private UserAdapter userAdapter;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        userArrayList = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        getData();

        binding.recyclerMain.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(userArrayList,this);
        binding.recyclerMain.setAdapter(userAdapter);

    }

    private void getData() {
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(!(Objects.equals(dataSnapshot.child("email").getValue(), firebaseUser.getEmail()))){
                        User user = dataSnapshot.getValue(User.class);
                        userArrayList.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void logoutClicked(View view){
        auth.signOut();
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        finish();
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
        ArrayList<User> filteredList = new ArrayList<>();
        for(User item : userArrayList){
            if(item.getName().toLowerCase().contains(newText)){
                filteredList.add(item);
            }
        }
        userAdapter.filteredList(filteredList);
    }


}