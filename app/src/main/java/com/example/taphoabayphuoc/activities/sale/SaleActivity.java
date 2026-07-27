package com.example.taphoabayphuoc.activities.sale;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.taphoabayphuoc.adapter.InvoiceAdapter;
import com.example.taphoabayphuoc.adapter.ProductSearchAdapter;
import com.example.taphoabayphuoc.databinding.ActivitySaleBinding;
import com.example.taphoabayphuoc.listener.InvoiceItemListener;
import com.example.taphoabayphuoc.models.Invoice;
import com.example.taphoabayphuoc.models.InvoiceItem;
import com.example.taphoabayphuoc.models.Product;
import com.example.taphoabayphuoc.models.SettingEntity;
import com.example.taphoabayphuoc.printer.PrintService;
import com.example.taphoabayphuoc.repository.InvoiceRepository;
import com.example.taphoabayphuoc.repository.ProductRepository;
import com.example.taphoabayphuoc.repository.SettingRepository;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SaleActivity extends AppCompatActivity implements InvoiceItemListener {

    private ActivitySaleBinding binding;
    private Invoice invoice;
    private InvoiceAdapter adapter;
    private ProductRepository productRepository;
    private ProductSearchAdapter productSearchAdapter;
    private InvoiceRepository invoiceRepository;
    private ProcessCameraProvider cameraProvider;
    private BarcodeScanner barcodeScanner;
    private ExecutorService cameraExecutor;
    private boolean scanned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySaleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productRepository = new ProductRepository(getApplicationContext());
        invoiceRepository = new InvoiceRepository(this);
        cameraExecutor = Executors.newSingleThreadExecutor();
        barcodeScanner = BarcodeScanning.getClient();
        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        invoice = new Invoice("HD" + System.currentTimeMillis(), new Date());
        adapter = new InvoiceAdapter(invoice.getItems(), this);
        binding.rvInvoice.setLayoutManager(new LinearLayoutManager(this));
        binding.rvInvoice.setAdapter(adapter);
        
        productSearchAdapter = new ProductSearchAdapter(this, productRepository);
        binding.edtSearch.setAdapter(productSearchAdapter);
        binding.edtSearch.setThreshold(1);

        initEvent();
        updateTotal();

        // Show keyboard automatically
        binding.edtSearch.postDelayed(() -> {
            binding.edtSearch.requestFocus();
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(binding.edtSearch, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
            }
        }, 200);
    }

    private void initEvent() {
        binding.layoutSearch.setStartIconOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                requestCameraPermission.launch(Manifest.permission.CAMERA);
            }
        });

        binding.edtSearch.setOnItemClickListener((parent, view, position, id) -> {
            Product selectedProduct = productSearchAdapter.getItem(position);
            if (selectedProduct != null) {
                addProduct(selectedProduct);
            }
            binding.edtSearch.setText("");
        });

        binding.btnClear.setOnClickListener(v -> {
            invoice.getItems().clear();
            adapter.notifyDataSetChanged();
            updateTotal();
        });

        binding.btnPayment.setOnClickListener(v -> {
            if (invoice.getItems().isEmpty()) {
                Toast.makeText(this, "Hóa đơn trống", Toast.LENGTH_SHORT).show();
                return;
            }

            invoiceRepository.saveInvoice(invoice);
            SettingRepository settingRepository = new SettingRepository(this);
            SettingEntity setting = settingRepository.getSetting();
            
            if (setting != null && setting.getPrinterMac() != null) {
                PrintService.printInvoice(this, invoice, setting.getPrinterMac());
            }

            Toast.makeText(this, "Đã lưu hóa đơn", Toast.LENGTH_SHORT).show();
            invoice.getItems().clear();
            adapter.notifyDataSetChanged();
            invoice = new Invoice("HD" + System.currentTimeMillis(), new Date());
            updateTotal();
        });

        binding.btnCloseCamera.setOnClickListener(v -> closeCamera());
    }

    private void addProduct(Product product) {
        for (InvoiceItem item : invoice.getItems()) {
            if (Objects.equals(item.getProduct().getId(), product.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                invoice.calculateTotal();
                adapter.notifyDataSetChanged();
                updateTotal();
                return;
            }
        }

        invoice.getItems().add(new InvoiceItem(product, 1, product.getSellPrice()));
        invoice.calculateTotal();
        adapter.notifyDataSetChanged();
        updateTotal();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
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
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        binding.txtTotal.setText(format.format(invoice.getTotal()));
    }

    private final ActivityResultLauncher<String> requestCameraPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) openCamera();
                else Toast.makeText(this, "Bạn cần cấp quyền Camera", Toast.LENGTH_SHORT).show();
            });

    private void openCamera() {
        binding.previewCamera.setVisibility(View.VISIBLE);
        binding.btnCloseCamera.setVisibility(View.VISIBLE);

        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(this);
        future.addListener(() -> {
            try {
                cameraProvider = future.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.previewCamera.getSurfaceProvider());

                CameraSelector selector = CameraSelector.DEFAULT_BACK_CAMERA;
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, selector, preview, imageAnalysis);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void closeCamera() {
        if (cameraProvider != null) cameraProvider.unbindAll();
        binding.previewCamera.setVisibility(View.GONE);
        binding.btnCloseCamera.setVisibility(View.GONE);
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void analyzeImage(ImageProxy imageProxy) {
        if (scanned) {
            imageProxy.close();
            return;
        }
        Image mediaImage = imageProxy.getImage();
        if (mediaImage == null) {
            imageProxy.close();
            return;
        }

        InputImage inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
        barcodeScanner.process(inputImage)
                .addOnSuccessListener(barcodes -> {
                    if (barcodes.isEmpty()) return;
                    Barcode barcode = barcodes.get(0);
                    String value = barcode.getRawValue();
                    if (value == null) return;
                    scanned = true;
                    handleBarcode(value);
                })
                .addOnFailureListener(Throwable::printStackTrace)
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void handleBarcode(String barcodeValue) {
        runOnUiThread(() -> {
            List<Product> matches = productRepository.findAllByBarcode(barcodeValue);
            if (matches.isEmpty()) {
                Toast.makeText(this, "Không tìm thấy: " + barcodeValue, Toast.LENGTH_SHORT).show();
                scanned = false;
                return;
            }

            if (matches.size() == 1) {
                addProduct(matches.get(0));
                closeCamera();
                scanned = false;
            } else {
                closeCamera();
                scanned = false;
                binding.edtSearch.setText(barcodeValue);
                binding.edtSearch.requestFocus();
            }
        });
    }
}
