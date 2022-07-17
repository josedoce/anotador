package github.josedoce.anotador.views.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        AnnotationsAdapter adapter = new AnnotationsAdapter(annotationList);
        mViewHolder.rv_annotations_list.setAdapter(adapter);

        //layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(F.getContext());
        mViewHolder.rv_annotations_list.setLayoutManager(layoutManager);
        mViewHolder.tv_annotation_total.setText(String.format("%d anotações no total", annotationList.size()));
        return F;
    }

    private static class ViewHolder {
        RecyclerView rv_annotations_list;
        TextView tv_annotation_total;
    }

    private static class CustomViewHolder extends RecyclerView.ViewHolder {
        private final TextView
                tv_annotation,
                tv_description,
                tv_email,
                tv_password,
                tv_url,
                tv_date,
                tv_hour
        ;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_annotation = itemView.findViewById(R.id.tv_title);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_email = itemView.findViewById(R.id.tv_email);
            tv_password = itemView.findViewById(R.id.tv_password);
            tv_url = itemView.findViewById(R.id.tv_url);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_hour = itemView.findViewById(R.id.tv_hour);
        }

        public void bindData(Annotation annotation) {
            tv_annotation.setText(annotation.getTitle());
            tv_description.setText(annotation.getDescription());
            tv_email.setText(annotation.getEmail());
            tv_password.setText(annotation.getPassword());
            tv_url.setText(annotation.getUrl());
            tv_date.setText(annotation.getDate()+" - ");
            tv_hour.setText(annotation.getHour());
        }
    }

    private static class AnnotationsAdapter extends RecyclerView.Adapter<CustomViewHolder> {
        private final List<Annotation> annotationList;

        public AnnotationsAdapter(List<Annotation> annotationList){
            this.annotationList = annotationList;
        }

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View viewAnnotations = inflater.inflate(R.layout.row_annotation, parent,false);
            return new CustomViewHolder(viewAnnotations);
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
