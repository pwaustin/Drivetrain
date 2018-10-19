package com.example.milec.drivetrain_new;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.TreeMap;

import static java.lang.Double.parseDouble;

public class ResultsActivity extends AppCompatActivity {

    ProgressDialog mProgressDialog;

    String modelPrediction;
    String yearPrediction;
    TextView tv, tv2, tv3;
    String URLS[] = new String[3];

    String[] topModels;
    String[] topYears;
    String[] fscoreModels;
    String[] fscoreYears;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        URLS = prepareURLS();
        tv = findViewById(R.id.textView);
        tv2 = findViewById(R.id.textView2);
        tv3 = findViewById(R.id.textView3);


        getImages myImages = new getImages();
        myImages.execute();



    }

    private void loadText() {

        float percentageModels[] = new float[3];
        float percentageYears[] = new float[3];
        for (int i = 0; i < 3; i++){
            percentageModels[i]=Float.parseFloat(fscoreModels[i]) * 100;
            fscoreModels[i] = Float.toString(percentageModels[i]);
            fscoreModels[i] = fscoreModels[i].substring(0, Math.min(fscoreModels[i].length(), 5));

            percentageYears[i]=Float.parseFloat(fscoreYears[i]) * 100;
            fscoreYears[i] = Float.toString(percentageYears[i]);
            fscoreYears[i] = fscoreYears[i].substring(0, Math.min(fscoreYears[i].length(), 5));
        }

        String s = "Prediction 1:\nModel = " + topModels[0] + " (" + fscoreModels[0] + "%)" + "\nYear = " + topYears[0] + " (" + fscoreYears[0] + "%)";
        String s2 = "Prediction 2:\nModel = " + topModels[1] + " (" + fscoreModels[1] + "%)" + "\nYear = " + topYears[1] + " (" + fscoreYears[1] + "%)";
        String s3 = "Prediction 3:\nModel = " + topModels[2] + " (" + fscoreModels[2] + "%)" + "\nYear = " + topYears[2] + " (" + fscoreYears[2] + "%)";
        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setText(s);
        tv2.setText(s2);
        tv3.setText(s3);
    }

    String[] prepareURLS()
    {

        modelPrediction = getIntent().getStringExtra("MODEL");
        yearPrediction = getIntent().getStringExtra("YEAR");

        //load prediction data saved from query screen
        //parse predictions for top 3 predicted classes
        //regex to discrete raw prediction strings

        String[] processedModelPrediction = modelPrediction.split("[,}{=]");
        String[] processedYearPrediction = yearPrediction.split("[,}{=]");

        //load raw values into hash map

        HashMap<Double, String> unsortedModels = new HashMap<Double, String>();
        HashMap<Double, String> unsortedYears = new HashMap<Double, String>();

        //handle hash map for model

        //start at 3 and go to max - 6 to ignore extraneous values

        for (int i = 3; i < processedModelPrediction.length - 6; i++) {

            //on odd values only...

            if (i % 2 == 1) {
                //if leading white space, remove it

                processedModelPrediction[i] = processedModelPrediction[i].trim();

                //add i as value and i+1 as key to hash map

                unsortedModels.put(parseDouble(processedModelPrediction[i + 1]), processedModelPrediction[i]);

            }

        }

        //handle hash map for year

        for (int i = 3; i < processedYearPrediction.length - 6; i++) {
            if (i % 2 == 1) {
                //if leading white space, remove it

                processedYearPrediction[i] = processedYearPrediction[i].trim();

                //add i as value and i+1 as key to hash map

                unsortedYears.put(parseDouble(processedYearPrediction[i + 1]), processedYearPrediction[i]);

            }

        }

        //sort the hash maps

        TreeMap<Double, String> sortedModels = new TreeMap<>();
        TreeMap<Double, String> sortedYears = new TreeMap<>();
        sortedModels.putAll(unsortedModels);
        sortedYears.putAll(unsortedYears);

        //extract the top 3 models and years with their fscores

        topModels = new String[3];
        topYears = new String[3];
        fscoreModels = new String[3];
        fscoreYears = new String[3];

        for (int i = 0; i < 3; i++) {

            //extract values for last entry

            topModels[i] = sortedModels.lastEntry().getValue();
            fscoreModels[i] = sortedModels.lastEntry().getKey().toString();
            topYears[i] = sortedYears.lastEntry().getValue();
            fscoreYears[i] = sortedYears.lastEntry().getKey().toString();

            //remove last entry

            sortedModels.remove(sortedModels.lastKey());
            sortedYears.remove(sortedYears.lastKey());

        }


        //get raw URLs for image search

        String[] rawURLS = new String[3];

        for (int i = 0; i < 3; i++)
            rawURLS[i] = "https://www.google.com/search?q=" + topYears[i] + "+" + topModels[i] + "&tbm=isch";

        //scrape URL HTML for image URL - begin async task

        return rawURLS;

    }

    //Use asyncTask for UI thread
    private class getImages extends AsyncTask<Void, Void, Bitmap[]> {

        @Override
        protected void onPreExecute() {

            mProgressDialog = new ProgressDialog(ResultsActivity.this);
            mProgressDialog.setTitle("Generating Prediction...");
            mProgressDialog.setMessage("Generating images...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Bitmap[] doInBackground(Void... params) {

            Bitmap[] bitmap = new Bitmap[3];

            //get an image for each prediction

            for ( int i = 0; i < 3; i++) {

                try {
                    // Connect to the web site
                    Document document = Jsoup.connect(URLS[i]).get();
                    // Using Elements to get the class data
                    Elements img = document.getElementsByClass("rg_meta notranslate");
                    //split the HTML on "
                    String rawHTML = img.toString();
                    String[] processedHTML = rawHTML.split("\"");

                    //find the first split string starting with http- this is the target image

                    Integer j = 0;
                    while ( !processedHTML[j].startsWith("http"))
                    {
                        j++;
                    }

                    //final URL found at index j, store it

                    String finalURL = processedHTML[j];

                    Log.d("Final URL", finalURL);

                    //load from the final URL

                    InputStream input = new java.net.URL(finalURL).openStream();

                    //Decode Bitmap

                    bitmap[i] = BitmapFactory.decodeStream(input);

                } catch (IOException e) {
                    Log.d("E", e.toString());
                }

            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap[] images) {

            loadText();
            //handle bitmap display

            //example code:
            //Set downloaded image into ImageView
            ImageView logoimg = findViewById(R.id.imageView1);
            ImageView logoimg2 = findViewById(R.id.imageView2);
            ImageView logoimg3 = findViewById(R.id.imageView3);
            logoimg.setImageBitmap(images[0]);
            logoimg2.setImageBitmap(images[1]);
            logoimg3.setImageBitmap(images[2]);

            mProgressDialog.dismiss();
        }
    }



}
