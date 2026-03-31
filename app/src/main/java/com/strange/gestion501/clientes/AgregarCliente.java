package com.strange.gestion501.clientes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.strange.gestion501.Cliente;
import com.strange.gestion501.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AgregarCliente extends AppCompatActivity {

    private static final int MAX_IMAGE_SIDE = 720;
    private static final int MAX_PHOTO_BYTES = 350 * 1024;

    // Referencia visual del UID de sesión.
    TextView tvCodUserI;
    // Imagen del cliente que se selecciona o captura.
    ImageView fotoClienteA;
    // Campos del formulario de cliente.
    EditText etNombresI, etApellidosI, etCorreoI, etDniI, etTelefonoI, etDireccionI;
    // Botón para persistir cliente.
    Button btnGuardarI;
    // Ruta principal en Realtime Database.
    DatabaseReference db_usuario;
    FirebaseAuth  firebaseAuth;

    FirebaseUser firebaseUser;
    // UID de sesión de respaldo cuando no hay currentUser directo.
    private String uidSesion = "";
    // Foto serializada en Base64 para guardar en Firebase.
    private String fotoClienteBase64 = "";

    // Selector de imagen desde galería.
    private final ActivityResultLauncher<String> galeriaLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::procesarImagenGaleria);

    // Captura rápida de foto desde cámara.
    private final ActivityResultLauncher<Void> camaraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
                if (bitmap == null) {
                    return;
                }
                // Muestra la foto tomada y la convierte a Base64.
                String fotoBase64 = bitmapToBase64(bitmap);
                if (fotoBase64.isEmpty()) {
                    Toast.makeText(this, "La foto es demasiado pesada, use otra imagen", Toast.LENGTH_LONG).show();
                    return;
                }
                fotoClienteA.setImageBitmap(bitmap);
                fotoClienteBase64 = fotoBase64;
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Habilita diseño edge-to-edge.
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializa vistas, Firebase y UID.
        inicializarComponentes();
        obtenerUser();

        // Guarda cliente con los datos actuales del formulario.
        btnGuardarI.setOnClickListener(view -> agregarCliente());

        // Abre selector para cargar foto o tomarla.
        fotoClienteA.setOnClickListener(v -> abrirSelectorImagen());

    }



    private void inicializarComponentes() {

        tvCodUserI = findViewById(R.id.txtUidI);
        fotoClienteA = findViewById(R.id.ivfotoClienteA);

        etNombresI = findViewById(R.id.etNombresI);
        etApellidosI = findViewById(R.id.etApellidosI);
        etCorreoI = findViewById(R.id.etCorreoI);
        etDniI = findViewById(R.id.etDniI);
        etTelefonoI = findViewById(R.id.etTelefonoI);
        etDireccionI = findViewById(R.id.etDireccionI);

        btnGuardarI = findViewById(R.id.btnGuardarI);

        // Referencia base: usuarios/{uid}/clientes
        db_usuario = FirebaseDatabase.getInstance().getReference("usuarios");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

    }
    private void agregarCliente() {

        // Usa el UID actual o el UID recuperado del intent.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String uid = currentUser != null ? currentUser.getUid() : uidSesion;

        String nombres = etNombresI.getText().toString().trim();
        String apellidos = etApellidosI.getText().toString().trim();
        String correo = etCorreoI.getText().toString().trim();
        String dni = etDniI.getText().toString().trim();
        String telefono = etTelefonoI.getText().toString().trim();
        String direccion = etDireccionI.getText().toString().trim();

        // Validaciones mínimas para guardar.
        if (uid == null || uid.trim().isEmpty()) {
            Toast.makeText(this, "No hay sesion activa", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nombres.isEmpty()) {
            Toast.makeText(this, "Debe ingresar el nombre del cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        // Evita doble tap mientras se persiste en Firebase.
        btnGuardarI.setEnabled(false);

        // Genera ID único del cliente dentro del nodo del usuario.
        DatabaseReference clientesRef = db_usuario.child(uid).child("clientes");
        String id_cliente = clientesRef.push().getKey();
        if (id_cliente == null) {
            Toast.makeText(this, "No se pudo generar el ID del cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construye objeto y adjunta foto Base64 (si existe).
        Cliente cliente = new Cliente();
        cliente.setId_cliente(id_cliente);
        cliente.setUid_cliente(uid);
        cliente.setNombres(nombres);
        cliente.setApellidos(apellidos);
        cliente.setCorreo(correo);
        cliente.setDni(dni);
        cliente.setTelefono(telefono);
        cliente.setDireccion(direccion);
        cliente.setFoto(fotoClienteBase64);

        // Guarda en la estructura: usuarios/{uid}/clientes/{id_cliente}
        clientesRef.child(id_cliente).setValue(cliente)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Cliente agregado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnGuardarI.setEnabled(true);
                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

    }

    private void obtenerUser() {

        // Prioriza sesión actual; si no, intenta UID enviado por intent.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            uidSesion = currentUser.getUid();
        } else {
            String uidExtra = getIntent().getStringExtra("uid");
            uidSesion = uidExtra != null ? uidExtra.trim() : "";
        }

        // Muestra UID en pantalla para referencia.
        tvCodUserI.setText(uidSesion);
    }

    private void abrirSelectorImagen() {
        // Muestra diálogo simple con dos fuentes de imagen.
        String[] opciones = {"Subir foto", "Abrir camara"};

        new AlertDialog.Builder(this)
                .setTitle("Foto del cliente")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        galeriaLauncher.launch("image/*");
                    } else {
                        camaraLauncher.launch(null);
                    }
                })
                .show();
    }

    private void procesarImagenGaleria(Uri uri) {
        // Ignora selección vacía.
        if (uri == null) {
            return;
        }

        try {
            // Decodifica en tamaño controlado para evitar picos de memoria.
            Bitmap bitmap = decodeSampledBitmapFromUri(uri);
            if (bitmap == null) {
                Toast.makeText(this, "No se pudo leer la imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            String fotoBase64 = bitmapToBase64(bitmap);
            if (fotoBase64.isEmpty()) {
                Toast.makeText(this, "La foto es demasiado pesada, use una imagen mas ligera", Toast.LENGTH_LONG).show();
                return;
            }

            fotoClienteA.setImageBitmap(bitmap);
            fotoClienteBase64 = fotoBase64;
        } catch (IOException e) {
            Toast.makeText(this, "Error al abrir imagen", Toast.LENGTH_SHORT).show();
        } catch (OutOfMemoryError e) {
            Toast.makeText(this, "Memoria insuficiente al procesar imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        // Comprime a JPEG para reducir tamaño antes de guardar en texto.
        Bitmap bitmapEscalado = scaleBitmapKeepingRatio(bitmap);
        int calidad = 80;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmapEscalado.compress(Bitmap.CompressFormat.JPEG, calidad, outputStream);

        while (outputStream.size() > MAX_PHOTO_BYTES && calidad > 45) {
            outputStream.reset();
            calidad -= 5;
            bitmapEscalado.compress(Bitmap.CompressFormat.JPEG, calidad, outputStream);
        }

        if (outputStream.size() > MAX_PHOTO_BYTES) {
            return "";
        }

        byte[] imagenBytes = outputStream.toByteArray();
        return Base64.encodeToString(imagenBytes, Base64.NO_WRAP);
    }

    private Bitmap decodeSampledBitmapFromUri(Uri uri) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        try (InputStream boundsStream = getContentResolver().openInputStream(uri)) {
            if (boundsStream == null) {
                return null;
            }
            BitmapFactory.decodeStream(boundsStream, null, options);
        }

        options.inSampleSize = calculateInSampleSize(options);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        try (InputStream decodeStream = getContentResolver().openInputStream(uri)) {
            if (decodeStream == null) {
                return null;
            }
            return BitmapFactory.decodeStream(decodeStream, null, options);
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > MAX_IMAGE_SIDE || width > MAX_IMAGE_SIDE) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= MAX_IMAGE_SIDE && (halfWidth / inSampleSize) >= MAX_IMAGE_SIDE) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private Bitmap scaleBitmapKeepingRatio(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int largestSide = Math.max(width, height);

        if (largestSide <= MAX_IMAGE_SIDE) {
            return bitmap;
        }

        float scale = (float) MAX_IMAGE_SIDE / (float) largestSide;
        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}