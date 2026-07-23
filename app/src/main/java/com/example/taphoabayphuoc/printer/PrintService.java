package com.example.taphoabayphuoc.printer;

import android.content.Context;

import com.example.taphoabayphuoc.models.Invoice;
import com.example.taphoabayphuoc.models.InvoiceItem;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PrintService {

    public static void printInvoice(
            Context context,
            Invoice invoice,
            String printerMac){

        String receipt = buildReceipt(invoice);

        // TODO:
        // Bước tiếp theo sẽ gửi chuỗi receipt này tới máy in Bluetooth
        // bằng thư viện ESC/POS.
        BluetoothPrinter printer = new BluetoothPrinter();

        if(printer.connect(printerMac)){

            printer.print(receipt);

            printer.disconnect();

        }
    }

    private static String buildReceipt(Invoice invoice) {

        NumberFormat money =
                NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy HH:mm",
                        Locale.getDefault());

        StringBuilder builder = new StringBuilder();

        builder.append("[C]<b>TẠP HÓA BẢY PHƯỚC</b>\n");
        builder.append("[C]==============================\n");

        builder.append("[L]Hóa đơn: ")
                .append(invoice.getId())
                .append("\n");

        builder.append("[L]Ngày: ")
                .append(sdf.format(invoice.getCreatedDate()))
                .append("\n");

        builder.append("[C]------------------------------\n");

        for (InvoiceItem item : invoice.getItems()) {

            builder.append("[L]")
                    .append(item.getProduct().getName())
                    .append("\n");

            builder.append("[L]")
                    .append(item.getQuantity())
                    .append(" x ")
                    .append(money.format(item.getPrice()))
                    .append("[R]")
                    .append(money.format(item.getTotal()))
                    .append("\n");
        }

        builder.append("[C]------------------------------\n");

        builder.append("[R]<b>")
                .append(money.format(invoice.getTotal()))
                .append("</b>\n");

        builder.append("\n");
        builder.append("[C]Xin cảm ơn Quý khách!\n");
        builder.append("[C]Hẹn gặp lại!\n\n\n");

        return builder.toString();
    }
}