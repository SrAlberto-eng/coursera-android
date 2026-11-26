package com.sralberto.paperrecipes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

/**
 * Tarjetas de la cuadrícula de recetas.
 */
public class RecetaAdaptador extends RecyclerView.Adapter<RecetaAdaptador.RecetaViewHolder> {

    public interface AlTocarReceta {
        void alTocar(int posicion);
    }

    private final List<Receta> recetas;
    private final AlTocarReceta alTocar;
    // En tableta la receta abierta en el panel de detalle lleva borde terracota
    private int seleccionada = RecyclerView.NO_POSITION;

    public RecetaAdaptador(List<Receta> recetas, AlTocarReceta alTocar) {
        this.recetas = recetas;
        this.alTocar = alTocar;
    }

    public void seleccionar(int posicion) {
        int anterior = seleccionada;
        seleccionada = posicion;
        if (anterior != RecyclerView.NO_POSITION) {
            notifyItemChanged(anterior);
        }
        notifyItemChanged(posicion);
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

        boolean elegida = position == seleccionada;
        holder.tarjeta.setStrokeWidth(elegida
                ? holder.itemView.getResources().getDimensionPixelSize(R.dimen.borde_seleccion)
                : 0);
        holder.tarjeta.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primario));

        holder.itemView.setOnClickListener(v -> {
            int actual = holder.getBindingAdapterPosition();
            if (actual != RecyclerView.NO_POSITION) {
                alTocar.alTocar(actual);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recetas.size();
    }

    static class RecetaViewHolder extends RecyclerView.ViewHolder {

        final MaterialCardView tarjeta;
        final TextView tvNombre;
        final TextView tvResumen;

        RecetaViewHolder(@NonNull View itemView) {
            super(itemView);
            tarjeta = (MaterialCardView) itemView;
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvResumen = itemView.findViewById(R.id.tv_resumen);
        }
    }
}
