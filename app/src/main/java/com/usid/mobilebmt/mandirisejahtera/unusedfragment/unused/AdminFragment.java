//package com.usid.mobilebmt.mandirisejahtera.unusedfragment.unused;
//
//import android.Manifest;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Bundle;
//import androidx.core.app.ActivityCompat;
//import androidx.fragment.app.Fragment;
//import androidx.core.content.ContextCompat;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.usid.mobilebmt.mandirisejahtera.R;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.akun.AboutTentang;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.akun.GantiPin;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.akun.InboxList;
//
//import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;
//

//public class AdminFragment extends Fragment {
//    private View myFragmentView;
//    private Button btnGantiPIN, btnInbox, btnAbout, btnCallCentre;
//    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0;
//
//    public AdminFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        // Set title
////        ((MainLamaActivity) getActivity()).setActionBarTitle(getString(R.string.administrasi));
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        myFragmentView = inflater.inflate(R.layout.fragment_admin, container, false);
//        btnGantiPIN = (Button) myFragmentView.findViewById(R.id.btGantiPIN);
//        btnInbox = (Button) myFragmentView.findViewById(R.id.btInbox);
//        btnAbout = (Button) myFragmentView.findViewById(R.id.btAbout);
//        btnCallCentre = (Button) myFragmentView.findViewById(R.id.btCallCenter);
//        btnGantiPIN.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), GantiPin.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        btnInbox.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), InboxList.class);
//                startActivity(intent);
//            }
//        });
//        btnAbout.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), AboutTentang.class);
//                startActivity(intent);
//            }
//        });
//        btnCallCentre.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                showConfirm("Hubungi Call Center", "Jam Layanan dan keluhan pelanggan pada Hari Sabtu - Kamis pukul 08.00 - 14.00.\nAnda yakin untuk menghubungi call center sekarang?");
//            }
//        });
//        return myFragmentView;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Intent callIntent = new Intent(Intent.ACTION_CALL);
//                    callIntent.setData(Uri.parse("tel:"+getString(R.string.call_center)));
//                    startActivity(callIntent);
//                    // permission was granted, yay! Do the phone call
//
//                } else {
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, Permision denied", Toast.LENGTH_SHORT).show();
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }
//
//    private void showConfirm(String title, String message) {
//        new android.app.AlertDialog.Builder(getActivity()).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent callIntent = new Intent(Intent.ACTION_CALL);
//                callIntent.setData(Uri.parse("tel:"+ getString(R.string.call_center)));
//                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//
//                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
//
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
//}
