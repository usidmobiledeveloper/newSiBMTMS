package com.usid.mobilebmt.mandirisejahtera.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.base.BaseApp;
import com.usid.mobilebmt.mandirisejahtera.model.User;
import com.usid.mobilebmt.mandirisejahtera.repository.Resource;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.createPayload;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.createSignature;

public class AuthLoginPPOB extends AsyncTask<Void, Void, Boolean> {
    Context mContext;
    //boolean stat;
    private String strTgl = "", pattern = "yyyyMMddHHmmss", jwt = "", psrsm = "";
    private Date today = new Date();
    private Locale id = new Locale("in", "ID");
    private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
    IAsyncHandler handler = null;

    //new parameters
    String url = "", datetime = "", kodebmt = "", finalHeaders = "", finalSignature = "";

    public AuthLoginPPOB(IAsyncHandler h, Context context) {
        mContext = context;
        handler = h;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        strTgl = sdf.format(today);

        //new paramseter
        datetime = strTgl;
        kodebmt = BaseApp.getAppContext().getResources().getString(R.string.kodebmt);
        String username = MyVal.username();
        String password = MyVal.password;
        String key = password + " " + username + " " + kodebmt;
        url = MyVal.URL_BASE() + MyVal.URI_LOGIN();

        String headers = username + ":" + password + ":" + datetime;

        try {
            headers = new ApExeSky2(BaseApp.getAppContext()).encrypt(headers);

        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            e.printStackTrace();

        }

        finalHeaders = headers.replace("\n", "");
        String signature = "";

        try {
            signature = createSignature(createPayload(MyVal.URI_LOGIN(), "POST",
                    finalHeaders, datetime, toJson(datetime, kodebmt)), key);

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            e.printStackTrace();

        }

        finalSignature = signature.replace("\n", "");

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected Boolean doInBackground(Void... params) {
        try {

            URL obj = new URL(MyVal.URL_BASE_PPOB() + new NumSky(mContext).decrypt(mContext.getResources().getString(R.string.urlLoginPPOB)));
            Log.d("AYIK", "authppob:url "+ obj.toString());

            HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
            conJ.setRequestMethod("POST");
            conJ.setRequestProperty("Content-Type", "application/json");

            conJ.setRequestProperty("Timestamp", Utility2.getISO8601());
            conJ.setRequestProperty("Authorization", finalHeaders);
            conJ.setRequestProperty("Signature", finalSignature);

            Log.d("AYIK", "authppob:Timestamp "+ Utility2.getISO8601());
            Log.d("AYIK", "authppob:Authorization "+ finalHeaders);
            Log.d("AYIK", "authppob:Signature "+ finalSignature);

            //String strCek = toJsonString(strTgl); //fungs ilama
            String strCek = toJson(datetime, kodebmt);
            Log.d("AYIK", "authppob:body "+ strCek);

            conJ.setConnectTimeout(600000);
            conJ.setReadTimeout(600000);
            conJ.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conJ.getOutputStream());
            wr.writeBytes(strCek);
            wr.flush();
            wr.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(conJ.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine).append("\n");
            }
            in.close();

            Log.d("AYIK", "authresponse "+ response.toString());

            JSONParser parser = new JSONParser();
            Object objects = parser.parse(response.toString());
            JSONObject jsonObject = (JSONObject) objects;
            jwt = (String) jsonObject.get("token");
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;

    }

    @Override
    protected void onPostExecute(final Boolean success) {

        Log.d("AYIK", "authppob "+ success+ " "+ jwt);
        if (success) {
            handler.onPostExec(success, jwt);
        } else {
            jwt = "0";
            handler.onPostExec(success, jwt);
        }
    }

    private String toJsonString(String strtgl) {
        JSONObject obj = new JSONObject();
        obj.put("datetime", strtgl);
        return obj.toString();
    }

    private String toJson(String datetime, String kodebmt) {
        org.json.JSONObject obj = new org.json.JSONObject();
        try {
            obj.put("datetime", datetime);
            obj.put("kodebmt", kodebmt);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return obj.toString();
    }
}
