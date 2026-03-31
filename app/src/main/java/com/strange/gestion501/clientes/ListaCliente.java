package com.strange.gestion501.clientes;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.strange.gestion501.Cliente;
import com.strange.gestion501.R;
import com.strange.gestion501.viewholder.ViewHolderCliente;

public class ListaCliente extends AppCompatActivity {

    // Vista principal del listado.
    RecyclerView recyclerView;
    // Referencias a Firebase para lectura de clientes.
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    // Adapter y opciones dinámicas según búsqueda.
    FirebaseRecyclerAdapter<Cliente, ViewHolderCliente> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Cliente>firebaseRecyclerOptions;

    // Botón para abrir pantalla de registro.
    FloatingActionButton btnAgregarCliente;

    // Campo de búsqueda por nombre.
    TextInputEditText buscarCliente;
    // Texto actual para filtrar por nombre, apellido o telefono.
    String filtroActual = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Activa dibujo edge-to-edge para pantallas modernas.
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializa vistas y Firebase.
        btnAgregarCliente = findViewById(R.id.btnAgregarCliente);
        recyclerView = findViewById(R.id.recyclerviewClientes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(ListaCliente.this,2));
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("usuarios");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Carga inicial y escucha de cambios en búsqueda.
        buscarCliente = findViewById(R.id.etBuscar);
        listarClinetes(null);
        configurarBusqueda();


        // Navega a AgregarCliente validando sesión activa.
        btnAgregarCliente.setOnClickListener(view -> {
            Toast.makeText(ListaCliente.this, "Bienvenidos a Lista de Clientes", Toast.LENGTH_SHORT).show();

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(ListaCliente.this, "Sesion expirada", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(ListaCliente.this, AgregarCliente.class);
            intent.putExtra("uid", currentUser.getUid());
            startActivity(intent);
        });

    }

    private void configurarBusqueda() {
        // Refresca la lista cada vez que cambia el texto.
        buscarCliente.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filtroActual = editable != null ? editable.toString().trim().toLowerCase() : "";
                if (firebaseRecyclerAdapter == null) {
                    listarClinetes(filtroActual);
                } else {
                    int totalItems = firebaseRecyclerAdapter.getItemCount();
                    if (totalItems > 0) {
                        firebaseRecyclerAdapter.notifyItemRangeChanged(0, totalItems);
                    }
                }
            }
        });
    }

    private void listarClinetes(String textoBusqueda) {
        // Refresca usuario por si cambió entre pantallas.
        firebaseUser = firebaseAuth.getCurrentUser();

        // Evita consultas cuando no hay usuario autenticado.
        if (firebaseUser == null) {
            return;
        }

        // Detiene adapter previo antes de aplicar nuevo filtro.
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }

        // Guarda el filtro actual para aplicarlo en cada item.
        filtroActual = textoBusqueda != null ? textoBusqueda.trim().toLowerCase() : "";

        // Carga base de clientes; el filtro multi-campo se aplica en onBind.
        Query query = databaseReference.child(firebaseUser.getUid()).child("clientes").orderByChild("nombres");

        // Crea opciones de FirebaseUI usando la consulta construida.
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Cliente>().setQuery(query, Cliente.class).build();

        firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderCliente viewHolderCliente, int i, @NonNull Cliente cliente) {

                // Oculta items que no coinciden por nombre, apellido o telefono.
                if (!coincideFiltro(cliente, filtroActual)) {
                    viewHolderCliente.itemView.setVisibility(View.GONE);
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewHolderCliente.itemView.getLayoutParams();
                    params.width = 0;
                    params.height = 0;
                    viewHolderCliente.itemView.setLayoutParams(params);
                    return;
                }

                viewHolderCliente.itemView.setVisibility(View.VISIBLE);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewHolderCliente.itemView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                viewHolderCliente.itemView.setLayoutParams(params);

                // Vincula datos de texto del cliente al item.
                viewHolderCliente.setearDatosCliente(
                        cliente.getId_cliente(),
                        cliente.getUid_cliente(),
                        cliente.getNombres(),
                        cliente.getApellidos(),
                        cliente.getCorreo(),
                        cliente.getDni(),
                        cliente.getDireccion(),
                        cliente.getTelefono()
                );
                // Carga foto Base64; si no existe, el ImageView se oculta.
                viewHolderCliente.setearFotoCliente(cliente.getFoto());


            }

            @NonNull
            @Override
            public ViewHolderCliente onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Infla el layout de cada tarjeta de cliente.
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cliente,parent,false);
                ViewHolderCliente viewHolderCliente= new ViewHolderCliente(view);

                // Maneja click normal sobre tarjeta del cliente.
                viewHolderCliente.setOnClickListener(new ViewHolderCliente.clickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(ListaCliente.this, "on click item", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        // Maneja click prolongado para futuras acciones contextuales.
                        Toast.makeText(ListaCliente.this, "On item Long click", Toast.LENGTH_SHORT).show();

                    }
                });
                return viewHolderCliente;
            }
        };

        // Activa escucha en tiempo real y asigna adapter al RecyclerView.
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private boolean coincideFiltro(Cliente cliente, String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            return true;
        }

        String nombres = cliente.getNombres() != null ? cliente.getNombres().toLowerCase() : "";
        String apellidos = cliente.getApellidos() != null ? cliente.getApellidos().toLowerCase() : "";
        String telefono = cliente.getTelefono() != null ? cliente.getTelefono().toLowerCase() : "";

        return nombres.contains(filtro) || apellidos.contains(filtro) || telefono.contains(filtro);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Si aún no existe adapter (o cambió sesión), reconstruye listado.
        if (firebaseRecyclerAdapter == null) {
            String filtroActual = buscarCliente.getText() != null ? buscarCliente.getText().toString() : null;
            listarClinetes(filtroActual);
        }

        // Reanuda escucha cuando la pantalla vuelve a primer plano.
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Libera escucha para evitar consumo innecesario.
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }
}