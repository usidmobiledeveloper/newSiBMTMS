package com.usid.mobilebmt.mandirisejahtera.dashboard.scan;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Hashtable;

public class ZoomBarcodeActivity extends AppCompatActivity {

    ImageView imgQR;
    String rekening, nama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_zoom_barcode);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                rekening = "";
                nama = "";
            } else {
                rekening = extras.getString("rekening");
                nama = extras.getString("nama");
            }
        } else {
            rekening = (String) savedInstanceState.getSerializable("rekening");
            nama = (String) savedInstanceState.getSerializable("nama");
        }

        imgQR = findViewById(R.id.img_qrcode);
        TextView tvNama = findViewById(R.id.tv_nama);

        tvNama.setText("a.n. "+nama);

        rekening = rekening.replace(".", "");

        try {
            rekening = new NumSky(ZoomBarcodeActivity.this).encrypt(rekening);
            new GenerateBarcode().execute();

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    class GenerateBarcode extends AsyncTask<Uri, Void, Void> {
        @Override
        protected void onPreExecute() {

        }

        protected Void doInBackground(Uri... imageUri) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
                        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                        Writer codeWriter;
                        codeWriter = new Code128Writer();
                        BitMatrix byteMatrix = codeWriter.encode(rekening, BarcodeFormat.CODE_128, 1000, 200, hintMap);
                        int width = byteMatrix.getWidth();
                        int height = byteMatrix.getHeight();
                        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        for (int i = 0; i < width; i++) {
                            for (int j = 0; j < height; j++) {
                                bitmap.setPixel(i, j, byteMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                            }
                        }
                        imgQR.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            });
            return null;
        }

        protected void onPostExecute(Void result) {
           /* if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }*/
        }
    }
}