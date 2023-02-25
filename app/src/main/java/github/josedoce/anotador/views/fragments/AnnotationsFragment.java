package github.josedoce.anotador.views.fragments;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import github.josedoce.anotador.adapter.AnnotationsAdapter;
import github.josedoce.anotador.annotations.Senhador;
import github.josedoce.anotador.context.AnotadorContext;
import github.josedoce.anotador.database.DBAnnotations;
import github.josedoce.anotador.database.DBHelper;
import github.josedoce.anotador.model.Annotation;
import github.josedoce.anotador.views.HomeActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import static java.lang.String.format;
import java.util.ArrayList;
import java.util.List;

public class AnnotationsFragment extends Fragment {
    private FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView rv_annotations_list;
    private TextView tv_annotation_total;
    private ImageButton ib_search_btn;
    private EditText et_search;
    private AnnotationsAdapter adapter;
    private final List<Annotation> annotationListOriginalCopy = new ArrayList<>();
    private List<Annotation> annotationList;
    private DBHelper db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HomeActivity homeActivity = (HomeActivity) getActivity();
        if(homeActivity != null){
            this.bottomNavigationView = homeActivity.getBottomNavigationView();
            this.fragmentManager = homeActivity.getSupportFragmentManager();
        }
    }

    @SuppressLint({"DefaultLocale"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View F = inflater.inflate(R.layout.annotations_fragment_layout, container, false);
        rv_annotations_list = F.findViewById(R.id.rv_annotations_list);
        tv_annotation_total = F.findViewById(R.id.tv_annotation_total);
        ib_search_btn = F.findViewById(R.id.ib_search_btn);
        et_search = F.findViewById(R.id.et_search);

        annotationList = new ArrayList<>();


        db = new DBHelper(getContext());
        DBAnnotations dbAnnotations = new DBAnnotations(db);

        Cursor cursor = dbAnnotations.selectAll();
        cursor.moveToFirst();
        if(cursor.getCount() > 0){

            Context context = getActivity();
            AnotadorContext anotadorContext = null;
            if(context != null){
                anotadorContext = (AnotadorContext) context.getApplicationContext();
            }
            do {
                Annotation annotation = new Annotation(cursor);
                if(anotadorContext != null){
                    Senhador.createDecryptedModel(anotadorContext.getUser(), annotation);
                }
                annotationList.add(annotation);
            }while(cursor.moveToNext());
        }
        annotationListOriginalCopy.addAll(annotationList);


        //adapter
        adapter = new AnnotationsAdapter(annotationList, getContext(), fragmentManager, bottomNavigationView);
        rv_annotations_list.setAdapter(adapter);

        //layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(F.getContext());
        rv_annotations_list.setLayoutManager(layoutManager);
        updateTotal();

        //search
        et_search.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus && et_search.getText().toString().isEmpty()){
                ib_search_btn.setVisibility(View.GONE);
            }else{
                ib_search_btn.setVisibility(View.VISIBLE);
            }
        });
        et_search.addTextChangedListener(new CustomTextWatcher(this));

        ib_search_btn.setOnClickListener((view)->{
            et_search.getText().clear();
            search();
        });
        return F;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void search(){
        String search = et_search.getText().toString();

        if(search.isEmpty()){
            annotationList.clear();
            annotationList.addAll(annotationListOriginalCopy);
            adapter.notifyDataSetChanged();
            updateTotal();
            return;
        }
        int annotationsSize = annotationListOriginalCopy.size();
        annotationList.clear();
        if(annotationsSize > 0){
            for(int i = 0; i < annotationsSize; i++){
                if(annotationListOriginalCopy.get(i).getTitle().contains(search)){
                    annotationList.add(annotationListOriginalCopy.get(i));
                }
            }

            //https://www.tutorialspoint.com/how-to-update-recyclerview-adapter-data-in-android
            adapter.notifyDataSetChanged();
            updateTotal();
        }
    }

    @SuppressLint("DefaultLocale")
    private void updateTotal(){
        tv_annotation_total.setText(format("%d anotações no total", annotationList.size()));
    }

    private static class CustomTextWatcher implements TextWatcher {

        private AnnotationsFragment annotationsFragment;
        public CustomTextWatcher(AnnotationsFragment annotationsFragment){
            this.annotationsFragment = annotationsFragment;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            annotationsFragment.search();
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    /*
    private static class CustomViewHolder extends RecyclerView.ViewHolder {
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
                tv_hour
        ;

        public CustomViewHolder(@NonNull View itemView, Context context, FragmentManager fragmentManager) {
            super(itemView);
            this.context = context;
            this.fragmentManager = fragmentManager;
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
        }

        private void editItem(View view) {
            Bundle bundle = new Bundle();
            bundle.putString("status","edit");
            bundle.putString("id", annotation.getId().toString());
            bottomNavigationView.setSelectedItemId(R.id.ic_add);
            AddFragment addFragment = new AddFragment(bottomNavigationView);
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
                            AnnotationsFragment.bottomNavigationView.setSelectedItemId(R.id.ic_annotations);
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

            //tv_email.setText(annotation.getEmail());
            //tv_password.setText(annotation.getPassword());
            //tv_url.setText(annotation.getUrl());

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
        private final FragmentManager fragmentManager;

        public AnnotationsAdapter(List<Annotation> annotationList, Context ctx, FragmentManager fragmentManager){
            this.annotationList = annotationList;
            this.context = ctx;
            this.fragmentManager = fragmentManager;
        }

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();

            LayoutInflater inflater = LayoutInflater.from(context);
            View viewAnnotations = inflater.inflate(R.layout.row_annotation, parent,false);
            return new CustomViewHolder(viewAnnotations, context, fragmentManager);
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

     */
}
