package com.strange.gestion501.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.strange.gestion501.R;

public class ViewHolderCliente extends RecyclerView.ViewHolder {

    View mview;
    private ViewHolderCliente.clickListener mclicklistener;

    public interface clickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(ViewHolderCliente.clickListener clicklistener) {
        mclicklistener = clicklistener;
    }

    public ViewHolderCliente(@NonNull View itemView) {
        super(itemView);
        mview = itemView;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mclicklistener.onItemClick(view, getAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mclicklistener.onItemLongClick(view, getAdapterPosition());
                return true;
            }
        });

    }

    public void setearDatosCliente(Context context, String id_cliente, String uid_cleinte,
                                   String nombres, String apellidos, String correo,
                                   String dni, String direccion, String telefono){

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
}

