package com.sralberto.petagram;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MascotaAdaptador extends RecyclerView.Adapter<MascotaAdaptador.MascotaViewHolder> {

    private final ArrayList<Mascota> mascotas;

    public MascotaAdaptador(ArrayList<Mascota> mascotas) {
        this.mascotas = mascotas;
    }

    @NonNull
    @Override
    public MascotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mascota, parent, false);
        return new MascotaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull MascotaViewHolder holder, int position) {
        Mascota mascota = mascotas.get(position);

        holder.imgFoto.setImageResource(mascota.getFoto());
        holder.imgFoto.setContentDescription(mascota.getNombre());
        holder.tvNombre.setText(mascota.getNombre());
        holder.tvRaiting.setText(String.valueOf(mascota.getRaiting()));

        holder.btnHueso.setOnClickListener(v -> {
            mascota.darHueso();
            holder.tvRaiting.setText(String.valueOf(mascota.getRaiting()));
        });
    }

    @Override
    public int getItemCount() {
        return mascotas.size();
    }

    public static class MascotaViewHolder extends RecyclerView.ViewHolder {

        final ImageView imgFoto;
        final ImageButton btnHueso;
        final TextView tvNombre;
        final TextView tvRaiting;

        public MascotaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFoto = itemView.findViewById(R.id.img_foto);
            btnHueso = itemView.findViewById(R.id.btn_hueso);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvRaiting = itemView.findViewById(R.id.tv_raiting);
        }
    }
}
