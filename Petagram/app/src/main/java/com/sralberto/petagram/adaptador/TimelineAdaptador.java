package com.sralberto.petagram.adaptador;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sralberto.petagram.R;
import com.sralberto.petagram.modelo.Foto;
import com.sralberto.petagram.modelo.UsuarioInstagram;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TimelineAdaptador extends RecyclerView.Adapter<TimelineAdaptador.PublicacionViewHolder> {

    public interface AlDarLike {
        void alDarLike(Foto foto);
    }

    private final ArrayList<Foto> fotos = new ArrayList<>();
    private final Set<String> enviando = new HashSet<>();
    private final AlDarLike alDarLike;

    public TimelineAdaptador(AlDarLike alDarLike) {
        this.alDarLike = alDarLike;
    }

    public void setFotos(ArrayList<Foto> nuevas) {
        fotos.clear();
        fotos.addAll(nuevas);
        notifyDataSetChanged();
    }

    /** Desactiva el hueso de una foto mientras su like va en camino. */
    public void setEnviando(Foto foto, boolean enCamino) {
        if (enCamino) {
            enviando.add(foto.getId());
        } else {
            enviando.remove(foto.getId());
        }
        actualizar(foto);
    }

    /** Vuelve a dibujar una foto, por ejemplo cuando cambia su número de likes. */
    public void actualizar(Foto foto) {
        for (int i = 0; i < fotos.size(); i++) {
            if (fotos.get(i).getId().equals(foto.getId())) {
                fotos.set(i, foto);
                notifyItemChanged(i);
                return;
            }
        }
    }

    @NonNull
    @Override
    public PublicacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_publicacion, parent, false);
        return new PublicacionViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicacionViewHolder holder, int position) {
        Foto foto = fotos.get(position);
        UsuarioInstagram usuario = foto.getUsuario();
        Context contexto = holder.itemView.getContext();

        holder.tvUsuario.setText(contexto.getString(R.string.formato_usuario, usuario.getUsername()));
        // created_time viene en segundos y DateUtils trabaja en milisegundos
        holder.tvFecha.setText(DateUtils.getRelativeTimeSpanString(
                foto.getCreadaEn() * 1000L, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
        holder.tvLikes.setText(String.valueOf(foto.getLikes()));
        boolean likeEnCamino = enviando.contains(foto.getId());
        holder.btnHueso.setEnabled(!likeEnCamino);
        holder.btnHueso.setAlpha(likeEnCamino ? 0.4f : 1f);
        holder.btnHueso.setOnClickListener(v -> alDarLike.alDarLike(foto));
        holder.tvTexto.setText(foto.getTexto());
        holder.tvTexto.setVisibility(foto.getTexto().isEmpty() ? View.GONE : View.VISIBLE);
        holder.imgFoto.setContentDescription(
                contexto.getString(R.string.descripcion_foto_de, usuario.getNombreCompleto()));

        Glide.with(holder.itemView)
                .load(foto.getUrlFoto())
                .placeholder(R.drawable.placeholder_foto)
                .error(R.drawable.placeholder_foto)
                .into(holder.imgFoto);

        // CircularImageView dibuja mal las animaciones de Glide, por eso dontAnimate
        Glide.with(holder.itemView)
                .load(usuario.getUrlFotoPerfil())
                .placeholder(R.drawable.placeholder_foto)
                .dontAnimate()
                .into(holder.imgUsuario);
    }

    @Override
    public int getItemCount() {
        return fotos.size();
    }

    public static class PublicacionViewHolder extends RecyclerView.ViewHolder {

        final ImageView imgUsuario;
        final TextView tvUsuario;
        final TextView tvFecha;
        final ImageView imgFoto;
        final ImageButton btnHueso;
        final TextView tvLikes;
        final TextView tvTexto;

        public PublicacionViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUsuario = itemView.findViewById(R.id.img_usuario);
            tvUsuario = itemView.findViewById(R.id.tv_usuario);
            tvFecha = itemView.findViewById(R.id.tv_fecha);
            imgFoto = itemView.findViewById(R.id.img_foto);
            btnHueso = itemView.findViewById(R.id.btn_hueso);
            tvLikes = itemView.findViewById(R.id.tv_likes);
            tvTexto = itemView.findViewById(R.id.tv_texto);
        }
    }
}
