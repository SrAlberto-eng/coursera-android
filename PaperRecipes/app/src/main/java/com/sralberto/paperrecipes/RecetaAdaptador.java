package com.sralberto.paperrecipes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

/**
 * Tarjetas de la cuadrícula de recetas.
 */
public class RecetaAdaptador extends RecyclerView.Adapter<RecetaAdaptador.RecetaViewHolder> {

    public interface AlTocarReceta {
        void alTocar(long idReceta);
    }

    public static final long SIN_SELECCION = -1;

    private final List<Receta> recetas = new ArrayList<>();
    private final AlTocarReceta alTocar;
    // En tableta la receta abierta en el panel de detalle lleva borde terracota
    private long seleccionada = SIN_SELECCION;

    public RecetaAdaptador(AlTocarReceta alTocar) {
        this.alTocar = alTocar;
    }

    public void setRecetas(List<Receta> nuevas) {
        recetas.clear();
        recetas.addAll(nuevas);
        notifyDataSetChanged();
    }

    public void seleccionar(long idReceta) {
        seleccionada = idReceta;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receta, parent, false);
        return new RecetaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull RecetaViewHolder holder, int position) {
        Receta receta = recetas.get(position);
        holder.tvNombre.setText(receta.getNombre());
        holder.tvResumen.setText(holder.itemView.getContext().getString(R.string.resumen_tarjeta,
                receta.getIngredientes().size(), receta.getPorciones()));

        float radio = holder.itemView.getResources().getDimension(R.dimen.radio_tarjeta);
        Fotos.mostrar(holder.ivFoto, receta.getFoto(), radio, 0);

        boolean elegida = receta.getId() == seleccionada;
        holder.tarjeta.setStrokeWidth(elegida
                ? holder.itemView.getResources().getDimensionPixelSize(R.dimen.borde_seleccion)
                : 0);
        holder.tarjeta.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primario));

        holder.itemView.setOnClickListener(v -> alTocar.alTocar(receta.getId()));
    }

    @Override
    public int getItemCount() {
        return recetas.size();
    }

    static class RecetaViewHolder extends RecyclerView.ViewHolder {

        final MaterialCardView tarjeta;
        final ImageView ivFoto;
        final TextView tvNombre;
        final TextView tvResumen;

        RecetaViewHolder(@NonNull View itemView) {
            super(itemView);
            tarjeta = (MaterialCardView) itemView;
            ivFoto = itemView.findViewById(R.id.iv_foto);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvResumen = itemView.findViewById(R.id.tv_resumen);
        }
    }
}
