package com.sralberto.paperrecipes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Detalle de una receta. En celular vive dentro de DetalleActivity y en tableta en el panel derecho.
 * Lee la receta de la base de datos cada vez que se muestra, así refleja lo que se editó.
 */
public class DetalleFragment extends Fragment {

    /** Lo implementa MainActivity para mostrar otra receta en el panel después de borrar. */
    public interface AlBorrarReceta {
        void alBorrarReceta();
    }

    private static final String ARG_ID_RECETA = "id_receta";
    private static final String ARG_EN_PANEL = "en_panel";

    private long idReceta;
    private boolean enPanel;
    private Receta receta;

    private MaterialToolbar toolbar;
    private ImageView ivFoto;
    private TextView tvDescripcion;
    private TextView tvRinde;
    private TextView tvTituloPasos;
    private LinearLayout listaIngredientes;
    private LinearLayout listaPasos;

    public static DetalleFragment nuevo(long idReceta, boolean enPanel) {
        Bundle argumentos = new Bundle();
        argumentos.putLong(ARG_ID_RECETA, idReceta);
        argumentos.putBoolean(ARG_EN_PANEL, enPanel);
        DetalleFragment fragmento = new DetalleFragment();
        fragmento.setArguments(argumentos);
        return fragmento;
    }

    public DetalleFragment() {
        super(R.layout.fragment_detalle);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idReceta = requireArguments().getLong(ARG_ID_RECETA);
        enPanel = requireArguments().getBoolean(ARG_EN_PANEL);

        toolbar = view.findViewById(R.id.toolbar);
        ivFoto = view.findViewById(R.id.iv_foto);
        tvDescripcion = view.findViewById(R.id.tv_descripcion);
        tvRinde = view.findViewById(R.id.tv_rinde);
        tvTituloPasos = view.findViewById(R.id.tv_titulo_pasos);
        listaIngredientes = view.findViewById(R.id.lista_ingredientes);
        listaPasos = view.findViewById(R.id.lista_pasos);

        if (enPanel) {
            // En tableta la lista está a un lado, así que no hace falta regresar
            toolbar.setNavigationIcon(null);
        } else {
            toolbar.setNavigationOnClickListener(v ->
                    requireActivity().getOnBackPressedDispatcher().onBackPressed());
        }
        toolbar.inflateMenu(R.menu.menu_detalle);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.accion_editar) {
                Intent intent = new Intent(requireContext(), RegistroActivity.class);
                intent.putExtra(RegistroActivity.EXTRA_ID_RECETA, idReceta);
                startActivity(intent);
                return true;
            }
            if (item.getItemId() == R.id.accion_borrar) {
                confirmarBorrado();
                return true;
            }
            return false;
        });

        view.findViewById(R.id.btn_preparar).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PrepararActivity.class);
            intent.putExtra(PrepararActivity.EXTRA_ID_RECETA, idReceta);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        receta = BaseDatosRecetas.obtener(requireContext()).obtenerReceta(idReceta);
        if (receta == null) {
            // La receta ya no existe, por ejemplo si se borró desde otra pantalla
            if (!enPanel) {
                requireActivity().finish();
            }
            return;
        }
        mostrar();
    }

    private void mostrar() {
        toolbar.setTitle(receta.getNombre());
        Fotos.mostrar(ivFoto, receta.getFoto(),
                getResources().getDimension(R.dimen.radio_tarjeta),
                getResources().getDimension(R.dimen.radio_tarjeta));

        tvDescripcion.setText(receta.getDescripcion());
        tvDescripcion.setVisibility(receta.getDescripcion().isEmpty() ? View.GONE : View.VISIBLE);
        tvRinde.setText(getResources().getQuantityString(R.plurals.rinde_porciones,
                receta.getPorciones(), receta.getPorciones()));

        VistasReceta.llenarIngredientes(listaIngredientes, receta.getIngredientes(),
                1, VistasReceta.SIN_ANCLA);
        VistasReceta.llenarPasos(listaPasos, receta.getPasos());
        int visibilidadPasos = receta.getPasos().isEmpty() ? View.GONE : View.VISIBLE;
        tvTituloPasos.setVisibility(visibilidadPasos);
        listaPasos.setVisibility(visibilidadPasos);
    }

    private void confirmarBorrado() {
        if (receta == null) {
            return;
        }
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.borrar_titulo)
                .setMessage(getString(R.string.borrar_mensaje, receta.getNombre()))
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.borrar, (dialogo, boton) -> borrar())
                .show();
    }

    private void borrar() {
        BaseDatosRecetas.obtener(requireContext()).borrarReceta(receta.getId());
        Fotos.borrar(receta.getFoto());
        Toast.makeText(requireContext(), R.string.receta_borrada, Toast.LENGTH_SHORT).show();
        if (enPanel) {
            ((AlBorrarReceta) requireActivity()).alBorrarReceta();
        } else {
            requireActivity().finish();
        }
    }
}
