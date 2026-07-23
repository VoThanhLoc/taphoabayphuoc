package com.example.taphoabayphuoc.activities.invoice;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.taphoabayphuoc.adapter.InvoiceDetailAdapter;
import com.example.taphoabayphuoc.database.DatabaseClient;
import com.example.taphoabayphuoc.databinding.ActivityInvoiceDetailBinding;
import com.example.taphoabayphuoc.models.InvoiceEntity;
import com.example.taphoabayphuoc.models.InvoiceItemEntity;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class InvoiceDetailActivity extends AppCompatActivity {

    private ActivityInvoiceDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding =
                ActivityInvoiceDetailBinding.inflate(
                        getLayoutInflater());

        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int invoiceId =
                getIntent().getIntExtra(
                        "invoiceId",
                        0);

        InvoiceEntity invoice =
                DatabaseClient.getInstance(this)
                        .invoiceDao()
                        .getById(invoiceId);

        List<InvoiceItemEntity> items =
                DatabaseClient.getInstance(this)
                        .invoiceItemDao()
                        .getByInvoiceId(invoiceId);

        binding.txtCode.setText(invoice.getCode());

        binding.txtDate.setText(
                new SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.getDefault())
                        .format(invoice.getCreatedDate()));

        binding.txtTotal.setText(
                NumberFormat
                        .getCurrencyInstance(
                                new Locale("vi","VN"))
                        .format(invoice.getTotal()));

        binding.rvDetail.setLayoutManager(
                new LinearLayoutManager(this));

        binding.rvDetail.setAdapter(
                new InvoiceDetailAdapter(items));

        binding.btnPrint.setOnClickListener(v->{

            // bước sau sẽ in Bluetooth

        });

    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;

    }

}