package com.example.taphoabayphuoc.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taphoabayphuoc.R;
import com.example.taphoabayphuoc.databinding.ItemInvoiceBinding;
import com.example.taphoabayphuoc.listener.InvoiceItemListener;
import com.example.taphoabayphuoc.models.InvoiceItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import com.bumptech.glide.Glide;
public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder> {

    private final List<InvoiceItem> items;
    private final InvoiceItemListener listener;

    public InvoiceAdapter(List<InvoiceItem> items,
                          InvoiceItemListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemInvoiceBinding binding = ItemInvoiceBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        InvoiceItem item = items.get(position);

        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemInvoiceBinding binding;

        ViewHolder(ItemInvoiceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(InvoiceItem item) {

            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            binding.txtName.setText(item.getProduct().getName());

            binding.txtBarcode.setText(item.getProduct().getBarcode());

            binding.txtPrice.setText(format.format(item.getPrice()));

            binding.txtQuantity.setText(String.valueOf(item.getQuantity()));

            binding.txtTotal.setText(format.format(item.getTotal()));

            binding.btnAdd.setOnClickListener(v ->
                    listener.onIncrease(item));

            binding.btnMinus.setOnClickListener(v ->
                    listener.onDecrease(item));

            binding.btnDelete.setOnClickListener(v ->
                    listener.onDelete(item));
            if (item.getProduct().getImageUrl() == null ||
                    item.getProduct().getImageUrl().isEmpty()) {

                binding.imgProduct.setImageResource(R.drawable.ic_product);

            } else {

                Glide.with(binding.getRoot())
                        .load(item.getProduct().getImageUrl())
                        .placeholder(R.drawable.ic_product)
                        .into(binding.imgProduct);
            }
        }

    }
}