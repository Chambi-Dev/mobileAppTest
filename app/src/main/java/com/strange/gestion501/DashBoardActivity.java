package com.strange.gestion501;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.strange.gestion501.dashboard.empresa.Empresa;
import com.strange.gestion501.dashboard.gastos.Gastos;
import com.strange.gestion501.dashboard.misdatos.MisDatos;
import com.strange.gestion501.dashboard.tareas.Tareas;

public class DashBoardActivity extends AppCompatActivity {
    CardView cvEmpresa, cvGastos, cvTareas, cvListaTareas, cvFavoritos, cvMisDatos;
    TextView txtNombreApellido, txtCodigoUser;

    Button btnCerrarSesion, btnDesarrollado;
    FirebaseAuth mAuth;
    FirebaseUser fireBaseUser;
    DatabaseReference usuarios;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dash_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        cvEmpresa = findViewById(R.id.cvEmpresa);
        cvGastos = findViewById(R.id.cvGastos);
        cvTareas = findViewById(R.id.cvTareas);
        cvListaTareas = findViewById(R.id.cvListaTareas);
        cvFavoritos = findViewById(R.id.cvFavoritos);
        cvMisDatos = findViewById(R.id.cvMisDatos);



        txtNombreApellido = findViewById(R.id.txtNombreApellido);
        txtCodigoUser = findViewById(R.id.txtCodigoUser);

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnDesarrollado = findViewById(R.id.btnDesarrollado);

        mAuth = FirebaseAuth.getInstance();
        fireBaseUser = mAuth.getCurrentUser();

        usuarios = FirebaseDatabase.getInstance().getReference("usuarios");



        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarSesion();
            }
        });

        cvEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashBoardActivity.this, "Es es Empresa", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashBoardActivity.this, Empresa.class));
            }
        });

        cvGastos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashBoardActivity.this, "esto es Gastos", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashBoardActivity.this, Gastos.class));
            }
        });

        cvTareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashBoardActivity.this, "esto es Tareas", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashBoardActivity.this, Tareas.class));
            }
        });

        cvListaTareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashBoardActivity.this, "esto es Lista de Tareas", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashBoardActivity.this, Tareas.class));
            }
        });

        cvFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashBoardActivity.this, "esto es Favoritos" , Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashBoardActivity.this, Tareas.class));
            }
            });

        cvMisDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashBoardActivity.this, "Esto es Mis Datos", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashBoardActivity.this, MisDatos.class));
            }
        });





    }

    private void cerrarSesion() {
        mAuth.signOut();
        startActivity(new Intent(DashBoardActivity.this, MainActivity.class));
        Toast.makeText(this, "Cerraste sesion exitosamente", Toast.LENGTH_SHORT).show();
        finish();

    }

    @Override
    protected void onStart() {
        super.onStart();
        fireBaseUser = mAuth.getCurrentUser();
        comprobarSesion();
    }

    private void comprobarSesion() {
        if (fireBaseUser != null){
            cargarDatos();
        } else {
            startActivity(new Intent(DashBoardActivity.this, MainActivity.class));
            finish();
        }

    }

    private void cargarDatos(){
        String uid = mAuth.getUid();
        if (uid == null) return;

        usuarios.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String uid = ""+snapshot.child("uid").getValue();
                    String nombre = ""+snapshot.child("nombre").getValue();
                    String apellido = ""+snapshot.child("apellido").getValue();

                    txtNombreApellido.setText(nombre+" "+apellido);
                    txtCodigoUser.setText(uid);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}