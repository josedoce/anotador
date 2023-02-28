package github.josedoce.anotador.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import github.josedoce.anotador.R;
import github.josedoce.anotador.holders.CustomViewHolder;
import github.josedoce.anotador.model.Annotation;

public class AnnotationsAdapter extends RecyclerView.Adapter<CustomViewHolder> {
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