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
//import com.usid.mobilebmt.mandirisejahtera.R;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembelian.PLNpembelian;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembelian.PulsaPembelian;
//
//import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;
//

//public class PembelianFragment extends Fragment {
//    private View myFragmentView;
//    private Button btnTokenPLN, btnPulsa;
//
//    public PembelianFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        // Set title
////        ((MainLamaActivity) getActivity()).setActionBarTitle(getString(R.string.pembelian));
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        myFragmentView = inflater.inflate(R.layout.fragment_pembelian, container, false);
//        btnTokenPLN = (Button) myFragmentView.findViewById(R.id.btTokenPLN);
//        btnPulsa = (Button) myFragmentView.findViewById(R.id.btPulsaSeluler);
//        btnTokenPLN.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), PLNpembelian.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        btnPulsa.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), PulsaPembelian.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        return myFragmentView;
//    }
//}
