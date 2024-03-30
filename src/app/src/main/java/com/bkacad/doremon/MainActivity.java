package com.bkacad.doremon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SearchView searchBar;
    private ListView lvChapter;
    private List<Chapter> dataSource = new ArrayList<>();
    private ChapterAdapter chapterAdapter;
    private ArrayList dataSource2 = new ArrayList();
    private ArrayAdapter arrayAdapter;
    private RequestQueue myQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                                //lay du lieu
                                dataSource.add(new Chapter(id, title, url));
                                //lay du lieu hien thi listview
                                dataSource2.add("Chương " + id + " - " + title);
                                arrayAdapter.notifyDataSetChanged();
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

        //search bar =========================================================================
        searchBar = findViewById(R.id.searchBar);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                arrayAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });

        //listView on click item ==============================================================
        lvChapter = findViewById(R.id.lvChapter);
        lvChapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //lay thong tin cua chapter da bam
                //chapter filter
                String selectedChapterItem = arrayAdapter.getItem(position).toString().replaceAll("[\\D]", "");
                int selectedChapterId = Integer.parseInt(selectedChapterItem) - 1;
                //du lieu
                int chapId = dataSource.get(selectedChapterId).getId();
                String chapTitle = dataSource.get(selectedChapterId).getName();
                String chapUrl = dataSource.get(selectedChapterId).getUrl();

                //khoi dong activity moi
                Intent i = new Intent(MainActivity.this, ReadChapter.class);
                i.putExtra("id", chapId);
                i.putExtra("title", chapTitle);
                i.putExtra("url", chapUrl);
                startActivity(i);
            }
        });

        //tao adapter va set adapter cho listview ==============================================
        arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, dataSource2);
        lvChapter.setAdapter(arrayAdapter);
    }

}