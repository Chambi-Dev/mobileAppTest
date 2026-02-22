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

public class RegistroActivity extends AppCompatActivity {

    private TextInputEditText etNuevoUsuario, etNuevaPassword, etConfirmarPassword;
    private Button btnRegistrar;
    private TextView tvYaTienesCuenta;
    private UsuarioPrefs usuarioPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuarioPrefs = new UsuarioPrefs(this);

        etNuevoUsuario       = findViewById(R.id.etNuevoUsuario);
        etNuevaPassword      = findViewById(R.id.etNuevaPassword);
        etConfirmarPassword  = findViewById(R.id.etConfirmarPassword);
        btnRegistrar         = findViewById(R.id.btnRegistrar);
        tvYaTienesCuenta     = findViewById(R.id.tvYaTienesCuenta);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());

        tvYaTienesCuenta.setOnClickListener(v -> {
            startActivity(new Intent(RegistroActivity.this, MainActivity.class));
            finish();
        });
    }

    private void registrarUsuario() {
        String usuario   = etNuevoUsuario.getText() != null
                ? etNuevoUsuario.getText().toString().trim() : "";
        String password  = etNuevaPassword.getText() != null
                ? etNuevaPassword.getText().toString() : "";
        String confirmar = etConfirmarPassword.getText() != null
                ? etConfirmarPassword.getText().toString() : "";

        // Validaciones
        if (TextUtils.isEmpty(usuario)) {
            etNuevoUsuario.setError("El usuario no puede estar vacío");
            etNuevoUsuario.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etNuevaPassword.setError("La contraseña no puede estar vacía");
            etNuevaPassword.requestFocus();
            return;
        }
        if (password.length() < 4) {
            etNuevaPassword.setError("La contraseña debe tener al menos 4 caracteres");
            etNuevaPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmar)) {
            etConfirmarPassword.setError("Las contraseñas no coinciden");
            etConfirmarPassword.requestFocus();
            return;
        }

        // Intentar registrar
        boolean registrado = usuarioPrefs.registrar(usuario, password);
        if (registrado) {
            Toast.makeText(this, "✅ Usuario \"" + usuario + "\" registrado con éxito",
                    Toast.LENGTH_LONG).show();
            // Regresar al login
            startActivity(new Intent(RegistroActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "❌ El usuario \"" + usuario + "\" ya existe",
                    Toast.LENGTH_LONG).show();
        }
    }
}