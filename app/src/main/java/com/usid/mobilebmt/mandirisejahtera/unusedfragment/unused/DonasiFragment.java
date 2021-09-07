//package com.usid.mobilebmt.mandirisejahtera.unusedfragment.unused;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import androidx.fragment.app.Fragment;
//import androidx.appcompat.app.AlertDialog;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.usid.mobilebmt.mandirisejahtera.R;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.donasi.Donasi;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.donasi.DonaturLazTetapActivity;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;
//
//public class DonasiFragment extends Fragment {
//    private View myFragmentView;
//    private Button btnLAZ, btnLKAF;
//
//    public DonasiFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        // Set title
////        ((MainLamaActivity) getActivity()).setActionBarTitle(getString(R.string.donasi));
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        myFragmentView = inflater.inflate(R.layout.fragment_donasi, container, false);
//        btnLKAF = (Button) myFragmentView.findViewById(R.id.btLKAF);
//        btnLAZ = (Button) myFragmentView.findViewById(R.id.btLAZ);
//        btnLKAF.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), Donasi.class);
//                    intent.putExtra("TITLE", "Donasi LKaf-BMT");
//                    startActivity(intent);
//                }
//            }
//        });
//        btnLAZ.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
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
//        return myFragmentView;
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
//
//            }
//        });
//        AlertDialog alertDialogObject = dialogBuilder.create();
//        alertDialogObject.show();
//    }
//
//}
