package com.usid.mobilebmt.mandirisejahtera.dashboard.scan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.usid.mobilebmt.mandirisejahtera.R;

public class ScanActivity extends AppCompatActivity implements
        DecoratedBarcodeView.TorchListener {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private Button switchFlashlightButton;
    private boolean isFlashLightOn = false;

    private String strPIN = "", imsi = "", nocard = "";
    private SharedPreferences config;
    private Drawable imgON, imgOFF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_scan);

        config = getSharedPreferences("config", 0);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);
        switchFlashlightButton = (Button) findViewById(R.id.switch_flashlight);

        imgON = switchFlashlightButton.getContext().getResources().getDrawable(R.drawable.ic_flash_on);
        imgOFF = switchFlashlightButton.getContext().getResources().getDrawable(R.drawable.ic_flash_off);

        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        } else {
            switchFlashlightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchFlashlight();
                }
            });
        }

        Button btnQRprofile = findViewById(R.id.qrprofile);
        btnQRprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScanActivity.this, QRProfileActivity.class));
                finish();
            }
        });

        //start capture
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashlight() {
        if (isFlashLightOn) {
            barcodeScannerView.setTorchOff();
            isFlashLightOn = false;
        } else {
            barcodeScannerView.setTorchOn();
            isFlashLightOn = true;
        }
    }

    @Override
    public void onTorchOn() {
        switchFlashlightButton.setText("Flash On");
        switchFlashlightButton.setCompoundDrawablesWithIntrinsicBounds(imgON, null, null, null);
    }

    @Override
    public void onTorchOff() {
        switchFlashlightButton.setText("Flash Off");
        switchFlashlightButton.setCompoundDrawablesWithIntrinsicBounds(imgOFF, null, null, null);

    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    private static long mLastClickTime = 0;

    /*private void showConfirmPinCekSaldo(String msg, final int fi) {
        final Dialog dialog = new Dialog(ScannerActivity.this);
        finisa = false;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_confirm_pin);
        TextView textPin = (TextView) dialog.findViewById(R.id.textPin);
        final EditText edPin = (EditText) dialog.findViewById(R.id.edInfoPin);
        textPin.setText(msg);
        dialog.setCancelable(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        Button buttonDialogNo = (Button) dialog.findViewById(R.id.button_dialog_no);
        buttonDialogNo.setText("BATAL");
        buttonDialogNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finisa = true;
                dialog.cancel();
            }
        });
        final Button buttonDialogYes = (Button) dialog.findViewById(R.id.button_dialog_yes);
        buttonDialogYes.setText("OK");
        buttonDialogYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (jwtpub.equals("0"))
                    Toast.makeText(ScannerActivity.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    buttonDialogYes.setEnabled(false);
                    strPIN = edPin.getText().toString().trim();
                    edPin.setText("");
                    config = getSharedPreferences("config", 0);
                    SnHp telpMan = new SnHp(ScannerActivity.this);
                    if (5 != telpMan.telephonyManager().getSimState()) {
                        imsi = "TIDAK ADA KARTU";
                    } else {
                        try {
                            imsi = telpMan.telephonyManager().getSimSerialNumber();
                        } catch (Exception e) {
                        }
                    }
                    NumSky nmsk = new NumSky(ScannerActivity.this);
                    try {
                        nocard = nmsk.decrypt(config.getString("3D0k", ""));
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (Utility.is6digit(strPIN)) {
                        if (fi == 1) new AsyncCekSaldo().execute();
                    } else {
                        showAlert("Error", "Pin harus 6 digit angka!");
                    }
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }*/

    /*private void showAlert(String title, String message) {
        new android.app.AlertDialog.Builder(NewScanActivity.this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finisa = true;
                dialog.dismiss();
            }
        }).show();
    }*/

    /*private class AsyncCekSaldo extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(NewScanActivity.this);
        private Boolean stCekSaldo = false;
        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
        private Date today = new Date();
        private Locale id = new Locale("in", "ID");
        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
        private String tgl = "", saldo = "0", norek = "", ket = "404 Error koneksi terputus!!\nSilahkan coba lagi";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setCancelable(false);
            pdLoading.setIndeterminate(false);
            pdLoading.setTitle("Memproses");
            pdLoading.setMessage("Tunggu...");
            pdLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL obj = new URL(new NumSky(ScannerActivity.this).decrypt(getResources().getString(R.string.urlCekSaldo)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = toJsonString(Utility.md5(nocard), Utility.md5(imsi), Utility.md5(strPIN), 0);

                conJ.setConnectTimeout(20000);
                conJ.setReadTimeout(19000);
                conJ.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(conJ.getOutputStream());
                wr.writeBytes(strCek);
                wr.flush();
                wr.close();
                ket = conJ.getResponseCode() + " " + conJ.getResponseMessage();
                BufferedReader in = new BufferedReader(new InputStreamReader(conJ.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine).append("\n");
                }
                in.close();

                JSONParser parser = new JSONParser();
                Object objects = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) objects;
                stCekSaldo = (Boolean) jsonObject.get("status");
                ket = (String) jsonObject.get("keterangan");
                saldo = (String) jsonObject.get("saldoakhir");
                tgl = (String) jsonObject.get("datetime");
                norek = (String) jsonObject.get("rekening");
                jwtpub = (String) jsonObject.get("jwt");
            } catch (Exception ex) {
                ex.printStackTrace();
                stCekSaldo = false;

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoading.isShowing()) pdLoading.dismiss();

            if (stCekSaldo) {
                Intent intent = new Intent(ScannerActivity.this, QRProfile.class);
                intent.putExtra("norek", norek);
                startActivity(intent);
            } else {
                String msga = "#" + ket + "\n";
                showAlert("GAGAL", msga);
            }
        }
    }*/

    /*private String toJsonString(String nokartu, String imsi, String pin, int jmltrx) {
        JSONObject obj = new JSONObject();
        obj.put("nokartu", nokartu);
        obj.put("imsi", imsi);
        obj.put("pin", pin);
        if (jmltrx != 0) obj.put("jmltransaksi", jmltrx);

        return obj.toString();
    }*/
}