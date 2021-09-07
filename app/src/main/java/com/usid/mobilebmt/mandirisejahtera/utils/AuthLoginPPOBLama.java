//package com.usid.mobilebmt.mandirisejahtera.utils;
//
//import android.content.Context;
//import android.os.AsyncTask;
//
//import com.usid.mobilebmt.mandirisejahtera.R;
//
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//

//
//public class AuthLoginPPOBLama extends AsyncTask<Void, Void, Boolean> {
//    Context mContext;
//    boolean stat;
//    private String strTgl = "", pattern = "yyMMddHHmmss", jwt = "", psrsm = "";
//    private Date today = new Date();
//    private Locale id = new Locale("in", "ID");
//    private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
//    IAsyncHandler handler = null;
//
//    public AuthLoginPPOBLama(IAsyncHandler h, Context context) {
//        mContext = context;
//        handler = h;
//    }
//
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//        strTgl = sdf.format(today);
//    }
//
//    @Override
//    protected Boolean doInBackground(Void... params) {
//        try {
//            String salt1 = "", salt2 = "";
//            salt1 = new NumSky(mContext).decrypt(MyVal.salt1());
//            salt2 = new NumSky(mContext).decrypt(MyVal.salt2());
//            psrsm = new ApExeSky(mContext).encrypt(salt1 + ":" + salt2 + strTgl);
//            URL obj = new URL(MyVal.URL_BASE_PPOB() + new NumSky(mContext).decrypt(mContext.getResources().getString(R.string.urlLoginPPOB)));
//            HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
//            conJ.setRequestMethod("POST");
//            conJ.setRequestProperty("Content-Type", "application/json");
//            conJ.setRequestProperty("Authorization", psrsm);
//            String strCek = toJsonString(strTgl);
//            conJ.setConnectTimeout(600000);
//            conJ.setReadTimeout(600000);
//            conJ.setDoOutput(true);
//            DataOutputStream wr = new DataOutputStream(conJ.getOutputStream());
//            wr.writeBytes(strCek);
//            wr.flush();
//            wr.close();
//            BufferedReader in = new BufferedReader(new InputStreamReader(conJ.getInputStream()));
//            String inputLine;
//            StringBuilder response = new StringBuilder();
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine).append("\n");
//            }
//            in.close();
//            JSONParser parser = new JSONParser();
//            Object objects = parser.parse(response.toString());
//            JSONObject jsonObject = (JSONObject) objects;
//            jwt = (String) jsonObject.get("jwt");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return false;
//        }
//        return true;
//
//    }
//
//    @Override
//    protected void onPostExecute(final Boolean success) {
//        if (success) {
//            handler.onPostExec(success, jwt);
//        } else {
//            jwt = "0";
//            handler.onPostExec(success, jwt);
//        }
//    }
//
//    private String toJsonString(String strtgl) {
//        JSONObject obj = new JSONObject();
//        obj.put("datetime", strtgl);
//        return obj.toString();
//    }
//}
