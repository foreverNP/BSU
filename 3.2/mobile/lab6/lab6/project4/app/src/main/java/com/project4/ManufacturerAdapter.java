package com.project4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class ManufacturerAdapter extends ArrayAdapter<Manufacturer> {
    private List<Manufacturer> manufacturers;
    private Context context;

    public ManufacturerAdapter(@NonNull Context context, int resource, @NonNull List<Manufacturer> objects) {
        super(context, resource, objects);
        this.manufacturers = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.text_spinner_item);
        Manufacturer manufacturer = getItem(position);

        if (manufacturer != null) {
            textView.setText(manufacturer.getName());
        }

        return convertView;
    }
}