package com.example.taphoabayphuoc.activities.invoice;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.taphoabayphuoc.adapter.InvoiceHistoryAdapter;
import com.example.taphoabayphuoc.database.DatabaseClient;
import com.example.taphoabayphuoc.databinding.ActivityInvoiceBinding;
import com.example.taphoabayphuoc.models.InvoiceEntity;

import java.util.List;

public class InvoiceActivity extends AppCompatActivity {

    private ActivityInvoiceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInvoiceBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        if(getSupportActionBar()!=null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        List<InvoiceEntity> invoices =
                DatabaseClient.getInstance(this)
                        .invoiceDao()
                        .getAll();

        InvoiceHistoryAdapter adapter =
                new InvoiceHistoryAdapter(
                        invoices,
                        invoice -> {

                            Intent intent =
                                    new Intent(
                                            this,
                                            InvoiceDetailActivity.class);

                            intent.putExtra(
                                    "invoiceId",
                                    invoice.getId());

                            startActivity(intent);

                        });

        binding.rvInvoice.setLayoutManager(
                new LinearLayoutManager(this));

        binding.rvInvoice.setAdapter(adapter);

    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;

    }

}