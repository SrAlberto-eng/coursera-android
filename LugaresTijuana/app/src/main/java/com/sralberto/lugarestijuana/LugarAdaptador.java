package com.sralberto.lugarestijuana;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LugarAdaptador extends RecyclerView.Adapter<LugarAdaptador.LugarViewHolder> {

    public interface AlSeleccionarLugar {
        void alSeleccionar(int posicion);
    }

    private final List<Lugar> lugares;
    private final AlSeleccionarLugar alSeleccionar;

    public LugarAdaptador(List<Lugar> lugares, AlSeleccionarLugar alSeleccionar) {
        this.lugares = lugares;
        this.alSeleccionar = alSeleccionar;
    }

    @NonNull
    @Override
    public LugarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lugar, parent, false);
        return new LugarViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull LugarViewHolder holder, int position) {
        Lugar lugar = lugares.get(position);
        holder.imgLugar.setImageResource(lugar.getIlustracion());
        holder.tvNombre.setText(lugar.getNombre());
        holder.itemView.setOnClickListener(v ->
                alSeleccionar.alSeleccionar(holder.getBindingAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return lugares.size();
    }

    public static class LugarViewHolder extends RecyclerView.ViewHolder {

        final ImageView imgLugar;
        final TextView tvNombre;

        public LugarViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLugar = itemView.findViewById(R.id.img_lugar);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
        }
    }
}
