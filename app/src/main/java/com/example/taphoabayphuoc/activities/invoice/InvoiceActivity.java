package com.example.taphoabayphuoc.activities.invoice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.taphoabayphuoc.adapter.InvoiceHistoryAdapter;
import com.example.taphoabayphuoc.database.DatabaseClient;
import com.example.taphoabayphuoc.databinding.ActivityInvoiceBinding;
import com.example.taphoabayphuoc.models.InvoiceEntity;
import com.example.taphoabayphuoc.repository.InvoiceRepository;

import java.util.List;

public class InvoiceActivity extends AppCompatActivity {

    private ActivityInvoiceBinding binding;
    private InvoiceRepository repository;
    private InvoiceHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInvoiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new InvoiceRepository(this);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.rvInvoice.setLayoutManager(new LinearLayoutManager(this));
        binding.swipeRefresh.setOnRefreshListener(this::syncInvoices);

        loadInvoices();
    }

    private void loadInvoices() {
        List<InvoiceEntity> invoices = DatabaseClient.getInstance(this)
                .invoiceDao()
                .getAll();

        adapter = new InvoiceHistoryAdapter(invoices, invoice -> {
            Intent intent = new Intent(this, InvoiceDetailActivity.class);
            intent.putExtra("invoiceId", invoice.getId());
            startActivity(intent);
        });

        binding.rvInvoice.setAdapter(adapter);

        if (invoices.isEmpty()) {
            syncInvoices();
        }
    }

    private void syncInvoices() {
        binding.swipeRefresh.setRefreshing(true);
        repository.syncInvoicesFromFirebase(new InvoiceRepository.SyncCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    binding.swipeRefresh.setRefreshing(false);
                    refreshList();
                    Toast.makeText(InvoiceActivity.this, "Đã cập nhật hóa đơn", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    binding.swipeRefresh.setRefreshing(false);
                    Toast.makeText(InvoiceActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void refreshList() {
        List<InvoiceEntity> invoices = DatabaseClient.getInstance(this)
                .invoiceDao()
                .getAll();
        
        // Note: InvoiceHistoryAdapter doesn't have setInvoices method yet, 
        // I will add it or just re-create adapter.
        adapter = new InvoiceHistoryAdapter(invoices, invoice -> {
            Intent intent = new Intent(this, InvoiceDetailActivity.class);
            intent.putExtra("invoiceId", invoice.getId());
            startActivity(intent);
        });
        binding.rvInvoice.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
