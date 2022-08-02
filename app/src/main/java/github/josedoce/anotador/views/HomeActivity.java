package github.josedoce.anotador.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import github.josedoce.anotador.R;
import github.josedoce.anotador.views.fragments.AddFragment;
import github.josedoce.anotador.views.fragments.AnnotationsFragment;
import github.josedoce.anotador.views.fragments.SettingFragment;

public class HomeActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 101;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Acesso ao armazenamento permitida.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Acesso ao armazenamento negada.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //fire blue event
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //https://gist.github.com/Tanapruk/7f11982a0519642c4d73e910f3905ccd
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText || v instanceof Button) {
                Rect outRect = new Rect();

                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    Log.d("focus", "touchevent");
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }


        return super.dispatchTouchEvent(event);
    }
}
