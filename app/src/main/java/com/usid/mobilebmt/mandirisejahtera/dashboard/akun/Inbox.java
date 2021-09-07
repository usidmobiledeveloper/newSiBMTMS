package com.usid.mobilebmt.mandirisejahtera.dashboard.akun;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.usid.mobilebmt.mandirisejahtera.BuildConfig;
import com.usid.mobilebmt.mandirisejahtera.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Inbox extends AppCompatActivity {
    private Bundle extras;
    private String strTgl = "", strMsg = "";
    private TextView tvjudul, tvPesan;
    private SimpleDateFormat sdf4 = new SimpleDateFormat("yyyyMMdd-HHmmss");
    private String now;
    //    private Button btnPDF;
    private LinearLayout layout_pdf;

    boolean isToken = false;
    String token = "";
//    ScrollView relativeLayout;
    @BindView(R.id.Scr)
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_inbox);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if (extras == null) {
                strTgl = "";
                strMsg = "";
            } else {
                strTgl = extras.getString("tgl");
                strMsg = extras.getString("ket");
            }
        } else {
            strTgl = (String) savedInstanceState.getSerializable("tgl");
            strMsg = (String) savedInstanceState.getSerializable("ket");
        }

        Log.d("AYIK", "inbox:msg\n"+strMsg);

        assert strMsg != null;
        isToken = strMsg.startsWith("Pembelian Token PLN SUKSES (Reff)");

        if (isToken) {
            String[] msgSplit = strMsg.split("\n");
            if (msgSplit.length >= 24) {
                token = msgSplit[22];
                token = token.replaceAll("\\s+", " ").replace(" ", "").replace("TOKEN:", "");
                StringBuilder s;
                s = new StringBuilder(token);

                for (int i = 4; i < s.length(); i += 5) {
                    s.insert(i, " ");
                }
                token = s.toString();
            }
        }

        tvjudul = (TextView) findViewById(R.id.tvJudul);
        tvPesan = (TextView) findViewById(R.id.tvPesan);
        layout_pdf = (LinearLayout) findViewById(R.id.layoutpdf);
        //relativeLayout = findViewById(R.id.Scr);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            layout_pdf.setVisibility(View.VISIBLE);
        }
        if (strMsg.contains("(Reff)")) {
            layout_pdf.setVisibility(View.VISIBLE);
        } else {
            layout_pdf.setVisibility(View.GONE);
        }

        tvjudul.setText(strTgl);
        tvPesan.setText(strMsg);

        Typeface face = Typeface.createFromAsset(getAssets(), "cour.ttf");
        tvPesan.setTypeface(face);
    }

    public void ButtonClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSMS:
                Intent intent = new Intent(Inbox.this, ForwardSMS.class);
                intent.putExtra("ket", strMsg);
                startActivity(intent);
                //finish();
                break;
            case R.id.buttonShare:
                takeScreenshot();
                break;
            case R.id.buttonPDF:
                new PdfGenerationTask().execute();
                break;
        }
    }

    private void takeScreenshot() {
        try {
            Date naw = new Date();
            now = sdf4.format(naw);
            String sDCard = "";
            sDCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            File newFolder = new File(sDCard + getString(R.string.path1));
            if (!newFolder.exists()) {
                newFolder.mkdirs();
            }
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = sDCard + getString(R.string.path2) + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
           // Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            Bitmap bitmap = getBitmapFromView(scrollView, scrollView.getChildAt(0).getHeight(), scrollView.getChildAt(0).getWidth());

            bitmap = mark(bitmap, getString(R.string.watermark));
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);
            try {
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                int quality = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            sendScreenshoot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            Toast.makeText(Inbox.this, "Gagal menyimpan screenshoot", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void sendScreenshoot(File imageFile) {
//        Uri uri = Uri.parse("file://" + imageFile.getAbsolutePath());
        Uri uri = FileProvider.getUriForFile(Inbox.this, BuildConfig.APPLICATION_ID + ".provider", imageFile);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType("image/*");
        startActivity(Intent.createChooser(sendIntent, "Share images to..."));
    }

    /*private static Bitmap mark(Bitmap src, String watermark) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setAlpha(1);
        paint.setColor(Color.LTGRAY);
        paint.setTextSize(50);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        canvas.drawText(watermark, 50, h * 99 / 100, paint);

        return result;
    }*/

    private Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    private Bitmap mark(Bitmap src, String watermark) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setAlpha(1);
        paint.setColor(getResources().getColor(R.color.trans));
        paint.setTextSize(50);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        canvas.drawText(watermark, 50, h * 99 / 100, paint);

        return result;
    }

    private class PdfGenerationTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            PdfDocument document = new PdfDocument();

            View content = findViewById(R.id.tvPesan);

            int pageNumber = 1;
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(content.getWidth(), content.getHeight(), pageNumber).create();

            PdfDocument.Page page = document.startPage(pageInfo);

            content.draw(page.getCanvas());

            document.finishPage(page);

            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmss");
            String pdfName = getString(R.string.app_name) + sdf.format(Calendar.getInstance().getTime()) + ".pdf";
            String sDCard = "";
            sDCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            File newFolder = new File(sDCard + getString(R.string.path1));
            if (!newFolder.exists()) {
                newFolder.mkdirs();
            }

            File outputFile = new File(sDCard + getString(R.string.path2), pdfName);

            try {
                outputFile.createNewFile();
                OutputStream out = new FileOutputStream(outputFile);
                document.writeTo(out);
                document.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return outputFile.getPath();
        }

        @Override
        protected void onPostExecute(String filePath) {
            if (filePath != null) {
                Toast.makeText(getApplicationContext(), "PDF tersimpan di " + filePath, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Gagal simpan PDF " + filePath, Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inbox_menu, menu);

        MenuItem item = menu.findItem(R.id.salin_token);

        if (isToken) {
            item.setVisible(true);
        } else {
            item.setVisible(false);
        }

        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.salin_token) {
            ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("Salin token", token);
            assert manager != null;
            manager.setPrimaryClip(clipData);

            Toast.makeText(Inbox.this, "Salin token", Toast.LENGTH_SHORT).show();
        }

        return true;
    }
}
