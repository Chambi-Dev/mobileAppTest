package com.strange.gestion501;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * MainActivity handles user authentication and login functionality.
 * This activity serves as the entry point for the application where users
 * can log in with their email and password credentials.
 */

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etUsuario, etPassword;
    private Button btnIngresar;
    private TextView tvCrearCuenta;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    String usuario="", password="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        etUsuario     = findViewById(R.id.etUsuario);
        etPassword    = findViewById(R.id.etPassword);
        btnIngresar   = findViewById(R.id.Lbutton);
        tvCrearCuenta = findViewById(R.id.textView6);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Iniciando sesión");
        progressDialog.setCanceledOnTouchOutside(false);


        btnIngresar.setOnClickListener(v -> validarDatos());

        tvCrearCuenta.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegistroActivity.class));
        });
    }

    private void validarDatos() {
        usuario = etUsuario.getText().toString().trim();
        password = etPassword.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(usuario).matches()){
            Toast.makeText(this, "Debe inresar una contraseña", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Debe inresar una contraseña", Toast.LENGTH_SHORT).show();
        } else {
            loginUsuario();
            
        }
    }

    private void loginUsuario() {
        progressDialog.setMessage("Iniciando Sesion ...");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(usuario, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(MainActivity.this, DashBoardActivity.class));
                            Toast.makeText(MainActivity.this, "Bienvenido "+user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Correo y contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(MainActivity.this, "Ocurrio unproblema", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}