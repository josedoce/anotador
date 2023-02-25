package github.josedoce.anotador.holders;

import static java.lang.String.format;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import github.josedoce.anotador.R;
import github.josedoce.anotador.database.DBAnnotations;
import github.josedoce.anotador.database.DBHelper;
import github.josedoce.anotador.model.Annotation;
import github.josedoce.anotador.views.DetailActivity;
import github.josedoce.anotador.views.fragments.AddFragment;
import github.josedoce.anotador.views.fragments.AnnotationsFragment;

public class CustomViewHolder extends RecyclerView.ViewHolder {
    private BottomNavigationView bottomNavigationView;
    private DBHelper db;
    private DBAnnotations dbAnnotations;
    private final Context context;
    private Annotation annotation;
    private final FragmentManager fragmentManager;

    private final ImageButton ib_delete, ib_edite;
    private static boolean isPasswordShown = false;
    private final TextView
            tv_annotation,
            tv_description,
            tv_date,
            tv_hour;

    public CustomViewHolder(@NonNull View itemView, Context context, FragmentManager fragmentManager, BottomNavigationView bottomNavigationView) {
        super(itemView);
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.bottomNavigationView = bottomNavigationView;
        this.db = new DBHelper(context);
        this.dbAnnotations = new DBAnnotations(db);
        tv_annotation = itemView.findViewById(R.id.tv_title);
        tv_description = itemView.findViewById(R.id.tv_description);

        tv_date = itemView.findViewById(R.id.tv_date);
        tv_hour = itemView.findViewById(R.id.tv_hour);
        ib_delete = itemView.findViewById(R.id.ib_delete);
        ib_edite = itemView.findViewById(R.id.ib_edite);

        ib_delete.setOnClickListener(this::showDeleteDialogActions);
        ib_edite.setOnClickListener(this::editItem);
        itemView.setOnClickListener((view)->{
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("annotationId", annotation.getId());
            context.startActivity(intent);
        });
    }

    private void editItem(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("status","edit");
        bundle.putString("id", annotation.getId().toString());
        bottomNavigationView.setSelectedItemId(R.id.ic_add);
        AddFragment addFragment = new AddFragment();
        addFragment.setArguments(bundle);
        fragmentManager
                .beginTransaction()
                .replace(R.id.fl_framelayout_home, addFragment)
                .commit();
    }

    private void showDeleteDialogActions(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder
                .setMessage("Deseja mesmo excluir anotação ?")
                .setPositiveButton("sim", (dialog, which) -> {
                    long res = dbAnnotations.delete(annotation.getId());
                    if(res!=0){
                        Toast.makeText(view.getContext(), "Excluido!", Toast.LENGTH_SHORT).show();
                        bottomNavigationView.setSelectedItemId(R.id.ic_annotations);
                    }else{
                        Toast.makeText(view.getContext(), "Não foi excluido!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("não",((dialog, which) -> {

                }));
        builder.show();
    }

    private void showAndHidePassword(ImageButton button, TextView textView){
        if(!isPasswordShown){
            textView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            button.setImageResource(R.drawable.ic_close_eye_24);
            isPasswordShown = true;
        }else{
            textView.setTransformationMethod(PasswordTransformationMethod.getInstance());
            button.setImageResource(R.drawable.ic_open_eye_24);
            isPasswordShown = false;
        }
    }

    public void bindData(Annotation annotation) {
        this.annotation = annotation;
        tv_annotation.setText(annotation.getTitle());
        tv_description.setText(annotation.getDescription());
        tv_date.setText(format("%s - ",annotation.getDate()));
        tv_hour.setText(annotation.getHour());
    }

    private void copyText(TextView textView, Context context){
        String text = textView.getText().toString();
        if(!text.isEmpty()){
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("key", text);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(context, "Copiado!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(context, "Não foi possivel copiar.", Toast.LENGTH_LONG).show();
        }
    }
}