package com.strange.gestion501;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etUsuario, etPassword;
    private Button btnIngresar;
    private TextView tvCrearCuenta;
    private UsuarioPrefs usuarioPrefs;

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

        usuarioPrefs  = new UsuarioPrefs(this);

        etUsuario     = findViewById(R.id.etUsuario);
        etPassword    = findViewById(R.id.etPassword);
        btnIngresar   = findViewById(R.id.button);
        tvCrearCuenta = findViewById(R.id.textView6);

        btnIngresar.setOnClickListener(v -> validarLogin());

        tvCrearCuenta.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegistroActivity.class));
        });
    }

    private void validarLogin() {
        String usuario  = etUsuario.getText() != null
                ? etUsuario.getText().toString().trim() : "";
        String password = etPassword.getText() != null
                ? etPassword.getText().toString() : "";

        if (TextUtils.isEmpty(usuario)) {
            etUsuario.setError("Ingrese su usuario");
            etUsuario.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Ingrese su contraseña");
            etPassword.requestFocus();
            return;
        }

        if (usuarioPrefs.validar(usuario, password)) {
            Toast.makeText(this, "✅ Bienvenido, " + usuario + "!", Toast.LENGTH_SHORT).show();
            // TODO: navegar a la pantalla principal
            // startActivity(new Intent(this, HomeActivity.class));
            // finish();
        } else {
            Toast.makeText(this, "❌ Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
            etPassword.setText("");
            etPassword.requestFocus();
        }
    }
}