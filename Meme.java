package com.ms.memes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Meme {
    static String url;
   static Bitmap img;
   static Context ctx;

    public Meme(Context ctx,String url) {
        this.ctx = ctx;
        this.url = url;
    }
    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(Bitmap response);
    }

    public void getMeme(VolleyResponseListener vrl){
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                new Thread(){
                    @Override
                    public void run() {
                        String sec = null;
                        try {
                            sec = response.getString("url");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                      //  Toast.makeText(ctx, sec, Toast.LENGTH_SHORT).show();
                        try {
                            img = BitmapFactory.decodeStream(new URL(sec).openConnection().getInputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        vrl.onResponse(img);
                    }
                }.start();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                vrl.onError("Error in Image Loading..");
            }
        });
        MainActivity.rq.add(jor);

    }

}
