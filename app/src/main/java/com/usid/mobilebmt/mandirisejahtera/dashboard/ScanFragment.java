package com.usid.mobilebmt.mandirisejahtera.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.usid.mobilebmt.mandirisejahtera.dashboard.scan.ScanActivity;
import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.transfer.AntarRekening;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;


public class ScanFragment extends Fragment {

    private boolean cam = false;

    public ScanFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        RelativeLayout relativeLayout = view.findViewById(R.id.layout_scan);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(getActivity()).setCaptureActivity(ScanActivity.class).initiateScan();
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Scan dibatalkan", Toast.LENGTH_SHORT).show();
            } else {
                IntentResult resultx = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (resultx != null) {
                    if (resultx.getContents() == null) {
                        Toast.makeText(getActivity(), "Scan dibatalkan", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            String norek = new NumSky(getActivity()).decrypt(resultx.getContents());
                            if (norek.length() == 13) {
                                if (jwtpub.equals("0"))
                                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                                else {
                                    Intent intent = new Intent(getActivity(), AntarRekening.class);
                                    intent.putExtra("norek", norek);
                                    startActivity(intent);
                                }
                                if (!cam) {
                                    cam = true;
                                }
                            } else {
                                Toast.makeText(getActivity(), "Format No. Rekening salah, silahkan coba lagi", Toast.LENGTH_SHORT).show();
                            }
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Format No. Rekening salah, silahkan coba lagi", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Format No. Rekening salah, silahkan coba lagi", Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
