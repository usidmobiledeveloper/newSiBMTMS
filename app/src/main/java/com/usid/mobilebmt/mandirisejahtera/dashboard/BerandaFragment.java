package com.usid.mobilebmt.mandirisejahtera.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
/*
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;*/
import com.example.library.banner.BannerLayout;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.ViewImageActivity;
import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.content.InfoBiaya;
import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.content.KantorLayanan;
import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.content.ProdukActivity;
import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.content.PromoActivity;
import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.InfoMutasi;
import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.transfer.AntarBank;
import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.transfer.AntarRekening;
import com.usid.mobilebmt.mandirisejahtera.model.SysDB;
import com.usid.mobilebmt.mandirisejahtera.utils.MyVal;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;
import com.usid.mobilebmt.mandirisejahtera.utils.SnHp;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.info.SublimePickerFragment;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility2;
import com.usid.mobilebmt.mandirisejahtera.utils.banner.WebBannerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.finisa;
import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;

public class BerandaFragment extends Fragment {

    public BerandaFragment() {
        // Required empty public constructor
    }

    private CardView cvSaldo, cvMutasi, cvMda, cvPembiayaan, cvSimpanan, cvBiaya, btnTrfRekening, btnTrfBmt, btnTrfBank;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0;
    private String strPIN = "", imsi = "", nocard = "";
    private int jmltrx = 5;
    private SharedPreferences config;
    //private BannerLayout bannerLayout;

    BannerLayout recyclerBanner;
    String strImsi = "", strNokartu = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder;
    MaterialDatePicker materialDatePicker;
    String start = "", end = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.new_fragment_beranda, container, false);

        // DATE PICKER
        materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        materialDateBuilder.setTitleText("PILIH PERIODE TANGGAL");
        /*materialDateBuilder.setSelection(new Pair(MaterialDatePicker.thisMonthInUtcMilliseconds(),MaterialDatePicker.todayInUtcMilliseconds()));*/
        //To apply a dialog
        materialDateBuilder.setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar);
        //To apply the fullscreen:
        //materialDateBuilder.setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar_Fullscreen);
        materialDatePicker = materialDateBuilder.build();
        materialDatePicker.addOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d("AYIK", "cancel");
                finisa = true;
            }
        });

        materialDatePicker.addOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.d("AYIK", "dismiss");
                finisa = true;
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPositiveButtonClick(Object selection) {
                finisa = true;
                Pair selectedDates = (Pair) materialDatePicker.getSelection();
                final Pair<Date, Date> rangeDate = new Pair<>(new Date((Long) selectedDates.first), new Date((Long) selectedDates.second));

                Date startDate = rangeDate.first;
                Date endDate = rangeDate.second;
                Date nowDate = Calendar.getInstance().getTime();

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");

                start = simpleFormat.format(startDate);
                end = simpleFormat.format(endDate);

                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    long svnDays = endDate.getTime() - startDate.getTime();
                    svnDays = TimeUnit.DAYS.convert(svnDays, TimeUnit.MILLISECONDS);

                    long thrDays = nowDate.getTime() - startDate.getTime();
                    thrDays = TimeUnit.DAYS.convert(thrDays, TimeUnit.MILLISECONDS);

                    if (thrDays <= 30) {
                        if (svnDays <= 7) {
                            showConfirmPinCekSaldo(getString(R.string.pin_mobilebmt), 6);
                        } else {
                            Toast.makeText(getActivity(), "Maksimal periode yang dipilih adalah 7 hari", Toast.LENGTH_SHORT).show();
                            //Snackbar.make(view, "Maksimal periode yang dipilih adalah 7 hari", Snackbar.LENGTH_LONG));
                        }
                    } else {
                        Toast.makeText(getActivity(), "Maksimal 30 hari ke belakang dari tanggal sekarang", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        btnTrfRekening = rootView.findViewById(R.id.cv_antarrekening);
        btnTrfBmt = rootView.findViewById(R.id.cv_antarbmt);
        btnTrfBank = rootView.findViewById(R.id.cv_antarbank);
        cvSaldo = rootView.findViewById(R.id.cv_saldo);
        cvMutasi = rootView.findViewById(R.id.cv_mutasi);
        cvMda = rootView.findViewById(R.id.cv_mda);
        cvPembiayaan = rootView.findViewById(R.id.cv_pembiayaan);
        cvSimpanan = rootView.findViewById(R.id.cv_simpanan);
        cvBiaya = rootView.findViewById(R.id.cv_infobiaya);
//        bannerLayout = rootView.findViewById(R.id.banner);
        recyclerBanner = rootView.findViewById(R.id.recycler);
        CardView cvLokasi = (CardView) rootView.findViewById(R.id.cv_kantor);
        CardView cvPromo = (CardView) rootView.findViewById(R.id.cv_promo);
        CardView cvProduk = (CardView) rootView.findViewById(R.id.cv_produk);

        config = getActivity().getSharedPreferences("config", 0);
        SnHp telpMan = new SnHp(getActivity());

        if (5 != telpMan.telephonyManager().getSimState()) {
            strImsi = "TIDAK ADA KARTU";

        } else {
            strImsi = Utility.getIMSIRead(getActivity());
        }
        NumSky nmsk = new NumSky(getActivity());
        try {
            strNokartu = nmsk.decrypt(config.getString("3D0k", ""));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnTrfRekening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(getActivity(), AntarRekening.class);
                    startActivity(intent);
                }
            }
        });

        btnTrfBmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SoonActivity.class);
                i.putExtra("title", "Transfer Antar BMT");
                startActivity(i);
            }
        });

        btnTrfBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(getActivity(), AntarBank.class);
                    startActivity(intent);
                }*/

                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    new AsyncStat().execute();

                }
            }
        });

        cvSaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else showConfirmPinCekSaldo(getString(R.string.pin_mobilebmt), 1);
            }
        });
        cvMutasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else
                    //showFilterInfoMutasi("Limit Transaksi");
                    showOpsi();

            }
        });
        cvMda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else showConfirmPinCekSaldo(getString(R.string.pin_mobilebmt), 3);
            }
        });
        cvPembiayaan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else showConfirmPinCekSaldo(getString(R.string.pin_mobilebmt), 4);
            }
        });
        cvSimpanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else showConfirmPinCekSaldo(getString(R.string.pin_mobilebmt), 5);
            }
        });
        cvBiaya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(getActivity(), InfoBiaya.class);
                    startActivity(intent);
                }
            }
        });

        cvProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProdukActivity.class);
                startActivity(intent);
            }
        });
        cvLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(getActivity(), KantorLayanan.class);
                    startActivity(intent);
                }
            }
        });

        cvPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(getActivity(), PromoActivity.class);
                    startActivity(intent);
                }
            }
        });

        /*bannerLayout.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                   *//* Intent intent = new Intent(getActivity(), PromoActivity.class);
                    startActivity(intent);*//*
                }
                //Toast.makeText(getActivity(), String.valueOf(position), Toast.LENGTH_SHORT).show();
            }
        });*/

        Button btnLihat = rootView.findViewById(R.id.btn_lihat_semua);
        btnLihat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    /*Intent intent = new Intent(getActivity(), PromoActivity.class);
                    startActivity(intent);*/
                }
            }
        });

        Activity activity = getActivity();
        if (activity != null) {
            new asyncPromo().execute();
        }

        return rootView;
    }

    private void showConfirmCS(String title, String message) {
        new android.app.AlertDialog.Builder(getActivity()).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + getString(R.string.call_center)));
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                } else {
                    //You already have permission
                    try {
                        startActivity(callIntent);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
                dialog.dismiss();
            }
        }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + getString(R.string.call_center)));
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(callIntent);
                    // permission was granted, yay! Do the phone call
                } else {
                    Toast.makeText(getActivity(), "Tidak bisa diproses, Permision denied", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void showOpsi() {

        finisa = false;

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle("Pilih Metode");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
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
                    showFilterInfoMutasi("Limit Transaksi");
                } else {
                    finisa = false;
                    materialDatePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                }
            }
        });
        builderSingle.show();
    }

    private void showFilterInfoMutasi(String msg) {

        final Dialog dialog = new Dialog(getActivity());
        finisa = false;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_confirm_info_mutasi);
        TextView textInfoMutasi = (TextView) dialog.findViewById(R.id.textInfoMutasi);
        textInfoMutasi.setText(msg);
        dialog.setCancelable(true);
        Button buttonDialogNo = (Button) dialog.findViewById(R.id.button_dialog_no);
        buttonDialogNo.setText("BATAL");
        buttonDialogNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finisa = true;
                dialog.cancel();
            }
        });
        Button buttonDialogYes = (Button) dialog.findViewById(R.id.button_dialog_yes);
        buttonDialogYes.setText("OK");
        buttonDialogYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    RadioGroup rgTrx = (RadioGroup) dialog.findViewById(R.id.RgTrx);
                    int selectedId = rgTrx.getCheckedRadioButtonId();
                    final RadioButton rbTrx = (RadioButton) dialog.findViewById(selectedId);
                    String jm = rbTrx.getText().toString().replaceAll("[^0-9]", "");
                    jmltrx = Integer.parseInt(jm);
                    showConfirmPinCekSaldo(getString(R.string.pin_mobilebmt), 2);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private static long mLastClickTime = 0;

    private void showConfirmPinCekSaldo(String msg, final int fi) {
        final Dialog dialog = new Dialog(getActivity());
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
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    buttonDialogYes.setEnabled(false);
                    strPIN = edPin.getText().toString().trim();
                    edPin.setText("");
                    config = getActivity().getSharedPreferences("config", 0);
                    SnHp telpMan = new SnHp(getActivity());
                    if (5 != telpMan.telephonyManager().getSimState()) {
                        imsi = "TIDAK ADA KARTU";
                    } else {
                        imsi = Utility.getIMSIRead(getActivity());
                    }

                    Log.d("AYIK", "beranda: imsi " + imsi);

                    NumSky nmsk = new NumSky(getActivity());
                    try {
                        nocard = nmsk.decrypt(config.getString("3D0k", ""));
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.d("AYIK", "beranda: nocard " + nocard);

                    if (Utility.is6digit(strPIN)) {
                        if (fi == 1) new AsyncCekSaldo().execute();
                        else if (fi == 2) new AsyncInfoMutasi().execute();
                        else if (fi == 3) new AsyncInfoMDA().execute();
                        else if (fi == 4) new AsyncInfoPembiayaan().execute();
                        else if (fi == 5) new AsyncInfoSimpanan().execute();
                        else if (fi == 6) new AsyncInfoMutasiPeriod().execute();
                    } else {
                        showAlert("Error", "Pin harus 6 digit angka!");
                    }
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void showConfirmInfoSaldo(String Judul, String tgls, String rek, String saldo, String strTgl, String msga) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_confirm_infosaldo);
        TextView textPin = (TextView) dialog.findViewById(R.id.textPin);
        textPin.setText(Judul);
        TextView tgl = (TextView) dialog.findViewById(R.id.tgl);
        tgl.setText(tgls);
        TextView adm = (TextView) dialog.findViewById(R.id.rekening);
        adm.setText(rek);
        TextView jml = (TextView) dialog.findViewById(R.id.saldo);
        jml.setText(saldo);
        dialog.setCancelable(true);
        Button buttonDialogYes = (Button) dialog.findViewById(R.id.button_dialog_yes);
        buttonDialogYes.setText("OK");
        buttonDialogYes.setTextSize(20);
        buttonDialogYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finisa = true;
                dialog.dismiss();
            }
        });

        Button buttonDialogInbox = (Button) dialog.findViewById(R.id.button_dialog_inbox);
        buttonDialogInbox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SysDB dbsys = new SysDB(getActivity());
                dbsys.open();
                dbsys.CreateTableSys();
                dbsys.insertSys(strTgl, "Info Saldo " + msga);
                dbsys.close();
                finisa = true;
                dialog.dismiss();
                Toast.makeText(getActivity(), "Sukses simpan ke Inbox", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void showAlert(String title, String message) {
        new android.app.AlertDialog.Builder(getActivity()).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finisa = true;
                dialog.dismiss();
            }
        }).show();

    }

    private class AsyncCekSaldo extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(getActivity());
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
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(getActivity()).decrypt(getActivity().getResources().getString(R.string.urlCekSaldo)));

                Log.d("AYIK", "urlceksaldo " + obj.toString());

                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());

                Log.d("AYIK", "ceksaldo:nokartu " + nocard);
                Log.d("AYIK", "ceksaldo:imsi " + imsi);

                String strCek = toJsonString(Utility.md5(nocard), Utility.md5(imsi), Utility.md5(strPIN), 0);

                Log.d("AYIK", "ceksaldo " + strCek);

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

                Log.d("AYIK", "ceksaldo " + response.toString());

                JSONParser parser = new JSONParser();
                Object objects = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) objects;
                stCekSaldo = (Boolean) jsonObject.get("status");
                ket = (String) jsonObject.get("keterangan");
                saldo = (String) jsonObject.get("saldoakhir");
                tgl = (String) jsonObject.get("datetime");
                norek = (String) jsonObject.get("rekening");
                jwtpub = (String) jsonObject.get("jwt");
                Utility2.setPrefsAuthToken(jwtpub);
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
            strTgl = sdf.format(today);
            if (stCekSaldo) {
                String msga = "Tanggal : " + tgl + "\nNo. Rek : " + norek + "\nSaldo anda saat ini Rp. " + saldo + "\n";
               /* SysDB dbsys = new SysDB(getActivity());
                dbsys.open();
                dbsys.CreateTableSys();
                dbsys.insertSys(strTgl, "Info Saldo " + msga);
                dbsys.close();*/

                showConfirmInfoSaldo("Info Saldo SUKSES", ": " + tgl, ": " + norek, ": Rp. " + saldo, strTgl, msga);
            } else {
                String msga = "#" + ket + "\n";
//                if (!ket.equals("404 Error koneksi terputus!!\nSilahkan coba lagi")) {
//                    SysDB dbsys = new SysDB(getActivity());
//                    dbsys.open();
//                    dbsys.CreateTableSys();
//                    dbsys.insertSys(strTgl, "GAGAL Inquiry Saldo " + msga);
//                    dbsys.close();
//                }
                showAlert("GAGAL Inquiry Saldo", msga);
            }
        }
    }

    private class AsyncInfoMDA extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        private Boolean stInfoMDA = false;
        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
        private Date today = new Date();
        private Locale id = new Locale("in", "ID");
        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi", data = "", tgl = "";

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
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(getActivity()).decrypt(getActivity().getResources().getString(R.string.urlInfoMDA)));

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
                stInfoMDA = (Boolean) jsonObject.get("status");
                ket = (String) jsonObject.get("keterangan");
                data = jsonObject.toJSONString();
                tgl = (String) jsonObject.get("datetime");
                /*jwtpub = (String) jsonObject.get("jwt");
                Utility2.setPrefsAuthToken(jwtpub);*/

            } catch (Exception ex) {
                ex.printStackTrace();
                stInfoMDA = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoading.isShowing()) pdLoading.dismiss();
            strTgl = sdf.format(today);
            if (stInfoMDA) {
                Intent intent = new Intent(getActivity(), InfoMutasi.class);
                intent.putExtra("list.mutasi", data);
                intent.putExtra("tgl", tgl);
                intent.putExtra("TITLE", "Info MDA Berjangka");
                startActivity(intent);
            } else {
                String msga = "#" + ket + "\n";
//                SysDB dbsys = new SysDB(getActivity());
//                dbsys.open();
//                dbsys.CreateTableSys();
                if (ket.startsWith("505")) {
//                    dbsys.insertSys(strTgl, "GAGAL Inquiry MDA Berjangka " + msga);
                    Intent intent = new Intent(getActivity(), InfoMutasi.class);
                    intent.putExtra("list.mutasi", data);
                    intent.putExtra("tgl", tgl);
                    intent.putExtra("TITLE", "Info MDA Berjangka");
                    startActivity(intent);
                } else if (!ket.equals("404 Error koneksi terputus!!\nSilahkan coba lagi")) {
//                    dbsys.insertSys(strTgl, "GAGAL Inquiry MDA Berjangka " + msga);
                    showAlert("GAGAL Inquiry MDA Berjangka", msga);
                } else {
                    showAlert("GAGAL Inquiry MDA Berjangka", msga);
                }
//                dbsys.close();
            }
        }
    }

    private class AsyncInfoPembiayaan extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        private Boolean stInfoPembiayaan = false;
        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
        private Date today = new Date();
        private Locale id = new Locale("in", "ID");
        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi", data = "", tgl = "";

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
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(getActivity()).decrypt(getActivity().getResources().getString(R.string.urlInfoPembiayaan)));

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
                stInfoPembiayaan = (Boolean) jsonObject.get("status");
                ket = (String) jsonObject.get("keterangan");
                data = jsonObject.toJSONString();
                tgl = (String) jsonObject.get("datetime");
                jwtpub = (String) jsonObject.get("jwt");
                Utility2.setPrefsAuthToken(jwtpub);
            } catch (Exception ex) {
                ex.printStackTrace();
                stInfoPembiayaan = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoading.isShowing()) pdLoading.dismiss();
            strTgl = sdf.format(today);
            if (stInfoPembiayaan) {
                Intent intent = new Intent(getActivity(), InfoMutasi.class);
                intent.putExtra("list.mutasi", data);
                intent.putExtra("tgl", tgl);
                intent.putExtra("TITLE", "Info Pembiayaan");
                startActivity(intent);
            } else {
                String msga = "#" + ket + "\n";
//                SysDB dbsys = new SysDB(getActivity());
//                dbsys.open();
//                dbsys.CreateTableSys();
                if (ket.startsWith("505")) {
//                    dbsys.insertSys(strTgl, "GAGAL Inquiry Pembiayaan " + msga);
                    Intent intent = new Intent(getActivity(), InfoMutasi.class);
                    intent.putExtra("list.mutasi", data);
                    intent.putExtra("tgl", tgl);
                    intent.putExtra("TITLE", "Info Pembiayaan");
                    startActivity(intent);
                } else if (!ket.equals("404 Error koneksi terputus!!\nSilahkan coba lagi")) {
//                    dbsys.insertSys(strTgl, "GAGAL Inquiry Pembiayaan " + msga);
                    showAlert("GAGAL Inquiry Pembiayaan", msga);
                } else {
                    showAlert("GAGAL Inquiry Pembiayaan", msga);
                }
//                dbsys.close();
            }
        }
    }

    private class AsyncInfoMutasi extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        Boolean stInfoMutasi = false;
        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
        private Date today = new Date();
        private Locale id = new Locale("in", "ID");
        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi", data = "", tgl = "";

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
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(getActivity()).decrypt(getActivity().getResources().getString(R.string.urlInfoMutasi)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = toJsonString(Utility.md5(nocard), Utility.md5(imsi), Utility.md5(strPIN), jmltrx);
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
                stInfoMutasi = (Boolean) jsonObject.get("status");
                ket = (String) jsonObject.get("keterangan");
                data = jsonObject.toJSONString();
                tgl = (String) jsonObject.get("datetime");
                //jwtpub = (String) jsonObject.get("jwt");
                //Utility2.setPrefsAuthToken(jwtpub);
            } catch (Exception ex) {
                ex.printStackTrace();
                stInfoMutasi = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoading.isShowing()) pdLoading.dismiss();
            strTgl = sdf.format(today);
            if (stInfoMutasi) {
                Intent intent = new Intent(getActivity(), InfoMutasi.class);
                intent.putExtra("list.mutasi", data);
                intent.putExtra("tgl", tgl);
                intent.putExtra("TITLE", "Info Mutasi");
                startActivity(intent);
            } else {
                String msga = "#" + ket + "\n";
                if (!ket.equals("404 Error koneksi terputus!!\nSilahkan coba lagi")) {
//                    SysDB dbsys = new SysDB(getActivity());
//                    dbsys.open();
//                    dbsys.CreateTableSys();
//                    dbsys.insertSys(strTgl, "GAGAL Inquiry Mutasi " + msga);
//                    dbsys.close();
                }
                showAlert("GAGAL Inquiry Mutasi", msga);
            }
        }
    }

    private class AsyncInfoMutasiPeriod extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        Boolean stInfoMutasi = false;
        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
        private Date today = new Date();
        private Locale id = new Locale("in", "ID");
        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi", data = "", tgl = "";

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
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(getActivity()).decrypt(getActivity().getResources().getString(R.string.urlInfoMutasiPeriod)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = toJsonString(Utility.md5(nocard), Utility.md5(imsi), Utility.md5(strPIN), jmltrx);
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
                stInfoMutasi = (Boolean) jsonObject.get("status");
                ket = (String) jsonObject.get("keterangan");
                data = jsonObject.toJSONString();
                tgl = (String) jsonObject.get("datetime");
                //jwtpub = (String) jsonObject.get("jwt");
                //Utility2.setPrefsAuthToken(jwtpub);
            } catch (Exception ex) {
                ex.printStackTrace();
                stInfoMutasi = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoading.isShowing()) pdLoading.dismiss();
            strTgl = sdf.format(today);
            if (stInfoMutasi) {
                Intent intent = new Intent(getActivity(), InfoMutasi.class);
                intent.putExtra("list.mutasi", data);
                intent.putExtra("tgl", tgl);
                intent.putExtra("TITLE", "Info Mutasi");
                startActivity(intent);
            } else {
                String msga = "#" + ket + "\n";
                if (!ket.equals("404 Error koneksi terputus!!\nSilahkan coba lagi")) {
//                    SysDB dbsys = new SysDB(getActivity());
//                    dbsys.open();
//                    dbsys.CreateTableSys();
//                    dbsys.insertSys(strTgl, "GAGAL Inquiry Mutasi " + msga);
//                    dbsys.close();
                }
                showAlert("GAGAL Inquiry Mutasi", msga);
            }
        }
    }

    private class AsyncInfoSimpanan extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        Boolean stInfoSimpanan = false;
        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
        private Date today = new Date();
        private Locale id = new Locale("in", "ID");
        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi", data = "", tgl = "";

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
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(getActivity()).decrypt(getActivity().getResources().getString(R.string.urlInfoSimpanan)));

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
                stInfoSimpanan = (Boolean) jsonObject.get("status");
                ket = (String) jsonObject.get("keterangan");
                data = jsonObject.toJSONString();
                tgl = (String) jsonObject.get("datetime");
                jwtpub = (String) jsonObject.get("jwt");
                Utility2.setPrefsAuthToken(jwtpub);
            } catch (Exception ex) {
                ex.printStackTrace();
                stInfoSimpanan = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoading.isShowing()) pdLoading.dismiss();
            strTgl = sdf.format(today);
            if (stInfoSimpanan) {
                Intent intent = new Intent(getActivity(), InfoMutasi.class);
                intent.putExtra("list.mutasi", data);
                intent.putExtra("tgl", tgl);
                intent.putExtra("TITLE", "Info Simpanan");
                startActivity(intent);
            } else {
                String msga = "#" + ket + "\n";
//                SysDB dbsys = new SysDB(getActivity());
//                dbsys.open();
//                dbsys.CreateTableSys();
                if (ket.startsWith("505")) {
//                    dbsys.insertSys(strTgl, "GAGAL Inquiry Simpanan " + msga);
                    Intent intent = new Intent(getActivity(), InfoMutasi.class);
                    intent.putExtra("list.mutasi", data);
                    intent.putExtra("tgl", tgl);
                    intent.putExtra("TITLE", "Info Simpanan");
                    startActivity(intent);
                } else if (!ket.equals("404 Error koneksi terputus!!\nSilahkan coba lagi")) {
//                    dbsys.insertSys(strTgl, "GAGAL Inquiry Simpanan " + msga);
                    showAlert("GAGAL Inquiry Simpanan", msga);
                } else {
                    showAlert("GAGAL Inquiry Simpanan", msga);
                }
//                dbsys.close();
            }
        }

    }

    private String toJsonString(String nokartu, String imsi, String pin, int jmltrx) {
        JSONObject obj = new JSONObject();
        obj.put("nokartu", nokartu);
        obj.put("imsi", imsi);
        obj.put("pin", pin);
        if (jmltrx != 0) obj.put("jmltransaksi", jmltrx);

        obj.put("kodebmt", getString(R.string.kodebmt));
        obj.put("tglmulai", start);
        obj.put("tglsampai", end);

        return obj.toString();
    }

    private class asyncPromo extends AsyncTask<String, Void, Boolean> {
        private static final String REQUEST_METHOD = "GET";
        private static final int READ_TIMEOUT = 29000;
        private static final int CONNECTION_TIMEOUT = 30000;
        private Boolean status = false;
        private String keterangan = "404 Error koneksi terputus!!\nSilahkan coba lagi.", inputLine;
        private List<String> urls = new ArrayList<>();

        private List<String> bannerDatas;

        @Override
        protected Boolean doInBackground(String... params) {

            bannerDatas = new ArrayList<>();

            try {


                URL myUrl = new URL(MyVal.URL_BASE()
                        + new NumSky(getActivity()).decrypt(getActivity().getResources().getString(R.string.urlInfoPromo2)) + getActivity().getResources().getString(R.string.appurlnameLower) + "&status=2");

                Log.d("AYIK","banner "+ myUrl);

                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", getPrefsAuthToken());
                connection.connect();
                keterangan = connection.getResponseCode() + " " + connection.getResponseMessage();
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                org.json.JSONObject obj = new org.json.JSONObject(stringBuilder.toString());
                status = obj.getBoolean("status");
                if (status) {
                    keterangan = obj.getString("keterangan");
                    JSONArray arr = (JSONArray) obj.get("keterangan");
                    String uris = MyVal.URL_BASE_CONTENT() + new NumSky(getActivity()).decrypt(getActivity().getResources().getString(R.string.urlInfoSplash));

                    Log.d("AYIK","banner uris "+ uris);

                    for (int i = 0; i < arr.length(); i++) {
                        String imga = arr.getString(i);
                        urls.add(uris + imga);

                        Log.d("AYIK","banner uris img "+ uris+imga);

                        bannerDatas.add(uris + imga + "#" + imga);
                        bannerNameSvr.add(imga);
                    }

                } else keterangan = obj.getString("keterangan");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            return status;
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            String sDCard = "";
            sDCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            File bannerFolder = new File(sDCard + "/M-BMT/data/banner");
            if (!bannerFolder.exists()) {
                bannerFolder.mkdirs();
            }

            bannerDirPath = sDCard + "/M-BMT/data/banner/";

            File[] files = bannerFolder.listFiles();
            assert files != null;

            for (int i = 0; i < files.length; i++) {
                bannerNameDir.add(files[i].getName());
            }

            int countSvr = bannerNameSvr.size();
            int countDir = bannerNameDir.size();
            int countMatch = 0;

            Collections.sort(bannerNameSvr);
            Collections.sort(bannerNameDir);

            boolean mustDownload = false;

            if (countSvr == countDir) {
                for (int i = 0; i < bannerNameDir.size(); i++) {
                    if (bannerNameSvr.get(i).equals(bannerNameDir.get(i))) {
                        countMatch++;
                    }
                }
                mustDownload = countMatch != countSvr;
            } else {
                mustDownload = true;
            }

            if (mustDownload && result) {
                if (bannerFolder.isDirectory()) {
                    String[] children = bannerFolder.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(bannerFolder, children[i]).delete();
                    }
                }

                if (!bannerFolder.exists()) {
                    bannerFolder.mkdirs();
                }

                arrBannerPath.clear();
                for (int i = 0; i < bannerDatas.size(); i++) {
                    String str = bannerDatas.get(i);
                    new AsyncDownloadImage().execute(str);
                    if (i == (bannerDatas.size() - 1)) {
                        isFinished = true;
                    }

                }
            } else {
                if (countSvr <= 0) {
                    if (bannerFolder.isDirectory()) {
                        String[] children = bannerFolder.list();
                        for (int i = 0; i < children.length; i++) {
                            new File(bannerFolder, children[i]).delete();
                        }
                    }

                    if (!bannerFolder.exists()) {
                        bannerFolder.mkdirs();
                    }
                } else {

                    arrBannerPath.clear();
                    for (File file : files) {
                        arrBannerPath.add(bannerDirPath + file.getName());
                    }
                    loadBanner();
                }
            }
        }
    }

    private boolean isFinished = false;
    private String bannerDirPath = "";

    private List<String> arrBannerPath = new ArrayList<>();
    private List<String> bannerNameSvr = new ArrayList<>();
    private List<String> bannerNameDir = new ArrayList<>();

    private void loadBanner() {

        Collections.sort(arrBannerPath, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        Activity activity = getActivity();
        if (activity != null) {

            WebBannerAdapter webBannerAdapter = new WebBannerAdapter(getActivity(), arrBannerPath);
            webBannerAdapter.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Intent i = new Intent(getActivity(), ViewImageActivity.class);
                    i.putExtra("path", arrBannerPath.get(position));
                    i.putExtra("position", position);
                    startActivity(i);
                    //Toast.makeText(getActivity(), position, Toast.LENGTH_SHORT).show();
                }
            });
            recyclerBanner.setAdapter(webBannerAdapter);
        }

        if (getActivity() != null) {
            String kodebmt = getActivity().getResources().getString(R.string.kodebmt);

            if (kodebmt.equals("0022")) {//KISLAMET/QS
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String currentDate = df.format(c);

                if (getActivity() != null) {
                    SharedPreferences prefs = getActivity().getSharedPreferences(MyVal.PREFS_NAME, MODE_PRIVATE);
                    String dateLoadpromo = prefs.getString("dateLoadpromo", "");

                    if (!dateLoadpromo.equals(currentDate)) {
                        Intent i = new Intent(getActivity(), ViewImageActivity.class);
                        i.putExtra("path", arrBannerPath.get(0));
                        i.putExtra("position", 0);
                        startActivity(i);

                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MyVal.PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("dateLoadpromo", currentDate);
                        editor.apply();
                    }
                }
            }
        }
    }


    private class AsyncDownloadImage extends AsyncTask<String, String, String> {

        private String resp = "";
        String bannerPath = "", bannerUrl = "", bannerName = "";

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {

            String[] strX = params[0].split("#");

            bannerUrl = strX[0];
            bannerName = strX[1];

            try {
                URL url = new URL(bannerUrl);
                InputStream in = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n = in.read(buf);
                while (n != -1) {
                    out.write(buf, 0, n);
                    n = in.read(buf);
                }
                out.close();
                in.close();
                byte[] response = out.toByteArray();

                bannerPath = bannerDirPath + bannerName;

                FileOutputStream fos = new FileOutputStream(bannerPath);
                fos.write(response);
                fos.close();

                resp = "success";

            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            if (result.equals("success")) {
                arrBannerPath.add(bannerPath);
                if (isFinished)
                    loadBanner();
            }
        }

    }

    private class AsyncStat extends AsyncTask<Void, Void, Void> {
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi";
        Boolean stats = false;
        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        org.json.simple.JSONArray datas = null;

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

                String uri = new NumSky(getActivity()).decrypt(getActivity().getResources().getString(R.string.urlStatusTrf));
                URL obj = new URL(MyVal.URL_BASE() + uri);
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());

                String strCek = toJsonString2(Utility.md5(strNokartu), Utility.md5(strImsi), getActivity().getResources().getString(R.string.kodebmt));


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
                stats = (Boolean) jsonObject.get("status");
                ket = (String) jsonObject.get("keterangan");

                datas = (org.json.simple.JSONArray) jsonObject.get("data");

            } catch (Exception ex) {
                stats = false;
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoading.isShowing()) pdLoading.dismiss();

            if (stats) {
                Intent intent = new Intent(getActivity(), AntarBank.class);
                intent.putExtra("datas", datas.toJSONString());
                startActivity(intent);
            } else {
                showAlert("GAGAL", ket);
            }
        }
    }

    private String toJsonString2(String nokartu, String imsi, String kodebmt) {
        JSONObject obj = new JSONObject();
        obj.put("nokartu", nokartu);
        obj.put("imsi", imsi);
        obj.put("kodebmt", kodebmt);
        return obj.toString();
    }
}
