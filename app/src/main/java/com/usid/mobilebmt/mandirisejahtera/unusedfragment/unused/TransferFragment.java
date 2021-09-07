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
//import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.transfer.AntarBank;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.transfer.AntarRekening;
//
//import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;
//

//public class TransferFragment extends Fragment {
//    private View myFragmentView;
//    private Button btnTransRek, btnTransBMT, btnTransBank;
//
//    public TransferFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        // Set title
////        ((MainLamaActivity) getActivity()).setActionBarTitle(getString(R.string.transfer));
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        myFragmentView = inflater.inflate(R.layout.fragment_transfer, container, false);
//        btnTransRek = (Button) myFragmentView.findViewById(R.id.btTransAntarRek);
//        btnTransBMT = (Button) myFragmentView.findViewById(R.id.btTransAntarBMT);
//        btnTransBank = (Button) myFragmentView.findViewById(R.id.btTransBank);
//        btnTransRek.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), AntarRekening.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        btnTransBMT.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Under Construction Menu!", Toast.LENGTH_LONG).show();
//            }
//        });
//        btnTransBank.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
////                Toast.makeText(getActivity(), "Under Construction Menu!", Toast.LENGTH_LONG).show();
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), AntarBank.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        return myFragmentView;
//    }
//}
