package com.sralberto.petagram;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FotoAdaptador extends RecyclerView.Adapter<FotoAdaptador.FotoViewHolder> {

    private final ArrayList<Foto> fotos;

    public FotoAdaptador(ArrayList<Foto> fotos) {
        this.fotos = fotos;
    }

    @NonNull
    @Override
    public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_foto, parent, false);
        return new FotoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
        Foto foto = fotos.get(position);
        holder.imgFoto.setImageResource(foto.getImagen());
        holder.tvRaiting.setText(String.valueOf(foto.getRaiting()));
    }

    @Override
    public int getItemCount() {
        return fotos.size();
    }

    public static class FotoViewHolder extends RecyclerView.ViewHolder {

        final ImageView imgFoto;
        final TextView tvRaiting;

        public FotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFoto = itemView.findViewById(R.id.img_foto);
            tvRaiting = itemView.findViewById(R.id.tv_raiting);
        }
    }
}
