package com.anonim.blueben.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.anonim.blueben.databinding.ActivityRegisterBinding;
import com.anonim.blueben.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    String name,email,password,cnfPassword;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(view);

        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        if(firebaseUser != null){
            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
            finish();
        }
    }

    public void registerClicked(View view){

        name = binding.userNameEditText.getText().toString();
        email = binding.userEmailEditText.getText().toString();
        password = binding.userPasswordEditText.getText().toString();
        cnfPassword = binding.userConfirmPassword.getText().toString();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(User.class.getSimpleName());

        if(!(name.equals("")) && !(email.equals("")) && !(password.equals("")) && !(cnfPassword.equals(""))){
            if(password.equals(cnfPassword)){
                progressDialog.setMessage("Kayıt Olunuyor...");
                progressDialog.show();

                User user = new User(name,email,password);
                databaseReference.push().setValue(user);

                auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressDialog.dismiss();
                        startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Bir Hata Oluştu!", Toast.LENGTH_LONG).show();
                    }
                });
            }else{
                Toast.makeText(this, "Şifreler Eşleşmiyor!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Gerekli alanları doldurunuz!", Toast.LENGTH_LONG).show();
        }
    }

    public void goToLoginPage(View view){
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
    }
}