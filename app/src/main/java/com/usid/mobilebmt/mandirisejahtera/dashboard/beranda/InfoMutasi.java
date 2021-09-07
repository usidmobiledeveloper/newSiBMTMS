package com.usid.mobilebmt.mandirisejahtera.dashboard.beranda;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.mutasipembiayaan.MutasiPembiayaan;
import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.mutasisimpanan.MutasiSimpanan;
import com.usid.mobilebmt.mandirisejahtera.databinding.ActivityInfoMutasiBinding;
import com.usid.mobilebmt.mandirisejahtera.model.SysDB;
import com.usid.mobilebmt.mandirisejahtera.notifications.Config;
import com.usid.mobilebmt.mandirisejahtera.utils.AuthLogin2;
import com.usid.mobilebmt.mandirisejahtera.utils.Ctd;
import com.usid.mobilebmt.mandirisejahtera.utils.IAsyncHandler;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.finisa;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility.disableViews;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility.enableViews;

public class InfoMutasi extends AppCompatActivity implements IAsyncHandler {
    private Bundle extras;
    private String datas;
    private TextView tvInfo;
    private ListView lv1;
    private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss", titleActionBar = "", msga = "", tgl = "";
    private Date today = new Date();
    private Locale id = new Locale("in", "ID");
    private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
    static final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

    ActivityInfoMutasiBinding binding;

    private Menu statusMenu;
    public static String jwtlocal;
    private int sts = 0;
    private boolean stats = false;
    private Ctd cnd;
    private NetworkChangeReceiver receiver;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean finis = true;

    MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder;
    MaterialDatePicker materialDatePicker;
    String start = "", end = "";
    String strnorek = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        binding = ActivityInfoMutasiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cnd = new Ctd(InfoMutasi.this);

        tvInfo = (TextView) findViewById(R.id.tvInfo);
        lv1 = (ListView) findViewById(android.R.id.list);
        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if (extras == null) {
                datas = "";
                titleActionBar = "";
                tgl = "";
            } else {
                datas = extras.getString("list.mutasi");
                titleActionBar = extras.getString("TITLE");
                tgl = extras.getString("tgl");
            }
        } else {
            datas = (String) savedInstanceState.getSerializable("list.mutasi");
            titleActionBar = (String) savedInstanceState.getSerializable("TITLE");
            tgl = (String) savedInstanceState.getSerializable("tgl");
        }

        actionBar.setTitle(titleActionBar);

        defaultAdapter();

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (titleActionBar.equals("Info Pembiayaan")) {
                    strnorek = list.get(+position).get("tgl");
                    strnorek = strnorek.replace("Rekening : ", "");

                    showFilterInfoMutasi("Limit Transaksi", strnorek);

                } else if (titleActionBar.equals("Info Simpanan")) {
                    strnorek = list.get(+position).get("tgl");
                    strnorek = strnorek.replace("Rekening : ", "");
                    //showFilterInfoMutasi("Limit Transaksi", strnorek);
                    finisa = false;
                    showOpsi(strnorek);
                    //materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                }

            }
        });

        mReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, intentFilter);

        binding.btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toInbox();
            }
        });

        materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        materialDateBuilder.setTitleText("PILIH PERIODE TANGGAL");
        materialDateBuilder.setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar);
        materialDatePicker = materialDateBuilder.build();

        materialDatePicker.addOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d("AYIK", "cancel");
                finis = true;
            }
        });

        materialDatePicker.addOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.d("AYIK", "dismiss");
                finis = true;
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPositiveButtonClick(Object selection) {
                Pair selectedDates = (Pair) materialDatePicker.getSelection();
                final Pair<Date, Date> rangeDate = new Pair<>(new Date((Long) selectedDates.first), new Date((Long) selectedDates.second));

                Date startDate = rangeDate.first;
                Date endDate = rangeDate.second;
                Date nowDate = Calendar.getInstance().getTime();

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");

                start = simpleFormat.format(startDate);
                end = simpleFormat.format(endDate);

                long svnDays = endDate.getTime() - startDate.getTime();
                svnDays = TimeUnit.DAYS.convert(svnDays, TimeUnit.MILLISECONDS);

                long thrDays = nowDate.getTime() - startDate.getTime();
                thrDays = TimeUnit.DAYS.convert(thrDays, TimeUnit.MILLISECONDS);

                if (jwtlocal.equals("0"))
                    Toast.makeText(InfoMutasi.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {

                    if (thrDays <= 30) {
                        if (svnDays <= 7) {
                            Intent intent = new Intent(InfoMutasi.this, MutasiSimpanan.class);
                            intent.putExtra("norek", strnorek);
                            intent.putExtra("tglmulai", start);
                            intent.putExtra("tglsampai", end);
                            intent.putExtra("jmltrx", jmltrx);
                            startActivity(intent);

                        } else {
                            Toast.makeText(InfoMutasi.this, "Maksimal periode yang dipilih adalah 7 hari", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(InfoMutasi.this, "Maksimal 30 hari ke belakang dari tanggal sekarang", Toast.LENGTH_SHORT).show();

                    }
                    //showConfirmPinCekSaldo(getString(R.string.pin_mobilebmt), 6);

                }

            }

        });
    }

    private void showOpsi(String strnorek) {

        finisa = false;

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(InfoMutasi.this);
        builderSingle.setTitle("Pilih Metode");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(InfoMutasi.this,
                android.R.layout.simple_list_item_1);
        arrayAdapter.add("Berdasarkan Jumlah Transaksi Terakhir");
        arrayAdapter.add("Berdasarkan Periode Tanggal");

        builderSingle.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finisa = true;
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finisa = true;
                String opsi = arrayAdapter.getItem(which);
                Log.d("AYIK", "opsi " + opsi);
                if (opsi.equals("Berdasarkan Jumlah Transaksi Terakhir")) {
                    showFilterInfoMutasi("Limit Transaksi", strnorek);
                } else {
                    finisa = false;
                    materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                }
            }
        });
        builderSingle.show();
    }

    /*@OnClick(R.id.btnOK)*/
    private void toInbox() {
        SysDB dbsys = new SysDB(getApplicationContext());
        dbsys.open();
        dbsys.CreateTableSys();
        dbsys.insertSys(strTgl, msga);
        dbsys.close();
        Toast.makeText(this, "Sukses simpan ke Inbox", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void defaultAdapter() {
        list.clear();
        lv1.setVisibility(View.VISIBLE);
        tvInfo.setVisibility(View.GONE);

        SimpleAdapter adapterMutasi = new SimpleAdapter(this, list, R.layout.list_mutasi_strip, new String[]{"tgl", "ket", "nominal", "dk"}, new int[]{R.id.text1, R.id.text2, R.id.text3, R.id.text4}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the current item from ListView
                View view = super.getView(position, convertView, parent);
                TextView dk = view.findViewById(R.id.text4);
                TextView ket = view.findViewById(R.id.text2);
                TextView db = view.findViewById(R.id.tv_ppob);
                TextView kr = view.findViewById(R.id.tv_transfer);

                TextView nom = view.findViewById(R.id.text3);

                ImageButton ibshow = view.findViewById(R.id.plus);
                ImageButton ibhide = view.findViewById(R.id.minus);

                if (position % 2 == 1) {
                    view.setBackgroundColor(Color.parseColor("#FFD1F8E2"));

                } else {
                    view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }

                if (dk.getText().toString().equalsIgnoreCase("DEBIT")) {
                    db.setVisibility(View.VISIBLE);
                    kr.setVisibility(View.GONE);
                    nom.setTextColor(Color.parseColor("#990000"));
                } else {
                    db.setVisibility(View.GONE);
                    kr.setVisibility(View.VISIBLE);
                    nom.setTextColor(Color.parseColor("#00923F"));
                }

                ibshow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ibhide.setVisibility(View.VISIBLE);
                        ibshow.setVisibility(View.GONE);
                        ket.setVisibility(View.VISIBLE);

                    }
                });

                ibhide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ibhide.setVisibility(View.GONE);
                        ibshow.setVisibility(View.VISIBLE);
                        ket.setVisibility(View.GONE);
                    }
                });


                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ket.isShown()) {
                            ibhide.setVisibility(View.GONE);
                            ibshow.setVisibility(View.VISIBLE);
                            ket.setVisibility(View.GONE);
                        } else {
                            ibhide.setVisibility(View.VISIBLE);
                            ibshow.setVisibility(View.GONE);
                            ket.setVisibility(View.VISIBLE);
                        }
                    }
                });

                return view;
            }
        };

        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.list_mutasi, new String[]{"tgl", "ket", "nominal"}, new int[]{R.id.text1, R.id.text2, R.id.text3}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the current item from ListView
                View view = super.getView(position, convertView, parent);

                ImageView ivMda = view.findViewById(R.id.iv_mutasi_mda);
                ImageView ivPembiayaan = view.findViewById(R.id.iv_mutasi_pembiayaan);
                ImageView ivSimpanan = view.findViewById(R.id.iv_mutasi_simpanan);

                switch (titleActionBar) {
                    case "Info MDA Berjangka":
                        enableViews(ivMda);
                        disableViews(ivPembiayaan, ivSimpanan);
                        break;
                    case "Info Pembiayaan":
                        enableViews(ivPembiayaan);
                        disableViews(ivMda, ivSimpanan);
                        break;
                    case "Info Simpanan":
                        enableViews(ivSimpanan);
                        disableViews(ivPembiayaan, ivMda);
                        break;
                }

                return view;
            }
        };

        if (datas != null) {
            try {

                strTgl = sdf.format(today);

                switch (titleActionBar) {
                    case "Info Mutasi": {
                        msga = "Info Mutasi " + tgl;
                        JSONParser parser = new JSONParser();
                        Object objects = parser.parse(datas);
                        JSONObject jsonObject = (JSONObject) objects;
                        JSONArray jsarrys = (JSONArray) jsonObject.get("data");
                        int lghtjasrr = jsarrys.size();
                        if (lghtjasrr != 0) {
                            for (int i = 0; i < lghtjasrr; i++) {
                                JSONObject jobj = (JSONObject) jsarrys.get(i);
                                String tgl = (String) jobj.get("tgl");
                                String jml = (String) jobj.get("jumlah");
                                String dk = (String) jobj.get("dk");
                                String ket = (String) jobj.get("keterangan");
                                msga = msga + "\n" + tgl + " - " + "Rp. " + jml + " " + dk;
                                HashMap<String, String> temp = new HashMap<String, String>();
                                temp.put("tgl", tgl);
                                temp.put("ket", ket);


                                if (dk.equals("D")) {
                                    dk = "DEBIT";
                                    temp.put("nominal", "-Rp. " + jml);
                                } else {
                                    dk = "KREDIT";
                                    temp.put("nominal", "Rp. " + jml);
                                }

                                temp.put("dk", "" + dk);
                                list.add(temp);
                            }
                            lv1.setAdapter(adapterMutasi);
                        } else {
                            msga = "Info Mutasi " + tgl + " Data Mutasi tidak ada";
                            lv1.setVisibility(View.GONE);
                            binding.btnOK.setVisibility(View.GONE);
                            tvInfo.setVisibility(View.VISIBLE);
                            switch (titleActionBar) {
                                case "Info Mutasi":
                                    tvInfo.setText("Tidak Ada Mutasi");
                                    break;
                                case "Info MDA Berjangka":
                                    tvInfo.setText("Tidak Ada MDA Berjangka");
                                    break;
                                case "Info Pembiayaan":
                                    tvInfo.setText("Tidak Ada Pembiayaan");
                                    break;
                            }
                        }
                        break;
                    }

                    case "Info MDA Berjangka": {
                        msga = "Info MDA Berjangka " + tgl + "\n";
                        JSONParser parser = new JSONParser();
                        Object objects = parser.parse(datas);
                        JSONObject jsonObject = (JSONObject) objects;
                        JSONArray jsarrys = (JSONArray) jsonObject.get("data");
                        int lghtjasrr = jsarrys.size();
                        if (lghtjasrr != 0) {
                            for (int i = 0; i < lghtjasrr; i++) {
                                JSONObject jobj = (JSONObject) jsarrys.get(i);
                                String tgl = (String) jobj.get("jatuhtempo");
                                String jml = (String) jobj.get("saldo");
                                String rek = (String) jobj.get("rekeningdeposito");
                                msga = msga + "\nJatuh Tempo : " + tgl + "\nRekening : " + rek + "\nSaldo : Rp. " + jml + "\n";
                                HashMap<String, String> temp = new HashMap<String, String>();
                                temp.put("tgl", "Rekening : " + rek);
                                temp.put("ket", "Jatuh Tempo : " + tgl);
                                temp.put("nominal", "Saldo : Rp. " + jml);
                                list.add(temp);
                            }
                            lv1.setAdapter(adapter);
                        } else {
                            msga = "Info MDA Berjangka " + tgl + " Data MDA Berjangka tidak ada";
                            lv1.setVisibility(View.GONE);
                            binding.btnOK.setVisibility(View.GONE);
                            tvInfo.setVisibility(View.VISIBLE);
                            switch (titleActionBar) {
                                case "Info Mutasi":
                                    tvInfo.setText("Tidak Ada Mutasi");
                                    break;
                                case "Info MDA Berjangka":
                                    tvInfo.setText("Tidak Ada MDA Berjangka");
                                    break;
                                case "Info Pembiayaan":
                                    tvInfo.setText("Tidak Ada Pembiayaan");
                                    break;
                            }
                        }
                        break;
                    }

                    case "Info Pembiayaan": {
                        msga = "Info Pembiayaan " + tgl + "\n";
                        JSONParser parser = new JSONParser();
                        Object objects = parser.parse(datas);
                        JSONObject jsonObject = (JSONObject) objects;
                        JSONArray jsarrys = (JSONArray) jsonObject.get("data");
                        int lghtjasrr = jsarrys.size();
                        if (lghtjasrr != 0) {
                            for (int i = 0; i < lghtjasrr; i++) {
                                JSONObject jobj = (JSONObject) jsarrys.get(i);
                                String tgl = (String) jobj.get("tgl");
                                String jml = (String) jobj.get("saldototal");
                                String rek = (String) jobj.get("rekeningpembiayaan");
                                msga = msga + "\nTgl Pencairan : " + tgl + "\nRekening : " + rek + "\nSaldo : Rp. " + jml + "\n";
                                HashMap<String, String> temp = new HashMap<String, String>();
                                temp.put("tgl", "Rekening : " + rek);
                                temp.put("ket", "Tgl Pencairan : " + tgl);
                                temp.put("nominal", "Saldo : Rp. " + jml);
                                list.add(temp);
                            }
                            lv1.setAdapter(adapter);
                        } else {
                            msga = "Info Pembiayaan " + tgl + " Data Pembiayaan tidak ada";
                            lv1.setVisibility(View.GONE);
                            binding.btnOK.setVisibility(View.GONE);
                            tvInfo.setVisibility(View.VISIBLE);
                            switch (titleActionBar) {
                                case "Info Mutasi":
                                    tvInfo.setText("Tidak Ada Mutasi");
                                    break;
                                case "Info MDA Berjangka":
                                    tvInfo.setText("Tidak Ada MDA Berjangka");
                                    break;
                                case "Info Pembiayaan":
                                    tvInfo.setText("Tidak Ada Pembiayaan");
                                    break;
                            }
                        }
                        break;
                    }

                    case "Info Simpanan": {
                        msga = "Info Simpanan " + tgl + "\n";
                        JSONParser parser = new JSONParser();
                        Object objects = parser.parse(datas);
                        JSONObject jsonObject = (JSONObject) objects;
                        JSONArray jsarrys = (JSONArray) jsonObject.get("data");
                        int lghtjasrr = jsarrys.size();
                        if (lghtjasrr != 0) {
                            for (int i = 0; i < lghtjasrr; i++) {
                                JSONObject jobj = (JSONObject) jsarrys.get(i);
                                String tgl = (String) jobj.get("golongan");
                                String jml = (String) jobj.get("saldo");
                                String rek = (String) jobj.get("rekening");
                                msga = msga + "\nJenis : " + tgl + "\nRekening : " + rek + "\nSaldo : Rp. " + jml + "\n";
                                HashMap<String, String> temp = new HashMap<String, String>();
                                temp.put("tgl", "Rekening : " + rek);
                                temp.put("ket", "Jenis : " + tgl);
                                temp.put("nominal", "Saldo : Rp. " + jml);
                                list.add(temp);

                            }
                            lv1.setAdapter(adapter);
                        } else {
                            msga = "Info Simpanan " + tgl + " Data Simpanan tidak ada";
                            binding.btnOK.setVisibility(View.GONE);
                            lv1.setVisibility(View.GONE);
                            tvInfo.setVisibility(View.VISIBLE);

                            switch (titleActionBar) {
                                case "Info Mutasi":
                                    tvInfo.setText("Tidak Ada Mutasi");
                                    break;
                                case "Info MDA Berjangka":
                                    tvInfo.setText("Tidak Ada MDA Berjangka");
                                    break;
                                case "Info Pembiayaan":
                                    tvInfo.setText("Tidak Ada Pembiayaan");
                                    break;
                            }
                        }
                        break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                binding.btnOK.setVisibility(View.GONE);
                lv1.setVisibility(View.GONE);
                tvInfo.setVisibility(View.VISIBLE);
                switch (titleActionBar) {
                    case "Info Mutasi":
                        tvInfo.setText("Tidak Ada Mutasi");
                        break;
                    case "Info MDA Berjangka":
                        tvInfo.setText("Tidak Ada MDA Berjangka");
                        break;
                    case "Info Pembiayaan":
                        tvInfo.setText("Tidak Ada Pembiayaan");
                        break;
                }
            }
        } else {
            lv1.setVisibility(View.GONE);
            binding.btnOK.setVisibility(View.GONE);
            tvInfo.setVisibility(View.VISIBLE);
            switch (titleActionBar) {
                case "Info Mutasi":
                    tvInfo.setText("Tidak Ada Mutasi");
                    break;
                case "Info MDA Berjangka":
                    tvInfo.setText("Tidak Ada MDA Berjangka");
                    break;
                case "Info Pembiayaan":
                    tvInfo.setText("Tidak Ada Pembiayaan");
                    break;
            }
        }

    }

    private int jmltrx = 0;

    private void showFilterInfoMutasi(String msg, String norek) {
        final Dialog dialog = new Dialog(this);
        finis = false;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_confirm_info_mutasi);
        TextView textInfoMutasi = (TextView) dialog.findViewById(R.id.textInfoMutasi);
        textInfoMutasi.setText(msg);
        dialog.setCancelable(true);
        Button buttonDialogNo = (Button) dialog.findViewById(R.id.button_dialog_no);
        buttonDialogNo.setText("BATAL");
        buttonDialogNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finis = true;
                dialog.cancel();
            }
        });
        Button buttonDialogYes = (Button) dialog.findViewById(R.id.button_dialog_yes);
        buttonDialogYes.setText("OK");
        buttonDialogYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (jwtlocal.equals("0"))
                    Toast.makeText(InfoMutasi.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    RadioGroup rgTrx = (RadioGroup) dialog.findViewById(R.id.RgTrx);
                    int selectedId = rgTrx.getCheckedRadioButtonId();
                    final RadioButton rbTrx = (RadioButton) dialog.findViewById(selectedId);
                    String jm = rbTrx.getText().toString().replaceAll("[^0-9]", "");
                    jmltrx = Integer.parseInt(jm);
                    dialog.dismiss();

                    if (titleActionBar.equals("Info Pembiayaan")) {
                        Intent intent = new Intent(InfoMutasi.this, MutasiPembiayaan.class);
                        intent.putExtra("norek", norek);
                        intent.putExtra("jmltrx", jmltrx);
                        startActivity(intent);
                    } else if (titleActionBar.equals("Info Simpanan")) {
                        Intent intent = new Intent(InfoMutasi.this, MutasiSimpanan.class);
                        intent.putExtra("norek", strnorek);
                        intent.putExtra("tglmulai", start);
                        intent.putExtra("tglsampai", end);
                        intent.putExtra("jmltrx", jmltrx);
                        startActivity(intent);

                    }

                }
            }
        });
        dialog.show();
    }

    @Override
    public void onPostExec(Boolean status, String jwt/*, String tokenid*/) {
        if (status) {
            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_blue);
            jwtlocal = jwt;
            cnd.countD.start();
        } else {
            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_red);
            jwtlocal = jwt;
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
            AuthLogin2 task = new AuthLogin2(InfoMutasi.this, InfoMutasi.this);
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
            AuthLogin2 task = new AuthLogin2(InfoMutasi.this, InfoMutasi.this);
            task.execute();
            stats = true;
            sts = 1;
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
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
                    if (finis) finish();
                    else {
//                        Toast.makeText(GantiPin.this, "Waktu koneksi habis, pastikan indikator koneksi berwarna biru, silahkan restart aplikasi", Toast.LENGTH_SHORT).show();
                        cnd.countD.cancel();
                        jwtlocal = "0";
                        statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_yellow);
                        AuthLogin2 task = new AuthLogin2(InfoMutasi.this, InfoMutasi.this);
                        task.execute();
                    }
                }
            }
        };
    }
}
