/*
Daniel Palmer
PROJ-207-A
Workshop 8 - Android App
2022-05-03
 */

package com.example.travelexpertsandroidapp;

/*
    This is an android app prototype that is designed to allow the Agents of Travel Experts
    The ability to modify their various forms of data from a mobile platform
    This prototype demonstrate the maintenance of packages in the database
    This application was created and completed by Daniel Palmer

    Connects to the RESTful API created by Richard (Chef) Cook

    The application has a hard coded url at the following places, where the IP address will need to
    be changed
    THESE LINES ARE NOT ACCURATE ANYMORE, HOWEVER THE LINES ARE STILL RELATIVELY NEAR THE LISTED
    I AM TOO LAZY TO GO AND DO THIS AGAIN, SORRY
    1. Line 111 of MainActivity.java (this page)
    2. Line 164 of PackageViewActivity.java
    3. Line 217 of PackageViewActivity.java
    4. Line 285 of PackageViewActivity.java
    5. Line 325 of PackageViewActivity.java
*/

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import Model.Package;
// MainActicity serves as the main page of application, allowing the user to view all packages,
// select a package to modify, or create a new one
public class MainActivity extends AppCompatActivity {

    private ListView lstPackages;
    private Button btnAdd;
    private RequestQueue requestQueue;
    private ArrayAdapter<Package> adapter;

    // Handles loading all the packages in to the list view as well redirecting
    // the user based on the input selected (list item or add button)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);

        lstPackages = findViewById(R.id.lstPackages);
        btnAdd = findViewById(R.id.btnAdd);

        Executors.newSingleThreadExecutor().execute(new GetPackages());

        // Loads the PackageViewActivity in "edit" mode when a list item is selected
        // Passes the package id of the selected item to the PackageViewActivity page
        lstPackages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Package pkg = (Package) lstPackages.getAdapter().getItem(i);
                Intent intent = new Intent(getApplicationContext(), PackageViewActivity.class);
                intent.putExtra("package", pkg.getPackageID());
                intent.putExtra("mode", "edit");
                startActivity(intent);
            }
        });
        // Loads the PackageViewActivity in "add" mode when the "Add Product" button is clicked
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PackageViewActivity.class);
                intent.putExtra("mode", "add");
                startActivity(intent);
            }
        });
    }
    // Method onResume is run when ever the user is redirected to the main page
    // Clears the list view and reload the data to capture any changes made
    @Override
    protected void onResume() {
        super.onResume();
        try{
            // Pauses the app for half of a second to allow the API changes to process
            // Helps avoid loading values that have been deleted
            // Or loading the page before the new values have registered in the API
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            adapter.clear();
            Executors.newSingleThreadExecutor().execute(new GetPackages());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    // Runnable class that handles creating and submitting GET requests to the API service
    // Uses the JSON response object to load the list view with each package returned
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
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                            } catch (Exception e) {
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