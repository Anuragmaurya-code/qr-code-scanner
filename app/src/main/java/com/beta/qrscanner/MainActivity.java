package com.beta.qrscanner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button btnScanBarcode;
    EditText resultText;
    Button btnGalleyBarcode;
    Button btnContacts;
    private static final int REQUEST_IMAGE_GALLERY = 101;
    private static final int REQUEST_CONTACTS = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnScanBarcode = findViewById(R.id.btnScanBarcode);
        resultText = findViewById(R.id.result_text);
        btnGalleyBarcode = findViewById(R.id.btnGalleyBarcode);
        btnContacts = findViewById(R.id.btnContacts);

        btnScanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScannedBarcodeActivity.class);
                scanBarcodeLauncher.launch(intent);
            }
        });

        btnGalleyBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
            }
        });

        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactsIntent = new Intent(MainActivity.this, ContactsActivity.class);
                startActivityForResult(contactsIntent, REQUEST_CONTACTS);
            }
        });
    }

    private ActivityResultLauncher<Intent> scanBarcodeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.hasExtra("intentData")) {
                        String intentData = data.getStringExtra("intentData");
                        resultText.setText(intentData);
                        Toast.makeText(MainActivity.this, intentData, Toast.LENGTH_LONG).show();
                    }
                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();
                if (!barcodeDetector.isOperational()) {
                    Toast.makeText(getApplicationContext(), "Could not set up the detector!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);
                if (barcodes.size() > 0) {
                    Barcode qrCode = barcodes.valueAt(0);
                    resultText.setText(qrCode.displayValue);
                } else {
                    Toast.makeText(getApplicationContext(), "No QR code found in the selected image!", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_CONTACTS && resultCode == RESULT_OK && data != null) {
            String selectedNumber = data.getStringExtra("selectedNumber");
            resultText.setText(selectedNumber);
        }
    }
}
