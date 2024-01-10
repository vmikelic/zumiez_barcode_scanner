package com.google.mlkit.vision.demo.java.barcodescanner;

import com.google.mlkit.vision.barcode.common.Barcode;

import java.util.Objects;

public class Item {
    private final Barcode barcode;
    private final String six;
    private final String info;

    public Item(Barcode barcode,String six,String info) {
        this.barcode = barcode;
        this.six = six;
        this.info = info;
    }

    public Barcode getBarcode() {
        return this.barcode;
    }

    public String getInfo() {
        return this.info;
    }

    public String getSix() {
        return this.six;
    }

    @Override
    public int hashCode() {
        int i = Objects.requireNonNull(this.getSix()).hashCode();
        return i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Item) {
            boolean i = Objects.equals(this.getSix(), ((Item) o).getSix());
            return i;
        }
        return false;
    }
}
