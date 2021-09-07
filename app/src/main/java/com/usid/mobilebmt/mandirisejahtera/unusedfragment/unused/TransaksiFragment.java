//package com.usid.mobilebmt.mandirisejahtera.unusedfragment.unused;
//
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import androidx.fragment.app.Fragment;
//import androidx.appcompat.app.AlertDialog;
//import androidx.cardview.widget.CardView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.view.animation.Animation;
//import android.view.animation.Transformation;
//import android.widget.AdapterView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListAdapter;
//import android.widget.ListView;
//import android.widget.SimpleAdapter;
//import android.widget.Toast;
//
//import com.usid.mobilebmt.mandirisejahtera.R;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.donasi.Donasi;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.donasi.DonaturLazTetapActivity;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembayaran.PDAMpembayaran;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembayaran.PLNpembayaran;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembayaran.TelkomPembayaran;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembelian.PLNpembelian;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembelian.PulsaPembelian;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.transfer.AntarBank;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.transfer.AntarRekening;
//import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;
//
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//
//import at.markushi.ui.CircleButton;
//
//import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.finisa;
//import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;
//
//public class TransaksiFragment extends Fragment implements View.OnClickListener {
//    private LinearLayout layoutTrf, layoutByr, layoutBli, layoutDonasi;
//    private ImageView imgArrowTrf, imgArrowByr, imgArrowBli, imgArrowDonasi;
//    private CardView mExpandButtonTrf, mExpandButtonByr, mExpandButtonBli, mExpandButtonDonasi;
//    private CircleButton btnTrfRekening, btnTrfBmt, btnTrfBank, btnLaz, btnLkaf, btnListrik, btnPdam, btnTelepon, btnInternet, btnTv, btnTiket, btnPendidikan, btnAsuransi, btnMultifinance, btnTokenPln, btnPulsa;
//    private String kodeproduk = "";
//    private ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
//    ListView list;
//    private Class nextintent = null;
//
//    public TransaksiFragment() {
//        // Required empty public constructor
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
//        View view = inflater.inflate(R.layout.fragment_transaksi, container, false);
//
//        mExpandButtonTrf = view.findViewById(R.id.expandButton_trf);
//        imgArrowTrf = view.findViewById(R.id.img_arrow_trf);
//
//        mExpandButtonByr = view.findViewById(R.id.expandButton_byr);
//        imgArrowByr = view.findViewById(R.id.img_arrow_byr);
//
//        mExpandButtonBli = view.findViewById(R.id.expandButton_bli);
//        imgArrowBli = view.findViewById(R.id.img_arrow_bli);
//
//        mExpandButtonDonasi = view.findViewById(R.id.expandButton_donasi);
//        imgArrowDonasi = view.findViewById(R.id.img_arrow_donasi);
//
//        layoutTrf = view.findViewById(R.id.layout_trf);
//        layoutByr = view.findViewById(R.id.layout_byr);
//        layoutBli = view.findViewById(R.id.layout_bli);
//        layoutDonasi = view.findViewById(R.id.layout_donasi);
//        btnTrfRekening = view.findViewById(R.id.btn_transfer_antar_rekening);
//        btnTrfBmt = view.findViewById(R.id.btn_transfer_antar_bmt);
//        btnTrfBank = view.findViewById(R.id.btn_transfer_antar_bank);
//        btnLaz = view.findViewById(R.id.btn_laz);
//        btnLkaf = view.findViewById(R.id.btn_lkaf);
//        btnListrik = view.findViewById(R.id.btn_listrik);
//        btnPdam = view.findViewById(R.id.btn_pdam);
//        btnTelepon = view.findViewById(R.id.btn_telepon);
//        btnInternet = view.findViewById(R.id.btn_internet);
//        btnTv = view.findViewById(R.id.btn_tv);
//        btnTiket = view.findViewById(R.id.btn_tiket);
//        btnPendidikan = view.findViewById(R.id.btn_pendidikan);
//        btnAsuransi = view.findViewById(R.id.btn_asuransi);
//        btnMultifinance = view.findViewById(R.id.btn_multifinance);
//        btnTokenPln = view.findViewById(R.id.btn_pln);
//        btnPulsa = view.findViewById(R.id.btn_pulsa);
//        expand(layoutTrf);
//        collapse(layoutByr);
//        collapse(layoutBli);
//        collapse(layoutDonasi);
//
//        imgArrowTrf.setImageResource(R.drawable.ic_arrow_up);
//        imgArrowByr.setImageResource(R.drawable.ic_arrow_down);
//        imgArrowBli.setImageResource(R.drawable.ic_arrow_down);
//        imgArrowDonasi.setImageResource(R.drawable.ic_arrow_down);
//
//        mExpandButtonTrf.setOnClickListener(this);
//        mExpandButtonByr.setOnClickListener(this);
//        mExpandButtonBli.setOnClickListener(this);
//        mExpandButtonDonasi.setOnClickListener(this);
//        btnTrfRekening.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), AntarRekening.class);
//                    startActivity(intent);
//                }
//            }
//        });
//
//        btnTrfBmt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getActivity(), "Under Construction Menu!", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        btnTrfBank.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Toast.makeText(getActivity(), "Under Construction Menu!", Toast.LENGTH_LONG).show();
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), AntarBank.class);
//                    startActivity(intent);
//                }
//            }
//        });
//
//        btnLaz.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    /*Intent intent = new Intent(getActivity(), Donasi.class);
//                    intent.putExtra("TITLE", "Donasi LAZ-BMT");
//                    startActivity(intent);*/
//                    dialogLaz();
//                }
//            }
//        });
//
//        btnLkaf.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), Donasi.class);
//                    intent.putExtra("TITLE", "Donasi LKaf-BMT");
//                    startActivity(intent);
//                }
//            }
//        });
//
//        btnListrik.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), PLNpembayaran.class);
//                    startActivity(intent);
//                }
//            }
//        });
//
//        btnPdam.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    kodeproduk = "pdam";
//                    nextintent = PDAMpembayaran.class;
//                    new AsyncListProd().execute();
//                }
//            }
//        });
//
//        btnTelepon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    kodeproduk = "telp_pasca";
//                    nextintent = TelkomPembayaran.class;
//                    new AsyncListProd().execute();
//                }
//            }
//        });
//
//        btnInternet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getActivity(), "Under Construction Menu!", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        btnTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getActivity(), "Under Construction Menu!", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        btnTiket.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getActivity(), "Under Construction Menu!", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        btnPendidikan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getActivity(), "Under Construction Menu!", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        btnAsuransi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getActivity(), "Under Construction Menu!", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        btnMultifinance.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getActivity(), "Under Construction Menu!", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        btnTokenPln.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), PLNpembelian.class);
//                    startActivity(intent);
//                }
//            }
//        });
//
//        btnPulsa.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), PulsaPembelian.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        return view;
//    }
//
//    @Override
//    public void onClick(final View v) {
//        switch (v.getId()) {
//            case R.id.expandButton_trf:
//
//                if (layoutTrf.isShown()) {
//                    collapse(layoutTrf);
//                    imgArrowTrf.setImageResource(R.drawable.ic_arrow_down);
//                } else {
//                    expand(layoutTrf);
//                    imgArrowTrf.setImageResource(R.drawable.ic_arrow_up);
//                }
//
//                break;
//
//            case R.id.expandButton_byr:
//
//                if (layoutByr.isShown()) {
//                    collapse(layoutByr);
//                    imgArrowByr.setImageResource(R.drawable.ic_arrow_down);
//                } else {
//                    expand(layoutByr);
//                    imgArrowByr.setImageResource(R.drawable.ic_arrow_up);
//                }
//
//                break;
//
//            case R.id.expandButton_bli:
//
//                if (layoutBli.isShown()) {
//                    collapse(layoutBli);
//                    imgArrowBli.setImageResource(R.drawable.ic_arrow_down);
//                } else {
//                    expand(layoutBli);
//                    imgArrowBli.setImageResource(R.drawable.ic_arrow_up);
//                }
//
//                break;
//
//            case R.id.expandButton_donasi:
//
//                if (layoutDonasi.isShown()) {
//                    collapse(layoutDonasi);
//                    imgArrowDonasi.setImageResource(R.drawable.ic_arrow_down);
//                } else {
//                    expand(layoutDonasi);
//                    imgArrowDonasi.setImageResource(R.drawable.ic_arrow_up);
//                }
//
//                break;
//        }
//    }
//
//    public static void expand(final View v) {
//        v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        final int targetHeight = v.getMeasuredHeight();
//
//        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
//        v.getLayoutParams().height = 1;
//        v.setVisibility(View.VISIBLE);
//        Animation a = new Animation() {
//            @Override
//            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                v.getLayoutParams().height = interpolatedTime == 1 ? WindowManager.LayoutParams.WRAP_CONTENT : (int) (targetHeight * interpolatedTime);
//                v.requestLayout();
//            }
//
//            @Override
//            public boolean willChangeBounds() {
//                return true;
//            }
//        };
//
//        // 1dp/ms
//        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
//        v.startAnimation(a);
//    }
//
//    public static void collapse(final View v) {
//        final int initialHeight = v.getMeasuredHeight();
//
//        Animation a = new Animation() {
//            @Override
//            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                if (interpolatedTime == 1) {
//                    v.setVisibility(View.GONE);
//                } else {
//                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
//                    v.requestLayout();
//                }
//            }
//
//            @Override
//            public boolean willChangeBounds() {
//                return true;
//            }
//        };
//
//        // 1dp/ms
//        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
//        v.startAnimation(a);
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
//    private void dialogLaz() {
//        List<String> mList = new ArrayList<String>();
//        mList.add("Donatur Insiden");
//        mList.add("Donatur Tetap");
//
//        final CharSequence[] mDonatur = mList.toArray(new String[mList.size()]);
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
//        dialogBuilder.setTitle("Pilih Jenis Donatur");
//        dialogBuilder.setItems(mDonatur, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int item) {
//                String mPilih = mDonatur[item].toString();
//                if (mPilih.equals("Donatur Insiden")) {
//                    Intent intent = new Intent(getActivity(), Donasi.class);
//                    intent.putExtra("TITLE", "Donasi LAZ-BMT");
//                    startActivity(intent);
//                } else {
//                    Intent intent = new Intent(getActivity(), DonaturLazTetapActivity.class);
//                    intent.putExtra("TITLE", "Donasi LAZ-BMT");
//                    startActivity(intent);
//                }
//
//            }
//        });
//        AlertDialog alertDialogObject = dialogBuilder.create();
//        alertDialogObject.show();
//    }
//
//    private class AsyncListProd extends AsyncTask<Void, Void, Boolean> {
//        ProgressDialog pdLoading = new ProgressDialog(getActivity());
//        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi";
//        Boolean stats = false;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            finisa = false;
//            oslist.clear();
//            pdLoading.setCancelable(false);
//            pdLoading.setIndeterminate(false);
//            pdLoading.setTitle("Memproses");
//            pdLoading.setMessage("Tunggu...");
//            pdLoading.show();
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            try {
//                String uri = new NumSky(getActivity()).decrypt(getResources().getString(R.string.urlListProd)) + kodeproduk;
//                URL obj = new URL(uri);
//                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//                con.setRequestMethod("GET");
//                con.setRequestProperty("Content-Type", "application/json");
//                con.setConnectTimeout(30000);
//                con.setReadTimeout(29000);
//                ket = con.getResponseCode() + " " + con.getResponseMessage();
//                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//                String inputLine;
//                StringBuffer response = new StringBuffer();
//                while ((inputLine = in.readLine()) != null) {
//                    response.append(inputLine + "\n");
//                }
//                in.close();
//                if (con.getResponseCode() == 200) {
//                    stats = true;
//                    JSONParser parser = new JSONParser();
//                    Object objects = parser.parse(response.toString());
//                    JSONObject jsonObject = (JSONObject) objects;
//                    Set keys = jsonObject.keySet();
//                    Iterator a = keys.iterator();
//                    if (kodeproduk.equals("telp_pasca")) {
//                        HashMap<String, String> map = new HashMap<>();
//                        map.put("nmproduk", "TELKOM");
//                        map.put("kdproduk", "TELEPON");
//                        oslist.add(map);
//                        HashMap<String, String> map2 = new HashMap<>();
//                        map2.put("nmproduk", "SPEEDY");
//                        map2.put("kdproduk", "SPEEDY");
//                        oslist.add(map2);
//                    }
//                    while (a.hasNext()) {
//                        String key = (String) a.next();
//                        String value = (String) jsonObject.get(key);
//                        HashMap<String, String> map = new HashMap<>();
//                        map.put("nmproduk", value);
//                        map.put("kdproduk", key);
//                        oslist.add(map);
//                    }
//                    Collections.sort(oslist, new Comparator<HashMap<String, String>>() {
//                        @Override
//                        public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
//                            return lhs.get("nmproduk").compareTo(rhs.get("nmproduk"));
//                        }
//                    });
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return stats;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            super.onPostExecute(result);
//            if (pdLoading.isShowing()) pdLoading.dismiss();
//            try {
//                if (result) {
//                    ListAdapter adapter = new SimpleAdapter(getActivity(), oslist, R.layout.list_item, new String[]{"nmproduk"}, new int[]{R.id.text1});
//                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
//                    list = new ListView(getActivity());
//                    list.setAdapter(adapter);
//                    alertDialog.setView(list);
//                    final Dialog dialog = alertDialog.create();
//                    dialog.show();
//                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            String kdprod = oslist.get(+position).get("kdproduk");
//                            String wil = oslist.get(+position).get("nmproduk");
//                            Intent intent = new Intent(getActivity(), nextintent);
//                            intent.putExtra("JNSTRX", kdprod);
//                            intent.putExtra("WILAYAH", wil);
//                            startActivity(intent);
//                            dialog.dismiss();
//                        }
//                    });
//                } else {
//                    String msga = "#" + ket + "\n";
//                    showAlert("GAGAL Inquiry List Produk", msga);
//                }
//            } catch (Exception o) {
//                o.printStackTrace();
//                String msga = "#" + ket + "\n";
//                showAlert("GAGAL Inquiry List Produk", msga);
//            }
//        }
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
//}
