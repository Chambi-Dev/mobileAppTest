package com.strange.gestion501.dashboard.misdatos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import pl.droidsonroids.gif.GifImageView;

public class MisDatos extends AppCompatActivity {

    TextInputLayout myInfoNombre, myInfoApellido, myInfoDOB, myInfoPhone, myInfoEdad, myInfoDomicilio, myInfoTiktok, myInfoProfesion;
    GifImageView inconCake, iconPhone;
    TextView txtNombreApellido, txtCodigoUser;
    TextInputEditText etFechaNacimiento;
    FirebaseAuth mAuth;
    FirebaseUser fireBaseUser;
    DatabaseReference usuarios;
    Button btnRegistrar;
    ImageView imagenPerfil;
    String nombre = "", apellido = "", fechaNacimiento = "", edad = "", telefono = "", domicilio = "", tiktok = "", profesion = "", imagen = "";

    ProgressDialog progressDialog;
    ActivityResultLauncher<String> selectorImagenLauncher;
    ActivityResultLauncher<Void> camaraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_datos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializarComponentes();
        configurarSelectorImagen();
    }

    private void inicializarComponentes() {
        imagenPerfil = findViewById(R.id.imagePerfil);
        txtNombreApellido = findViewById(R.id.myInfoCorreo);
        txtCodigoUser = findViewById(R.id.myInfoCodigo);
        myInfoNombre = findViewById(R.id.myInfoNombre);
        myInfoApellido = findViewById(R.id.myInfoApellido);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        myInfoEdad = findViewById(R.id.myInfoEdad);
        myInfoPhone = findViewById(R.id.myInfoPhoneInput);
        myInfoDOB = findViewById(R.id.myInfoBOD);
        inconCake = findViewById(R.id.myInfoCakeIcon);
        iconPhone = findViewById(R.id.myInfoPhoneIcon);
        myInfoDomicilio = findViewById(R.id.myInfoDomicilio);
        myInfoTiktok = findViewById(R.id.myInfoTiktok);
        myInfoProfesion = findViewById(R.id.myInfoProfesion);
        btnRegistrar = findViewById(R.id.btnGuardarMisDatos);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();
        fireBaseUser = mAuth.getCurrentUser();
        usuarios = FirebaseDatabase.getInstance().getReference("usuarios");

        imagenPerfil.setOnClickListener(v -> abrirSelectorImagen());
        iconPhone.setOnClickListener(v -> mostrarTeclado());
        inconCake.setOnClickListener(v -> mostrarCalendario());
        btnRegistrar.setOnClickListener(v -> verificarDatos());
    }

    private void configurarSelectorImagen() {
        selectorImagenLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri == null) return;
            try {
                Bitmap bitmap = obtenerBitmapDesdeUri(uri);
                if (bitmap != null) {
                    aplicarImagenSeleccionada(bitmap);
                }
            } catch (IOException e) {
                Toast.makeText(this, "No se pudo leer la imagen", Toast.LENGTH_SHORT).show();
            }
        });

        camaraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
            if (bitmap != null) {
                aplicarImagenSeleccionada(bitmap);
            }
        });
    }

    private void abrirSelectorImagen() {
        String[] opciones = {"Seleccionar foto", "Abrir camara"};
        new AlertDialog.Builder(this)
                .setTitle("Foto de perfil")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        selectorImagenLauncher.launch("image/*");
                    } else {
                        camaraLauncher.launch(null);
                    }
                })
                .show();
    }

    private Bitmap obtenerBitmapDesdeUri(Uri imagenUri) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imagenUri);
            return ImageDecoder.decodeBitmap(source);
        }
        return MediaStore.Images.Media.getBitmap(getContentResolver(), imagenUri);
    }

    private void aplicarImagenSeleccionada(Bitmap bitmapOriginal) {
        Bitmap bitmapRedimensionado = Bitmap.createScaledBitmap(bitmapOriginal, 512, 512, true);
        imagenPerfil.setImageBitmap(bitmapRedimensionado);
        imagen = bitmapToBase64(bitmapRedimensionado);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 70, baos);
        } else {
            bitmap.compress(Bitmap.CompressFormat.WEBP, 70, baos);
        }
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    private Bitmap base64ToBitmap(String base64) {
        try {
            byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void mostrarCalendario() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona tu fecha de nacimiento")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String fechaSeleccionada = sdf.format(new Date(selection));
            if (myInfoDOB.getEditText() != null) {
                myInfoDOB.getEditText().setText(fechaSeleccionada);
            }

            int edadCalculada = calcularEdadDesdeMillis(selection);
            if (myInfoEdad.getEditText() != null) {
                myInfoEdad.getEditText().setText(String.valueOf(edadCalculada));
            }
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private int calcularEdadDesdeMillis(long fechaNacimientoMillis) {
        Calendar nacimiento = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        nacimiento.setTimeInMillis(fechaNacimientoMillis);

        Calendar hoy = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        int edadCalculada = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR);
        int mesHoy = hoy.get(Calendar.MONTH);
        int diaHoy = hoy.get(Calendar.DAY_OF_MONTH);
        int mesNac = nacimiento.get(Calendar.MONTH);
        int diaNac = nacimiento.get(Calendar.DAY_OF_MONTH);

        if (mesHoy < mesNac || (mesHoy == mesNac && diaHoy < diaNac)) {
            edadCalculada--;
        }

        return Math.max(edadCalculada, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fireBaseUser = mAuth.getCurrentUser();
        confirmarSesion();
    }

    private void confirmarSesion() {
        if (fireBaseUser != null) {
            cargarDatos();
        } else {
            startActivity(new Intent(MisDatos.this, MainActivity.class));
        }
    }

    private void cargarDatos() {
        String uid = mAuth.getUid();
        if (uid == null) return;

        usuarios.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String uid = snapshot.child("uid").getValue(String.class);
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    String apellido = snapshot.child("apellido").getValue(String.class);
                    String correo = snapshot.child("correo").getValue(String.class);
                    String fechaNacimiento = snapshot.child("fechaNacimiento").getValue(String.class);
                    String edad = snapshot.child("edad").getValue(String.class);
                    String telefono = snapshot.child("telefono").getValue(String.class);
                    String domicilio = snapshot.child("domicilio").getValue(String.class);
                    String tiktok = snapshot.child("tiktok").getValue(String.class);
                    String profesion = snapshot.child("profesion").getValue(String.class);
                    String imagen = snapshot.child("imagen").getValue(String.class);

                    if (uid == null) uid = "";
                    if (nombre == null) nombre = "";
                    if (apellido == null) apellido = "";
                    if (correo == null) correo = "";
                    if (fechaNacimiento == null) fechaNacimiento = "";
                    if (edad == null) edad = "";
                    if (telefono == null) telefono = "";
                    if (domicilio == null) domicilio = "";
                    if (tiktok == null) tiktok = "";
                    if (profesion == null) profesion = "";
                    if (imagen == null) imagen = "";

                    txtNombreApellido.setText(correo);
                    txtCodigoUser.setText(uid);
                    setTextoInput(myInfoNombre, nombre);
                    setTextoInput(myInfoApellido, apellido);
                    etFechaNacimiento.setText(fechaNacimiento);
                    setTextoInput(myInfoEdad, edad);
                    setTextoInput(myInfoPhone, telefono);
                    setTextoInput(myInfoDomicilio, domicilio);
                    setTextoInput(myInfoTiktok, tiktok);
                    setTextoInput(myInfoProfesion, profesion);
                    MisDatos.this.imagen = imagen;

                    if (!imagen.isEmpty()) {
                        Bitmap bitmap = base64ToBitmap(imagen);
                        if (bitmap != null) {
                            imagenPerfil.setImageBitmap(bitmap);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MisDatos.this, "Error al cargar datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void verificarDatos() {
        nombre = obtenerTexto(myInfoNombre);
        apellido = obtenerTexto(myInfoApellido);
        fechaNacimiento = etFechaNacimiento.getText() != null ? etFechaNacimiento.getText().toString().trim() : "";
        edad = obtenerTexto(myInfoEdad);
        telefono = obtenerTexto(myInfoPhone);
        domicilio = obtenerTexto(myInfoDomicilio);
        tiktok = obtenerTexto(myInfoTiktok);
        profesion = obtenerTexto(myInfoProfesion);

        if (nombre.isEmpty()) {
            Toast.makeText(this, "esta vacio el campo nombre", Toast.LENGTH_SHORT).show();
        } else if (apellido.isEmpty()) {
            Toast.makeText(this, "esta vacio el apellido", Toast.LENGTH_SHORT).show();
        } else if (fechaNacimiento.isEmpty()) {
            Toast.makeText(this, "esta vacio el campo fecha de nacimiento", Toast.LENGTH_SHORT).show();
        } else if (edad.isEmpty()) {
            Toast.makeText(this, "esta vacio el campo edad", Toast.LENGTH_SHORT).show();
        } else if (telefono.isEmpty()) {
            Toast.makeText(this, "esta vacio el campo telefono", Toast.LENGTH_SHORT).show();
        } else if (domicilio.isEmpty()) {
            Toast.makeText(this, "esta vacio el campo domicilio", Toast.LENGTH_SHORT).show();
        } else if (tiktok.isEmpty()) {
            Toast.makeText(this, "esta vacio el campo tiktok", Toast.LENGTH_SHORT).show();
        } else if (profesion.isEmpty()) {
            Toast.makeText(this, "esta vacio el campo profesion", Toast.LENGTH_SHORT).show();
        } else if (imagen.isEmpty()) {
            Toast.makeText(this, "selecciona una imagen de perfil", Toast.LENGTH_SHORT).show();
        } else {
            procesaDatos();
        }
    }

    private void procesaDatos() {
        progressDialog.setMessage("Guardando datos...");
        progressDialog.show();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Sesion no valida. Inicia sesion de nuevo", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MisDatos.this, MainActivity.class));
            finish();
            return;
        }

        guardarDatosEnRealtime(user.getUid(), imagen);
    }

    private void guardarDatosEnRealtime(String uid, String imagenBase64) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        Map<String, Object> nuevosDatos = new HashMap<>();
        nuevosDatos.put("nombre", nombre);
        nuevosDatos.put("apellido", apellido);
        nuevosDatos.put("fechaNacimiento", fechaNacimiento);
        nuevosDatos.put("edad", edad);
        nuevosDatos.put("telefono", telefono);
        nuevosDatos.put("domicilio", domicilio);
        nuevosDatos.put("tiktok", tiktok);
        nuevosDatos.put("profesion", profesion);
        nuevosDatos.put("imagen", imagenBase64);

        db.getReference("usuarios").child(uid).updateChildren(nuevosDatos)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private String obtenerTexto(TextInputLayout inputLayout) {
        if (inputLayout.getEditText() == null || inputLayout.getEditText().getText() == null) {
            return "";
        }
        return inputLayout.getEditText().getText().toString().trim();
    }

    private void setTextoInput(TextInputLayout inputLayout, String valor) {
        if (inputLayout.getEditText() != null) {
            inputLayout.getEditText().setText(valor);
        }
    }

    private void mostrarTeclado() {
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