package com.sralberto.petagram.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sralberto.petagram.R;
import com.sralberto.petagram.modelo.Foto;

import java.util.ArrayList;

/**
 * Cuadrícula de fotos del perfil. Debajo de cada foto van sus likes como huesos.
 */
public class FotoAdaptador extends RecyclerView.Adapter<FotoAdaptador.FotoViewHolder> {

    private final ArrayList<Foto> fotos = new ArrayList<>();

    public void setFotos(ArrayList<Foto> nuevas) {
        fotos.clear();
        fotos.addAll(nuevas);
        notifyDataSetChanged();
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
        holder.tvRaiting.setText(String.valueOf(foto.getLikes()));
        holder.imgFoto.setContentDescription(foto.getTexto());

        Glide.with(holder.itemView)
                .load(foto.getUrlMiniatura())
                .placeholder(R.drawable.placeholder_foto)
                .error(R.drawable.placeholder_foto)
                .into(holder.imgFoto);
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
