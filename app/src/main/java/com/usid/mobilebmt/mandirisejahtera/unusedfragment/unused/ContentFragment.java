//package com.usid.mobilebmt.mandirisejahtera.unusedfragment.unused;
//
//import android.content.Intent;
//import android.os.Bundle;
//import androidx.fragment.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.usid.mobilebmt.mandirisejahtera.notifications.NotificationsInfoActivity;
//import com.usid.mobilebmt.mandirisejahtera.R;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.content.InfoBiaya;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.content.KalkulatorZakat;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.content.KantorLayanan;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.content.ProdukActivity;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.content.PromoActivity;
//
//import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class ContentFragment extends Fragment {
//    private View myFragmentView;
//    private Button btnKalk,btnBiaya,btnProduk,btnLokasi, btnNotifikasi, btnPromo;
//
//    public ContentFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        // Set title
////        ((MainLamaActivity) getActivity()).setActionBarTitle(getString(R.string.content));
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        myFragmentView = inflater.inflate(R.layout.fragment_content, container, false);
//        btnLokasi = (Button) myFragmentView.findViewById(R.id.lokasi);
//        btnBiaya = (Button) myFragmentView.findViewById(R.id.btBiaya);
//        btnProduk = (Button) myFragmentView.findViewById(R.id.btProduk);
//        btnPromo = (Button) myFragmentView.findViewById(R.id.btPromo);
//        btnKalk = (Button) myFragmentView.findViewById(R.id.btKalk);
//        btnNotifikasi = (Button) myFragmentView.findViewById(R.id.btnNotifikasi);
//        btnLokasi.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), KantorLayanan.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        btnBiaya.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), InfoBiaya.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        btnProduk.setOnClickListener(new View.OnClickListener() {
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
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), PromoActivity.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        btnKalk.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), KalkulatorZakat.class);
//                startActivity(intent);
//            }
//        });
//
//        btnNotifikasi.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), NotificationsInfoActivity.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        return myFragmentView;
//    }
//
//}
