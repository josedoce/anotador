package github.josedoce.anotador.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import github.josedoce.anotador.R;

public class SettingAdapter extends ArrayAdapter<ModelConfig> {
    private Context context;
    private List<ModelConfig> configList;

    public SettingAdapter(@NonNull Context context, List<ModelConfig> configList) {
        super(context, R.layout.row_setting, configList);
        this.context = context;
        this.configList = configList;
    }

    @Override
    public int getCount() {
        return configList.size();
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //it's work
        //LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater inflater = LayoutInflater.from(context);//it's to work too
        View v = inflater.inflate(R.layout.row_setting, null, false);

        TextView tv_config_title = v.findViewById(R.id.tv_config_title);
        TextView tv_config_description = v.findViewById(R.id.tv_config_description);
        ModelConfig mc = configList.get(position);

        tv_config_title.setText(mc.getTitle());
        tv_config_description.setText(mc.getDescription());

        return v;
    }
}
