package com.sralberto.petagram;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PaginasAdaptador extends FragmentStateAdapter {

    public static final int PAGINA_LISTA = 0;
    public static final int PAGINA_PERFIL = 1;
    private static final int TOTAL_PAGINAS = 2;

    public PaginasAdaptador(@NonNull FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == PAGINA_PERFIL) {
            return new PerfilMascotaFragment();
        }
        return new ListaMascotasFragment();
    }

    @Override
    public int getItemCount() {
        return TOTAL_PAGINAS;
    }
}
