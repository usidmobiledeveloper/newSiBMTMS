package com.usid.mobilebmt.mandirisejahtera.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;
import com.usid.mobilebmt.mandirisejahtera.BuildConfig;
import com.usid.mobilebmt.mandirisejahtera.NewMainActivity;
import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.dashboard.akun.AboutTentang;
import com.usid.mobilebmt.mandirisejahtera.dashboard.akun.AkunDetailActivity;
import com.usid.mobilebmt.mandirisejahtera.dashboard.akun.ContactUsActivity;
import com.usid.mobilebmt.mandirisejahtera.dashboard.akun.GantiPin;
import com.usid.mobilebmt.mandirisejahtera.dashboard.akun.InboxList;
import com.usid.mobilebmt.mandirisejahtera.dashboard.scan.QRProfileActivity;
import com.usid.mobilebmt.mandirisejahtera.dashboard.scan.ScanActivity;
import com.usid.mobilebmt.mandirisejahtera.databinding.NewFragmentAkunBinding;
import com.usid.mobilebmt.mandirisejahtera.utils.MyVal;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;
import com.usid.mobilebmt.mandirisejahtera.utils.SnHp;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility2;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;

import static android.app.Activity.RESULT_OK;
import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;


public class AkunFragment extends Fragment {

    public AkunFragment() {
        // Required empty public constructor
    }

    private CardView cvGantiPin, cvBantuan, cvInbox, cvTentang, cvVisitwww;
    PackageInfo pInfo;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    NewFragmentAkunBinding binding;
    private SharedPreferences config;
    private String strPIN = "", imsi = "", nocard = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = NewFragmentAkunBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        progress_dialog = new ProgressDialog(getActivity());
        pdLoading = new ProgressDialog(getActivity());

        dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + getString(R.string.path2) + "data";

        cvBantuan = (CardView) rootView.findViewById(R.id.cv_bantuan);
        cvGantiPin = (CardView) rootView.findViewById(R.id.cv_gantipin);
        cvInbox = (CardView) rootView.findViewById(R.id.cv_inbox);
        cvTentang = (CardView) rootView.findViewById(R.id.cv_tentang);
        cvVisitwww = (CardView) rootView.findViewById(R.id.cv_visitwww);

        Button btnLogout = rootView.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Yakin logout? Anda akan diarahkan untuk melakukan Re-Aktivasi " + getString(R.string.app_name))
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                Utility2.reAktivasi(getActivity());
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                builder.create();
                builder.show();

            }
        });

        ImageButton btnCallUsid = rootView.findViewById(R.id.btn_call_usid);

        btnCallUsid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmCS("Hubungi Call Center", getString(R.string.jam_layanan));
            }
        });

        cvGantiPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(getActivity(), GantiPin.class);
                    startActivity(intent);
                }
            }
        });

        cvInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InboxList.class);
                startActivity(intent);
            }
        });
        cvBantuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ContactUsActivity.class);
                startActivity(intent);
            }
        });


        cvTentang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutTentang.class);
                startActivity(intent);
            }
        });

        cvVisitwww.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.usid.co.id";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        binding.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Foto Akun");
                final String[] items = {"Ambil Foto", "Galeri"};
                alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String action = items[i];
                        if (action.equalsIgnoreCase("ambil foto")) {
                            camera();
                        } else {
                            Crop.pickImage(getActivity());
                        }
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        binding.fakunTapviewva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jwtpub.equals("0")) {
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();

                } else {
                    //new AsyncVA().execute();
                    startActivity(new Intent(getActivity(), AkunDetailActivity.class));

                }
            }
        });

        binding.ibChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Foto Akun");
                final String[] items = {"Ambil Foto", "Galeri"};
                alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String action = items[i];
                        if (action.equalsIgnoreCase("ambil foto")) {
                            camera();
                        } else {
                            Crop.pickImage(getActivity());
                        }
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

        });

        binding.cvQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), QRProfileActivity.class));
            }
        });

        config = getActivity().getSharedPreferences("config", 0);
        binding.fakunNama.setText(config.getString("NAMA", ""));
        binding.fakunNohp.setText(config.getString("NOHP", ""));

        new LoadImage().execute();

        return rootView;
    }

    private void showConfirmCS(String title, String message) {
        new android.app.AlertDialog.Builder(getActivity()).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + getString(R.string.call_center)));
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                } else {
                    //You already have permission
                    try {
                        startActivity(callIntent);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
                dialog.dismiss();
            }
        }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //==============================================================================================

    private Uri fileUri;
    static final int REQUEST_IMAGE_CAPTURE = 8888;
    private ProgressDialog progress_dialog;
    private boolean cam = false;
    ProgressDialog pdLoading;

    String dirPath = "";
    String fileName = "fp_mobilebmt.jpg";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            new ResultPhoto().execute();
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }

    }


    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(getActivity());
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            progress_dialog.show();
            Uri img = Crop.getOutput(result);
            new Save().execute(img);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(getActivity(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void camera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File newfile = createFile("SrcFilePhoto");
            fileUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", newfile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(getActivity(), "No Camera", Toast.LENGTH_SHORT).show();
        }
    }

    private File createFile(String fileName) {
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();
        File newfile = new File(dir, fileName + ".jpg");
        try {
            newfile.createNewFile();
        } catch (IOException e) {
        }
        return newfile;
    }

    private Bitmap imageOreintationValidator(Bitmap bitmap, String path) {
        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.setRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return bitmap;
    }

    @SuppressLint("HandlerLeak")
    private Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (progress_dialog.isShowing()) progress_dialog.dismiss();
                    break;
                case 1:
                    break;
            }
        }
    };

    class ResultPhoto extends AsyncTask<Void, Void, Void> {
        private ProgressDialog mProgressDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            //mProgressDialog.setTitle("Whatever title");
            mProgressDialog.setMessage("Memproses Foto...");
            mProgressDialog.show();
        }

        protected Void doInBackground(Void... params) {
            File newfile = new File(dirPath + "/" + "SrcFilePhoto.jpg");
            Bitmap bitmap = BitmapFactory.decodeFile(dirPath + "/" + "SrcFilePhoto.jpg");
            bitmap = imageOreintationValidator(bitmap, newfile.getAbsolutePath());
            saveImage(newfile.getAbsolutePath(), bitmap);
            Uri imgFile = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", newfile);
            beginCrop(imgFile);
            return null;
        }

        protected void onPostExecute(Void result) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    class Save extends AsyncTask<Uri, Void, Void> {
        private ProgressDialog mProgressDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Memproses Cropping...");
            mProgressDialog.show();
        }

        protected Void doInBackground(Uri... imageUri) {
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            File dir = new File(dirPath);
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dirPath, fileName);
            try {
                FileOutputStream fOut = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    File newFile = new File(dirPath + "/" + fileName);
                    Uri imgFile = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", newFile);
                    if (newFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(newFile.getAbsolutePath());
                        binding.imgProfile.setImageBitmap(myBitmap);
                    } else {
                        binding.imgProfile.setImageURI(imgFile);
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                    progressHandler.sendEmptyMessage(0);
                }
            });
            return null;
        }

        protected void onPostExecute(Void result) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }

    private void saveImage(String imagePath, Bitmap b) {

        try {
            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);
            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            } else if (imageType.equalsIgnoreCase("jpeg") || imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            fOut.flush();
            fOut.close();
            b.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class LoadImage extends AsyncTask<Uri, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        protected Void doInBackground(Uri... imageUri) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(dirPath, ".nomedia");
                    if (!file.exists()) {
                        File dir = new File(dirPath);
                        if (!dir.exists()) dir.mkdirs();
                        File files = new File(dirPath, ".nomedia");
                        try {
                            FileOutputStream fOut = new FileOutputStream(files);
                            fOut.flush();
                            fOut.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    File imgFile = new File(dirPath + "/" + fileName);
                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        binding.imgProfile.setImageBitmap(myBitmap);
                    }

                }
            });
            return null;
        }

        protected void onPostExecute(Void result) {

            // here
        }
    }

    private class AsyncVA extends AsyncTask<Void, Void, Void> {
        private Boolean getVA = false;
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi";

        String nama = "", alamat = "", rekening = "", telepon = "";
        String kodebmt = "";
        long novaint;
        String novastr = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            binding.fakunPbar.setVisibility(View.VISIBLE);
            binding.fakunTapviewva.setVisibility(View.GONE);

            SnHp telpMan = new SnHp(getActivity());
            if (5 != telpMan.telephonyManager().getSimState()) {
                imsi = "TIDAK ADA KARTU";
            } else {
                imsi = Utility.getIMSIRead(getActivity());
            }
            NumSky nmsk = new NumSky(getActivity());
            try {
                nocard = nmsk.decrypt(config.getString("3D0k", ""));
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(getActivity()).decrypt(getResources().getString(R.string.urlGetVA)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = toJsonString(Utility.md5(nocard), Utility.md5(imsi));

                Log.d("AYIK", "va-strcek " + strCek);

                conJ.setConnectTimeout(20000);
                conJ.setReadTimeout(19000);
                conJ.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(conJ.getOutputStream());
                wr.writeBytes(strCek);
                wr.flush();
                wr.close();
                ket = conJ.getResponseCode() + " " + conJ.getResponseMessage();
                BufferedReader in = new BufferedReader(new InputStreamReader(conJ.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine).append("\n");
                }
                in.close();

                JSONParser parser = new JSONParser();
                Object objects = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) objects;
                getVA = (Boolean) jsonObject.get("status");
                ket = (String) jsonObject.get("keterangan");

                telepon = (String) jsonObject.get("telepon");
                nama = (String) jsonObject.get("nama");
                alamat = (String) jsonObject.get("alamat");
                rekening = (String) jsonObject.get("rekening");
                novaint = (long) jsonObject.get("nova");
                novastr = String.format("%06d", novaint);

               /* jwtpub = (String) jsonObject.get("jwt");
                Utility2.setPrefsAuthToken(jwtpub);*/

            } catch (Exception ex) {
                ex.printStackTrace();
                getVA = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            binding.fakunPbar.setVisibility(View.GONE);

            if (getVA) {
                kodebmt = getResources().getString(R.string.kodebmt);
                String finalnova = "888080" + kodebmt + rekening.substring(0, 3) + novastr;
                binding.llVa.setVisibility(View.VISIBLE);
                binding.fakunVa.setText(finalnova);
            } else {
                binding.fakunTapviewva.setVisibility(View.VISIBLE);
                binding.fakunVa.setVisibility(View.GONE);
                String msga = "#" + ket + "\n";
                Toast.makeText(getActivity(), msga, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String toJsonString(String nokartu, String imsi) {
        JSONObject obj = new JSONObject();
        obj.put("nokartu", nokartu);
        obj.put("imsi", imsi);

        return obj.toString();
    }

}
