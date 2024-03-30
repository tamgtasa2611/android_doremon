package com.bkacad.doremon;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.barteksc.pdfviewer.PDFView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ReadChapter extends AppCompatActivity {

    //data
    private List<Chapter> dataSource = new ArrayList<>();
    private RequestQueue myQueue;

    //tao bien luu tru PDF view.
    PDFView pdfView;
    //title textView
    TextView title;
    Button exitBtn;
    Button nextBtn;
    int chapId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        //Call API =========================================================================
        myQueue = Volley.newRequestQueue(this);
        String api = "https://tamgtasa2611.github.io/android_doremon/api.json";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, api, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("chapters");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject chapter = jsonArray.getJSONObject(i);
                                int id = chapter.getInt("id");
                                String title = chapter.getString("title");
                                String url = chapter.getString("url");
                                dataSource.add(new Chapter(id, title, url));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        myQueue.add(request);
        //end Call API =====================================================================

        //chap id + title
        chapId = getIntent().getExtras().getInt("id");
        title = findViewById(R.id.titleView);
        title.setText("Chương " + chapId + " - " + getIntent().getExtras().getString("title"));
        //exit button
        exitBtn = findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ReadChapter.this, MainActivity.class);
                startActivity(i);
            }
        });
        //next button
        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextChapId = chapId;
                //kiem tra xem co phai chuong cuoi cung khong
                if(nextChapId < dataSource.size()) {
                    Intent i = new Intent(ReadChapter.this, ReadChapter.class);
                    i.putExtra("id",  dataSource.get(nextChapId).getId());
                    i.putExtra("title", dataSource.get(nextChapId).getName());
                    i.putExtra("url", dataSource.get(nextChapId).getUrl());
                    startActivity(i);
                } else {
                    Toast.makeText(ReadChapter.this, "Đây là chương cuối cùng!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //url link
        //khoi tao PDF View
        pdfView = findViewById(R.id.pdfView);
        new RetrivePDFfromUrl().execute(getIntent().getExtras().getString("url"));
    }

    //tao 1 async task class de load PDF tu URL
    class RetrivePDFfromUrl extends AsyncTask<String, Void, InputStream> {

        @Override
        protected InputStream doInBackground(String... strings) {
            //dung inputstream de lay PDF
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                // tao ket noi
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    //response thanh cong
                    //luu input tu URL vao bien
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                }

            } catch (IOException e) {
                //method xu ly error
                e.printStackTrace();
                return null;
            }

            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            // load PDF vao view sau khi chay async
            pdfView.fromStream(inputStream).load();

        }
    }

}