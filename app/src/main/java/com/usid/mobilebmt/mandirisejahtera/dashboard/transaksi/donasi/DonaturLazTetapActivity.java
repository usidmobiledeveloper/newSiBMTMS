//package com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.donasi;

//
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteException;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.net.ConnectivityManager;
//import android.os.AsyncTask;
//import android.os.Environment;
//import android.os.SystemClock;
//
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.Menu;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.usid.mobilebmt.mandirisejahtera.NewMainActivity;
//import com.usid.mobilebmt.mandirisejahtera.R;
//import com.usid.mobilebmt.mandirisejahtera.SplashActivity;
//import com.usid.mobilebmt.mandirisejahtera.model.SysDB;
//import com.usid.mobilebmt.mandirisejahtera.notifications.Config;
//import com.usid.mobilebmt.mandirisejahtera.utils.AuthLogin2;
//import com.usid.mobilebmt.mandirisejahtera.utils.Ctd;
//import com.usid.mobilebmt.mandirisejahtera.utils.IAsyncHandler;
//import com.usid.mobilebmt.mandirisejahtera.utils.MyVal;
//import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;
//import com.usid.mobilebmt.mandirisejahtera.utils.NumberTextWatcher;
//import com.usid.mobilebmt.mandirisejahtera.utils.SnHp;
//import com.usid.mobilebmt.mandirisejahtera.utils.Utility;
//import com.usid.mobilebmt.mandirisejahtera.utils.Utility2;
//
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.security.GeneralSecurityException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
//import static com.usid.mobilebmt.mandirisejahtera.utils.Utility.DoubleToCurrency;
//import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;
//import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;
//
//public class DonaturLazTetapActivity extends AppCompatActivity implements IAsyncHandler {
//
//    private TextView tvJudul, tvErrorMsg;
//    private Bundle extras;
//    private String titleActionBar = "", jenisDonasi, npwz = "";
//    private EditText edJenisDonasi, edNominal, edNPWZ;
//    private Button btnCancel, btnDonasi, btnDaftarTrf;
//    private String[] donasiArr = {"ZAKAT", "INFAQ"};
//    private String strNokartu = "", strImsi = "", strPIN = "", trxID = "";
//    private Menu statusMenu;
//    private String jwtlocal;
//    private int sts = 0;
//    private boolean stats = false;
//    private Ctd cnd;
//    private NetworkChangeReceiver receiver;
//    private SharedPreferences config;
//    private BroadcastReceiver mRegistrationBroadcastReceiver;
//    private boolean finis = true;
//    private ProgressDialog pdLoadingInq;
//
//    private long nominal = 0;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_donatur_laz_tetap);
//
//        cnd = new Ctd(DonaturLazTetapActivity.this);
//
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//        tvJudul = (TextView) findViewById(R.id.tvJudul);
//        tvErrorMsg = (TextView) findViewById(R.id.tvErrorMsg);
//        edJenisDonasi = (EditText) findViewById(R.id.edJenisDonasi);
//        edNPWZ = (EditText) findViewById(R.id.edNPWZ);
//        edJenisDonasi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialogViewOperator();
//            }
//        });
//        edNominal = (EditText) findViewById(R.id.edNomDonasi);
//        if (savedInstanceState == null) {
//            extras = getIntent().getExtras();
//            if (extras == null) {
//                titleActionBar = "";
//            } else {
//                titleActionBar = extras.getString("TITLE");
//            }
//        } else {
//            titleActionBar = (String) savedInstanceState.getSerializable("TITLE");
//        }
//        pdLoadingInq = new ProgressDialog(DonaturLazTetapActivity.this);
//        pdLoadingInq.setCancelable(false);
//        pdLoadingInq.setIndeterminate(false);
//        pdLoadingInq.setTitle("Memproses");
//        pdLoadingInq.setMessage("Tunggu...");
//        setTitle(titleActionBar);
//        tvJudul.setText("Donatur Tetap");
//        config = getApplicationContext().getSharedPreferences("config", 0);
//        SnHp telpMan = new SnHp(getApplicationContext());
//        if (5 != telpMan.telephonyManager().getSimState()) {
//            strImsi = "TIDAK ADA KARTU";
//        } else {
//            /*try {
//                strImsi = telpMan.telephonyManager().getSimSerialNumber();
//            } catch (Exception e) {
//            }*/
//            strImsi = Utility.getIMSIRead(this);
//        }
//        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//        receiver = new NetworkChangeReceiver();
//        registerReceiver(receiver, intentFilter);
//        mReceiver();
//
//        NumSky nmsk = new NumSky(getApplicationContext());
//        try {
//            strNokartu = nmsk.decrypt(config.getString("3D0k", ""));
//        } catch (GeneralSecurityException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        edNominal.addTextChangedListener(new NumberTextWatcher(edNominal));
//        btnDonasi = (Button) findViewById(R.id.btnDonasi);
//        btnDaftarTrf = (Button) findViewById(R.id.buttonDaftarRek);
//        btnDaftarTrf.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(DonaturLazTetapActivity.this, DaftarDonaturNPWZ.class);
//                startActivityForResult(intent, 0);
//            }
//        });
//        btnDonasi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (jwtlocal.equals("0")) {
//                    Toast.makeText(DonaturLazTetapActivity.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                } else {
//                    if (TextUtils.isEmpty(jenisDonasi)) {
//                        tvErrorMsg.setVisibility(View.VISIBLE);
//                        tvErrorMsg.setText("Jenis donasi tidak boleh kosong!");
//                        showAlert("Error", "Jenis donasi tidak boleh kosong!");
//                    } else {
//                        if (jenisDonasi.equals("ZAKAT")) {
//                            if (TextUtils.isEmpty(edNPWZ.getText().toString())) {
//                                tvErrorMsg.setVisibility(View.VISIBLE);
//                                tvErrorMsg.setText("NPWZ tidak boleh kosong!");
//                                showAlert("Error", "NPWZ tidak boleh kosong!");
//                            } else {
//                                cekStrNominal();
//                                tvErrorMsg.setVisibility(View.GONE);
//                                if (!validateNominal()) {
//                                    return;
//                                }
//                                npwz = edNPWZ.getText().toString().trim();
//                                showConfirmDonasi("KONFIRMASI ZAKAT", ": " + edJenisDonasi.getText().toString(), ": " + npwz, ": Rp. " + DoubleToCurrency(nominal) + ",-");
//                            }
//                        } else {
//                            cekStrNominal();
//                            tvErrorMsg.setVisibility(View.GONE);
//                            if (!validateNominal()) {
//                                return;
//                            }
//                            showConfirmDonasi("KONFIRMASI INFAQ", ": " + edJenisDonasi.getText().toString(), ": " + npwz, ": Rp. " + DoubleToCurrency(nominal) + ",-");
//                        }
//                    }
//                }
//            }
//        });
//        btnCancel = (Button) findViewById(R.id.btCancel);
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });
//        if (strImsi.equals("TIDAK ADA KARTU")) {
//            showExt("ERROR", "Masukkan SIM CARD!");
//        } else {
//            new AsyncInqBMT().execute();
//        }
//    }
//
//    private void AlertDialogViewOperator() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(DonaturLazTetapActivity.this);
//        builder.setTitle("Pilih Jenis Donasi");
//        builder.setSingleChoiceItems(donasiArr, -1, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int item) {
//                jenisDonasi = donasiArr[item];
//            }
//        });
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                edJenisDonasi.setText(jenisDonasi);
//                edNominal.setText("");
//                if (jenisDonasi.equals("ZAKAT")) {
//                    edNPWZ.setEnabled(true);
//                    edNPWZ.requestFocus();
//                } else {
//                    edNPWZ.setText("");
//                    edNPWZ.setEnabled(false);
//                    edNominal.requestFocus();
//                }
//                dialog.dismiss();
//            }
//        });
//        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.cancel();
//            }
//        });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
//
//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        if (requestCode == 0) {
//            if (resultCode == RESULT_OK) {
//                String rekening = intent.getStringExtra("NPWZ");
//                edNPWZ.setText(rekening);
//                edNPWZ.requestFocus();
//                edNPWZ.setSelection(edNPWZ.length());
//            } else if (resultCode == RESULT_CANCELED) {
//            }
//        }
//    }
//
//    @Override
//    public void onPostExec(Boolean status, String jwt) {
//        if (status) {
//            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_blue);
//            jwtlocal = jwt;
//            cnd.countD.start();
//        } else {
//            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_red);
//            jwtlocal = jwt;
//            if (jwtlocal.equals("401")) {
//                Intent intent = new Intent(this, SplashActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
//            }
//        }
//        sts = sts + 1;
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.KEY_COUNTDOWNTIMER));
//        if (stats && sts > 1) {
//            cnd.countD.cancel();
//            jwtlocal = "0";
//            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_yellow);
//            AuthLogin2 task = new AuthLogin2(DonaturLazTetapActivity.this, DonaturLazTetapActivity.this);
//            task.execute();
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        menu.findItem(R.id.action_status).setEnabled(false);
//        menu.findItem(R.id.action_logout).setVisible(false);
//        statusMenu = menu;
//        if (!stats) {
//            cnd.countD.cancel();
//            jwtlocal = "0";
//            AuthLogin2 task = new AuthLogin2(DonaturLazTetapActivity.this, DonaturLazTetapActivity.this);
//            task.execute();
//            stats = true;
//            sts = 1;
//        }
//        return true;
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
//        cnd.countD.cancel();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(receiver);
//
//    }
//
//    public class NetworkChangeReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(final Context context, final Intent intent) {
//            if (Utility.isNetworkAvailable(context)) ;
//            onResume();
//        }
//    }
//
//    private void cekStrNominal() {
//        String nom = "";
//        if (edNominal.getText().toString().indexOf(",") != -1) {
//            nom = edNominal.getText().toString().replace(",", "");
//            if (nom.equals("")) nominal = 0;
//            else nominal = Long.parseLong(nom);
//        } else {
//            nom = edNominal.getText().toString().replace(".", "");
//            if (nom.equals("")) nominal = 0;
//            else nominal = Long.parseLong(nom);
//        }
//    }
//
//    private void showAlert(String title, String message) {
//        new android.app.AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finis = true;
//                dialog.dismiss();
//            }
//        }).show();
//    }
//
//    private void showExt(String title, String message) {
//        new android.app.AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//                dialog.dismiss();
//            }
//        }).show();
//    }
//
//    private static long mLastClickTime = 0;
//
//    private void showConfirmDonasi(String Judul, final String prod, final String npwz, String nomi) {
//        final Dialog dialog = new Dialog(DonaturLazTetapActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.custom_dialog_confirm_trxdonatur);
//        LinearLayout lin3 = dialog.findViewById(R.id.linLayout3);
//        TextView textPin = (TextView) dialog.findViewById(R.id.textPin);
//        textPin.setText(Judul);
//        TextView prods = (TextView) dialog.findViewById(R.id.oprt);
//        prods.setText(prod);
//        TextView noh = (TextView) dialog.findViewById(R.id.nohp);
//        noh.setText(npwz);
//        TextView nom = (TextView) dialog.findViewById(R.id.nom);
//        nom.setText(nomi);
//        SysDB dbsys = new SysDB(DonaturLazTetapActivity.this);
//        dbsys.open();
//        dbsys.CreateTableDonatur();
//        int jumla = 0;
//        Cursor cur1 = dbsys.cekDonaturData(edNPWZ.getText().toString());
//        if (cur1.moveToFirst()) {
//            jumla = cur1.getInt(0);
//        }
//        cur1.close();
//        dbsys.close();
//        final CheckBox cbsimpan = dialog.findViewById(R.id.cbSimpan);
//        final LinearLayout lysimpan = dialog.findViewById(R.id.LySimpan);
//        final EditText edNama = dialog.findViewById(R.id.infonama);
//        lysimpan.setVisibility(View.GONE);
//        cbsimpan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (cbsimpan.isChecked()) {
//                    lysimpan.setVisibility(View.VISIBLE);
//                    edNama.requestFocus();
//                    edNama.setSelection(edNama.getText().length());
//                } else lysimpan.setVisibility(View.GONE);
//            }
//        });
//        if (!jenisDonasi.equals("ZAKAT")) {
//            lin3.setVisibility(View.GONE);
//            cbsimpan.setVisibility(View.GONE);
//            lysimpan.setVisibility(View.GONE);
//            cbsimpan.setEnabled(false);
//        }
//        if (jumla > 0) {
//            cbsimpan.setVisibility(View.GONE);
//            lysimpan.setVisibility(View.GONE);
//            cbsimpan.setEnabled(false);
//        }
//        dialog.setCancelable(false);
//
//        Button buttonDialogNo = (Button) dialog.findViewById(R.id.button_dialog_no);
//        buttonDialogNo.setText("BATAL");
//        buttonDialogNo.setTextSize(20);
//        buttonDialogNo.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                dialog.cancel();
//            }
//        });
//        Button buttonDialogYes = (Button) dialog.findViewById(R.id.button_dialog_yes);
//        buttonDialogYes.setText("OK");
//        buttonDialogYes.setTextSize(20);
//        buttonDialogYes.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (jwtlocal.equals("0"))
//                    Toast.makeText(DonaturLazTetapActivity.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    if (cbsimpan.isChecked()) {
//                        if (edNama.getText().toString().equals("")) {
//                            Toast.makeText(DonaturLazTetapActivity.this, "Tidak bisa diproses, Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
//                        } else {
//                            try {
//                                SysDB dbsys = new SysDB(DonaturLazTetapActivity.this);
//                                dbsys.open();
//                                String info = edNama.getText().toString().trim();
//                                dbsys.insertDonatur(edNPWZ.getText().toString(), info);
//                                dbsys.close();
//                            } catch (SQLiteException e) {
//                                e.printStackTrace();
//                            }
//                            if (titleActionBar.equals("Donasi LAZ-BMT"))
//                                showConfirmPinDonasi(getString(R.string.pin_mobilebmt), 1);
//                            dialog.dismiss();
//                        }
//                    } else {
//                        if (titleActionBar.equals("Donasi LAZ-BMT"))
//                            showConfirmPinDonasi(getString(R.string.pin_mobilebmt), 1);
//                        dialog.dismiss();
//                    }
//                }
//            }
//        });
//        dialog.show();
//    }
//
//    private void showConfirmDonasiSukses(String Judul, String tgls, String faktur, String darirek) {
//        final Dialog dialog = new Dialog(DonaturLazTetapActivity.this);
//        Utility.playNotificationSound(DonaturLazTetapActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.custom_dialog_confirm_donasisukses);
//        TextView textPin = (TextView) dialog.findViewById(R.id.textPin);
//        textPin.setText(Judul);
//        TextView tgl = (TextView) dialog.findViewById(R.id.tgl);
//        tgl.setText(tgls);
//        TextView prods = (TextView) dialog.findViewById(R.id.faktur);
//        prods.setText(faktur);
//        TextView noh = (TextView) dialog.findViewById(R.id.darirek);
//        noh.setText(darirek);
//        dialog.setCancelable(false);
//        Button buttonDialogYes = (Button) dialog.findViewById(R.id.button_dialog_yes);
//        buttonDialogYes.setText("OK SIMPAN");
//        buttonDialogYes.setTextSize(20);
//        buttonDialogYes.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Dialog dialog2 = Dialog.class.cast(dialog);
//                takeScreenshot(dialog2);
//                onBackPressed();
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//    }
//
//    private void showConfirmPinDonasi(String msg, final int fi) {
//        final Dialog dialog = new Dialog(DonaturLazTetapActivity.this);
//        finis = false;
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.custom_dialog_confirm_pin);
//        TextView textPin = (TextView) dialog.findViewById(R.id.textPin);
//        final EditText edPin = (EditText) dialog.findViewById(R.id.edInfoPin);
//        textPin.setText(msg);
//        dialog.setCancelable(true);
//        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        Button buttonDialogNo = (Button) dialog.findViewById(R.id.button_dialog_no);
//        buttonDialogNo.setText("BATAL");
//        buttonDialogNo.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                finis = true;
//                dialog.cancel();
//            }
//        });
//        final Button buttonDialogYes = (Button) dialog.findViewById(R.id.button_dialog_yes);
//        buttonDialogYes.setText("OK");
//        buttonDialogYes.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//                    return;
//                }
//                mLastClickTime = SystemClock.elapsedRealtime();
//                if (jwtlocal.equals("0")) {
//                    Toast.makeText(DonaturLazTetapActivity.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                } else {
//                    buttonDialogYes.setEnabled(false);
//                    strPIN = edPin.getText().toString().trim();
//                    edPin.setText("");
//                    config = getApplicationContext().getSharedPreferences("config", 0);
//                    if (Utility.is6digit(strPIN)) {
//                        if (fi == 1) new AsyncLAZ().execute();
//                    } else {
//                        showAlert("Error", "Pin harus 6 digit angka!");
//                    }
//                    dialog.dismiss();
//                }
//            }
//        });
//        dialog.show();
//    }
//
//    private boolean validateNominal() {
//        if (edNominal.getText().toString().trim().equals("")) {
//            edNominal.requestFocus();
//            tvErrorMsg.setVisibility(View.VISIBLE);
//            tvErrorMsg.setText("Nominal tidak boleh kosong!");
//            showAlert("Error", "Nominal tidak boleh kosong!");
//            return false;
//        } else if (nominal < 10000) {
//            edNominal.requestFocus();
//            edNominal.setSelection(edNominal.getText().length());
//            tvErrorMsg.setVisibility(View.VISIBLE);
//            tvErrorMsg.setText("Nominal donasi minimal Rp. 10.000,-");
//            showAlert("Error", "Nominal donasi minimal Rp. 10.000,-");
//            return false;
//        } else if (nominal > 5000000) {
//            edNominal.requestFocus();
//            edNominal.setSelection(edNominal.getText().length());
//            tvErrorMsg.setVisibility(View.VISIBLE);
//            tvErrorMsg.setText("Nominal transfer maksimal Rp. 5.000.000,-");
//            showAlert("Error", "Nominal transfer maksimal Rp. 5.000.000,-");
//            return false;
//        } else {
//            tvErrorMsg.setVisibility(View.GONE);
//            tvErrorMsg.setText("");
//        }
//        return true;
//    }
//
//    private class AsyncInqBMT extends AsyncTask<Void, Void, Void> {
//        private Boolean status = false;
//        private long IDtrx = 0;
//        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi.";
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pdLoadingInq.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                URL obj = new URL(MyVal.URL_BASE() + new NumSky(DonaturLazTetapActivity.this).decrypt(getResources().getString(R.string.urlGetID)));
//                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
//                conJ.setRequestMethod("GET");
//                conJ.setRequestProperty("Content-Type", "application/json");
//                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
//                conJ.setConnectTimeout(30000);
//                conJ.setReadTimeout(29000);
//                ket = conJ.getResponseCode() + " " + conJ.getResponseMessage();
//                BufferedReader in = new BufferedReader(new InputStreamReader(conJ.getInputStream()));
//                String inputLine;
//                StringBuilder response = new StringBuilder();
//                while ((inputLine = in.readLine()) != null) {
//                    response.append(inputLine).append("\n");
//                }
//                in.close();
//                JSONParser parser = new JSONParser();
//                Object objects = parser.parse(response.toString());
//                JSONObject jsonObject = (JSONObject) objects;
//                IDtrx = (long) jsonObject.get("trxid");
//                status = true;
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                status = false;
//                ket = "405 Error Exception, koneksi terputus!!\nSilahkan coba lagi.";
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            if (pdLoadingInq.isShowing()) pdLoadingInq.dismiss();
//            if (status) {
//                trxID = Long.toString(IDtrx);
//            } else {
//                String msga = "#" + ket + "\n";
//                showExt("GAGAL", msga);
//            }
//        }
//    }
//
//    private class AsyncLAZ extends AsyncTask<Void, Void, Void> {
//        ProgressDialog pdLoading = new ProgressDialog(DonaturLazTetapActivity.this);
//        private Boolean stLAZ = false;
//        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
//        private Date today = new Date();
//        private Locale id = new Locale("in", "ID");
//        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
//        private String ket = "404 Error koneksi terputus!!\nSilahkan cek transaksi terakhir.", strFaktur = "", strRekAsal = "", strDate = "";
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pdLoading.setCancelable(false);
//            pdLoading.setIndeterminate(false);
//            pdLoading.setTitle("Memproses");
//            pdLoading.setMessage("Tunggu...");
//            pdLoading.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                URL obj = new URL(MyVal.URL_BASE() + new NumSky(DonaturLazTetapActivity.this).decrypt(getResources().getString(R.string.urlLAZ)));
//                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
//                conJ.setRequestMethod("POST");
//                conJ.setRequestProperty("Content-Type", "application/json");
//                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
//                String strCek = "";
//                if (jenisDonasi.equals("ZAKAT"))
//                    strCek = toJsonString(Utility.md5(strNokartu), Utility.md5(strImsi), nominal, Utility.md5(strPIN), "ZAKAT " + npwz, trxID);
//                else
//                    strCek = toJsonString(Utility.md5(strNokartu), Utility.md5(strImsi), nominal, Utility.md5(strPIN), "INFAQ Donasi LAZ", trxID);
//                conJ.setConnectTimeout(20000);
//                conJ.setReadTimeout(19000);
//                conJ.setDoOutput(true);
//                DataOutputStream wr = new DataOutputStream(conJ.getOutputStream());
//                wr.writeBytes(strCek);
//                wr.flush();
//                wr.close();
//                ket = conJ.getResponseCode() + " " + conJ.getResponseMessage();
//                BufferedReader in = new BufferedReader(new InputStreamReader(conJ.getInputStream()));
//                String inputLine;
//                StringBuilder response = new StringBuilder();
//                while ((inputLine = in.readLine()) != null) {
//                    response.append(inputLine).append("\n");
//                }
//                in.close();
//                JSONParser parser = new JSONParser();
//                Object objects = parser.parse(response.toString());
//                JSONObject jsonObject = (JSONObject) objects;
//                stLAZ = (Boolean) jsonObject.get("status");
//                ket = (String) jsonObject.get("keterangan");
//                nominal = (long) jsonObject.get("nominal");
//                strFaktur = (String) jsonObject.get("fakturtujuan");
//                strRekAsal = (String) jsonObject.get("rekeningasal");
//                strDate = (String) jsonObject.get("datetime");
//                jwtlocal = (String) jsonObject.get("jwt");
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                stLAZ = false;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            if (pdLoading.isShowing()) pdLoading.dismiss();
//            strTgl = sdf.format(today);
//            String info = "";
//            if (stLAZ) {
//                if (jenisDonasi.equals("ZAKAT")) {
//                    info = "Alhamdulillah... ZAKAT dari No. NPWZ : " + npwz + ", " + strRekAsal + " senilai Rp. " + DoubleToCurrency(nominal) + ",- telah diterima dengan baik oleh LAZ-BMT. Semoga bertambah barokah.";
//                    ket = "Tanggal : " + strDate + "\n" + strFaktur + "\n" + info;
//                } else {
//                    info = "Alhamdulillah... INFAQ Donasi dari " + strRekAsal + " senilai Rp. " + DoubleToCurrency(nominal) + ",- telah diterima dengan baik oleh LAZ-BMT. Semoga bertambah barokah.";
//                    ket = "Tanggal : " + strDate + "\n" + strFaktur + "\n" + info;
//                }
//                SysDB dbsys = new SysDB(DonaturLazTetapActivity.this);
//                dbsys.open();
//                dbsys.CreateTableSys();
//                dbsys.insertSys(strTgl, "Donasi LAZ SUKSES\n" + ket);
//                dbsys.close();
//                showConfirmDonasiSukses("Donasi LAZ SUKSES", ": " + strDate, ": " + strFaktur, info);
//            } else {
//                String msga = "#" + ket + "\n";
//                if (!ket.equals("404 Error koneksi terputus!!\nSilahkan cek transaksi terakhir.")) {
//                    SysDB dbsys = new SysDB(DonaturLazTetapActivity.this);
//                    dbsys.open();
//                    dbsys.CreateTableSys();
//                    dbsys.insertSys(strTgl, "Donasi LAZ GAGAL " + msga);
//                    dbsys.close();
//                }
//                tvErrorMsg.setVisibility(View.VISIBLE);
//                tvErrorMsg.setText(msga);
//                showAlert("Donasi LAZ GAGAL", msga);
//            }
//        }
//    }
//
//    private String toJsonString(String nokartu, String imsi, long nominal, String pin, String berita, String idtrx) {
//        JSONObject obj = new JSONObject();
//        obj.put("nokartu", nokartu);
//        obj.put("imsi", imsi);
//        obj.put("pin", pin);
//        obj.put("nominal", nominal);
//        obj.put("berita", berita);
//        obj.put("trxid", idtrx);
//        return obj.toString();
//    }
//
//    private void takeScreenshot(Dialog dialog) {
//        try {
//            String sDCard = "";
//            sDCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
//            File newFolder = new File(sDCard + getString(R.string.path1));
//            if (!newFolder.exists()) {
//                newFolder.mkdirs();
//            }
//            SimpleDateFormat sdf4 = new SimpleDateFormat("yyyyMMdd-HHmmss");
//            Date naw = new Date();
//            String now = sdf4.format(naw);
//            // image naming and path  to include sd card  appending name you choose for file
//            String mPath = sDCard + getString(R.string.path2) + now + ".jpg";
//            // create bitmap screen capture
//
//            View v1 = dialog.getWindow().getDecorView().getRootView();
////            View v1 = getWindow().getDecorView().getRootView();
//            v1.setDrawingCacheEnabled(true);
//            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
//            bitmap = mark(bitmap, getString(R.string.watermark));
//            v1.setDrawingCacheEnabled(false);
//            File imageFile = new File(mPath);
//            FileOutputStream outputStream = new FileOutputStream(imageFile);
//            int quality = 100;
//            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
//            outputStream.flush();
//            outputStream.close();
//            Toast.makeText(getApplicationContext(), "Screenshoot tersimpan di " + mPath, Toast.LENGTH_SHORT).show();
//        } catch (Throwable e) {
//            // Several error may come out with file handling or OOM
//            Toast.makeText(DonaturLazTetapActivity.this, "Gagal menyimpan screenshoot", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//    }
//
//    private static Bitmap mark(Bitmap src, String watermark) {
//        int w = src.getWidth();
//        int h = src.getHeight();
//        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
//        Canvas canvas = new Canvas(result);
//        canvas.drawBitmap(src, 0, 0, null);
//        Paint paint = new Paint();
//        paint.setAlpha(1);
//        paint.setColor(Color.LTGRAY);
//        paint.setTextSize(50);
//        paint.setAntiAlias(true);
//        paint.setFakeBoldText(true);
//        canvas.drawText(watermark, 50, h * 99 / 100, paint);
//        return result;
//    }
//
//    private void mReceiver() {
//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (intent.getAction().equals(Config.KEY_COUNTDOWNTIMER)) {
//                    if (finis) //finish();
//                        Utility2.showAlertRelogin(DonaturLazTetapActivity.this);
//                    else {
////                        Toast.makeText(GantiPin.this, "Waktu koneksi habis, pastikan indikator koneksi berwarna biru, silahkan restart aplikasi", Toast.LENGTH_SHORT).show();
//                        cnd.countD.cancel();
//                        jwtlocal = "0";
//                        statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_yellow);
//                        AuthLogin2 task = new AuthLogin2(DonaturLazTetapActivity.this, DonaturLazTetapActivity.this);
//                        task.execute();
//                    }
//                }
//            }
//        };
//    }
//}