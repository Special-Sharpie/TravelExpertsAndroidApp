/*
Daniel Palmer
PROJ-207-A
Workshop 8 - Android App
2022-05-03
 */

package com.example.travelexpertsandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;

import Model.Package;
// Android activity that provides the user with a form to Add, Edit or Delete packages from the
// Travel Experts database
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
    // On Create method that runs when the activity is created
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_view);

        requestQueue = Volley.newRequestQueue(this);

        //Selects the page controls
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
        // Checks to see which mode has been selected, either add, or edit
        if (intent.getStringExtra("mode").equals("edit")){
            // This page mode provides the user with the ability to edit the values of the currently
            // selected package, or delete it(does not work on existing package, i did not have time to sort the foreign key constraints)
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
            // Pulls the package id from the intent, and pulls the package data associated with from the
            // RESTful API service
            int pkg = (int) intent.getSerializableExtra("package");
            Executors.newSingleThreadExecutor().execute(new GetPackage(pkg));

            // Handles the Onclick event of the Update button, creating an instance of the package class
            // and posts those changes to the API service, finally redirecting the user back to the main page
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
            // Handle the OnClick event for the Delete button, sending a delete request to the API
            // service to the delete the package of the ID passed in the intent
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Executors.newSingleThreadExecutor().execute(new DeletePackage(pkg));
                    finish();
                }
            });

        }
        else if(intent.getStringExtra("mode").equals("add")){
            // This page mode provides the user the ability to create new packages in the database
            btnConfirmAdd.setEnabled(true);
            // Handles the OnClick event of the Add Package button, creates an instance of the Package
            // class and sends a put request to the API service to add the Package to the database
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
    // Runnable class that handles creating and submitting PUT requests to the API service
    // Creates a JSON object out of the Package object passed in the constructor
    class PutPackage implements Runnable{

        private Package pkg;

        public PutPackage(Package Pkg){
            this.pkg = Pkg;
        }

        @Override
        public void run() {
            String URL = "http://192.168.0.57:8081/Group1Term3RestM7-1.0-SNAPSHOT/api/putpackage";
            JSONObject obj = new JSONObject();
            SimpleDateFormat df = new SimpleDateFormat("\"MMM dd, yyyy\"");
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
    // Runnable class that handles creating and submitting GET requests to the API service
    // Uses the JSON response object to load the page fields with the data associated with the passed
    // ID in the database
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
    // Runnable class that handles creating and submitting DELETE requests to the API service
    // Sends a request that the passed ID be deleted
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
    // Runnable class that handles creating and submitting POST requests to the API service
    // Creates a JSON object out of the Package object passed in the constructor and sends
    // the request to the API service to update the package in the database
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