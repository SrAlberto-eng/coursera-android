package com.sralberto.paperrecipes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Detalle de una receta. En celular vive dentro de DetalleActivity y en tableta en el panel derecho.
 */
public class DetalleFragment extends Fragment {

    private static final String ARG_POSICION = "posicion";
    private static final String ARG_EN_PANEL = "en_panel";

    public static DetalleFragment nuevo(int posicion, boolean enPanel) {
        Bundle argumentos = new Bundle();
        argumentos.putInt(ARG_POSICION, posicion);
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
        int posicion = requireArguments().getInt(ARG_POSICION);
        boolean enPanel = requireArguments().getBoolean(ARG_EN_PANEL);
        Receta receta = Recetas.LISTA.get(posicion);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(receta.getNombre());
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
                intent.putExtra(RegistroActivity.EXTRA_POSICION, posicion);
                startActivity(intent);
                return true;
            }
            if (item.getItemId() == R.id.accion_borrar) {
                confirmarBorrado(receta, enPanel);
                return true;
            }
            return false;
        });

        TextView tvDescripcion = view.findViewById(R.id.tv_descripcion);
        tvDescripcion.setText(receta.getDescripcion());
        TextView tvRinde = view.findViewById(R.id.tv_rinde);
        tvRinde.setText(getResources().getQuantityString(R.plurals.rinde_porciones,
                receta.getPorciones(), receta.getPorciones()));

        view.findViewById(R.id.btn_preparar).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PrepararActivity.class);
            intent.putExtra(PrepararActivity.EXTRA_POSICION, posicion);
            startActivity(intent);
        });

        VistasReceta.llenarIngredientes(view.findViewById(R.id.lista_ingredientes),
                receta.getIngredientes(), 1, VistasReceta.SIN_ANCLA);
        VistasReceta.llenarPasos(view.findViewById(R.id.lista_pasos), receta.getPasos());
    }

    /** Solo muestra la confirmación. Borrar de verdad llega con la base de datos. */
    private void confirmarBorrado(Receta receta, boolean enPanel) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.borrar_titulo)
                .setMessage(getString(R.string.borrar_mensaje, receta.getNombre()))
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.borrar, (dialogo, boton) -> {
                    if (!enPanel) {
                        requireActivity().finish();
                    }
                })
                .show();
    }
}
