package com.neology.loyaltycard.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 3/02/16.
 */
public class Product implements Parcelable {

    private String productName;
    private String price;
    private String url;

    public Product(String productName, String price, String url) {
        this.productName = productName;
        this.price = price;
        this.url = url;

    }

    protected Product(Parcel in) {
        productName = in.readString();
        price = in.readString();
        url = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productName);
        dest.writeString(price);
        dest.writeString(url);
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
