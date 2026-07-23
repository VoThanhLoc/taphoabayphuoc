package com.example.taphoabayphuoc.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taphoabayphuoc.databinding.ItemInvoiceHistoryBinding;
import com.example.taphoabayphuoc.models.InvoiceEntity;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class InvoiceHistoryAdapter
        extends RecyclerView.Adapter<InvoiceHistoryAdapter.ViewHolder> {

    private final List<InvoiceEntity> invoices;
    private final OnInvoiceClick listener;

    private final NumberFormat money =
            NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    private final SimpleDateFormat sdf =
            new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public InvoiceHistoryAdapter(List<InvoiceEntity> invoices,
                                 OnInvoiceClick listener) {

        this.invoices = invoices;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        ItemInvoiceHistoryBinding binding =
                ItemInvoiceHistoryBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        InvoiceEntity invoice = invoices.get(position);

        holder.binding.txtCode.setText(invoice.getCode());

        holder.binding.txtDate.setText(
                sdf.format(invoice.getCreatedDate()));

        holder.binding.txtTotal.setText(
                money.format(invoice.getTotal()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(invoice);
            }
        });
    }

    @Override
    public int getItemCount() {
        return invoices.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ItemInvoiceHistoryBinding binding;

        ViewHolder(ItemInvoiceHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnInvoiceClick {
        void onClick(InvoiceEntity invoice);
    }
}