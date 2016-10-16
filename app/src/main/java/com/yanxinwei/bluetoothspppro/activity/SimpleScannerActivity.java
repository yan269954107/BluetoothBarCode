package com.yanxinwei.bluetoothspppro.activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;
import com.yanxinwei.bluetoothspppro.core.BaseActivity;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * SimpleScannerActivity
 * Created by yanxinwei on 16/10/8.
 */

public class SimpleScannerActivity extends BaseActivity implements ZXingScannerView.ResultHandler {

    private static String TAG = SimpleScannerActivity.class.getName();
    public static final String SCAN_CODE = "scan_code";
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        mScannerView.setAutoFocus(true);
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v(TAG, rawResult.getText()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        // If you would like to resume scanning, call this method below:
//        mScannerView.resumeCameraPreview(this);
        Intent intent = new Intent();
        intent.putExtra(SCAN_CODE, rawResult.getText());
        setResult(RESULT_OK, intent);
        finish();

    }

}
