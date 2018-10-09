package com.example.carloslores.lamusicachallenge;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class MainMusica extends AppCompatActivity {

    private String TAG = MainMusica.class.getSimpleName();

    private class Wrapper{
        private String onlineColor;
        private String onlineTitle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final GetCategories gt = new GetCategories();

        // get your ToggleButton
        final Button bt = findViewById(R.id.pressHere);

        // attach an OnClickListener
        bt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if(bt.isClickable()){
                    bt.setText("Downloading...");
                    gt.execute();
                }
                else Toast.makeText(MainMusica.this, "No Connection", Toast.LENGTH_LONG).show();
            }
        });
    }

    private class GetCategories extends AsyncTask<String, Void, Wrapper> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainMusica.this,"Json Data is downloading",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Wrapper doInBackground(String... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://api.lamusica.com/api/categories";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray data = jsonObj.getJSONArray("data");

                        // Here is where I select my json element index
                        JSONObject c = data.getJSONObject(6);
                        String color = c.getString("color");
                        String order = c.getString("order");
                        String slug = c.getString("slug");
                        String title = c.getString("title");

                        // Adding the _id node is JSON Object
                        JSONObject _id = c.getJSONObject("_id");
                        String orgID = _id.getString("$oid");


                        // Used an ArrayList to add the parsed JSON and get the specific
                        // field necessary for the output.
                        ArrayList<String> category = new ArrayList<>();

                        // adding each field to the ArrayList.
                        category.add(orgID);
                        category.add(color);
                        category.add(order);
                        category.add(slug);
                        category.add(title);

                        //get the necessary field for output
                        String colors = category.get(1);
                        String titles = category.get(4);

                        //wrapper class call to output to onPostExecute(Wrapper w)
                        Wrapper w = new Wrapper();
                        w.onlineColor = colors;
                        w.onlineTitle = titles;

                        return w;
//                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Wrapper w) {
            //find the button in the activity_main.
            Button bt = findViewById(R.id.pressHere);
            //changes the button text from the String.
            bt.setText(w.onlineTitle);
            //find the necessary background which in this case is the RelativeLayout.
            RelativeLayout rl = findViewById(R.id.backgroundColor);
            //set the background to the color code from the server!
            rl.setBackgroundColor(Color.parseColor(w.onlineColor));
        }
    }
}