package com.insoft.laris.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.insoft.laris.MainActivity;
import com.insoft.laris.R;
import com.insoft.laris.utils.Constants;

public class ReceiptActivity extends AppCompatActivity {

    FloatingActionButton fab, fshare;
    private WebView myWebView;
    private String posisi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        myWebView = findViewById(R.id.myWebView);
        fab = findViewById(R.id.fab);
        fshare = findViewById(R.id.fabshare);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printPDF(myWebView);
            }
        });

        fshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, myWebView.getUrl());
                startActivity(Intent.createChooser(shareIntent, "Share This Website!"));
                shareIntent.setPackage("com.whatsapp");

            }
        });

        //add webview client to handle event of loading
        myWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                //if page loaded successfully then show print button
                findViewById(R.id.fab).setVisibility(View.VISIBLE);
                printPDF(myWebView);
            }
        });

        String nota = getIntent().getStringExtra("_nota");

        myWebView.loadUrl(Constants.BASE_URL+"index.php/pelanggan/cetak_struk/"+nota);

    }

    //create a function to create the print job
    private void createWebPrintJob(WebView webView) {

        //create object of print manager in your device
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        //create object of print adapter
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

        //provide name to your newly generated pdf file
        String jobName = getString(R.string.app_name) + " Print Test";

        //open print dialog
        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
    }

    //perform click pdf creation operation on click of print button click
    public void printPDF(View view) {
        createWebPrintJob(myWebView);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }


}