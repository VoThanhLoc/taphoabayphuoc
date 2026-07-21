package com.example.taphoabayphuoc.activities.sale;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taphoabayphuoc.adapter.InvoiceAdapter;
import com.example.taphoabayphuoc.databinding.ActivitySaleBinding;
import com.example.taphoabayphuoc.listener.InvoiceItemListener;
import com.example.taphoabayphuoc.models.Invoice;
import com.example.taphoabayphuoc.models.InvoiceItem;
import com.example.taphoabayphuoc.models.Product;
import com.example.taphoabayphuoc.repository.ProductRepository;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Date;
import androidx.recyclerview.widget.LinearLayoutManager;
public class SaleActivity extends AppCompatActivity implements InvoiceItemListener {

    private ActivitySaleBinding binding;
    private Invoice invoice;
    private InvoiceAdapter adapter;
    private ProductRepository productRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySaleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productRepository = new ProductRepository(getApplicationContext());

        // Toolbar
        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Invoice
        invoice = new Invoice("HD001", new Date());

        // Adapter
        adapter = new InvoiceAdapter(invoice.getItems(), this);

        binding.rvInvoice.setLayoutManager(new LinearLayoutManager(this));
        binding.rvInvoice.setAdapter(adapter);

        binding.edtBarcode.requestFocus();

        binding.edtBarcode.setOnEditorActionListener((v, actionId, event) -> {

            String barcode = binding.edtBarcode.getText().toString().trim();

            if (!barcode.isEmpty()) {

                addProduct(barcode);

                binding.edtBarcode.setText("");
                binding.edtBarcode.requestFocus();

            }

            return true;
        });

        updateTotal();
    }

    private void addProduct(String barcode) {

        Product product = productRepository.findByBarcode(barcode);

        if (product == null) {
            Toast.makeText(this,
                    "Không tìm thấy sản phẩm",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        for (InvoiceItem item : invoice.getItems()) {

            if (item.getProduct().getBarcode().equals(barcode)) {

                item.setQuantity(item.getQuantity() + 1);

                invoice.calculateTotal();

                adapter.notifyDataSetChanged();

                updateTotal();

                return;
            }

        }

        invoice.getItems().add(
                new InvoiceItem(
                        product,
                        1,
                        product.getSellPrice()));

        invoice.calculateTotal();

        adapter.notifyDataSetChanged();

        updateTotal();
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    private void handleAction(InvoiceItem item) {

    }

    @Override
    public void onIncrease(InvoiceItem item) {
        item.setQuantity(item.getQuantity() + 1);

        invoice.calculateTotal();

        adapter.notifyDataSetChanged();

        updateTotal();
    }

    @Override
    public void onDecrease(InvoiceItem item) {
        if (item.getQuantity() > 1) {

            item.setQuantity(item.getQuantity() - 1);

        } else {

            invoice.getItems().remove(item);

        }

        invoice.calculateTotal();

        adapter.notifyDataSetChanged();

        updateTotal();
    }

    @Override
    public void onDelete(InvoiceItem item) {
        invoice.getItems().remove(item);

        invoice.calculateTotal();

        adapter.notifyDataSetChanged();

        updateTotal();
    }

    private void updateTotal() {

        invoice.calculateTotal();

        NumberFormat format =
                NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        binding.txtTotal.setText(format.format(invoice.getTotal()));
    }
}