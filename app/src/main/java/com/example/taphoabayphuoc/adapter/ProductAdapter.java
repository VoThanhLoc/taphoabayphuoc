package com.example.taphoabayphuoc.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.taphoabayphuoc.R;
import com.example.taphoabayphuoc.databinding.ItemProductBinding;
import com.example.taphoabayphuoc.listener.ProductListener;
import com.example.taphoabayphuoc.models.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final List<Product> items;
    private final ProductListener listener;

    public ProductAdapter(List<Product> items, ProductListener listener) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = ItemProductBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setProducts(List<Product> list) {
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductBinding binding;

        ViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Product item) {
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            binding.txtName.setText(item.getName());
            binding.txtBarcode.setText(item.getBarcode());
            binding.txtPrice.setText(format.format(item.getSellPrice()));
            binding.txtQuantity.setText("Tồn: " + item.getQuantity());

            binding.btnEdit.setOnClickListener(v -> listener.onEdit(item));
            binding.btnDelete.setOnClickListener(v -> listener.onDelete(item));

            if (item.getImageUrl() == null || item.getImageUrl().isEmpty()) {
                binding.imgProduct.setImageResource(R.drawable.ic_product);
            } else {
                Glide.with(binding.getRoot())
                        .load(item.getImageUrl())
                        .placeholder(R.drawable.ic_product)
                        .into(binding.imgProduct);
            }
        }
    }
}
