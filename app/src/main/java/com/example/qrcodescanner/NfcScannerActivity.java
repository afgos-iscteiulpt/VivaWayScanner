package com.example.qrcodescanner;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class NfcScannerActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_scanner);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter==null)
            Log.e("NFC", "NFC is not supported");
    }

    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        Intent intent = getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] ndefMessageArray = intent.getParcelableArrayExtra( NfcAdapter.EXTRA_NDEF_MESSAGES );
            if(ndefMessageArray != null) {
                NdefMessage ndefMessage = (NdefMessage) ndefMessageArray[0];
                //Toast.makeText(this, new String(ndefMessage.getRecords()[0].getPayload()), Toast.LENGTH_LONG).show();
                NdefRecord record = ndefMessage.getRecords()[0];
                String s = new String(record.getPayload());
                Log.e("LOL", s);
            }
        }
    }
}
