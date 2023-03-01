package github.josedoce.anotador.holders;

import static java.lang.String.format;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import github.josedoce.anotador.R;
import github.josedoce.anotador.model.Annotation;
import github.josedoce.anotador.views.DetailActivity;

public class CustomViewHolder extends RecyclerView.ViewHolder {
    private Annotation annotation;
    private final TextView
            tv_annotation,
            tv_description,
            tv_date;


    public CustomViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        tv_annotation = itemView.findViewById(R.id.tv_title);
        tv_description = itemView.findViewById(R.id.tv_description);
        tv_date = itemView.findViewById(R.id.tv_date);

        handlerItemPressed(context);
    }

    public void bindData(Annotation annotation) {
        this.annotation = annotation;
        tv_annotation.setText(annotation.getTitle());
        tv_description.setText(annotation.getDescription());
        tv_date.setText(format("%s",annotation.getDate()));

    }

    private void handlerItemPressed(Context context){
        itemView.setOnClickListener((view)->{
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("annotationId", annotation.getId());
            context.startActivity(intent);
        });
    }

}