package com.usid.mobilebmt.mandirisejahtera.dashboard.akun;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.usid.mobilebmt.mandirisejahtera.R;

public class ForwardSMS extends AppCompatActivity {
    private Bundle extras;
    private String strMsg = "", notelp = "";
    private String[] arrPhone;
    private TextView tvIsiPesan;
    private Button btnCancel, btnSMS, btnContacts;
    private EditText edNo;
    private ProgressDialog progress_dialog;
    private int mMessageSentParts;
    private int mMessageSentTotalParts;
    static final int PICK_CONTACT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_forward_sms);
        tvIsiPesan = (TextView) findViewById(R.id.tvIsiPesan);
        edNo = (EditText) findViewById(R.id.editText);
        btnCancel = (Button) findViewById(R.id.buttonCancel);
        btnSMS = (Button) findViewById(R.id.buttonSMS);
        btnContacts = (Button) findViewById(R.id.buttonContact);
        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if (extras == null) {
                strMsg = "";
            } else {
                strMsg = extras.getString("ket");
            }
        } else {
            strMsg = (String) savedInstanceState.getSerializable("ket");
        }
        progress_dialog = new ProgressDialog(this);
        progress_dialog.setCancelable(false);
        progress_dialog.setIndeterminate(false);
        progress_dialog.setTitle("Memproses");
        progress_dialog.setMessage("Tunggu...");
        tvIsiPesan.setText(strMsg);
        btnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edNo.getText().toString().trim().equals("")) {
                    Toast.makeText(getBaseContext(), "Nomor Telepon belum diisi...", Toast.LENGTH_LONG).show();
                } else {
                    /*progress_dialog.show();
                    Handler handler = new Handler();*/
                    sendSMS(edNo.getText().toString().trim(), getString(R.string.app_name) + " - " + strMsg);
                    /*Runnable runnableCode = new Runnable() {
                        @Override
                        public void run() {
                            if (progress_dialog.isShowing()) {
                                Toast.makeText(getBaseContext(), "Error, SMS Gagal...", Toast.LENGTH_SHORT).show();
                                progressHandler.sendEmptyMessage(0);
                            }
                        }
                    };
                    handler.postDelayed(runnableCode, 60000);*/
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (hasPhone.equalsIgnoreCase("1")) {
                            Toast.makeText(this, "HAS PHONE 1", Toast.LENGTH_SHORT).show();
                            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                            int i = 0;
                            arrPhone = new String[phones.getCount()];
                            while (phones.moveToNext()) {
                                String phoneNo = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                arrPhone[i] = phoneNo;
                                i++;
                            }
                            phones.close();
                        }

                        AlertDialogView();
                    }
                }
                break;
        }
    }

    private void AlertDialogView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForwardSMS.this);
        builder.setTitle("Pilih nomor telepon");
        builder.setSingleChoiceItems(arrPhone, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                notelp = arrPhone[item];
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                edNo.setText(notelp);
                edNo.setSelection(notelp.length());
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @SuppressLint("HandlerLeak")
    private Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (progress_dialog.isShowing()) progress_dialog.dismiss();
                    onBackPressed();
                    break;
            }
        }
    };

    private void sendSMS(String phoneNumber, String message) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        intent.putExtra("sms_body", message);
        startActivity(intent);
        finish();

        /*String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message);
        mMessageSentTotalParts = parts.size();
        ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
        for (int j = 0; j < mMessageSentTotalParts; j++) {
            sentIntents.add(sentPI);
            deliveryIntents.add(deliveredPI);
        }
        mMessageSentParts = 0;
        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        mMessageSentParts++;
                        if (mMessageSentParts == mMessageSentTotalParts) {
                            Toast.makeText(getBaseContext(), "SMS Terkirim...", Toast.LENGTH_SHORT).show();
                            progressHandler.sendEmptyMessage(0);
                        }
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
                        progressHandler.sendEmptyMessage(0);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
                        progressHandler.sendEmptyMessage(0);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                        progressHandler.sendEmptyMessage(0);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
                        progressHandler.sendEmptyMessage(0);
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));
        sms.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, deliveryIntents);*/
    }
}
