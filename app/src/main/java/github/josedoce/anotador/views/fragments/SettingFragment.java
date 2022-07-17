package github.josedoce.anotador.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import github.josedoce.anotador.R;
import github.josedoce.anotador.database.DBHelper;
import github.josedoce.anotador.views.SignUpActivity;

public class SettingFragment extends Fragment {
    public SettingFragment(BottomNavigationView bnv){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View F = inflater.inflate(R.layout.setting_fragment_layout, container, false);

        Button bt_delete_account = F.findViewById(R.id.bt_delete_account);
        bt_delete_account.setOnClickListener((view)->{
            if(getActivity() != null && getContext() != null){
                DBHelper.deleteDatabase(getContext());
                Toast.makeText(getContext(), "Excluído com sucesso.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), SignUpActivity.class);
                //https://stackoverflow.com/questions/6330260/finish-all-previous-activities
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }else{
                Toast.makeText(getContext(), "Não foi possivel excluir dados.", Toast.LENGTH_SHORT).show();
            }

        });
        return F;
    }
}
