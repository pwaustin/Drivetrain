package com.example.milec.drivetrain_new;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout parent, child;
    Button new_button, clear_button;
    String[] res0_array, res1_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //create 3 vectors: query, year prediction, model prediction

        Vector<String> times = new Vector<>(10, 2);
        Vector<String> queries = new Vector<>(10, 2);
        Vector<String> years = new Vector<>(10, 2);
        Vector<String> models = new Vector<> (10, 2);

        //open history file

        String filename = "history_file.srl";
        try {

            FileInputStream input = new FileInputStream(getFilesDir()+"/"+filename);

            java.util.Scanner s = new java.util.Scanner(input);

            s.useDelimiter("#");

            //until the end of the history file, add a query/model/year triple

            Integer i = 0;

            while ( s.hasNext() )
            {
                if ( i % 4 == 0 )
                    times.add(s.hasNext() ? s.next() :"");
                if ( i % 4 == 1 )
                    queries.add(s.hasNext() ? s.next() :"");
                if ( i % 4 == 2 )
                    models.add(s.hasNext() ? s.next() :"");
                if ( i % 4 == 2 )
                    years.add(s.hasNext() ? s.next() :"");

                i++;
            }

            //close streams

            s.close();
            input.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String data = prefs.getString("QUERY", "nothing selected");
        Log.d("BBBBBB", data);
        String[] button_data = data.split("#");

        //create button for each query
        parent = findViewById(R.id.ll_parentLayout);
        for (Integer j = 0; j < times.size(); j++)
        {
            if(!button_data[j].equals("Empty")) {
                Log.d("HISTORY ITEM:", j.toString());
                Log.d("TIME", times.elementAt(j));
                Log.d("QUERY", queries.elementAt(j));
                Log.d("MODEL", models.elementAt(j));
                Log.d("YEAR", years.elementAt(j));

                new_button = new Button(HistoryActivity.this);
                new_button.setId(j + 1);
                new_button.setText(times.elementAt(j) + "\n" + button_data[j]);
                new_button.setTag(j);
                parent.addView(new_button);
                new_button.setOnClickListener(HistoryActivity.this);
            }
        }

        //add a button to clear history
        clear_button = new Button(HistoryActivity.this);
        clear_button.setText("Clear History");
        clear_button.setTag(99);
        clear_button.setBackgroundResource(R.drawable.mybutton);
        clear_button.setTextColor(getResources().getColor(R.color.White));
        parent.addView(clear_button);
        clear_button.setOnClickListener(HistoryActivity.this);


        String res0 = prefs.getString("RESULTS0", "");
        String res1 = prefs.getString("RESULTS1", "");
        //Log.d("BBBBBB", data);
        res0_array = res0.split("#");
        res1_array = res1.split("#");
    }


    //on click, give results
    @Override
    public void onClick(View view) {
        String str = view.getTag().toString();

        if(str.equals("99")){
            String fileName =  getFilesDir() + "/history_file.srl";
            File myFile = new File(fileName);
            if(myFile.exists()) {
                myFile.delete();
                Toast.makeText(getApplicationContext(), "History cleared!!", Toast.LENGTH_LONG).show();
                recreate();
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
        }

        else{
            //Toast.makeText(getApplicationContext(), res0_array[0], Toast.LENGTH_LONG).show();

            Intent intent = new Intent(HistoryActivity.this, ResultsActivity.class);
            intent.putExtra("MODEL", res0_array[Integer.parseInt(str)]);
            intent.putExtra("YEAR", res1_array[Integer.parseInt(str)]);
            startActivity(intent);
        }
    }
}