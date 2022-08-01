package com.ms.memes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static MemeAdapter ma;
    static RequestQueue rq;
    private static int cnt = 0, ttl = 0;
    Context ctx = MainActivity.this;
    String url = "http://meme-api.herokuapp.com/gimme";
    private RecyclerView list;
    private ArrayList<Bitmap> images;
    private Button cntrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.meme_list);
       cntrl = findViewById(R.id.cntrl);
        cntrl.setTextColor(Color.rgb(255, 0, 0));

        images = new ArrayList<>();

       rq = Volley.newRequestQueue(ctx);

        cntrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cnt++;
                if (cnt % 2 == 0) {
                    cntrl.setText("Stop Loading");
                }
                if (cnt % 2 != 0) {
                    cntrl.setText("Start Loading");
                }

                cnt %= 2;
            }
        });

        ma = new MemeAdapter(images);


        init();

    }

    private void init() {
        list.setLayoutManager(new LinearLayoutManager(ctx));
        list.setAdapter(ma);

        Meme m = new Meme(getApplicationContext(), url);


        HandlerThread handlerThread = new HandlerThread("newHandlerThread");
        handlerThread.start();
        Handler myHandler = new Handler(handlerThread.getLooper());
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (cnt % 2 == 0) {
                        //    if (ttl % 20 == 0){ images.clear();}


                        m.getMeme(new Meme.VolleyResponseListener() {
                            @Override
                            public void onError(String message) {
                                Toast.makeText(ctx, "Error,,,", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(Bitmap response) {
                                // test.setImageBitmap(response);
                                ttl++;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setTitle("Memes (" + ttl + ")");

                                    }
                                });

                                images.add(response);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ma.notifyDataSetChanged();
                                    }
                                });

                            }
                        });
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //  ttl++;
                        //ttl %= 20;
                    }
                }
            }

        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(ctx, "Meme Ended...", Toast.LENGTH_SHORT).show();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
