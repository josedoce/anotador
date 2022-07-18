package github.josedoce.anotador.views.fragments;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import github.josedoce.anotador.R;
import github.josedoce.anotador.database.DBAnnotations;
import github.josedoce.anotador.database.DBHelper;
import github.josedoce.anotador.model.Annotation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import static java.lang.String.format;
import java.util.ArrayList;
import java.util.List;

public class AnnotationsFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private DBHelper db;
    private DBAnnotations dbAnnotations;
    private final ViewHolder mViewHolder = new ViewHolder();

    public AnnotationsFragment(BottomNavigationView bnv){
        this.bottomNavigationView = bnv;
    }

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View F = inflater.inflate(R.layout.annotations_fragment_layout, container, false);
        mViewHolder.rv_annotations_list = F.findViewById(R.id.rv_annotations_list);
        mViewHolder.tv_annotation_total = F.findViewById(R.id.tv_annotation_total);
        List<Annotation> annotationList = new ArrayList<>();

        db = new DBHelper(getContext());
        dbAnnotations = new DBAnnotations(db);

        Cursor cursor = dbAnnotations.selectAll();
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            do {
                Annotation annotation = new Annotation(cursor);
                annotationList.add(annotation);
            }while(cursor.moveToNext());
        }

        //adapter
        AnnotationsAdapter adapter = new AnnotationsAdapter(annotationList, getContext());
        mViewHolder.rv_annotations_list.setAdapter(adapter);

        //layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(F.getContext());
        mViewHolder.rv_annotations_list.setLayoutManager(layoutManager);
        mViewHolder.tv_annotation_total.setText(format("%d anotações no total", annotationList.size()));
        return F;
    }

    private static class ViewHolder {
        RecyclerView rv_annotations_list;
        TextView tv_annotation_total;
    }

    private static class CustomViewHolder extends RecyclerView.ViewHolder {
        private final EditText tv_password;
        private final ImageButton bt_show;
        private static boolean isPasswordShown = false;
        private final TextView
                tv_annotation,
                tv_description,
                tv_email,
                tv_url,
                tv_date,
                tv_hour
        ;

        public CustomViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            tv_annotation = itemView.findViewById(R.id.tv_title);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_email = itemView.findViewById(R.id.tv_email);
            tv_password = itemView.findViewById(R.id.tv_password);
            tv_url = itemView.findViewById(R.id.tv_url);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_hour = itemView.findViewById(R.id.tv_hour);
            bt_show = itemView.findViewById(R.id.bt_show);

            tv_email.setOnClickListener(view->copyText(tv_email, context));
            tv_password.setOnClickListener(view->copyText(tv_password, context));
            tv_url.setOnClickListener(view->copyText(tv_url, context));

            bt_show.setOnClickListener((view)->{
                if(!isPasswordShown){
                    tv_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    bt_show.setImageResource(R.drawable.ic_close_eye_24);
                    isPasswordShown = true;
                }else{
                    tv_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    bt_show.setImageResource(R.drawable.ic_open_eye_24);
                    isPasswordShown = false;
                }
            });

        }

        public void bindData(Annotation annotation) {
            tv_annotation.setText(annotation.getTitle());
            tv_description.setText(annotation.getDescription());

            tv_email.setText(annotation.getEmail());
            tv_password.setText(annotation.getPassword());
            tv_url.setText(annotation.getUrl());

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

    private static class AnnotationsAdapter extends RecyclerView.Adapter<CustomViewHolder> {
        private final List<Annotation> annotationList;
        private final Context context;

        public AnnotationsAdapter(List<Annotation> annotationList, Context ctx){
            this.annotationList = annotationList;
            this.context = ctx;
        }

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();

            LayoutInflater inflater = LayoutInflater.from(context);
            View viewAnnotations = inflater.inflate(R.layout.row_annotation, parent,false);
            return new CustomViewHolder(viewAnnotations, context);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
            Annotation annotation = annotationList.get(position);
            holder.bindData(annotation);
        }

        @Override
        public int getItemCount() {
            return annotationList.size();
        }
    }
}
