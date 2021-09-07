package com.usid.mobilebmt.mandirisejahtera.dashboard.akun;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.usid.mobilebmt.mandirisejahtera.R;

public class ContactUsActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0;
    private String strSMS, strWa;
    private TextView tvWa, tvSms;
    private ImageButton btnCallUsid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);

        ImageButton btnWhatsapp = findViewById(R.id.whatsapp);
        ImageButton btnSMS = findViewById(R.id.sms);

        tvWa = findViewById(R.id.text_wa);
        tvSms = findViewById(R.id.text_sms);
        btnCallUsid = findViewById(R.id.btn_call_usid);

        strSMS = tvSms.getText().toString();
        strWa = tvWa.getText().toString();
        btnCallUsid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmCS("Hubungi Call Center", getString(R.string.jam_layanan));
            }
        });
        btnWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWhatsApp();
            }
        });
        btnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + getString(R.string.no_contact_message))));
            }
        });
    }

    private void showConfirmCS(String title, String message) {
        new android.app.AlertDialog.Builder(ContactUsActivity.this).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + getString(R.string.call_center)));
                if (ContextCompat.checkSelfPermission(ContactUsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ContactUsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + getString(R.string.call_center)));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(callIntent);
                    // permission was granted, yay! Do the phone call
                } else {
                    Toast.makeText(ContactUsActivity.this, "Tidak bisa diproses, Permision denied", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void openWhatsApp() {
        try {
            Cursor c = ContactUsActivity.this.getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.Contacts.Data._ID}, ContactsContract.Data.DATA1 + "=?", new String[]{getString(R.string.no_contact_message).replace("+", "") + "@s.whatsapp.net"}, null);
            c.moveToFirst();
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("content://com.android.contacts/data/" + c.getString(0)));
            startActivity(i);
            c.close();
        } catch (Exception e) {
            showAlert("Nomor kontak CS " + getString(R.string.app_name) + " tidak ditemukan!\nTambahkan Kontak CS " + getString(R.string.app_name) + "?");
        }
    }

    private void showAlert(String message) {
        new AlertDialog.Builder(ContactUsActivity.this, AlertDialog.THEME_HOLO_DARK).setIconAttribute(android.R.attr.alertDialogIcon).setMessage(message).setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.NAME, "CS " + getString(R.string.app_name));
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, getString(R.string.no_contact_message));
                startActivity(intent);
                dialog.dismiss();
            }
        }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent sendIntent = new Intent("android.intent.action.MAIN");
                    sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                    sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(getString(R.string.no_contact_message).replace("+", "")) + "@s.whatsapp.net");
                    startActivity(sendIntent);

                } catch (Exception e) {
                    //Log.e(TAG, "ERROR_OPEN_MESSANGER" + e.toString());
                    Toast.makeText(ContactUsActivity.this, "Gagal membuka Whatsapp, silahkan install aplikasi Whatsapp", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        }).show();
    }
}
