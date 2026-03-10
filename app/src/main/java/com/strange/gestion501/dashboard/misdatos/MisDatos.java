package com.strange.gestion501.dashboard.misdatos;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.strange.gestion501.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MisDatos extends AppCompatActivity {

    TextInputEditText etFechaNacimiento;

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

        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);

        // Al hacer click en el campo de texto → abre calendario
        etFechaNacimiento.setOnClickListener(v -> mostrarCalendario());

        // Al hacer click en el ícono del pastel → abre calendario
        TextInputLayout tilFecha = (TextInputLayout) etFechaNacimiento.getParent().getParent();
        tilFecha.setEndIconOnClickListener(v -> mostrarCalendario());
    }

    private void mostrarCalendario() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona tu fecha de nacimiento")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Convertir milisegundos a fecha legible
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String fechaSeleccionada = sdf.format(new Date(selection));
            etFechaNacimiento.setText(fechaSeleccionada);
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }
}