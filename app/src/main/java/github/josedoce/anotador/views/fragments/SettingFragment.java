package github.josedoce.anotador.views.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import github.josedoce.anotador.R;
import github.josedoce.anotador.adapter.ModelConfig;
import github.josedoce.anotador.adapter.SettingAdapter;
import github.josedoce.anotador.database.DBAnnotations;
import github.josedoce.anotador.database.DBHelper;
import github.josedoce.anotador.database.DBUser;
import github.josedoce.anotador.model.Annotation;
import github.josedoce.anotador.model.User;
import github.josedoce.anotador.utils.Dio;
import github.josedoce.anotador.views.SignUpActivity;

public class SettingFragment extends Fragment {
    private DBUser user;
    private DBAnnotations annotations;
    private static final int STORAGE_PERMISSION_CODE_TO_READ_AND_WRITE = 101;

    public SettingFragment(BottomNavigationView bnv){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View F = inflater.inflate(R.layout.setting_fragment_layout, container, false);
        ListView lv_configs = F.findViewById(R.id.lv_configs);
        DBHelper db = new DBHelper(requireContext());
        user = new DBUser(db);
        annotations = new DBAnnotations(db);
        List<ModelConfig> configList = new ArrayList<>();
        configList.add(new ModelConfig("Deletar conta", "Deletará sua conta e todos os dados nela."));
        configList.add(new ModelConfig("Dioguardar", "Salvará sua conta e todos os dados nela em um arquivo de texto (.json)."));
        configList.add(new ModelConfig("Diopegar", "Importará para o app todos os dados salvos no arquivo de texto (.json)."));
        SettingAdapter adapter = new SettingAdapter(requireContext(), configList);
        lv_configs.setAdapter(adapter);
        Dio dio = new Dio(requireContext());
        lv_configs.setOnItemClickListener((adapterView, view, i, l) -> {
            switch (i){
                case 0:
                    if(getActivity() != null && getContext() != null){
                        showDialogActions("Deseja mesmo excluir dados de usuário e todas as anotações?", new DialogActions() {
                            @Override
                            public void onConfirm() {
                                DBHelper.deleteDatabase(getContext());
                                Toast.makeText(getContext(), "Excluído com sucesso.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getContext(), SignUpActivity.class);
                                //https://stackoverflow.com/questions/6330260/finish-all-previous-activities
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                    }else{
                        Toast.makeText(getContext(), "Não foi possivel excluir dados.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    Cursor c = user.selectAll();
                    c.moveToFirst();
                    List<Annotation> annotationList = new ArrayList<>();
                    Cursor cAnnotations = annotations.selectAll();
                    cAnnotations.moveToFirst();
                    if(cAnnotations.getCount() > 0){
                        do {
                            Annotation annotation = new Annotation(cAnnotations);
                            annotationList.add(annotation);
                        }while(cAnnotations.moveToNext());
                    }
                    checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE_TO_READ_AND_WRITE);
                    showDialogActions("Deseja mesmo exportar os dados para o dispositivo?", new DialogActions() {
                        @Override
                        public void onConfirm() {
                            dio.dioExport(new User(c), annotationList);
                        }
                    });

                    break;
                case 2:
                    checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE_TO_READ_AND_WRITE);
                    showDialogActions("Deseja mesmo importar os dados do dispositivo?", new DialogActions() {
                        @Override
                        public void onConfirm() {
                            dio.dioImport(db);
                        }
                    });
                    break;
            }
        });

        return F;
    }

    private interface DialogActions {
        void onConfirm();
    }

    private void showDialogActions(String msg, DialogActions dialogActions){
        Context context = requireContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setMessage(msg)
                .setPositiveButton("sim", (dialog, which) -> {
                   dialogActions.onConfirm();
                })
                .setNegativeButton("não",((dialog, which) -> {}));
        builder.show();
    }

    public void checkPermission(String permission, int requestCode)
    {
        //check permissions
        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(requireContext(), "Permissão não permitida.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(requireActivity(), new String[] { permission }, requestCode);
        }
    }
}
