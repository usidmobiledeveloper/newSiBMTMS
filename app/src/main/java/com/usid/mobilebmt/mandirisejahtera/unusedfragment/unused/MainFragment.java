//package com.usid.mobilebmt.mandirisejahtera.unusedfragment.unused;
//
//import android.os.Bundle;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//
//import com.usid.mobilebmt.mandirisejahtera.R;
//

//public class MainFragment extends Fragment {
//    private View myFragmentView;
//    private Button btnInfo, btnAdmin, btnTransfer, btnPembayaran, btnPembelian, btnContent, btnDonasi;
//
//    public MainFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        // Set title
////        ((MainLamaActivity) getActivity()).setActionBarTitle(getString(R.string.app_name));
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        myFragmentView = inflater.inflate(R.layout.fragment_main, container, false);
//        btnInfo = (Button) myFragmentView.findViewById(R.id.btInfo);
//        btnTransfer = (Button) myFragmentView.findViewById(R.id.btTransfer);
//        btnPembayaran = (Button) myFragmentView.findViewById(R.id.btPembayaran);
//        btnPembelian = (Button) myFragmentView.findViewById(R.id.btPembelian);
//        btnAdmin = (Button) myFragmentView.findViewById(R.id.btAdministrasi);
//        btnContent = (Button) myFragmentView.findViewById(R.id.btContent);
//        btnDonasi = (Button) myFragmentView.findViewById(R.id.btDonasi);
//        btnInfo.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                FragmentManager fm = getFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//                ft.replace(R.id.fragment_container, new InforekFragment()).addToBackStack(null);
//                ft.commit();
//            }
//        });
//        btnTransfer.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                FragmentManager fm = getFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//                ft.replace(R.id.fragment_container, new TransferFragment()).addToBackStack(null);
//                ft.commit();
//            }
//        });
//        btnPembayaran.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                FragmentManager fm = getFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//                ft.replace(R.id.fragment_container, new PembayaranFragment()).addToBackStack(null);
//                ft.commit();
//            }
//        });
//        btnPembelian.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                FragmentManager fm = getFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//                ft.replace(R.id.fragment_container, new PembelianFragment()).addToBackStack(null);
//                ft.commit();
//            }
//        });
//        btnAdmin.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                FragmentManager fm = getFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//                ft.replace(R.id.fragment_container, new AdminFragment()).addToBackStack(null);
//                ft.commit();
//            }
//        });
//        btnDonasi.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                FragmentManager fm = getFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//                ft.replace(R.id.fragment_container, new DonasiFragment()).addToBackStack(null);
//                ft.commit();
//            }
//        });
//        btnContent.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                FragmentManager fm = getFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//                ft.replace(R.id.fragment_container, new ContentFragment()).addToBackStack(null);
//                ft.commit();
//            }
//        });
//        return myFragmentView;
//    }
//
//}
