package com.neology.loyaltycard.adapters;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.neology.loyaltycard.R;
import com.neology.loyaltycard.model.Product;
import com.neology.loyaltycard.utils.FileDownloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by root on 3/02/16.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductAdapter_ViewHolder> {

    ArrayList<Product> productArrayList;
    Product product;
    AppCompatActivity c;
    String urlPdf = "";
    String pdfName = "";

    public ProductAdapter(ArrayList<Product> productArrayList, AppCompatActivity c) {
        this.productArrayList = productArrayList;
        this.c = c;
    }

    @Override
    public ProductAdapter_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product, parent, false);
        return new ProductAdapter_ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProductAdapter_ViewHolder holder, int position) {
        product = productArrayList.get(position);
        holder.productName.setText(product.getProductName());
        holder.price.setText(product.getPrice());
        holder.selector.setOnClickListener(pdfClickListener);
        urlPdf = product.getUrl();
        pdfName = product.getProductName();

    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public static class ProductAdapter_ViewHolder extends RecyclerView.ViewHolder {

        public TextView productName;
        public TextView price;
        public ImageView selector;

        public ProductAdapter_ViewHolder(View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.productNameId);
            price = (TextView) itemView.findViewById(R.id.priceId);
            selector = (ImageView) itemView.findViewById(R.id.descriptionID);
        }
    }

    public View.OnClickListener pdfClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(urlPdf));
//            a.startActivity(i);
            new DownloadFile().execute(urlPdf, pdfName);

        }
    };

    class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(c.getApplicationContext(), "Descarga Completa " + pdfName, Toast.LENGTH_SHORT).show();

        }

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];
            String fileName = strings[1];
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "Descripción Producto SHELL");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try {
                pdfFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
//            loadPdf();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showPdf();
        }
    }

    public void showPdf() {
        File file = new File(Environment.getExternalStorageDirectory() + "/Descripción Producto SHELL/" + pdfName);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/pdf");
        c.startActivity(intent);
    }
}
