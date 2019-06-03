package com.example.qrcodescanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void go_qr_activity(View view){
        startActivity(new Intent(this, QrCodeScannerActivity.class));
    }
    public void go_nfc_activity(View view){
        Intent intent = new Intent(getApplicationContext(), NfcScannerActivity.class);
        intent.setAction("android.nfc.action.NDEF_DISCOVERED");
        startActivity(intent);
    }
}
