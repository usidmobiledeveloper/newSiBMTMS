//package com.usid.mobilebmt.mandirisejahtera.unusedfragment.unused;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import androidx.fragment.app.Fragment;
//import androidx.cardview.widget.CardView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.usid.mobilebmt.mandirisejahtera.notifications.NotificationsInfoActivity;
//import com.usid.mobilebmt.mandirisejahtera.R;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.akun.ContactUsActivity;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.akun.GantiPin;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.akun.InboxList;
//
//import at.markushi.ui.CircleButton;
//
//import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;
//
//public class AkunFragment extends Fragment {
//    private TextView tvtentang;
//    private CardView cv_gantipin, cv_bantuan, cv_inbox, cv_notifikasi;
//    private CircleButton bt_gantipin, bt_bantuan, bt_inbox, bt_notifikasi;
//    PackageInfo pInfo;
//
//    public AkunFragment() {
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
//        View view = inflater.inflate(R.layout.fragment_akun, container, false);
//        cv_notifikasi = (CardView) view.findViewById(R.id.cv_notif);
//        cv_bantuan = (CardView) view.findViewById(R.id.cv_bantuan);
//        cv_gantipin = (CardView) view.findViewById(R.id.cv_gantipin);
//        cv_inbox = (CardView) view.findViewById(R.id.cv_inbox);
//        bt_gantipin = (CircleButton) view.findViewById(R.id.btn_ganti_pin);
//        bt_bantuan = (CircleButton) view.findViewById(R.id.btn_bantuan);
//        bt_inbox = (CircleButton) view.findViewById(R.id.btn_inbox);
//        bt_notifikasi = (CircleButton) view.findViewById(R.id.btn_notifikasi);
//        tvtentang = (TextView) view.findViewById(R.id.fakun_tentang);
//        cv_gantipin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), GantiPin.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        bt_gantipin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), GantiPin.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        cv_inbox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), InboxList.class);
//                startActivity(intent);
//            }
//        });
//        bt_inbox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), InboxList.class);
//                startActivity(intent);
//            }
//        });
//        cv_notifikasi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), NotificationsInfoActivity.class);
//                    startActivity(intent);
////                    getActivity().finish();
//                }
//            }
//        });
//        bt_notifikasi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), NotificationsInfoActivity.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        cv_bantuan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ContactUsActivity.class);
//                startActivity(intent);
//            }
//        });
//        bt_bantuan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ContactUsActivity.class);
//                startActivity(intent);
//            }
//        });
//        try {
//            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
//            tvtentang.setText(getString(R.string.app_name) + " Versi : " + Integer.toString(pInfo.versionCode).substring(0, 1) + "." + Integer.toString(pInfo.versionCode).substring(1));
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return view;
//    }
//
//    @Override
//    public void onAttach(Context ctx) {
//        super.onAttach(ctx);
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//    }
//}
