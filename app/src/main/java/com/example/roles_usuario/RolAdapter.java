package com.example.roles_usuario;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RolAdapter extends RecyclerView.Adapter<RolAdapter.RolViewHolder> {

    private final Context context;
    private ArrayList<Rol> listaRoles;
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;

    // Interfaz para manejar el clic normal (Editar)
    public interface OnItemClickListener {
        void onItemClick(Rol rol);
    }

    // Interfaz para manejar el clic largo (Eliminar)
    public interface OnItemLongClickListener {
        void onItemLongClick(Rol rol);
    }

    public RolAdapter(Context context, ArrayList<Rol> listaRoles, OnItemClickListener listener, OnItemLongClickListener longClickListener) {
        this.context = context;
        this.listaRoles = listaRoles;
        this.listener = listener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public RolViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rol, parent, false);
        return new RolViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RolViewHolder holder, int position) {
        Rol rol = listaRoles.get(position);
        holder.bind(rol, listener, longClickListener);
    }

    @Override
    public int getItemCount() {
        return listaRoles.size();
    }

    public void setRoles(ArrayList<Rol> newRoles) {
        this.listaRoles = newRoles;
        notifyDataSetChanged();
    }

    public static class RolViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion;

        public RolViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreRol);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionRol);
        }

        public void bind(final Rol rol, final OnItemClickListener listener, final OnItemLongClickListener longClickListener) {
            tvNombre.setText(rol.getNombre());
            tvDescripcion.setText(rol.getDescripcion());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(rol);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(rol);
                    return true;
                }
                return false;
            });
        }
    }
}
