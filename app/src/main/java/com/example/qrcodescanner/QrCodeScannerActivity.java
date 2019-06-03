package com.example.qrcodescanner;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;

import static android.Manifest.permission.CAMERA;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrCodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView qrCodeScanner;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        qrCodeScanner = new ZXingScannerView(this);
        setContentView(qrCodeScanner);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkPermission()) {
                qrCodeScanner.startCamera();
            } else {
                requestPermission();
            }
        }
        firestore = FirebaseFirestore.getInstance();

    }

    private boolean checkPermission()  {
        return (ContextCompat.checkSelfPermission(QrCodeScannerActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA );
    }

    public void onRequestPermissionResult(int requestCode, String permission[], int grantResults[]) {
        switch(requestCode) {
            case REQUEST_CAMERA:
                if(grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(!cameraAccepted) {
                        Toast.makeText(QrCodeScannerActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(CAMERA)) {
                            displayAlertMessage("You need to allow acess for both permissions", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                                }
                            });
                            return;
                        }
                    }
                }
                break;
        }

    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(QrCodeScannerActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkPermission()) {
                if(qrCodeScanner == null) {
                    qrCodeScanner = new ZXingScannerView(this);;
                    setContentView(qrCodeScanner);
                }
                qrCodeScanner.setResultHandler(this);
                qrCodeScanner.startCamera();
            } else {
                requestPermission();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        qrCodeScanner.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        final String scanResult = result.getText();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                qrCodeScanner.resumeCameraPreview(QrCodeScannerActivity.this);
            }
        });
        final DocumentReference doc_ref = firestore.collection("users").document(scanResult);
        doc_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot != null) {
                    boolean tmp = documentSnapshot.getBoolean("Valid");
                    if(tmp) {
                        builder.setMessage(scanResult + "   User is valid!!");
                    }else{
                        builder.setMessage(scanResult + "   User is NOT valid!!");
                    }
                    AlertDialog alert = builder.create();
                    alert.show();
                }else {
                    Log.d("LOGGER", "No such document");
                }
            }
        });
    }
}
