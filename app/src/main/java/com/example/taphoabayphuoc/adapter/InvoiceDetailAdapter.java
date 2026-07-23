package com.example.taphoabayphuoc.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taphoabayphuoc.databinding.ItemInvoiceDetailBinding;
import com.example.taphoabayphuoc.models.InvoiceItemEntity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class InvoiceDetailAdapter extends RecyclerView.Adapter<InvoiceDetailAdapter.ViewHolder> {

    private final List<InvoiceItemEntity> items;

    private final NumberFormat money =
            NumberFormat.getCurrencyInstance(new Locale("vi","VN"));

    public InvoiceDetailAdapter(List<InvoiceItemEntity> items){
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemInvoiceDetailBinding binding =
                ItemInvoiceDetailBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        InvoiceItemEntity item = items.get(position);

        holder.binding.txtName.setText(item.getProductName());

        holder.binding.txtQuantity.setText("x" + item.getQuantity());

        holder.binding.txtPrice.setText(
                money.format(item.getPrice()));

        holder.binding.txtTotal.setText(
                money.format(item.getTotal()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        ItemInvoiceDetailBinding binding;

        ViewHolder(ItemInvoiceDetailBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}