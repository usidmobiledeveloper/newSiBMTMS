package com.usid.mobilebmt.mandirisejahtera.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.base.BaseApp;
import com.usid.mobilebmt.mandirisejahtera.model.User;
import com.usid.mobilebmt.mandirisejahtera.utils.ApExeSky2;
import com.usid.mobilebmt.mandirisejahtera.utils.MyVal;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility2;
import com.usid.mobilebmt.mandirisejahtera.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.usid.mobilebmt.mandirisejahtera.utils.MyVal.MYTAG;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.createPayload;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.createSignature;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.setPrefsAuthToken;


public class AuthRepository {
    public MutableLiveData<Resource<User>> requestLogin(String datetime) {
        final MutableLiveData<Resource<User>> mutableLiveData = new MutableLiveData<>();
        String url = null;
        String token = "";
        setPrefsAuthToken(token);

        String kodebmt = BaseApp.getAppContext().getResources().getString(R.string.kodebmt);
        String username = MyVal.username();
        String password = MyVal.password;
        String key = password + " " + username + " " + kodebmt;
        url = MyVal.URL_BASE() + MyVal.URI_LOGIN();

        Log.d("AYIK", "urllogin "+ url);

        final String[] message = {""};

        mutableLiveData.setValue(Resource.loading(null));
        User model = new User();
        String headers = username + ":" + password + ":" + datetime;

        try {
            headers = new ApExeSky2(BaseApp.getAppContext()).encrypt(headers);

        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            e.printStackTrace();

        }

        String finalHeaders = headers.replace("\n", "");
        String signature = "";

        try {
            signature = createSignature(createPayload(MyVal.URI_LOGIN(), "POST",
                    finalHeaders, datetime, toJson(datetime, kodebmt)), key);

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            e.printStackTrace();

        }

        String finalSignature = signature.replace("\n", "");
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("AYIK", "response " + response);

                        try {
                            JSONObject obj = new JSONObject(response);
                            message[0] = obj.getString("responseDesc").trim();

                            if (message[0].equals("Sukses")) {
                                String token = obj.getString("token");

                                model.setToken(token);
                                mutableLiveData.setValue(Resource.success(model));

                            } else {
                                mutableLiveData.setValue(Resource.error(message[0], null));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mutableLiveData.setValue(Resource.error(e.getMessage(), null));
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        try {
                            int code;
                            code = error.networkResponse.statusCode;
                            String msgError = new String(error.networkResponse.data);

                            Log.d("AYIK", "code " + code);
                            Log.d("AYIK", "error " + msgError);

                            String message = Utility2.responseMessage(code, msgError);
                            mutableLiveData.setValue(Resource.error("#"+code + " " + message, null));
                        } catch (Exception e) {
                            mutableLiveData.setValue(Resource.error("ERROR, Tidak ada koneksi ke server", null));

                        }


                    }
                }) {
            @Override
            public byte[] getBody() {
                String raw = toJson(datetime, kodebmt);

                return raw.getBytes();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("Timestamp", Utility2.getISO8601());
                header.put("Authorization", finalHeaders);
                header.put("Signature", finalSignature);
                header.put("Content-Type", "application/json");
                return header;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(BaseApp.getAppContext()).addToRequestQueue(request);


        return mutableLiveData;
    }

    public MutableLiveData<Resource<User>> requestReLogin(String datetime) {
        final MutableLiveData<Resource<User>> mutableLiveData = new MutableLiveData<>();

        String url = MyVal.URL_BASE() + MyVal.URI_RELOGIN();

        String token = getPrefsAuthToken();

        final String[] message = {""};

        mutableLiveData.setValue(Resource.loading(null));
        User model = new User();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject obj = new JSONObject(response);
                            message[0] = obj.getString("responseDesc").trim();
                            if (message[0].equals("Sukses")) {
                                String token = obj.getString("token");

                                model.setToken(token);
                                mutableLiveData.setValue(Resource.success(model));

                            } else {
                                mutableLiveData.setValue(Resource.error(message[0], null));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mutableLiveData.setValue(Resource.error(e.getMessage(), null));

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        try {
                            int code;
                            code = error.networkResponse.statusCode;
                            String msgError = new String(error.networkResponse.data);

                            String message = Utility2.responseMessage(code, msgError);
                            mutableLiveData.setValue(Resource.error(message, null));
                        } catch (Exception e) {
                            mutableLiveData.setValue(Resource.error("ERROR, Tidak ada koneksi ke server", null));

                        }


                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();

                header.put("Authorization", token);
                header.put("Content-Type", "application/json");

                return header;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(BaseApp.getAppContext()).addToRequestQueue(request);


        return mutableLiveData;
    }

    private String toJson(String datetime, String kodebmt) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("datetime", datetime);
            obj.put("kodebmt", kodebmt);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return obj.toString();
    }

}
