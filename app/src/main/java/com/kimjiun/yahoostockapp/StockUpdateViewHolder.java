package com.kimjiun.yahoostockapp;

import android.text.format.DateFormat;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.kimjiun.yahoostockapp.databinding.StockUpdateItemBinding;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

public class StockUpdateViewHolder extends RecyclerView.ViewHolder {
    private static final NumberFormat PRICE_FORMAT = new DecimalFormat("#0.00");
    StockUpdateItemBinding stockUpdateItemBinding;

    public StockUpdateViewHolder(View v) {
        super(v);
        stockUpdateItemBinding = StockUpdateItemBinding.bind(v);
    }

    public void setStockSymbol(String stockSymbol) {
        stockUpdateItemBinding.stockItemSymbol.setText(stockSymbol);
    }

    public void setPrice(BigDecimal price) {
        stockUpdateItemBinding.stockItemPrice.setText(PRICE_FORMAT.format(price.floatValue()));
    }

    public void setDate(Date date) {
        stockUpdateItemBinding.stockItemDate.setText(DateFormat.format("yyyy-MM-dd hh:mm", date));
    }
}
