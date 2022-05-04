package com.example.travelexpertsandroidapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;

import Model.Package;

public class MainActivity extends AppCompatActivity {

    private ListView lstPackages;
    private Button btnAdd;
    private RequestQueue requestQueue;
    private ArrayAdapter<Package> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);

        lstPackages = findViewById(R.id.lstPackages);
        btnAdd = findViewById(R.id.btnAdd);

        Executors.newSingleThreadExecutor().execute(new GetPackages());

        lstPackages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), PackageViewActivity.class);
                intent.putExtra("package", (Package) lstPackages.getAdapter().getItem(i));
                startActivity(intent);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PackageViewActivity.class);
                intent.putExtra("mode", "add");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            adapter.clear();
            Executors.newSingleThreadExecutor().execute(new GetPackages());
        }
        catch(Exception e){
        }
    }

    class GetPackages implements Runnable{
        @Override
        public void run() {
            String URL = "http://192.168.0.57:8081/Group1Term3RestM7-1.0-SNAPSHOT/api/getpackages";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            VolleyLog.wtf(response, "utf-8");
                            adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1);
                            try{
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++){
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
                                    Date startDate = (Date) df.parse(obj.getString("pkgStartDate"));
                                    Date endDate = (Date) df.parse(obj.getString("pkgEndDate"));
                                    Package pkg = new Package(obj.getInt("packageId"),
                                            obj.getString("pkgName"),
                                            startDate,
                                            endDate,
                                            obj.getString("pkgDesc"),
                                            obj.getDouble("pkgBasePrice"),
                                            obj.getDouble("pkgAgencyCommission"));
                                    adapter.add(pkg);
                                }
                            } catch (JSONException | ParseException e) {
                                e.printStackTrace();
                            }
                            final ArrayAdapter<Package> finalAdapter = adapter;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    lstPackages.setAdapter(finalAdapter);
                                }
                            });
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            requestQueue.add(stringRequest);
        }
    }

}