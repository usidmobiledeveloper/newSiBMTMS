//package com.usid.mobilebmt.mandirisejahtera.unusedfragment.unused;
//
//import android.Manifest;
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.SystemClock;
//
//import androidx.core.app.ActivityCompat;
//import androidx.fragment.app.Fragment;
//import androidx.core.content.ContextCompat;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.usid.mobilebmt.mandirisejahtera.R;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.content.InfoBiaya;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.content.KantorLayanan;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.content.ProdukActivity;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.content.PromoActivity;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.InfoMutasi;
//import com.usid.mobilebmt.mandirisejahtera.model.SysDB;
//import com.usid.mobilebmt.mandirisejahtera.utils.MyVal;
//import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;
//import com.usid.mobilebmt.mandirisejahtera.utils.SnHp;
//import com.usid.mobilebmt.mandirisejahtera.utils.Utility;
//import com.usid.mobilebmt.mandirisejahtera.utils.imageloader.GlideImageLoader;
//import com.yyydjk.library.BannerLayout;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.security.GeneralSecurityException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//import at.markushi.ui.CircleButton;
//
//import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.finisa;
//import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;
//import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;
//
//public class BerandaFragment extends Fragment {
//
//    private BannerLayout bannerLayout;
//    private CircleButton btnSaldo, btnMutasi, btnMda, btnPembiayaan, btnSimpanan, btnBiaya, btnLokasi, btnProduk, btnPromo, btnCallUsid;
//    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0;
//    private String strPIN = "", imsi = "", nocard = "";
//    private int jmltrx = 5;
//    private SharedPreferences config;
//    private Button btnlihat;
//
//    public BerandaFragment() {
//
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.layout.fragment_beranda, container, false);
//        btnlihat = view.findViewById(R.id.btn_lihat_semua);
//        btnSaldo = view.findViewById(R.id.btn_saldo);
//        btnMutasi = view.findViewById(R.id.btn_mutasi);
//        btnMda = view.findViewById(R.id.btn_mda);
//        btnPembiayaan = view.findViewById(R.id.btn_pembiayaan);
//        btnSimpanan = view.findViewById(R.id.btn_simpanan);
//        btnBiaya = view.findViewById(R.id.btn_biaya);
//        btnLokasi = view.findViewById(R.id.btn_lokasi);
//        btnProduk = view.findViewById(R.id.btn_produk);
//        btnPromo = view.findViewById(R.id.btn_promo);
//        btnCallUsid = view.findViewById(R.id.btn_call_usid);
//        bannerLayout = view.findViewById(R.id.banner);
//
//        btnlihat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), PromoActivity.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        btnSaldo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else showConfirmPinCekSaldo(getString(R.string.pin_mobilebmt), 1);
//            }
//        });
//        btnMutasi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else showFilterInfoMutasi("Limit Transaksi");
//            }
//        });
//        btnMda.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else showConfirmPinCekSaldo(getString(R.string.pin_mobilebmt), 3);
//            }
//        });
//        btnPembiayaan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else showConfirmPinCekSaldo(getString(R.string.pin_mobilebmt), 4);
//            }
//        });
//        btnSimpanan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else showConfirmPinCekSaldo(getString(R.string.pin_mobilebmt), 5);
//            }
//        });
//        btnBiaya.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), InfoBiaya.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        btnLokasi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), KantorLayanan.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        btnProduk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), ProdukActivity.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        btnPromo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), PromoActivity.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        btnCallUsid.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showConfirmCS("Hubungi Call Center", "Jam Layanan dan keluhan pelanggan pada Hari Sabtu - Kamis pukul 08.00 - 14.00.\nAnda yakin untuk menghubungi call center sekarang?");
//            }
//        });
//        bannerLayout.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), PromoActivity.class);
//                    startActivity(intent);
//                }
//                //Toast.makeText(getActivity(), String.valueOf(position), Toast.LENGTH_SHORT).show();
//            }
//        });
//        new asyncPromo().execute();
//        return view;
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//    }
//
//    private void showConfirmCS(String title, String message) {
//        new android.app.AlertDialog.Builder(getActivity()).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent callIntent = new Intent(Intent.ACTION_CALL);
//                callIntent.setData(Uri.parse("tel:" + getString(R.string.call_center)));
//                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
//                } else {
//                    //You already have permission
//                    try {
//                        startActivity(callIntent);
//                    } catch (SecurityException e) {
//                        e.printStackTrace();
//                    }
//                }
//                dialog.dismiss();
//            }
//        }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        }).show();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Intent callIntent = new Intent(Intent.ACTION_CALL);
//                    callIntent.setData(Uri.parse("tel:" + getString(R.string.call_center)));
//                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                        // TODO: Consider calling
//                        //    ActivityCompat#requestPermissions
//                        // here to request the missing permissions, and then overriding
//                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                        //                                          int[] grantResults)
//                        // to handle the case where the user grants the permission. See the documentation
//                        // for ActivityCompat#requestPermissions for more details.
//                        return;
//                    }
//                    startActivity(callIntent);
//                    // permission was granted, yay! Do the phone call
//                } else {
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, Permision denied", Toast.LENGTH_SHORT).show();
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }
//
//    private void showFilterInfoMutasi(String msg) {
//        final Dialog dialog = new Dialog(getActivity());
//        finisa = false;
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.custom_dialog_confirm_info_mutasi);
//        TextView textInfoMutasi = (TextView) dialog.findViewById(R.id.textInfoMutasi);
//        textInfoMutasi.setText(msg);
//        dialog.setCancelable(true);
//        Button buttonDialogNo = (Button) dialog.findViewById(R.id.button_dialog_no);
//        buttonDialogNo.setText("BATAL");
//        buttonDialogNo.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                finisa = true;
//                dialog.cancel();
//            }
//        });
//        Button buttonDialogYes = (Button) dialog.findViewById(R.id.button_dialog_yes);
//        buttonDialogYes.setText("OK");
//        buttonDialogYes.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    RadioGroup rgTrx = (RadioGroup) dialog.findViewById(R.id.RgTrx);
//                    int selectedId = rgTrx.getCheckedRadioButtonId();
//                    final RadioButton rbTrx = (RadioButton) dialog.findViewById(selectedId);
//                    String jm = rbTrx.getText().toString().replaceAll("[^0-9]", "");
//                    jmltrx = Integer.parseInt(jm);
//                    showConfirmPinCekSaldo(getString(R.string.pin_mobilebmt), 2);
//                    dialog.dismiss();
//                }
//            }
//        });
//        dialog.show();
//    }
//
//    private static long mLastClickTime = 0;
//
//    private void showConfirmPinCekSaldo(String msg, final int fi) {
//        final Dialog dialog = new Dialog(getActivity());
//        finisa = false;
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
//                finisa = true;
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
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    buttonDialogYes.setEnabled(false);
//                    strPIN = edPin.getText().toString().trim();
//                    edPin.setText("");
//                    config = getActivity().getSharedPreferences("config", 0);
//                    SnHp telpMan = new SnHp(getActivity());
//                    if (5 != telpMan.telephonyManager().getSimState()) {
//                        imsi = "TIDAK ADA KARTU";
//                    } else {
//                        /*try {
//                            imsi = telpMan.telephonyManager().getSimSerialNumber();
//                        } catch (Exception e) {
//                        }*/
//                        imsi = Utility.getIMSIRead(getActivity());
//                    }
//                    NumSky nmsk = new NumSky(getActivity());
//                    try {
//                        nocard = nmsk.decrypt(config.getString("3D0k", ""));
//                    } catch (GeneralSecurityException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    if (Utility.is6digit(strPIN)) {
//                        if (fi == 1) new AsyncCekSaldo().execute();
//                        else if (fi == 2) new AsyncInfoMutasi().execute();
//                        else if (fi == 3) new AsyncInfoMDA().execute();
//                        else if (fi == 4) new AsyncInfoPembiayaan().execute();
//                        else if (fi == 5) new AsyncInfoSimpanan().execute();
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
//    private void showConfirmInfoSaldo(String Judul, String tgls, String rek, String saldo) {
//        final Dialog dialog = new Dialog(getActivity());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.custom_dialog_confirm_infosaldo);
//        TextView textPin = (TextView) dialog.findViewById(R.id.textPin);
//        textPin.setText(Judul);
//        TextView tgl = (TextView) dialog.findViewById(R.id.tgl);
//        tgl.setText(tgls);
//        TextView adm = (TextView) dialog.findViewById(R.id.rekening);
//        adm.setText(rek);
//        TextView jml = (TextView) dialog.findViewById(R.id.saldo);
//        jml.setText(saldo);
//        dialog.setCancelable(false);
//        Button buttonDialogYes = (Button) dialog.findViewById(R.id.button_dialog_yes);
//        buttonDialogYes.setText("OK");
//        buttonDialogYes.setTextSize(20);
//        buttonDialogYes.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                finisa = true;
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//    }
//
//    private void showAlert(String title, String message) {
//        new android.app.AlertDialog.Builder(getActivity()).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finisa = true;
//                dialog.dismiss();
//            }
//        }).show();
//    }
//
//    private class asyncPromo extends AsyncTask<String, Void, Boolean> {
//        private static final String REQUEST_METHOD = "GET";
//        private static final int READ_TIMEOUT = 29000;
//        private static final int CONNECTION_TIMEOUT = 30000;
//        private Boolean status = false;
//        private String keterangan = "404 Error koneksi terputus!!\nSilahkan coba lagi.", inputLine;
//        private List<String> urls = new ArrayList<>();
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//
//            try {
//                URL myUrl = new URL(MyVal.URL_BASE() + new NumSky(getActivity())
//                        .decrypt(getResources().getString(R.string.urlInfoPromo2))
//                        + getResources().getString(R.string.appurlnameLower) + "&status=2");
//
//
//                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
//                connection.setRequestMethod(REQUEST_METHOD);
//
//                connection.setRequestProperty("Content-Type", "application/json");
//                connection.setRequestProperty("Authorization", getPrefsAuthToken());
//                connection.setReadTimeout(READ_TIMEOUT);
//                connection.setConnectTimeout(CONNECTION_TIMEOUT);
//                connection.connect();
//                keterangan = connection.getResponseCode() + " " + connection.getResponseMessage();
//                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
//                BufferedReader reader = new BufferedReader(streamReader);
//                StringBuilder stringBuilder = new StringBuilder();
//                while ((inputLine = reader.readLine()) != null) {
//                    stringBuilder.append(inputLine);
//                }
//                reader.close();
//                streamReader.close();
//
//
//                org.json.JSONObject obj = new org.json.JSONObject(stringBuilder.toString());
//                status = obj.getBoolean("status");
//                if (status) {
//                    keterangan = obj.getString("keterangan");
//                    JSONArray arr = (JSONArray) obj.get("keterangan");
//                    String uris = MyVal.URL_BASE_CONTENT() + new NumSky(getActivity()).decrypt(getResources().getString(R.string.urlInfoSplash));
//                    for (int i = 0; i < arr.length(); i++) {
//                        String imga = arr.getString(i);
//                        urls.add(uris + imga);
//                    }
//
//                } else keterangan = obj.getString("keterangan");
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (GeneralSecurityException e) {
//                e.printStackTrace();
//            }
//            return status;
//        }
//
//        protected void onPostExecute(Boolean result) {
//            super.onPostExecute(result);
//            if (result) {
//                bannerLayout.setImageLoader(new GlideImageLoader());
//                bannerLayout.setViewUrls(urls);
//            } else {
//                String uris = "";
//                try {
//                    uris = MyVal.URL_BASE_CONTENT() + new NumSky(getActivity()).decrypt(getResources().getString(R.string.urlInfoSplash));
//                } catch (GeneralSecurityException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                urls.add(uris + "banner1.png");
//                urls.add(uris + "banner2.png");
//                urls.add(uris + "banner3.png");
//                bannerLayout.setImageLoader(new GlideImageLoader());
//                bannerLayout.setViewUrls(urls);
//            }
//        }
//    }
//
//    private class AsyncCekSaldo extends AsyncTask<Void, Void, Void> {
//        ProgressDialog pdLoading = new ProgressDialog(getActivity());
//        private Boolean stCekSaldo = false;
//        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
//        private Date today = new Date();
//        private Locale id = new Locale("in", "ID");
//        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
//        private String tgl = "", saldo = "0", norek = "", ket = "404 Error koneksi terputus!!\nSilahkan coba lagi";
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
//                URL obj = new URL(MyVal.URL_BASE() + new NumSky(getActivity()).decrypt(getResources().getString(R.string.urlCekSaldo)));
//                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
//                conJ.setRequestMethod("POST");
//                conJ.setRequestProperty("Content-Type", "application/json");
//                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
//                String strCek = toJsonString(Utility.md5(nocard), Utility.md5(imsi), Utility.md5(strPIN), 0);
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
//                stCekSaldo = (Boolean) jsonObject.get("status");
//                ket = (String) jsonObject.get("keterangan");
//                saldo = (String) jsonObject.get("saldoakhir");
//                tgl = (String) jsonObject.get("datetime");
//                norek = (String) jsonObject.get("rekening");
//                jwtpub = (String) jsonObject.get("jwt");
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                stCekSaldo = false;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            if (pdLoading.isShowing()) pdLoading.dismiss();
//            strTgl = sdf.format(today);
//            if (stCekSaldo) {
//                String msga = "Tanggal : " + tgl + "\nNo. Rek : " + norek + "\nSaldo anda saat ini Rp. " + saldo + "\n";
//                SysDB dbsys = new SysDB(getActivity());
//                dbsys.open();
//                dbsys.CreateTableSys();
//                dbsys.insertSys(strTgl, "Info Saldo " + msga);
//                dbsys.close();
//
//                showConfirmInfoSaldo("Info Saldo SUKSES", ": " + tgl, ": " + norek, ": Rp. " + saldo);
//            } else {
//                String msga = "#" + ket + "\n";
////                if (!ket.equals("404 Error koneksi terputus!!\nSilahkan coba lagi")) {
////                    SysDB dbsys = new SysDB(getActivity());
////                    dbsys.open();
////                    dbsys.CreateTableSys();
////                    dbsys.insertSys(strTgl, "GAGAL Inquiry Saldo " + msga);
////                    dbsys.close();
////                }
//                showAlert("GAGAL Inquiry Saldo", msga);
//            }
//        }
//    }
//
//    private class AsyncInfoMDA extends AsyncTask<Void, Void, Void> {
//        ProgressDialog pdLoading = new ProgressDialog(getActivity());
//        private Boolean stInfoMDA = false;
//        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
//        private Date today = new Date();
//        private Locale id = new Locale("in", "ID");
//        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
//        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi", data = "", tgl = "";
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
//                URL obj = new URL(new NumSky(getActivity()).decrypt(getResources().getString(R.string.urlInfoMDA)));
//                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
//                conJ.setRequestMethod("POST");
//                conJ.setRequestProperty("Content-Type", "application/json");
//                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
//                String strCek = toJsonString(Utility.md5(nocard), Utility.md5(imsi), Utility.md5(strPIN), 0);
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
//                stInfoMDA = (Boolean) jsonObject.get("status");
//                ket = (String) jsonObject.get("keterangan");
//                data = jsonObject.toJSONString();
//                tgl = (String) jsonObject.get("datetime");
//                jwtpub = (String) jsonObject.get("jwt");
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                stInfoMDA = false;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            if (pdLoading.isShowing()) pdLoading.dismiss();
//            strTgl = sdf.format(today);
//            if (stInfoMDA) {
//                Intent intent = new Intent(getActivity(), InfoMutasi.class);
//                intent.putExtra("list.mutasi", data);
//                intent.putExtra("tgl", tgl);
//                intent.putExtra("TITLE", "Info MDA Berjangka");
//                startActivity(intent);
//            } else {
//                String msga = "#" + ket + "\n";
////                SysDB dbsys = new SysDB(getActivity());
////                dbsys.open();
////                dbsys.CreateTableSys();
//                if (ket.startsWith("505")) {
////                    dbsys.insertSys(strTgl, "GAGAL Inquiry MDA Berjangka " + msga);
//                    Intent intent = new Intent(getActivity(), InfoMutasi.class);
//                    intent.putExtra("list.mutasi", data);
//                    intent.putExtra("tgl", tgl);
//                    intent.putExtra("TITLE", "Info MDA Berjangka");
//                    startActivity(intent);
//                } else if (!ket.equals("404 Error koneksi terputus!!\nSilahkan coba lagi")) {
////                    dbsys.insertSys(strTgl, "GAGAL Inquiry MDA Berjangka " + msga);
//                    showAlert("GAGAL Inquiry MDA Berjangka", msga);
//                } else {
//                    showAlert("GAGAL Inquiry MDA Berjangka", msga);
//                }
////                dbsys.close();
//            }
//        }
//    }
//
//    private class AsyncInfoPembiayaan extends AsyncTask<Void, Void, Void> {
//        ProgressDialog pdLoading = new ProgressDialog(getActivity());
//        private Boolean stInfoPembiayaan = false;
//        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
//        private Date today = new Date();
//        private Locale id = new Locale("in", "ID");
//        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
//        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi", data = "", tgl = "";
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
//                URL obj = new URL(new NumSky(getActivity()).decrypt(getResources().getString(R.string.urlInfoPembiayaan)));
//                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
//                conJ.setRequestMethod("POST");
//                conJ.setRequestProperty("Content-Type", "application/json");
//                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
//                String strCek = toJsonString(Utility.md5(nocard), Utility.md5(imsi), Utility.md5(strPIN), 0);
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
//                stInfoPembiayaan = (Boolean) jsonObject.get("status");
//                ket = (String) jsonObject.get("keterangan");
//                data = jsonObject.toJSONString();
//                tgl = (String) jsonObject.get("datetime");
//                jwtpub = (String) jsonObject.get("jwt");
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                stInfoPembiayaan = false;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            if (pdLoading.isShowing()) pdLoading.dismiss();
//            strTgl = sdf.format(today);
//            if (stInfoPembiayaan) {
//                Intent intent = new Intent(getActivity(), InfoMutasi.class);
//                intent.putExtra("list.mutasi", data);
//                intent.putExtra("tgl", tgl);
//                intent.putExtra("TITLE", "Info Pembiayaan");
//                startActivity(intent);
//            } else {
//                String msga = "#" + ket + "\n";
////                SysDB dbsys = new SysDB(getActivity());
////                dbsys.open();
////                dbsys.CreateTableSys();
//                if (ket.startsWith("505")) {
////                    dbsys.insertSys(strTgl, "GAGAL Inquiry Pembiayaan " + msga);
//                    Intent intent = new Intent(getActivity(), InfoMutasi.class);
//                    intent.putExtra("list.mutasi", data);
//                    intent.putExtra("tgl", tgl);
//                    intent.putExtra("TITLE", "Info Pembiayaan");
//                    startActivity(intent);
//                } else if (!ket.equals("404 Error koneksi terputus!!\nSilahkan coba lagi")) {
////                    dbsys.insertSys(strTgl, "GAGAL Inquiry Pembiayaan " + msga);
//                    showAlert("GAGAL Inquiry Pembiayaan", msga);
//                } else {
//                    showAlert("GAGAL Inquiry Pembiayaan", msga);
//                }
////                dbsys.close();
//            }
//        }
//    }
//
//    private class AsyncInfoMutasi extends AsyncTask<Void, Void, Void> {
//        ProgressDialog pdLoading = new ProgressDialog(getActivity());
//        Boolean stInfoMutasi = false;
//        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
//        private Date today = new Date();
//        private Locale id = new Locale("in", "ID");
//        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
//        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi", data = "", tgl = "";
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
//                URL obj = new URL(MyVal.URL_BASE() + new NumSky(getActivity()).decrypt(getResources().getString(R.string.urlInfoMutasi)));
//                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
//                conJ.setRequestMethod("POST");
//                conJ.setRequestProperty("Content-Type", "application/json");
//                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
//                String strCek = toJsonString(Utility.md5(nocard), Utility.md5(imsi), Utility.md5(strPIN), jmltrx);
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
//                stInfoMutasi = (Boolean) jsonObject.get("status");
//                ket = (String) jsonObject.get("keterangan");
//                data = jsonObject.toJSONString();
//                tgl = (String) jsonObject.get("datetime");
//                jwtpub = (String) jsonObject.get("jwt");
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                stInfoMutasi = false;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            if (pdLoading.isShowing()) pdLoading.dismiss();
//            strTgl = sdf.format(today);
//            if (stInfoMutasi) {
//                Intent intent = new Intent(getActivity(), InfoMutasi.class);
//                intent.putExtra("list.mutasi", data);
//                intent.putExtra("tgl", tgl);
//                intent.putExtra("TITLE", "Info Mutasi");
//                startActivity(intent);
//            } else {
//                String msga = "#" + ket + "\n";
//                if (!ket.equals("404 Error koneksi terputus!!\nSilahkan coba lagi")) {
////                    SysDB dbsys = new SysDB(getActivity());
////                    dbsys.open();
////                    dbsys.CreateTableSys();
////                    dbsys.insertSys(strTgl, "GAGAL Inquiry Mutasi " + msga);
////                    dbsys.close();
//                }
//                showAlert("GAGAL Inquiry Mutasi", msga);
//            }
//        }
//    }
//
//    private class AsyncInfoSimpanan extends AsyncTask<Void, Void, Void> {
//        ProgressDialog pdLoading = new ProgressDialog(getActivity());
//        Boolean stInfoSimpanan = false;
//        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
//        private Date today = new Date();
//        private Locale id = new Locale("in", "ID");
//        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
//        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi", data = "", tgl = "";
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
//                URL obj = new URL(new NumSky(getActivity()).decrypt(getResources().getString(R.string.urlInfoSimpanan)));
//                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
//                conJ.setRequestMethod("POST");
//                conJ.setRequestProperty("Content-Type", "application/json");
//                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
//                String strCek = toJsonString(Utility.md5(nocard), Utility.md5(imsi), Utility.md5(strPIN), 0);
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
//                stInfoSimpanan = (Boolean) jsonObject.get("status");
//                ket = (String) jsonObject.get("keterangan");
//                data = jsonObject.toJSONString();
//                tgl = (String) jsonObject.get("datetime");
//                jwtpub = (String) jsonObject.get("jwt");
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                stInfoSimpanan = false;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            if (pdLoading.isShowing()) pdLoading.dismiss();
//            strTgl = sdf.format(today);
//            if (stInfoSimpanan) {
//                Intent intent = new Intent(getActivity(), InfoMutasi.class);
//                intent.putExtra("list.mutasi", data);
//                intent.putExtra("tgl", tgl);
//                intent.putExtra("TITLE", "Info Simpanan");
//                startActivity(intent);
//            } else {
//                String msga = "#" + ket + "\n";
////                SysDB dbsys = new SysDB(getActivity());
////                dbsys.open();
////                dbsys.CreateTableSys();
//                if (ket.startsWith("505")) {
////                    dbsys.insertSys(strTgl, "GAGAL Inquiry Simpanan " + msga);
//                    Intent intent = new Intent(getActivity(), InfoMutasi.class);
//                    intent.putExtra("list.mutasi", data);
//                    intent.putExtra("tgl", tgl);
//                    intent.putExtra("TITLE", "Info Simpanan");
//                    startActivity(intent);
//                } else if (!ket.equals("404 Error koneksi terputus!!\nSilahkan coba lagi")) {
////                    dbsys.insertSys(strTgl, "GAGAL Inquiry Simpanan " + msga);
//                    showAlert("GAGAL Inquiry Simpanan", msga);
//                } else {
//                    showAlert("GAGAL Inquiry Simpanan", msga);
//                }
////                dbsys.close();
//            }
//        }
//    }
//
//    private String toJsonString(String nokartu, String imsi, String pin, int jmltrx) {
//        JSONObject obj = new JSONObject();
//        obj.put("nokartu", nokartu);
//        obj.put("imsi", imsi);
//        obj.put("pin", pin);
//        if (jmltrx != 0) obj.put("jmltransaksi", jmltrx);
//        return obj.toString();
//    }
//}
