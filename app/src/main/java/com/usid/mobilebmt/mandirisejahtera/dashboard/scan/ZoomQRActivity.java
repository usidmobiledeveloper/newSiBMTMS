package com.usid.mobilebmt.mandirisejahtera.dashboard.scan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

public class ZoomQRActivity extends AppCompatActivity {

    ImageView imgQR;
    String rekening, nama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_zoom_qr);

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
            rekening = new NumSky(ZoomQRActivity.this).encrypt(rekening);
            new GenerateQRCode().execute();

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    class GenerateQRCode extends AsyncTask<Uri, Void, Void> {
        @Override
        protected void onPreExecute() {

        }

        protected Void doInBackground(Uri... imageUri) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    QRCodeWriter writer = new QRCodeWriter();
                    try {
                        BitMatrix bitMatrix = writer.encode(rekening, BarcodeFormat.QR_CODE, 700, 700);
                        int width = bitMatrix.getWidth();
                        int height = bitMatrix.getHeight();
                        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                        for (int x = 0; x < width; x++) {
                            for (int y = 0; y < height; y++) {
                                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                            }
                        }

                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo_qr);
                        Bitmap merge = mergeBitmaps(bitmap, bmp);
                        imgQR.setImageBitmap(merge);
                        imgQR.setVisibility(View.VISIBLE);

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }

                }
            });
            return null;
        }

        protected void onPostExecute(Void result) {

            //new GenerateBarcode().execute();
        }
    }

    public Bitmap mergeBitmaps(Bitmap overlay, Bitmap bitmap) {

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        Bitmap combined = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(combined);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        canvas.drawBitmap(bitmap, new Matrix(), null);

        int centreX = (canvasWidth - overlay.getWidth()) / 2;
        int centreY = (canvasHeight - overlay.getHeight()) / 2;
        canvas.drawBitmap(overlay, centreX, centreY, null);

        return combined;
    }

}
