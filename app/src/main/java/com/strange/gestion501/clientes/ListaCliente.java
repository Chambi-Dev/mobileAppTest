package com.strange.gestion501.clientes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.strange.gestion501.R;

public class ListaCliente extends AppCompatActivity {

    FloatingActionButton btnAgregarCliente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnAgregarCliente = findViewById(R.id.btnAgregarCliente);

        btnAgregarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ListaCliente.this, "Bienvenidos a Lista de Clientes", Toast.LENGTH_SHORT).show();


                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    Toast.makeText(ListaCliente.this, "Sesion expirada", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(ListaCliente.this, AgregarCliente.class);
                intent.putExtra("uid", currentUser.getUid());
                startActivity(intent);
            }
        });

    }
}