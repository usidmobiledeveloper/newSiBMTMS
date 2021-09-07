package com.usid.mobilebmt.mandirisejahtera.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.setPrefsAuthToken;

/**
 * Created by AHMAD AYIK RIFAI on 7/27/2017.
 */

public class AuthLogin2 extends AsyncTask<Void, Void, Boolean> {
    Context mContext;
    boolean isSuccess = false;
    IAsyncHandler handler = null;
    String url = "", jwt = "";

    private String strTgl = "", pattern = "yyMMddHHmmss", ket = "";
    private Date today = new Date();
    private Locale id = new Locale("in", "ID");
    private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);

    public AuthLogin2(IAsyncHandler h, Context context) {
        mContext = context;
        handler = h;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        strTgl = sdf.format(today);

        url = MyVal.URL_BASE() + MyVal.URI_RELOGIN();
        jwt = getPrefsAuthToken();


    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", jwt);
            con.setConnectTimeout(60000);
            con.setReadTimeout(60000);

            ket = con.getResponseCode() + " " + con.getResponseMessage();
            if (con.getResponseCode() == 200) {
                isSuccess = true;
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine + "\n");
                }
                in.close();



                JSONParser parser = new JSONParser();
                Object objects = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) objects;
                jwt = (String) jsonObject.get("token");

                setPrefsAuthToken(jwt);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;

    }

    @Override
    protected void onPostExecute(final Boolean success) {

        if (success) {
            if (isSuccess) {
                handler.onPostExec(true, jwt);

            } else {
                jwt = "401";
                handler.onPostExec(false, jwt);
            }
        } else {
            jwt = "0";
            handler.onPostExec(false, jwt);
        }

    }

    private String toJsonString(String strtgl) {
        JSONObject obj = new JSONObject();
        obj.put("datetime", strtgl);
        return obj.toString();
    }
}

