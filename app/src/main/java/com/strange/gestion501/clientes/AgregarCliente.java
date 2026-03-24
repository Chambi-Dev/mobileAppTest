package com.strange.gestion501.clientes;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.strange.gestion501.Cliente;
import com.strange.gestion501.R;

public class AgregarCliente extends AppCompatActivity {

    TextView tvCodUserI;
    EditText etNombresI, etApellidosI, etCorreoI, etDniI, etTelefonoI, etDireccionI;
    Button btnGuardarI;
    DatabaseReference db_usuario;
    FirebaseAuth  firebaseAuth;

    FirebaseUser firebaseUser;
    private String uidSesion = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializarComponentes();
        obtenerUser();

        btnGuardarI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarCliente();
            }
        });

    }



    private void inicializarComponentes() {

        tvCodUserI = findViewById(R.id.txtUidI);

        etNombresI = findViewById(R.id.etNombresI);
        etApellidosI = findViewById(R.id.etApellidosI);
        etCorreoI = findViewById(R.id.etCorreoI);
        etDniI = findViewById(R.id.etDniI);
        etTelefonoI = findViewById(R.id.etTelefonoI);
        etDireccionI = findViewById(R.id.etDireccionI);

        btnGuardarI = findViewById(R.id.btnGuardarI);

        db_usuario = FirebaseDatabase.getInstance().getReference("usuarios");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

    }
    private void agregarCliente() {

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String uid = currentUser != null ? currentUser.getUid() : uidSesion;

        String nombres = etNombresI.getText().toString().trim();
        String apellidos = etApellidosI.getText().toString().trim();
        String correo = etCorreoI.getText().toString().trim();
        String dni = etDniI.getText().toString().trim();
        String telefono = etTelefonoI.getText().toString().trim();
        String direccion = etDireccionI.getText().toString().trim();

        if (uid == null || uid.trim().isEmpty()) {
            Toast.makeText(this, "No hay sesion activa", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nombres.isEmpty()) {
            Toast.makeText(this, "Debe ingresar el nombre del cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference clientesRef = db_usuario.child(uid).child("clientes");
        String id_cliente = clientesRef.push().getKey();
        if (id_cliente == null) {
            Toast.makeText(this, "No se pudo generar el ID del cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        Cliente cliente = new Cliente();
        cliente.setId_cliente(id_cliente);
        cliente.setUid_cliente(uid);
        cliente.setNombres(nombres);
        cliente.setApellidos(apellidos);
        cliente.setCorreo(correo);
        cliente.setDni(dni);
        cliente.setTelefono(telefono);
        cliente.setDireccion(direccion);

        // Guarda en la estructura: usuarios/{uid}/clientes/{id_cliente}
        clientesRef.child(id_cliente).setValue(cliente)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Cliente agregado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show());

    }

    private void obtenerUser() {

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            uidSesion = currentUser.getUid();
        } else {
            String uidExtra = getIntent().getStringExtra("uid");
            uidSesion = uidExtra != null ? uidExtra.trim() : "";
        }

        tvCodUserI.setText(uidSesion);
    }
}