package com.example.taphoabayphuoc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.taphoabayphuoc.models.Product;
import com.example.taphoabayphuoc.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class ProductSearchAdapter extends ArrayAdapter<Product> {
    private List<Product> mObjects = new ArrayList<>();
    private final ProductRepository repository;

    public ProductSearchAdapter(@NonNull Context context, ProductRepository repository) {
        super(context, android.R.layout.simple_dropdown_item_1line);
        this.repository = repository;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Nullable
    @Override
    public Product getItem(int position) {
        if (position >= 0 && position < mObjects.size()) {
            return mObjects.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }
        
        Product product = getItem(position);
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        
        if (product != null) {
            textView.setText(product.getName());
        }
        
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null && constraint.length() > 0) {
                    List<Product> matches = repository.search(constraint.toString());
                    results.values = matches;
                    results.count = matches.size();
                } else {
                    results.values = new ArrayList<Product>();
                    results.count = 0;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.values != null) {
                    mObjects = (List<Product>) results.values;
                    notifyDataSetChanged();
                } else {
                    mObjects = new ArrayList<>();
                    notifyDataSetInvalidated();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                if (resultValue instanceof Product) {
                    return ((Product) resultValue).getName();
                }
                return super.convertResultToString(resultValue);
            }
        };
    }
}
