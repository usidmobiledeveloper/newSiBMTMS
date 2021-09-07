package com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.transfer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.usid.mobilebmt.mandirisejahtera.NewMainActivity;
import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.SplashActivity;
import com.usid.mobilebmt.mandirisejahtera.model.SysDB;
import com.usid.mobilebmt.mandirisejahtera.notifications.Config;
import com.usid.mobilebmt.mandirisejahtera.utils.AuthLogin2;
import com.usid.mobilebmt.mandirisejahtera.utils.Ctd;
import com.usid.mobilebmt.mandirisejahtera.utils.IAsyncHandler;
import com.usid.mobilebmt.mandirisejahtera.utils.MyVal;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;
import com.usid.mobilebmt.mandirisejahtera.utils.NumberTextWatcher;
import com.usid.mobilebmt.mandirisejahtera.utils.SnHp;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility2;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.usid.mobilebmt.mandirisejahtera.utils.Utility.DoubleToCurrency;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.setPrefsAuthToken;

public class AntarBank extends AppCompatActivity implements IAsyncHandler {
    private Button btnCancel, btnTransfer, btnDaftarTrf;
    private TextView tvErrorMsg;
    private EditText edNominal, edRekTujuan, edKdBank;
    private String trxID = "", strPRDID = "", strOperator = "", strRekTujuan = "", strNokartu = "", strImsi = "", strPIN = "", strNama = "";
    private long nominal = 0, admin = 0;
    private SharedPreferences config;
    private String[] kodeY;
    private ArrayList kodeX = new ArrayList();
    private String[] namaY;
    private ArrayList namaX = new ArrayList();
    private ProgressDialog pdLoadingInq;
    //tambahan ayik==
    private Menu statusMenu;
    private String jwtlocal;
    private int sts = 0;
    private boolean stats = false;
    private Ctd cnd;
    private NetworkChangeReceiver receiver;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean finis = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_transferantarbank);

        // tambahan baru ayik
        cnd = new Ctd(AntarBank.this);

        btnDaftarTrf = (Button) findViewById(R.id.buttonDaftarRek);
        btnTransfer = (Button) findViewById(R.id.btnTransfer);
        btnCancel = (Button) findViewById(R.id.btCancel);
        tvErrorMsg = (TextView) findViewById(R.id.tvErrorMsg);
        edNominal = (EditText) findViewById(R.id.edNomTransfer);
        edRekTujuan = (EditText) findViewById(R.id.edRekTujuan);
        edKdBank = (EditText) findViewById(R.id.edKdBank);
        config = getApplicationContext().getSharedPreferences("config", 0);
        SnHp telpMan = new SnHp(getApplicationContext());
        if (5 != telpMan.telephonyManager().getSimState()) {
            strImsi = "TIDAK ADA KARTU";
        } else {
          /*  try {
                strImsi = telpMan.telephonyManager().getSimSerialNumber();
            } catch (Exception e) {
            }*/
            strImsi = Utility.getIMSIRead(this);
        }
        NumSky nmsk = new NumSky(getApplicationContext());
        try {
            strNokartu = nmsk.decrypt(config.getString("3D0k", ""));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        edNominal.addTextChangedListener(new NumberTextWatcher(edNominal));
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i)) && !Character.toString(source.charAt(i)).equals(".") && !Character.toString(source.charAt(i)).equals(" ") && !Character.toString(source.charAt(i)).equals(",")) {
                        return "";
                    }
                }
                return null;
            }

        };
        edRekTujuan.setFilters(new InputFilter[]{filter});
        edRekTujuan.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(30)});
        edRekTujuan.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        edKdBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialogViewOperator();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jwtlocal.equals("0")) {
                    Toast.makeText(AntarBank.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                } else {
                    cekStrNominal();
                    tvErrorMsg.setVisibility(View.GONE);
                    strRekTujuan = edRekTujuan.getText().toString().trim();
                    if (!validateNoRek()) {
                        return;
                    }
                    if (!validateNominal()) {
                        return;
                    }
                    new AsyncInqTransfer().execute();
                }
            }
        });
        btnDaftarTrf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AntarBank.this, DaftarAntarBank.class);
                startActivityForResult(intent, 0);
            }
        });

        // tambahan baru ayik
        mReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, intentFilter);
        pdLoadingInq = new ProgressDialog(AntarBank.this);
        pdLoadingInq.setCancelable(false);
        pdLoadingInq.setIndeterminate(false);
        pdLoadingInq.setTitle("Memproses");
        pdLoadingInq.setMessage("Tunggu...");
        if (strImsi.equals("TIDAK ADA KARTU")) {
            showExt("ERROR", "Masukkan SIM CARD!");
        } else {
            new AsyncInqListBank().execute();
        }
    }

    @SuppressLint("MissingSuperCall")
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String kdbank = intent.getStringExtra("KDBANK");
                String nmkdbank = intent.getStringExtra("NMKDBANK");
                String rekening = intent.getStringExtra("REKENING");
                strPRDID = kdbank;
                strOperator = nmkdbank;
                edKdBank.setText(nmkdbank);
                edRekTujuan.setText(rekening);
                edNominal.requestFocus();
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    ListAdapter adapter;
    ArrayList<HashMap<String, String>> filteredList;
    private ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();

    private void AlertDialogViewOperator() {

        final boolean[] isFilter = {false};

        adapter = new SimpleAdapter(this, oslist, R.layout.list_item,
                new String[]{"bank_name"}, new int[]{R.id.text1});

        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_pdam, null);
        alertDialog.setView(dialogView);

        ListView listView = dialogView.findViewById(R.id.list_pdam);
        listView.setAdapter(adapter);

        EditText edSearch = dialogView.findViewById(R.id.ed_search_pdam);

        edSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                isFilter[0] = true;

                query = query.toString().toUpperCase();
                filteredList = new ArrayList<HashMap<String, String>>();

                for (HashMap<String, String> model : oslist) {
                    final String text1 = model.get("bank_name");
                    final String text2 = model.get("bank_code");

                    if (text1.contains(query)) {
                        model.put("bank_name", text1);
                        model.put("bank_code", text2);
                        filteredList.add(model);
                    }
                }


                adapter = new SimpleAdapter(AntarBank.this, filteredList, R.layout.list_item,
                        new String[]{"bank_name"}, new int[]{R.id.text1});
                listView.setAdapter(adapter);
            }
        });

        final Dialog dialog = alertDialog.create();
        dialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (isFilter[0] && filteredList != null) {

                    strOperator = filteredList.get(+position).get("bank_name");
                    strPRDID = filteredList.get(+position).get("bank_code");
                } else {
                    strOperator = oslist.get(+position).get("bank_name");
                    strPRDID = oslist.get(+position).get("bank_code");

                }

                //Toast.makeText(AntarBank.this, "" + strPRDID + " " + strOperator, Toast.LENGTH_SHORT).show();
                edKdBank.setText(strOperator);

                dialog.dismiss();
            }
        });

        //==========================================================================================

       /* androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(AntarBank.this);
        builder.setTitle("Pilih Kode Bank");
        builder.setSingleChoiceItems(namaY, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                strOperator = namaY[item];
                strPRDID = kodeY[item];

                Toast.makeText(AntarBank.this, ""+ strPRDID+ " "+ strOperator, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                edKdBank.setText(strOperator);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();*/
    }


    private void cekStrNominal() {
        String nom = "";
        if (edNominal.getText().toString().indexOf(",") != -1) {
            nom = edNominal.getText().toString().replace(",", "");
            if (nom.equals("")) nominal = 0;
            else nominal = Long.parseLong(nom);
        } else {
            nom = edNominal.getText().toString().replace(".", "");
            if (nom.equals("")) nominal = 0;
            else nominal = Long.parseLong(nom);
        }
    }

    private boolean validateNominal() {
        if (edNominal.getText().toString().trim().equals("")) {
            edNominal.requestFocus();
            tvErrorMsg.setVisibility(View.VISIBLE);
            tvErrorMsg.setText("Nominal tidak boleh kosong!");
            showAlert("Error", "Nominal tidak boleh kosong!");
            return false;
        } else if (nominal < 10000) {
            edNominal.requestFocus();
            edNominal.setSelection(edNominal.getText().length());
            tvErrorMsg.setVisibility(View.VISIBLE);
            tvErrorMsg.setText("Nominal transfer minimal Rp. 10.000,-");
            showAlert("Error", "Nominal transfer minimal Rp. 10.000,-");
            return false;
//        } else if (nominal > 5000000) {
//            edNominal.requestFocus();
//            edNominal.setSelection(edNominal.getText().length());
//            tvErrorMsg.setVisibility(View.VISIBLE);
//            tvErrorMsg.setText("Nominal transfer maksimal Rp. 5.000.000,-");
//            showAlert("Error", "Nominal transfer maksimal Rp. 5.000.000,-");
//            return false;
        } else {
            tvErrorMsg.setVisibility(View.GONE);
            tvErrorMsg.setText("");
        }
        return true;
    }

    private boolean validateNoRek() {
        if (!strRekTujuan.isEmpty() && !strPRDID.isEmpty()) {
            tvErrorMsg.setVisibility(View.GONE);
            tvErrorMsg.setText("");
        } else {
            edRekTujuan.requestFocus();
            edRekTujuan.setSelection(edRekTujuan.getText().length());
            tvErrorMsg.setVisibility(View.VISIBLE);
            tvErrorMsg.setText("Penulisan No. Rekening atau Kode Bank salah/kurang.");
            showAlert("Error", "Penulisan No. Rekening atau Kode Bank salah/kurang.");
            return false;
        }
        return true;
    }

    private class AsyncInqListBank extends AsyncTask<Void, Void, Void> {
        private Boolean status = false;
        private String ket = "405 Error koneksi terputus!!\nSilahkan coba lagi.";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadingInq.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL objs = new URL(MyVal.URL_BASE() + new NumSky(AntarBank.this).decrypt(getResources().getString(R.string.urlGetBank)));
                HttpURLConnection conJ = (HttpURLConnection) objs.openConnection();
                conJ.setRequestMethod("GET");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                conJ.setConnectTimeout(30000);
                conJ.setReadTimeout(29000);
                ket = conJ.getResponseCode() + " " + conJ.getResponseMessage();
                BufferedReader in = new BufferedReader(new InputStreamReader(conJ.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine).append("\n");
                }
                in.close();
                if (conJ.getResponseCode() == 200) {
                    status = true;
                    JSONParser parser = new JSONParser();
                    Object objects = parser.parse(response.toString());
                    JSONObject jsonObject = (JSONObject) objects;
                    status = (Boolean) jsonObject.get("status");
                    if (status) {
                        JSONArray jsonArray = (JSONArray) jsonObject.get("data");
//                        System.out.println(">>>> " + jsonArray);
                        for (Object jObjs : jsonArray) {
                            JSONObject objsa = (JSONObject) jObjs;
                            String kd = (String) objsa.get("bank_code");
                            String nama = (String) objsa.get("bank_name");
//                            System.out.println(">>> " + kd);
//                            System.out.println(">>> " + nama);
                            namaX.add(nama);
                            kodeX.add(kd);

                            HashMap<String, String> map = new HashMap<>();
                            map.put("bank_name", nama);
                            map.put("bank_code", kd);
                            oslist.add(map);
                        }
                    } else ket = (String) jsonObject.get("keterangan");
                } else {
                    status = false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                status = false;
                ket = "405 Error Exception, koneksi terputus!!\nSilahkan coba lagi.";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoadingInq.isShowing()) pdLoadingInq.dismiss();
            if (!status) {
                String msga = "#" + ket + "\n";
                showExt("GAGAL", msga);
            } else {
                namaY = new String[namaX.size()];
                namaY = (String[]) namaX.toArray(namaY);

                kodeY = new String[kodeX.size()];
                kodeY = (String[]) kodeX.toArray(kodeY);
//                System.out.println(">>>> " + namaY[1]);
//                System.out.println(">>>>> " + kodeY[1]);
            }
        }
    }

    private class AsyncInqTransfer extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(AntarBank.this);
        private Boolean stInqTrf = false;
        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
        private Date today = new Date();
        private Locale id = new Locale("in", "ID");
        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi";

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
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(AntarBank.this).decrypt(getResources().getString(R.string.urlInqBank)));

                Log.d("AYIK", "trfbank:obj "+ obj.toString());

                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = toJsonString(Utility.md5(strNokartu), Utility.md5(strImsi), strRekTujuan, strPRDID, nominal, "0", "0", "0");

                Log.d("AYIK", "trfbank:cek "+ strCek);

                conJ.setConnectTimeout(30000);
                conJ.setReadTimeout(29000);
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

                Log.d("AYIK", "trfbank:response "+ response.toString());

                JSONParser parser = new JSONParser();
                Object objects = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) objects;
//                System.out.println(">>>> " + jsonObject);
                stInqTrf = (Boolean) jsonObject.get("status");
                ket = (String) jsonObject.get("keterangan");
                trxID = Long.toString((long) jsonObject.get("trxid"));
                strNama = (String) jsonObject.get("nama");
                nominal = (long) jsonObject.get("nominal");
                double adminx = (double) jsonObject.get("admin");
                admin = (long) adminx;
                strRekTujuan = (String) jsonObject.get("rekening");
                jwtlocal = (String) jsonObject.get("jwt");
                setPrefsAuthToken(jwtlocal);

            } catch (Exception ex) {
                ex.printStackTrace();
                stInqTrf = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoading.isShowing()) pdLoading.dismiss();
            strTgl = sdf.format(today);
            if (stInqTrf) {
                showConfirmTransfer("KONFIRMASI\nTRANSFER BANK", ": " + strRekTujuan, ": " + strNama,
                        ": Rp. " + DoubleToCurrency(nominal) + ",-",
                        ": Rp. " + DoubleToCurrency(admin) + ",-",
                        ": Rp. " + DoubleToCurrency(nominal + admin) + ",-");
            } else {
                String msga = "#" + ket + " - " + strRekTujuan + "\n";
                if (!ket.equals("404 Error koneksi terputus!!\nSilahkan coba lagi")) {
                    SysDB dbsys = new SysDB(AntarBank.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Transfer GAGAL " + msga);
                    dbsys.close();
                }
                tvErrorMsg.setVisibility(View.VISIBLE);
                tvErrorMsg.setText(msga);
                showAlert("Transfer GAGAL", msga);
            }
        }
    }

    private String toJsonString(String nokartu, String imsi, String rekening, String kdbank, long nominal, String pin, String idtrx, String nama) {
        JSONObject obj = new JSONObject();
        obj.put("nokartu", nokartu);
        obj.put("imsi", imsi);
        obj.put("rekening", rekening);
        obj.put("nominal", nominal);
        obj.put("kodebank", kdbank);
        if (!nama.equals("0")) obj.put("nama", nama);
        if (!idtrx.equals("0")) obj.put("trxid", idtrx);
        if (!pin.equals("0")) obj.put("pin", pin);
        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            obj.put("versi", pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    private class AsyncExeTransfer extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(AntarBank.this);
        private Boolean strExeTrf = false;
        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
        private Date today = new Date();
        private Locale id = new Locale("in", "ID");
        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
        private String ket = "404 Kesalahan koneksi, transaksi terputus! (TIMEOUT)\nSilahkan cek mutasi transaksi terakhir.",
                strFaktur = "", strRekAsal = "", strDate = "", reffno1 = "", reffno2 = "";

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
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(AntarBank.this).decrypt(getResources().getString(R.string.urlPayBank)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = toJsonString(Utility.md5(strNokartu), Utility.md5(strImsi), strRekTujuan, strPRDID, nominal, Utility.md5(strPIN), trxID, strNama);
                conJ.setConnectTimeout(30000);
                conJ.setReadTimeout(29000);
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
//                System.out.println(">>>> " + jsonObject);
                strExeTrf = (Boolean) jsonObject.get("status");
                ket = (String) jsonObject.get("keterangan");
                strNama = (String) jsonObject.get("nama");
                nominal = (long) jsonObject.get("nominal");
                double adminx = (double) jsonObject.get("admin");
                admin = (long) adminx;
                strRekTujuan = (String) jsonObject.get("rekening");
                strFaktur = (String) jsonObject.get("faktur");
                strRekAsal = (String) jsonObject.get("rekeningasal");
                strDate = (String) jsonObject.get("datetime");
                //jwtlocal = (String) jsonObject.get("jwt");
                trxID = (String) jsonObject.get("trxid");
                reffno1 = (String) jsonObject.get("reffno1");
                reffno2 = (String) jsonObject.get("reffno2");
            } catch (Exception ex) {
                ex.printStackTrace();
                strExeTrf = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoading.isShowing()) pdLoading.dismiss();
            strTgl = sdf.format(today);
            if (strExeTrf) {
                ket =     "Tanggal   : " + strDate +
                        "\nFaktur    : " + strFaktur +
                        "\nDari      : " + strRekAsal +
                        "\nKe        : " + strOperator +
                        "\nNo. Rek   : " + strRekTujuan +
                        "\nA.n.      : " + strNama +
                        "\nNominal   : Rp. " + DoubleToCurrency(nominal) + ",-" +
                        "\nAdmin     : Rp. " + DoubleToCurrency(admin) + ",-" +
                        "\nJumlah    : Rp. " + DoubleToCurrency(nominal+admin) + ",-" +
                        "\nNo. Reff1 : " + reffno1 +
                        "\nNo. Reff2 : " + reffno2 +
                        "\nTrxID     : " + trxID + "\n";
                SysDB dbsys = new SysDB(AntarBank.this);
                dbsys.open();
                dbsys.CreateTableSys();
                dbsys.insertSys(strTgl, "Transfer SUKSES\n" + ket);
                dbsys.close();
                showConfirmTransferSukses("Transfer SUKSES", "" + strDate, "" + strFaktur,
                        "" + strRekAsal, "" + strRekTujuan, "" + strNama,
                        "Rp " + DoubleToCurrency(nominal) + ",-", "" + reffno1, "" + reffno2,
                        "" + trxID, "Rp " + DoubleToCurrency(admin) + ",-", "Rp " + DoubleToCurrency(nominal + admin) + ",-");
            } else {
                String msga = "#" + ket + "\n";
                if (!ket.startsWith("404")) {
                    SysDB dbsys = new SysDB(AntarBank.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Transfer Bank GAGAL " + msga);
                    dbsys.close();
                    tvErrorMsg.setVisibility(View.VISIBLE);
                    tvErrorMsg.setText(msga);
                    showAlert("Transfer Bank GAGAL", msga);
                } else {
                    SysDB dbsys = new SysDB(AntarBank.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Transfer Bank TIMEOUT " + msga);
                    dbsys.close();
                    tvErrorMsg.setVisibility(View.VISIBLE);
                    tvErrorMsg.setText(msga);
                    showAlert("Transfer Bank TIMEOUT", msga);
                }
            }
        }
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finis = true;
                dialog.dismiss();
            }
        }).show();
    }

    private void showExt(String title, String message) {
        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        }).show();
    }

    private void showConfirmTransferSukses(String Judul, String tgls, String faktur, String darirek, String kerek, String atasnama, String nominal, String reff1, String reff2, String trxid, String admin, String jumlah) {

        Intent antar = new Intent(AntarBank.this, AntarBankSukses.class);

        ArrayList<String> arr = new ArrayList<String>();

        arr.add("Tanggal#" + tgls);
        arr.add("Faktur#" + faktur);
        arr.add("Dari Rek.#" + darirek);
        arr.add("Ke Bank#" + strOperator);
        arr.add("Ke Rek.#" + kerek);
        arr.add("A.n.#" + atasnama);
        arr.add("Nominal# " + nominal + "");
        arr.add("Admin# " + admin + "");
        arr.add("Jumlah# " + jumlah + "");
        arr.add("No. Reff1#" + reff1);
        arr.add("No. Reff2#" + reff2);
        arr.add("TrxID#" + trxid);

        antar.putExtra("judul", "Transfer Bank Berhasil");
        antar.putStringArrayListExtra("arr", arr);

        startActivity(antar);
        finish();

        /*final Dialog dialog = new Dialog(AntarBank.this);
        Utility.playNotificationSound(AntarBank.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_confirm_trfsukses_bank);
        TextView textPin = (TextView) dialog.findViewById(R.id.textPin);
        textPin.setText(Judul);
        TextView tgl = (TextView) dialog.findViewById(R.id.tgl);
        tgl.setText(tgls);
        TextView prods = (TextView) dialog.findViewById(R.id.faktur);
        prods.setText(faktur);
        TextView noh = (TextView) dialog.findViewById(R.id.darirek);
        noh.setText(darirek);
        TextView kdbnk = (TextView) dialog.findViewById(R.id.kdbank);
        kdbnk.setText(": " + strOperator);
        TextView tvkerek = (TextView) dialog.findViewById(R.id.kerek);
        tvkerek.setText(kerek);
        TextView tvan = (TextView) dialog.findViewById(R.id.atasnama);
        tvan.setText(atasnama);
        TextView tvNom = (TextView) dialog.findViewById(R.id.nominal);
        tvNom.setText(nominal);
        TextView tvAdmin = (TextView) dialog.findViewById(R.id.admin);
        tvAdmin.setText(admin);
        TextView tvJml = (TextView) dialog.findViewById(R.id.jumlah);
        tvJml.setText(jumlah);
        TextView nreff1 = (TextView) dialog.findViewById(R.id.reff1);
        nreff1.setText(reff1);
        TextView nreff2 = (TextView) dialog.findViewById(R.id.reff2);
        nreff2.setText(reff2);
        TextView ntrxid = (TextView) dialog.findViewById(R.id.trxid);
        ntrxid.setText(trxid);
        dialog.setCancelable(false);
        Button buttonDialogYes = (Button) dialog.findViewById(R.id.button_dialog_yes);
        buttonDialogYes.setText("OK SIMPAN");
        buttonDialogYes.setTextSize(20);
        buttonDialogYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Dialog dialog2 = Dialog.class.cast(dialog);
                takeScreenshot(dialog2);
                onBackPressed();
                dialog.dismiss();
            }
        });
        dialog.show();*/
    }

    private void showConfirmTransfer(String Judul, final String rek, final String an, String nom, String admin, String jumlah) {
        final Dialog dialog = new Dialog(AntarBank.this);
        finis = false;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_confirm_trx_bank);
        TextView textPin = (TextView) dialog.findViewById(R.id.textPin);
        textPin.setText(Judul);
        TextView kdbnk = (TextView) dialog.findViewById(R.id.kdbank);
        kdbnk.setText(": " + strOperator);
        TextView reke = (TextView) dialog.findViewById(R.id.norek);
        reke.setText(rek);
        TextView ana = (TextView) dialog.findViewById(R.id.nama);
        ana.setText(an);
        TextView tvNom = (TextView) dialog.findViewById(R.id.nominal);
        tvNom.setText(nom);
        TextView tvAdmin = (TextView) dialog.findViewById(R.id.admin);
        tvAdmin.setText(admin);
        TextView tvJml = (TextView) dialog.findViewById(R.id.jumlah);
        tvJml.setText(jumlah);
        SysDB dbsys = new SysDB(AntarBank.this);
        dbsys.open();
        dbsys.CreateTableTransferAB();
        int jumla = 0;
        Cursor cur1 = dbsys.cekTrfDataAB(strPRDID, strRekTujuan);
        if (cur1.moveToFirst()) {
            jumla = cur1.getInt(0);
        }
        cur1.close();
        dbsys.close();
        final CheckBox cbsimpan = (CheckBox) dialog.findViewById(R.id.cbSimpan);
        if (jumla > 0) {
            cbsimpan.setVisibility(View.GONE);
            cbsimpan.setEnabled(false);
        }
        dialog.setCancelable(false);
        Button buttonDialogNo = (Button) dialog.findViewById(R.id.button_dialog_no);
        buttonDialogNo.setText("BATAL");
        buttonDialogNo.setTextSize(20);
        buttonDialogNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finis = true;
                dialog.cancel();
            }
        });
        Button buttonDialogYes = (Button) dialog.findViewById(R.id.button_dialog_yes);
        buttonDialogYes.setText("OK");
        buttonDialogYes.setTextSize(20);
        buttonDialogYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (jwtlocal.equals("0"))
                    Toast.makeText(AntarBank.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    if (cbsimpan.isChecked()) {
                        try {
                            SysDB dbsys = new SysDB(AntarBank.this);
                            dbsys.open();
                            dbsys.insertTrfab(strPRDID, strOperator, strRekTujuan, strNama);
                            dbsys.close();
                        } catch (SQLiteException e) {
                            e.printStackTrace();
                        }
                    }
                    showConfirmPinTransfer(getString(R.string.pin_mobilebmt));
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private static long mLastClickTime = 0;

    private void showConfirmPinTransfer(String msg) {
        final Dialog dialog = new Dialog(AntarBank.this);
        finis = false;
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
                finis = true;
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
                if (jwtlocal.equals("0"))
                    Toast.makeText(AntarBank.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    buttonDialogYes.setEnabled(false);
                    strPIN = edPin.getText().toString().trim();
                    edPin.setText("");
                    if (Utility.is6digit(strPIN)) {
                        new AsyncExeTransfer().execute();
                    } else {
                        showAlert("Error", "Pin harus 6 digit angka!");
                    }
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void takeScreenshot(Dialog dialog) {
        try {
            String sDCard = "";
            sDCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            File newFolder = new File(sDCard + getString(R.string.path1));
            if (!newFolder.exists()) {
                newFolder.mkdirs();
            }
            SimpleDateFormat sdf4 = new SimpleDateFormat("yyyyMMdd-HHmmss");
            Date naw = new Date();
            String now = sdf4.format(naw);
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = sDCard + getString(R.string.path2) + now + ".jpg";
            // create bitmap screen capture

            View v1 = dialog.getWindow().getDecorView().getRootView();
//            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            bitmap = mark(bitmap, getString(R.string.watermark));
            v1.setDrawingCacheEnabled(false);
            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(getApplicationContext(), "Screenshoot tersimpan di " + mPath, Toast.LENGTH_SHORT).show();
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            Toast.makeText(AntarBank.this, "Gagal menyimpan screenshoot", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private static Bitmap mark(Bitmap src, String watermark) {
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
    }

    // tambahan baru ayik
    @Override
    public void onPostExec(Boolean status, String jwt) {
        if (status) {
            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_blue);
            jwtlocal = jwt;
            cnd.countD.start();
        } else {
            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_red);
            jwtlocal = jwt;

            if (jwtlocal.equals("401")) {
                Utility2.showAlertRelogin(AntarBank.this);
            }

        }
        sts = sts + 1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.KEY_COUNTDOWNTIMER));
        if (stats && sts > 1) {
            cnd.countD.cancel();
            jwtlocal = "0";
            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_yellow);
            AuthLogin2 task = new AuthLogin2(AntarBank.this, AntarBank.this);
            task.execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_status).setEnabled(false);
        menu.findItem(R.id.action_logout).setVisible(false);
        statusMenu = menu;
        if (!stats) {
            cnd.countD.cancel();
            jwtlocal = "0";
            AuthLogin2 task = new AuthLogin2(AntarBank.this, AntarBank.this);
            task.execute();
            stats = true;
            sts = 1;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        cnd.countD.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (Utility.isNetworkAvailable(context)) ;
            onResume();
        }
    }

    private void mReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.KEY_COUNTDOWNTIMER)) {
                    if (finis) //finish();
                        Utility2.showAlertRelogin(AntarBank.this);
                    else {
//                        Toast.makeText(GantiPin.this, "Waktu koneksi habis, pastikan indikator koneksi berwarna biru, silahkan restart aplikasi", Toast.LENGTH_SHORT).show();
                        cnd.countD.cancel();
                        jwtlocal = "0";
                        statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_yellow);
                        AuthLogin2 task = new AuthLogin2(AntarBank.this, AntarBank.this);
                        task.execute();
                    }
                }
            }
        };
    }
}
