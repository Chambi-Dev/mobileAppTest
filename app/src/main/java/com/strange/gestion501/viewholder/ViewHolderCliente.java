package com.strange.gestion501.viewholder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.strange.gestion501.R;

public class ViewHolderCliente extends RecyclerView.ViewHolder {

    private static final int MAX_DECODE_SIDE = 360;

    // Vista raíz del item para reutilizar findViewById.
    View mview;
    // Callback de interacción definido por el adapter.
    private ViewHolderCliente.clickListener mclicklistener;

    // Eventos de click simple y prolongado del item.
    public interface clickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(ViewHolderCliente.clickListener clicklistener) {
        // Registra el listener externo para notificar acciones del usuario.
        mclicklistener = clicklistener;
    }

    public ViewHolderCliente(@NonNull View itemView) {
        super(itemView);
        mview = itemView;

        // Reenvía click del item a la Activity/Fragment si la posición es válida.
        itemView.setOnClickListener(view -> {
            if (mclicklistener == null) {
                return;
            }
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                mclicklistener.onItemClick(view, position);
            }
        });

        // Reenvía long click para acciones secundarias del item.
        itemView.setOnLongClickListener(view -> {
            if (mclicklistener == null) {
                return false;
            }
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                mclicklistener.onItemLongClick(view, position);
                return true;
            }
            return false;
        });

    }

    public void setearDatosCliente(String id_cliente, String uid_cleinte,
                                   String nombres, String apellidos, String correo,
                                   String dni, String direccion, String telefono){

        // Enlaza y pinta los datos de texto del cliente en el item.

        TextView tvidClienteI, tvuidClienteI, tvNombresI, tvApellidosI, tvCorreoI, tvDniI, tvDireccionI, tvTelefonoI;

        tvidClienteI = mview.findViewById(R.id.tvIdClienteI);
        tvuidClienteI = mview.findViewById(R.id.tvUidClienteI);
        tvNombresI = mview.findViewById(R.id.tvNombresI);
        tvApellidosI = mview.findViewById(R.id.tvApellidosI);
        tvCorreoI = mview.findViewById(R.id.tvCorreoI);
        tvDniI = mview.findViewById(R.id.tvDniI);
        tvDireccionI = mview.findViewById(R.id.tvDireccionI);
        tvTelefonoI = mview.findViewById(R.id.tvTelefonoI);

        tvidClienteI.setText(id_cliente);
        tvuidClienteI.setText(uid_cleinte);
        tvNombresI.setText(nombres);
        tvApellidosI.setText(apellidos);
        tvCorreoI.setText(correo);
        tvDniI.setText(dni);
        tvDireccionI.setText(direccion);
        tvTelefonoI.setText(telefono);
    }

    public void setearFotoCliente(String fotoBase64) {
        // Muestra la foto solo cuando existe Base64 válido.
        ImageView ivClienteFotoI = mview.findViewById(R.id.ivClienteFotoI);

        if (fotoBase64 == null || fotoBase64.trim().isEmpty()) {
            ivClienteFotoI.setImageDrawable(null);
            ivClienteFotoI.setVisibility(View.GONE);
            return;
        }

        try {
            // Decodifica y renderiza la foto guardada como texto Base64.
            byte[] decodedBytes = Base64.decode(fotoBase64, Base64.DEFAULT);
            Bitmap bitmap = decodeScaledBitmap(decodedBytes, MAX_DECODE_SIDE, MAX_DECODE_SIDE);
            if (bitmap != null) {
                ivClienteFotoI.setImageBitmap(bitmap);
                ivClienteFotoI.setVisibility(View.VISIBLE);
            } else {
                ivClienteFotoI.setImageDrawable(null);
                ivClienteFotoI.setVisibility(View.GONE);
            }
        } catch (IllegalArgumentException | OutOfMemoryError e) {
            ivClienteFotoI.setImageDrawable(null);
            ivClienteFotoI.setVisibility(View.GONE);
        }
    }

    private Bitmap decodeScaledBitmap(byte[] data, int reqWidth, int reqHeight) {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, bounds);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = calculateInSampleSize(bounds, reqWidth, reqHeight);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}

