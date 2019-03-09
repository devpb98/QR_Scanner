package com.example.qr_scanner;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;


import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int RQCamera = 1;
    private ZXingScannerView scView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scView = new ZXingScannerView(this);
        setContentView(scView);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(cSPermission())
            {
                Toast.makeText(MainActivity.this,"Permission is Granted!",Toast.LENGTH_SHORT).show();
            }
            else
            {
                rqPermissions();
            }
        }
    }
    private boolean cSPermission()
    {
        return (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED);
    }

    private void rqPermissions()
    {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, RQCamera );
    }



    @Override
    protected void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (cSPermission())
            {
                if (scView == null)
                {
                    scView = new ZXingScannerView(this);
                    setContentView(scView);
                }
                scView.setResultHandler(this);
                scView.startCamera();
            }
            else
            {
                rqPermissions();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scView.stopCamera();
    }



    @Override
    public void handleResult(Result result) {
        final String scanResult = result.getText();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scView.resumeCameraPreview(MainActivity.this);
            }
        });
        builder.setNeutralButton("Visit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent iResult = new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult));
                startActivity(iResult);
            }
        });
        builder.setMessage(scanResult);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
