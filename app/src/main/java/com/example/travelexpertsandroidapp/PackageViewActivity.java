package com.example.travelexpertsandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.android.volley.toolbox.Volley;

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
        if (intent.getStringExtra("mode") == "edit"){
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
            Package pkg = (Package) intent.getSerializableExtra("package");

            tbPkgId.setText(pkg.getPackageID());
            tbPkgName.setText(pkg.getPkgName());
            //tbPkgStartDate.setText(pkg.getPkgStartDate().toString());
            //tbPkgEndDate.setText(pkg.getPkgEndDate().toString());
            //tbPkgDesc.setText(pkg.getPkgDesc());
            //tbPkgPrice.setText(String.valueOf(pkg.getPkgBasePrice()));
            //tbAgencyCommission.setText(String.valueOf(pkg.getPkgAgencyCommission()));
        }
        else if(intent.getStringExtra("mode").equals("add")){
            btnConfirmAdd.setEnabled(true);
            btnConfirmAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
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
}