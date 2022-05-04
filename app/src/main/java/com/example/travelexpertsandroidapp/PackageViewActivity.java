package com.example.travelexpertsandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;

import Model.Package;

public class PackageViewActivity extends AppCompatActivity {

    private EditText tbPkgId;
    private EditText tbPkgName;
    private EditText tbPkgStartDate;
    private EditText tbPkgEndDate;
    private EditText tbPkgDesc;
    private EditText tbPkgPrice;
    private EditText tbAgencyCommission;

    private Button btnUpdate;
    private Button btnDelete;
    private Button btnConfirmAdd;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_view);

        requestQueue = Volley.newRequestQueue(this);

        tbPkgId = findViewById(R.id.tbPkgID);
        tbPkgName = findViewById(R.id.tbPkgName);
        tbPkgStartDate = findViewById(R.id.tbPkgStartDate);
        tbPkgEndDate = findViewById(R.id.tbPkgEndDate);
        tbPkgDesc = findViewById(R.id.tbPkgDesc);
        tbPkgPrice = findViewById(R.id.tbPkgPrice);
        tbAgencyCommission = findViewById(R.id.tbAgencyCommission);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnConfirmAdd = findViewById(R.id.btnConfirmAdd);

        Intent intent = getIntent();
        if (intent.getStringExtra("mode").equals("edit")){
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
            int pkg = (int) intent.getSerializableExtra("package");
            Executors.newSingleThreadExecutor().execute(new GetPackage(pkg));

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    Date startDate = null, endDate = null;
                    try {
                        startDate = (Date) df.parse(tbPkgStartDate.getText().toString());
                        endDate = (Date) df.parse(tbPkgEndDate.getText().toString());
                    } catch (ParseException e) {
                        Toast.makeText(getApplicationContext(), "Invalid Date Format: Date must follow yyyy-mm-dd", Toast.LENGTH_LONG).show();
                    }
                    Package newPackage = new Package(Integer.parseInt(tbPkgId.getText().toString()),
                            tbPkgName.getText().toString(),
                            startDate,
                            endDate,
                            tbPkgDesc.getText().toString(),
                            Double.parseDouble(tbPkgPrice.getText().toString()),
                            Double.parseDouble(tbAgencyCommission.getText().toString()));

                    Executors.newSingleThreadExecutor().execute(new PostPackage(newPackage));
                    finish();
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Executors.newSingleThreadExecutor().execute(new DeletePackage(pkg));
                    finish();
                }
            });

        }
        else if(intent.getStringExtra("mode").equals("add")){
            btnConfirmAdd.setEnabled(true);
            btnConfirmAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    Date startDate = null, endDate = null;
                    try {
                        startDate = (Date) df.parse(tbPkgStartDate.getText().toString());
                        endDate = (Date) df.parse(tbPkgEndDate.getText().toString());
                    } catch (ParseException e) {
                        Toast.makeText(getApplicationContext(), "Invalid Date Format: Date must follow yyyy-mm-dd", Toast.LENGTH_LONG).show();
                    }
                    Package pkg = new Package(0,
                            tbPkgName.getText().toString(),
                            startDate,
                            endDate,
                            tbPkgDesc.getText().toString(),
                            Double.parseDouble(tbPkgPrice.getText().toString()),
                            Double.parseDouble(tbAgencyCommission.getText().toString()));
                    Executors.newSingleThreadExecutor().execute(new PutPackage(pkg));
                    finish();
                }
            });
        }
    }
    class PutPackage implements Runnable{

        private Package pkg;

        public PutPackage(Package Pkg){
            this.pkg = Pkg;
        }

        @Override
        public void run() {
            String URL = "http://192.168.0.57:8081/Group1Term3RestM7-1.0-SNAPSHOT/api/putpackage";
            JSONObject obj = new JSONObject();
            SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
            String startDate = df.format(pkg.getPkgStartDate());
            String endDate = df.format(pkg.getPkgEndDate());
            try{
                obj.put("packageId", 0);
                obj.put("pkgName", pkg.getPkgName());
                obj.put("pkgStartDate", startDate);
                obj.put("pkgEndDate", endDate);
                obj.put("pkgDesc", pkg.getPkgDesc());
                obj.put("pkgBasePrice", pkg.getPkgBasePrice());
                obj.put("pkgAgencyCommission", pkg.getPkgAgencyCommission());
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error Creating Package, please try again", Toast.LENGTH_LONG).show();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, URL, obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.wtf(response.toString(), "utf-8");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.wtf(error.getMessage(), "utf-8");
                }
            });
            requestQueue.add(jsonObjectRequest);
        }
    }

    class GetPackage implements Runnable{
        private int pkgId;

        public GetPackage(int id){
            pkgId = id;
        }

        @Override
        public void run() {
            String URL = "http://192.168.0.57:8081/Group1Term3RestM7-1.0-SNAPSHOT/api/getpackage/" + pkgId;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // I read this logging method and was curious what they meant the acronym
                            // So i googled it, seeing as it is logging i was hopping it was based on
                            // "Why the F***" as in, why isn't this working
                            // Instead it seems to a quote from Austin Powers
                            // "What a terrible failure"
                            // Good one google.
                            VolleyLog.wtf(response, "utf-8");
                            JSONObject obj = null;
                            try{
                                obj = new JSONObject(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            final JSONObject pkg = obj;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
                                    SimpleDateFormat targetDf = new SimpleDateFormat("yyyy-MM-dd");
                                    Date startDate = null, endDate = null;
                                    String formattedStartDate = null, formattedEndDate = null;
                                    try {
                                        startDate = (Date) df.parse(pkg.getString("pkgStartDate"));
                                        endDate = (Date) df.parse(pkg.getString("pkgEndDate"));
                                        formattedStartDate = targetDf.format(startDate);
                                        formattedEndDate = targetDf.format(endDate);
                                    } catch (ParseException | JSONException e) {
                                        Toast.makeText(getApplicationContext(), "Invalid Date Format: Date must follow yyyy-mm-dd", Toast.LENGTH_LONG).show();
                                    }
                                    try {
                                        tbPkgId.setText(String.valueOf(pkg.getInt("packageId")));
                                        tbPkgName.setText(pkg.getString("pkgName"));
                                        tbPkgStartDate.setText(formattedStartDate);
                                        tbPkgEndDate.setText(formattedEndDate);
                                        tbPkgDesc.setText(pkg.getString("pkgDesc"));
                                        tbPkgPrice.setText(pkg.getString("pkgBasePrice"));
                                        tbAgencyCommission.setText(pkg.getString("pkgAgencyCommission"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
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

    class DeletePackage implements Runnable{

        private int pkgid;

        public DeletePackage(int pkgid){
            this.pkgid = pkgid;
        }

        @Override
        public void run() {
            String URL = "http://192.168.0.57:8081/Group1Term3RestM7-1.0-SNAPSHOT/api/deletepackage/" + pkgid;
            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            VolleyLog.wtf(response.toString(), "utf-8");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Toast.makeText(getApplicationContext(), new JSONObject(response).getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.wtf(error.getMessage(), "utf-8");
                }
            });
            requestQueue.add(stringRequest);
        }
    }

    class PostPackage implements Runnable{

        private Package pkg;

        public PostPackage(Package pkg) {
            this.pkg = pkg;
        }

        @Override
        public void run() {
            String URL = "http://192.168.0.57:8081/Group1Term3RestM7-1.0-SNAPSHOT/api/postpackage/";
            JSONObject obj = new JSONObject();
            SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
            String startDate = df.format(pkg.getPkgStartDate());
            String endDate = df.format(pkg.getPkgEndDate());
            try{
                obj.put("packageId", pkg.getPackageID());
                obj.put("pkgName", pkg.getPkgName());
                obj.put("pkgStartDate", startDate);
                obj.put("pkgEndDate", endDate);
                obj.put("pkgDesc", pkg.getPkgDesc());
                obj.put("pkgBasePrice", pkg.getPkgBasePrice());
                obj.put("pkgAgencyCommission", pkg.getPkgAgencyCommission());
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error updating package, please try again", Toast.LENGTH_LONG).show();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.wtf(response.toString(), "utf-8");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.wtf(error.getMessage(), "utf-8");
                }
            });
            requestQueue.add(jsonObjectRequest);
        }
    }
}