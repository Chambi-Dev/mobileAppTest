package com.strange.gestion501.dashboard.misdatos;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.strange.gestion501.MainActivity;
import com.strange.gestion501.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import pl.droidsonroids.gif.GifImageView;

public class MisDatos extends AppCompatActivity {


    TextInputLayout myInfoDOB, myInfoPhone;
    GifImageView inconCake, iconPhone;
    TextView txtNombreApellido, txtCodigoUser;
    TextInputEditText etFechaNacimiento;
    FirebaseAuth mAuth;
    FirebaseUser fireBaseUser;
    DatabaseReference usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_datos);
        // Ajusta el padding para evitar solaparse con barra de estado/navegacion.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        txtNombreApellido = findViewById(R.id.myInfoCorreo);
        txtCodigoUser = findViewById(R.id.myInfoCodigo);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        myInfoPhone = findViewById(R.id.myInfoPhoneInput);
        myInfoDOB = findViewById(R.id.myInfoBOD);
        inconCake = findViewById(R.id.myInfoCakeIcon);
        iconPhone = findViewById(R.id.myInfoPhoneIcon);

        // Inicializa autenticacion y referencia a la coleccion de usuarios en Firebase.
        mAuth = FirebaseAuth.getInstance();
        fireBaseUser = mAuth.getCurrentUser();

        usuarios = FirebaseDatabase.getInstance().getReference("usuarios");

        iconPhone.setOnClickListener(v -> mostrarTeclado());

        // Al pulsar el icono se abre el selector de fecha de nacimiento.
        inconCake.setOnClickListener(v -> mostrarCalendario());
    }

    private void mostrarCalendario() {
        // Crea y muestra un DatePicker de Material con la fecha actual por defecto.
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona tu fecha de nacimiento")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Convierte el timestamp seleccionado a formato dd/MM/yyyy.
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String fechaSeleccionada = sdf.format(new Date(selection));
            assert myInfoDOB.getEditText() != null;
            myInfoDOB.getEditText().setText(fechaSeleccionada);
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Revalida el usuario activo cada vez que la vista vuelve al frente.
        fireBaseUser = mAuth.getCurrentUser();
        confirmarSesion();
    }

    private void confirmarSesion() {
        // Si no hay sesion activa, vuelve a la pantalla principal.
        if (fireBaseUser != null){
            cargarDatos();
        }else {
            startActivity(new Intent(MisDatos.this, MainActivity.class));
        }
    }

    private void cargarDatos() {
        // Si no hay UID autenticado, no intenta consultar la base de datos.
        String uid = mAuth.getUid();
        if (uid == null) return;
        
        // Escucha cambios del perfil en tiempo real para refrescar la pantalla.
        usuarios.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    // Lee datos del perfil desde Realtime Database de forma segura.
                    String uid = snapshot.child("uid").getValue(String.class);
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    String apellido = snapshot.child("apellido").getValue(String.class);

                    if (uid == null) uid = "";
                    if (nombre == null) nombre = "";
                    if (apellido == null) apellido = "";

                    txtNombreApellido.setText(nombre + " " + apellido);
                    txtCodigoUser.setText(uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Punto para manejar errores de lectura (log, toast o snackbar).
            }
        });
    }
    private void mostrarTeclado() {
        // Usa el EditText interno del TextInputLayout para foco y teclado.
        TextInputEditText etPhone = (TextInputEditText) myInfoPhone.getEditText();
        if (etPhone == null) return;

        etPhone.requestFocus();
        etPhone.post(() -> {
            if (etPhone.getText() != null) {
                etPhone.setSelection(etPhone.getText().length());
            }

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etPhone, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }
}