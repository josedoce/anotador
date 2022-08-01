package github.josedoce.anotador.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import github.josedoce.anotador.database.DBHelper;
import github.josedoce.anotador.model.Annotation;
import github.josedoce.anotador.model.User;

/**
 *
 * */
public class Dio {
    private final Context context;
    private String FOLDER_NAME = "Anotador";

    public Dio(Context context){
        this.context = context;
    }

    private Dio(Context context, String folderName){
        this.context = context;
        this.FOLDER_NAME = folderName;
    }

    private void alert(String msg){
        Toast.makeText(this.context, msg, Toast.LENGTH_LONG).show();
    }

    private File folder;
    private void makeFolder(){
        //pega o local publico
        //https://stackoverflow.com/questions/55742811/why-saves-donwload-manager-file-to-storage-emulated-0-directory-in-external-stor
        folder = new File(Environment.getExternalStorageDirectory(),FOLDER_NAME);
        if(!folder.exists()) {
            boolean mkdirs = folder.mkdirs();
            if(!mkdirs){
                Toast.makeText(this.context, "Não foi possivel criar pasta.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public File getCurrentFolder(){
        return folder;
    }

    public void dioExport(User user, List<Annotation> annotationList){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new DioExport( this, user, annotationList));
        executor.shutdown();
    }

    public static class DioExport implements Runnable {

        private final User user;
        private final List<Annotation> annotationList;
        private final Dio dio;

        public DioExport(Dio dio, User user, List<Annotation> annotationList){
            this.user = user;
            this.annotationList = annotationList;
            this.dio = dio;
        }

        @Override
        public void run() {
            Looper.prepare();
            StringBuilder sb = new StringBuilder();
            Gson gson = new Gson();
            String[] annotations = new String[annotationList.size()];
            int i = 0;
            for(Annotation anno : annotationList){
                annotations[i] = gson.toJson(anno);
                i++;
            }
            String userText = gson.toJson(user);
            sb.append("{")
                    .append("\"user\":")
                    .append(userText)
                    .append(",")
                    .append("\"annotations\":")
                    .append(gson.toJson(annotations))
                    .append("}");

            dio.save("backup.json",sb.toString());
            dio.alert("Exportação finalizada.");
            Looper.loop();
        }
    }

    public void dioImport(DBHelper db){

    }

    private void save(String filename, String content){
        FileOutputStream fileOutputStream = null;
        makeFolder();
        try {
            //no moto privado, o arquivo fica oculto no sistema de arquivos do usuário
            //fileOutputStream = requireContext().openFileOutput("teste.txt", Context.MODE_APPEND);
            File file = new File(folder.getPath(), filename);
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            this.alert("Não foi salvo.");
        } finally {
            if(fileOutputStream!=null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private StringBuilder read(String filename){
        FileInputStream fileInputStream = null;
        makeFolder();
        try {
            fileInputStream = new FileInputStream(new File(folder.getPath(),filename));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String text;
            while((text = br.readLine()) != null){
                sb.append(text).append("\n");
            }
            alert("Leitura concluida.");
            return sb;
        } catch (IOException e) {
            e.printStackTrace();
            this.alert("Não foi lido.");
        } finally {
            if(fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
