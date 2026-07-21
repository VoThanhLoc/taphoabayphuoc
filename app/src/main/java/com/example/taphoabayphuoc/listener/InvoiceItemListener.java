package com.example.taphoabayphuoc.listener;

import com.example.taphoabayphuoc.models.InvoiceItem;

public interface InvoiceItemListener {

    void onIncrease(InvoiceItem item);

    void onDecrease(InvoiceItem item);

    void onDelete(InvoiceItem item);

}