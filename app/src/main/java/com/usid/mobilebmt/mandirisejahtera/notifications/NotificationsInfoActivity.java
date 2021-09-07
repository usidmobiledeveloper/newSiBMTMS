package com.usid.mobilebmt.mandirisejahtera.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.usid.mobilebmt.mandirisejahtera.NewMainActivity;
import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Created by AHMAD AYIK RIFAI on 3/7/2017.
 */

public class NotificationsInfoActivity extends AppCompatActivity {

    private static final String TAG = NotificationsInfoActivity.class.getSimpleName();

    NotificationsAdapter adapter;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ProgressBar pBar;
    ListView list;

    String from;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_info);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if (extras == null) {
                from = "";
            } else {
                from = extras.getString("from");
            }
        } else {
            from = (String) savedInstanceState.getSerializable("from");
        }
        //ayik baru
        Utility.setNotificationStatus(this, "0");

        pBar = (ProgressBar) findViewById(R.id.pBar);
        list = (ListView) findViewById(R.id.listnotifications);

        pBar.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);

        Query ref = FirebaseDatabase.getInstance().getReference().child(getString(R.string.db_notification));
        ref.orderByKey().limitToLast(8).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get map of users in datasnapshot
                collectData((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pBar.setVisibility(View.GONE);
                Toast.makeText(NotificationsInfoActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                //handle databaseError
            }
        });

        //TAMBAHAN AYIK=============================================================================
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "new message: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };
        FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
        displayFirebaseRegId();
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        Log.e(TAG, "Firebase reg id: " + regId);
    }

    @Override
    protected void onPause() {
        // TAMBAHAN AYIK============================================================================
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        //==========================================================================================
        super.onPause();
    }

    private void collectData(Map<String, Object> users) {
        ArrayList<String> datas = new ArrayList<>();

        if (users != null) {
            for (Map.Entry<String, Object> entry : users.entrySet()) {
                //Get user map
                Map singleData = (Map) entry.getValue();
                String key = entry.getKey();
                String title = (String) singleData.get("title");
                String message = (String) singleData.get("message");
                datas.add(key + "#" + title + "#" + message);

                Log.d(TAG, "datas= " + key + "#" + title + "#" + message);
            }
            Collections.sort(datas, Collections.<String>reverseOrder());

            String[] arr = datas.toArray(new String[datas.size()]);
            adapter = new NotificationsAdapter(this, arr);
            list.setAdapter(adapter);
            pBar.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Tidak ada data", Toast.LENGTH_SHORT).show();
            pBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (from.equals("background")) {
            Intent nextIntent = new Intent(NotificationsInfoActivity.this, NewMainActivity.class);
            nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(nextIntent);
            finish();
        }

    }

    @Override
    protected void onResume() {
        //TAMBAHAN AYIK=============================================================================
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
        //==========================================================================================
        super.onResume();
    }

}