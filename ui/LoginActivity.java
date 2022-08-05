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

import com.anonim.blueben.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    String email,password;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
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
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
    }

    public void loginClicked(View view){
        email = binding.userEmailLogin.getText().toString();
        password = binding.userPasswordLogin.getText().toString();

        if(!(email.equals("")) && !(password.equals(""))){
            progressDialog.setMessage("Giriş yapılıyor...");
            progressDialog.show();
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    progressDialog.dismiss();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                    Toast.makeText(LoginActivity.this, "Hoşgeldiniz.", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Bir hata oluştu!", Toast.LENGTH_LONG).show();
                }
            });
        }else{
            Toast.makeText(this, "Email ve Şifre giriniz!", Toast.LENGTH_LONG).show();
        }

    }

    public void goToRegisterPage(View view){
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
    }

    public void forgetPasswordClicked(View view){
        Toast.makeText(this, "Yapım aşamasında!", Toast.LENGTH_SHORT).show();
    }
}