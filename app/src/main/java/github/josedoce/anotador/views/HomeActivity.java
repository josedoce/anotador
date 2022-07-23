package github.josedoce.anotador.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import github.josedoce.anotador.R;
import github.josedoce.anotador.views.fragments.AddFragment;
import github.josedoce.anotador.views.fragments.AnnotationsFragment;
import github.josedoce.anotador.views.fragments.SettingFragment;

public class HomeActivity extends AppCompatActivity {
    private final ViewHolder mViewHolder = new ViewHolder();

    public static class ViewHolder {
        FrameLayout fl_framelayout_home;
        BottomNavigationView bnv_menu_home;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        mViewHolder.fl_framelayout_home = findViewById(R.id.fl_framelayout_home);
        mViewHolder.bnv_menu_home = findViewById(R.id.bnv_menu_home);

        defaultFragment(new AnnotationsFragment(mViewHolder.bnv_menu_home, getSupportFragmentManager()));
        mViewHolder.bnv_menu_home.setOnItemSelectedListener(this::switchFraments);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }


    private void defaultFragment(Fragment fragment){
        fragmentManager(fragment);
    }

    @SuppressLint("NonConstantResourceId")
    private boolean switchFraments(MenuItem item){
        Fragment selectedFragment = null;
        switch (item.getItemId()){
            case R.id.ic_annotations:
                selectedFragment = new AnnotationsFragment(mViewHolder.bnv_menu_home, getSupportFragmentManager());
                break;
            case R.id.ic_add:
                selectedFragment = new AddFragment(mViewHolder.bnv_menu_home);
                break;
            case R.id.ic_setting:
                selectedFragment = new SettingFragment(mViewHolder.bnv_menu_home);
                break;
        }
        fragmentManager(selectedFragment);
        return true;
    }

    private void fragmentManager(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_framelayout_home, fragment)
                .commit();
    }

}
