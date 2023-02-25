package github.josedoce.anotador.views.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import github.josedoce.anotador.R;
import github.josedoce.anotador.adapter.ModelConfig;
import github.josedoce.anotador.adapter.SettingAdapter;
import github.josedoce.anotador.database.DBAnnotations;
import github.josedoce.anotador.database.DBHelper;
import github.josedoce.anotador.database.DBUser;
import github.josedoce.anotador.handler.DialogActions;
import github.josedoce.anotador.model.Annotation;
import github.josedoce.anotador.model.User;
import github.josedoce.anotador.utils.Dio;
import github.josedoce.anotador.views.HomeActivity;
import github.josedoce.anotador.views.SignUpActivity;

public class SettingFragment extends Fragment {
    private DBUser user;
    private DBAnnotations annotations;
    private static final int STORAGE_PERMISSION_CODE_TO_READ_AND_WRITE = 101;
    private List<Runnable> taskToVerifyPermissionList;

    private Handler handler;
    private HomeActivity homeActivity;
    public SettingFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeActivity = (HomeActivity) getActivity();
        DBHelper db = new DBHelper(requireContext());
        user = new DBUser(db);
        annotations = new DBAnnotations(db);
        Dio dio = new Dio(requireContext());
        if(homeActivity != null){
            handler = new Handler();
        }
        taskToVerifyPermissionList = new ArrayList<>();

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
        Runnable taskToVerifyPermission1 = ()-> {
            AtomicInteger executed = new AtomicInteger(0);
            Runnable task1 = taskToVerifyPermissionList.get(0);
            if(executed.get() != 0){
                handler.removeCallbacks(task1);
            }
            if (homeActivity.getWRStatusPermission() && executed.get() == 0) {
                DialogActions  dialogActions = new DialogActions(getContext(), "Aviso!", "Deseja mesmo exportar os dados para o dispositivo?");
                dialogActions.setOnAgree(()->{
                    dio.dioExport(new User(c), annotationList);
                    executed.set(1);
                });
                dialogActions.show();
            } else {
                handler.postDelayed(task1, 1000);
            }
        };
        taskToVerifyPermissionList.add(taskToVerifyPermission1);

        Runnable taskToVerifyPermission2 = () -> {
            AtomicInteger executed2 = new AtomicInteger(0);
            Runnable task2 = taskToVerifyPermissionList.get(1);
            Log.d(SettingFragment.class.getName(), "ue"+homeActivity.getWRStatusPermission());
            if(executed2.get() != 0) {
                handler.removeCallbacks(task2);
            }
            if (homeActivity.getWRStatusPermission() && executed2.get() == 0) {
                DialogActions  dialogActions = new DialogActions(getContext(), "Aviso!", "Deseja mesmo importar os dados do dispositivo?");
                dialogActions.setOnAgree(()->{
                    dio.dioImport(db);
                    executed2.set(1);
                });
                dialogActions.show();
            } else {
                handler.postDelayed(task2, 1000); // Verifica a cada 1 segundo
            }
        };
        taskToVerifyPermissionList.add(taskToVerifyPermission2);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View F = inflater.inflate(R.layout.setting_fragment_layout, container, false);
        ListView lv_configs = F.findViewById(R.id.lv_configs);
        List<ModelConfig> configList = new ArrayList<>();
        configList.add(new ModelConfig("Deletar conta", "Deletará sua conta e todos os dados nela."));
        configList.add(new ModelConfig("Dioguardar", "Salvará sua conta e todos os dados nela em um arquivo de texto (.json)."));
        configList.add(new ModelConfig("Diopegar", "Importará para o app todos os dados salvos no arquivo de texto (.json)."));
        SettingAdapter adapter = new SettingAdapter(requireContext(), configList);
        lv_configs.setAdapter(adapter);

        lv_configs.setOnItemClickListener((adapterView, view, i, l) -> {
            switch (i){
                case 0:
                    if(getActivity() != null && getContext() != null){
                        DialogActions  dialogActions = new DialogActions(getContext(), "Aviso!", "Deseja mesmo excluir dados de usuário e todas as anotações?");
                        dialogActions.setOnAgree(()->{
                            DBHelper.deleteDatabase(getContext());
                            Toast.makeText(getContext(), "Excluído com sucesso.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), SignUpActivity.class);
                            //https://stackoverflow.com/questions/6330260/finish-all-previous-activities
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        });
                        dialogActions.show();
                    }else{
                        Toast.makeText(getContext(), "Não foi possivel excluir dados.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    checkWR();
                    handler.postDelayed(taskToVerifyPermissionList.get(0), 1000);
                    break;
                case 2:
                    checkWR();
                    handler.postDelayed(taskToVerifyPermissionList.get(1), 1000);
                    break;
            }
        });

        return F;
    }

    private void checkWR(){
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE_TO_READ_AND_WRITE);
    }

    public void checkPermission(String permission, int requestCode)
    {
        //check permissions
        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_DENIED) {
            //Toast.makeText(requireContext(), "Permissão não permitida.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(requireActivity(), new String[] { permission }, requestCode);
            homeActivity.setWRStatusPermission(false);
            return;
        }
        homeActivity.setWRStatusPermission(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for(Runnable r : taskToVerifyPermissionList){
            handler.removeCallbacks(r);
        }
    }
}
