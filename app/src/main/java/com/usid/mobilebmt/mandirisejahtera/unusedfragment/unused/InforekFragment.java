//package com.usid.mobilebmt.mandirisejahtera.unusedfragment.unused;
//
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.SystemClock;
//
//import androidx.fragment.app.Fragment;
//
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
//import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.InfoMutasi;
//import com.usid.mobilebmt.mandirisejahtera.model.SysDB;
//import com.usid.mobilebmt.mandirisejahtera.utils.MyVal;
//import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;
//import com.usid.mobilebmt.mandirisejahtera.R;
//import com.usid.mobilebmt.mandirisejahtera.utils.SnHp;
//import com.usid.mobilebmt.mandirisejahtera.utils.Utility;
//
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
//import java.util.Date;
//import java.util.Locale;
//
//import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;
//import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.finisa;
//import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;
//

//public class InforekFragment extends Fragment {
//    private View myFragmentView;
//    private Button btnInfoSaldo, btnInfoMutasi, btnMDABerjangka, btnPembiayaan, btnSimpanan;
//    private String strPIN = "", imsi = "", nocard = "";
//    private int jmltrx = 5;
//    private SharedPreferences config;
//
//    public InforekFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        // Set title
////        ((MainLamaActivity) getActivity()).setActionBarTitle(getString(R.string.inforek));
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        myFragmentView = inflater.inflate(R.layout.fragment_inforek, container, false);
//        btnInfoSaldo = (Button) myFragmentView.findViewById(R.id.btInfoSaldo);
//        btnInfoMutasi = (Button) myFragmentView.findViewById(R.id.btInfoMutasi);
//        btnMDABerjangka = (Button) myFragmentView.findViewById(R.id.btInfoMDA);
//        btnPembiayaan = (Button) myFragmentView.findViewById(R.id.btInfoPembiayaan);
//        btnSimpanan = (Button) myFragmentView.findViewById(R.id.btInfoSimpanan);
//        btnInfoSaldo.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else showConfirmPinCekSaldo(getString(R.string.pin_mobilebmt), 1);
//            }
//        });
//        btnInfoMutasi.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else showFilterInfoMutasi("Limit Transaksi");
//            }
//        });
//        btnMDABerjangka.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else showConfirmPinCekSaldo(getString(R.string.pin_mobilebmt), 3);
//            }
//        });
//        btnPembiayaan.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else showConfirmPinCekSaldo(getString(R.string.pin_mobilebmt), 4);
//            }
//        });
//        btnSimpanan.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else showConfirmPinCekSaldo(getString(R.string.pin_mobilebmt), 5);
//            }
//        });
//        return myFragmentView;
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
//                       /* try {
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
//    private void showConfirm(String title, String message) {
//        new android.app.AlertDialog.Builder(getActivity()).setTitle(title).setMessage(message).setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finisa = true;
//                dialog.dismiss();
//            }
//        }).show();
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
//                showConfirm("Info Saldo SUKSES", msga);
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
//
//
