package com.example.milec.drivetrain_new;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import java.text.SimpleDateFormat;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.machinelearning.*;
import com.amazonaws.services.machinelearning.model.GetMLModelRequest;
import com.amazonaws.services.machinelearning.model.PredictRequest;
import com.amazonaws.services.machinelearning.model.PredictResult;
import com.amazonaws.services.machinelearning.model.*;
import com.amazonaws.mobile.client.AWSMobileClient;

public class QueryActivity extends AppCompatActivity {

    //declare variables
    AmazonMachineLearningClient client;
    PredictRequest predictRequest, predictRequest2;
    ProgressDialog mProgressDialog;

    Spinner gender_spinner, homeowner_renter_spinner, education_spinner, title_spinner, ethnicity_spinner;
    Spinner number_of_children_spinner, dwelling_type_spinner, marital_status_spinner;
    Spinner drive_type_spinner, vehicle_type_spinner, make_spinner;
    EditText edit_text_age, edit_text_income, edit_text_home_year_built, edit_text_area_code;

    HashMap<String,String> queryRecord;

    String results[] = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        //initialize spinners and add strings to it
        drive_type_spinner = findViewById(R.id.drive_type_spinner);
        ArrayAdapter<CharSequence> drive_type_adapter = ArrayAdapter.createFromResource(this, R.array.drive_type_array, R.layout.my_spinner);
        drive_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drive_type_spinner.setAdapter(drive_type_adapter);

        vehicle_type_spinner = findViewById(R.id.vehicle_type_spinner);
        ArrayAdapter<CharSequence> vehicle_type_adapter = ArrayAdapter.createFromResource(this, R.array.vehicle_type_array, R.layout.my_spinner);
        vehicle_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicle_type_spinner.setAdapter(vehicle_type_adapter);

        make_spinner = findViewById(R.id.make_spinner);
        ArrayAdapter<CharSequence> make_adapter = ArrayAdapter.createFromResource(this, R.array.make_array, R.layout.my_spinner);
        make_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        make_spinner.setAdapter(make_adapter);

        gender_spinner = findViewById(R.id.gender_spinner);
        ArrayAdapter<CharSequence> gender_adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, R.layout.my_spinner);
        gender_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender_spinner.setAdapter(gender_adapter);

        homeowner_renter_spinner = findViewById(R.id.homeowner_renter_spinner);
        ArrayAdapter<CharSequence> homeowner_renter_adapter = ArrayAdapter.createFromResource(this, R.array.homeowner_renter_array, R.layout.my_spinner);
        homeowner_renter_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Specify the layout to use when the list of choices appears
        homeowner_renter_spinner.setAdapter(homeowner_renter_adapter); // Apply the adapter to the spinner

        education_spinner = findViewById(R.id.education_spinner);
        ArrayAdapter<CharSequence> education_adapter = ArrayAdapter.createFromResource(this, R.array.education_array, R.layout.my_spinner);
        education_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Specify the layout to use when the list of choices appears
        education_spinner.setAdapter(education_adapter); // Apply the adapter to the spinner

        title_spinner = findViewById(R.id.title_spinner);
        ArrayAdapter<CharSequence> title_adapter = ArrayAdapter.createFromResource(this, R.array.title_array, R.layout.my_spinner);
        title_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Specify the layout to use when the list of choices appears
        title_spinner.setAdapter(title_adapter); // Apply the adapter to the spinner

        ethnicity_spinner = findViewById(R.id.ethnicity_spinner);
        ArrayAdapter<CharSequence> ethnicity_adapter = ArrayAdapter.createFromResource(this, R.array.ethnicity_array, R.layout.my_spinner);
        ethnicity_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Specify the layout to use when the list of choices appears
        ethnicity_spinner.setAdapter(ethnicity_adapter); // Apply the adapter to the spinner

        marital_status_spinner = findViewById(R.id.marital_status_spinner);
        ArrayAdapter<CharSequence> marital_status_adapter = ArrayAdapter.createFromResource(this, R.array.marital_status_array, R.layout.my_spinner);
        marital_status_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Specify the layout to use when the list of choices appears
        marital_status_spinner.setAdapter(marital_status_adapter); // Apply the adapter to the spinner

        number_of_children_spinner = findViewById(R.id.number_of_children_spinner);
        ArrayAdapter<CharSequence> number_of_children_adapter = ArrayAdapter.createFromResource(this, R.array.number_of_children_array, R.layout.my_spinner);
        number_of_children_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Specify the layout to use when the list of choices appears
        number_of_children_spinner.setAdapter(number_of_children_adapter); // Apply the adapter to the spinner

        dwelling_type_spinner = findViewById(R.id.dwelling_type_spinner);
        ArrayAdapter<CharSequence> dwelling_type_adapter = ArrayAdapter.createFromResource(this, R.array.dwelling_type_array, R.layout.my_spinner);
        dwelling_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Specify the layout to use when the list of choices appears
        dwelling_type_spinner.setAdapter(dwelling_type_adapter); // Apply the adapter to the spinner

        edit_text_age = findViewById(R.id.age_text);
        edit_text_income = findViewById(R.id.income_text);
        edit_text_home_year_built = findViewById(R.id.home_year_built_text);
        edit_text_area_code = findViewById(R.id.area_code_text);


        Button button = findViewById(R.id.query_2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //run the asynctask
                final AsyncTaskRunner runner = new AsyncTaskRunner();
                runner.execute();

                //handle timeout via handler object. On timeout, abort and toast error message
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run()   {
                        if ( runner.getStatus() == AsyncTask.Status.RUNNING)
                        {
                            Toast.makeText(getBaseContext(), "Error: Timed out accessing prediction server. Please try again.", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                            runner.cancel(true);
                        }
                    }
                }, 10000);


            }
        });


    }

    //AsyncTask enables proper use of UI thread; allows for background operations and publishes results
    //On pre execute - show the progress dialog
    //in background - connect to aws and get results
    //on post execute - send query to history screen and start results screen
    private class AsyncTaskRunner extends AsyncTask<Void, Void, String[]> {

        //********************************************************************//
        //********************IMPORTANT**************************************//
        //We deleted aws identity pool and ML model IDs to prevent additional costs//
        //Lines: 180, 188, 189
        //*******************************************************************//
        //*******************************************************************//
        @Override
        protected String[] doInBackground(Void... params) {

            //establish a connection with AWS Mobile. AWSMobileClient is a singleton that will be an interface for AWS services
            AWSMobileClient.getInstance().initialize(QueryActivity.this).execute();


            // Initialize the Amazon Cognito credentials provider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    // Identity pool ID goes here
                    Regions.US_EAST_1 // Region
            );
            client = new AmazonMachineLearningClient(credentialsProvider);



            // Use a created model that has a created real-time endpoint
            String mlModelId = ""; //aws ml model id 1 goes here
            String mlModelId2 = ""; //model id2 goes here
            GetMLModelRequest getMLModelRequest = new GetMLModelRequest();
            GetMLModelRequest getMLModelRequest2 = new GetMLModelRequest();
            getMLModelRequest.setMLModelId(mlModelId);
            getMLModelRequest2.setMLModelId(mlModelId2);
            GetMLModelResult mlModelResult = client.getMLModel(getMLModelRequest);
            GetMLModelResult mlModelResult2 = client.getMLModel(getMLModelRequest2);

            // Validate that the ML model is completed
            if (!mlModelResult.getStatus().equals(EntityStatus.COMPLETED.toString())
                    || !mlModelResult2.getStatus().equals(EntityStatus.COMPLETED.toString())) {
                results[0] = "ML Model is not completed: " + mlModelResult.getStatus();
                results[1] = "ML Model is not completed: " + mlModelResult2.getStatus();
                return results;
            }

            // Validate that the realtime endpoint is ready
            if (!mlModelResult.getEndpointInfo().getEndpointStatus().equals(RealtimeEndpointStatus.READY.toString())
                    || !mlModelResult2.getEndpointInfo().getEndpointStatus().equals(RealtimeEndpointStatus.READY.toString())){
                results[0] = "Realtime endpoint is not ready: " + mlModelResult.getEndpointInfo().getEndpointStatus();
                results[1] = "Realtime endpoint is not ready: " + mlModelResult2.getEndpointInfo().getEndpointStatus();
                return results;
            }

            // Create a Predict request with your ML model ID and the appropriate Record mapping
            predictRequest = new PredictRequest();
            predictRequest2 = new PredictRequest();
            predictRequest.setMLModelId(mlModelId);
            predictRequest2.setMLModelId(mlModelId2);


            predictRequest.setRecord(queryRecord);
            predictRequest2.setRecord(queryRecord);
            predictRequest.setPredictEndpoint(mlModelResult.getEndpointInfo().getEndpointUrl());
            predictRequest2.setPredictEndpoint(mlModelResult2.getEndpointInfo().getEndpointUrl());

            // Call Predict and print out your prediction
            PredictResult predictResult = client.predict(predictRequest);
            PredictResult predictResult2 = client.predict(predictRequest2);
            results[0] = predictResult.getPrediction().toString();
            results[1] = predictResult2.getPrediction().toString();


            return results;
        }


        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(String[] results) {
            mProgressDialog.dismiss();

            String filename = "history_file.srl";

            File f = new File (getFilesDir()+"/"+filename, "");

            FileOutputStream fOut;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date());

            Log.d("TIME", currentDateTime);

            //save the query in a file so history screen can access it
            if ( f.exists() && (!f.isDirectory())) {
                try {
                    fOut = new FileOutputStream(f,true);
                    fOut.write(currentDateTime.getBytes());
                    fOut.write("#".getBytes());
                    fOut.write(queryRecord.toString().getBytes());
                    fOut.write("#".getBytes());
                    fOut.write(results[0].getBytes());
                    fOut.write("#".getBytes());
                    fOut.write(results[1].getBytes());
                    fOut.write("#".getBytes());
                    fOut.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            else {
                try {
                    fOut = openFileOutput(filename,  MODE_PRIVATE);
                    fOut.write(currentDateTime.getBytes());
                    fOut.write("#".getBytes());
                    fOut.write(queryRecord.toString().getBytes());
                    fOut.write("#".getBytes());
                    fOut.write(results[0].getBytes());
                    fOut.write("#".getBytes());
                    fOut.write(results[1].getBytes());
                    fOut.write("#".getBytes());
                    fOut.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            //save results for history
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(QueryActivity.this);
            String res0 = prefs.getString("RESULTS0", "");
            String res1 = prefs.getString("RESULTS1", "");
            SharedPreferences.Editor editor = prefs.edit();
            if(res0.equals("")){
                editor.putString("RESULTS0", results[0]);
            }
            else {
                String appendedValue = res0 + "#" + results[0];
                editor.putString("RESULTS0", appendedValue);
            }
            if(res1.equals("")){
                editor.putString("RESULTS1", results[1]);
            }
            else {
                String appendedValue = res1 + "#" + results[1];
                editor.putString("RESULTS1", appendedValue);
            }
            editor.apply();

            //open results activity passing results
            Log.d("JOZOOoo", queryRecord.toString());
            String emptyQuery = "{Person Education=null, Area Code=, Dwelling Type=null, Vehicle Type=, Gender Code=null, Drive Type=," +
                    " Make=, Person Marital Status=null, Presence of Children Type=U, Person Exact Age=, " +
                    "CAPE: Inc: HH: Median Family Household Income=, Ethnic Insight - Grouping Code=null, Est. Household Income V5=U, Home Year Built=, " +
                    "Number of Children in LU=, Combined Homeowner/Renter=null, Person Title of Respect=}";

            //prevent having empty query
            if(!queryRecord.toString().equals(emptyQuery)) {
                Intent intent = new Intent(QueryActivity.this, ResultsActivity.class);
                intent.putExtra("MODEL", results[0]);
                intent.putExtra("YEAR", results[1]);
                startActivity(intent);
            }
            else
                Toast.makeText(getBaseContext(), "Error: empty query", Toast.LENGTH_LONG).show();


        }


        @Override
        protected void onPreExecute() {
            queryRecord = handleValues();
            mProgressDialog = new ProgressDialog(QueryActivity.this);
            mProgressDialog.setTitle("Generating Prediction...");
            mProgressDialog.setMessage("Connecting to AWS...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

    }

    HashMap<String,String> handleValues()
    {
        StringBuilder stringBuilder = new StringBuilder();

        //collect values from spinners

        String selectedDriveType = drive_type_spinner.getSelectedItem().toString();
        if(selectedDriveType != null && !selectedDriveType.isEmpty())
            stringBuilder.append(selectedDriveType + ", ");
        String selectedvehicleType = vehicle_type_spinner.getSelectedItem().toString();
        if(selectedvehicleType != null && !selectedvehicleType.isEmpty())
            stringBuilder.append(selectedvehicleType + ", ");
        String selectedMake = make_spinner.getSelectedItem().toString();
        if(selectedMake != null && !selectedMake.isEmpty())
            stringBuilder.append(selectedMake + ", ");
        String selectedGender = gender_spinner.getSelectedItem().toString();
        if(selectedGender != null && !selectedGender.isEmpty())
            stringBuilder.append(selectedGender + ", ");
        String recordTitle = title_spinner.getSelectedItem().toString();
        if(recordTitle != null && !recordTitle.isEmpty())
            stringBuilder.append(recordTitle + ", ");
        String selectedEthnicity = ethnicity_spinner.getSelectedItem().toString();
        if(selectedEthnicity != null && !selectedEthnicity.isEmpty())
            stringBuilder.append(selectedEthnicity + ", ");
        String selectedMaritalStatus = marital_status_spinner.getSelectedItem().toString();
        if(selectedMaritalStatus != null && !selectedMaritalStatus.isEmpty())
            stringBuilder.append(selectedMaritalStatus + ", ");
        String recordNumberOfChildren = number_of_children_spinner.getSelectedItem().toString();
        if(recordNumberOfChildren != null && !recordNumberOfChildren.isEmpty())
            stringBuilder.append("CHILDREN: " + recordNumberOfChildren + ", ");
        String selectedEducation =  education_spinner.getSelectedItem().toString();
        if(selectedEducation != null && !selectedEducation.isEmpty())
            stringBuilder.append(selectedEducation + ", ");
        String selectedDwellingType = dwelling_type_spinner.getSelectedItem().toString();
        if(selectedDwellingType != null && !selectedDwellingType.isEmpty())
            stringBuilder.append(selectedDwellingType + ", ");
        String selectedHomeownerRenter = homeowner_renter_spinner.getSelectedItem().toString();
        if(selectedHomeownerRenter != null && !selectedHomeownerRenter.isEmpty())
            stringBuilder.append(selectedHomeownerRenter + ", ");


        //collect values from text fields

        String recordAge = edit_text_age.getText().toString();
        if(recordAge != null && !recordAge.isEmpty())
            stringBuilder.append("AGE: " + recordAge + ", ");
        String recordIncome = edit_text_income.getText().toString();
        if(recordIncome != null && !recordIncome.isEmpty())
            stringBuilder.append("INCOME: " + recordIncome + ", ");
        String recordHomeYearBuilt = edit_text_home_year_built.getText().toString();
        if(recordHomeYearBuilt != null && !recordHomeYearBuilt.isEmpty())
            stringBuilder.append("HOME YEAR: " + recordHomeYearBuilt + ", ");
        String recordAreaCode = edit_text_area_code.getText().toString();
        if(recordAreaCode != null && !recordAreaCode.isEmpty())
            stringBuilder.append("AREA CODE: " + recordAreaCode + ", ");

        //send values to history activity for buttons
        String finalString = stringBuilder.toString();
        if (finalString != null && !finalString.isEmpty() && finalString.length() > 2)
            finalString = finalString.substring(0, finalString.length() - 2);

        if (finalString == null || finalString.isEmpty())
            finalString = "Empty";

        Log.d("AAAAAA", finalString);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String data = prefs.getString("QUERY", "");
        SharedPreferences.Editor editor = prefs.edit();
        if(data.equals("")){
            editor.putString("QUERY", finalString);
        }
        else {
            String appendedValue = data + "#" + finalString;
            editor.putString("QUERY", appendedValue);
        }

        editor.apply();

        //handle gender

        HashMap<String,String> genderHandler = new HashMap<>();
        genderHandler.put("Male", "MALE");
        genderHandler.put("Female", "FEMALE");

        String recordGender = genderHandler.get(selectedGender);

        //handle ethnicity

        HashMap<String,String> ethnicityHandler = new HashMap<>();
        ethnicityHandler.put("Western European", "K");
        ethnicityHandler.put("Hispanic", "O");
        ethnicityHandler.put("African American", "A");
        ethnicityHandler.put("South Asian", "C");
        ethnicityHandler.put("Central Asian", "D");
        ethnicityHandler.put("Southeast Asian", "B");
        ethnicityHandler.put("East Asian", "N");
        ethnicityHandler.put("Mediterranean", "E");
        ethnicityHandler.put("Native American", "F");
        ethnicityHandler.put("Scandinavian", "G");
        ethnicityHandler.put("Polynesian", "H");
        ethnicityHandler.put("Middle Eastern", "I");
        ethnicityHandler.put("Jewish", "J");
        ethnicityHandler.put("Eastern European", "L");
        ethnicityHandler.put("Caribbean", "M");
        ethnicityHandler.put(" ", "Z");

        String recordEthnicity = ethnicityHandler.get(selectedEthnicity);

        //handle marital status

        HashMap<String,String> maritalStatusHandler = new HashMap<>();
        maritalStatusHandler.put("Extremely Likely Married", "1M");
        maritalStatusHandler.put("Likely Married", "5M");
        maritalStatusHandler.put("Single", "5S");
        maritalStatusHandler.put(" ", "0U");

        String recordMaritalStatus = maritalStatusHandler.get(selectedMaritalStatus);

        //handle education

        HashMap<String,String> educationHandler = new HashMap<>();
        educationHandler.put("Some High School", "15");
        educationHandler.put("High School", "11");
        educationHandler.put("Some College", "12");
        educationHandler.put("Bachelor Degree", "13");
        educationHandler.put("Graduate Degree", "14");
        educationHandler.put("Some High School Likely", "55");
        educationHandler.put("High School Likely", "51");
        educationHandler.put("Some College Likely", "52");
        educationHandler.put("Bachelor Degree Likely", "53");
        educationHandler.put("Graduate Degree Likely", "54");
        educationHandler.put(" ", "00");

        String recordEducation = educationHandler.get(selectedEducation);

        //handle dwelling type

        HashMap<String,String> dwellingTypeHandler = new HashMap<>();
        dwellingTypeHandler.put("Apartment (w/ Number)", "A");
        dwellingTypeHandler.put("Apartment (w/o Number)", "M");
        dwellingTypeHandler.put("House", "S");
        dwellingTypeHandler.put("P.O. Box", "P");

        String recordDwellingType = dwellingTypeHandler.get(selectedDwellingType);

        //handle homeowner/renter

        HashMap<String,String> homeownerRenterHandler = new HashMap<>();
        homeownerRenterHandler.put("Probable Homeowner 70-79%", "7");
        homeownerRenterHandler.put("Probable Homeowner 80-89%", "8");
        homeownerRenterHandler.put("Probable Homeowner 90-100%", "9");
        homeownerRenterHandler.put("Homeowner", "H");
        homeownerRenterHandler.put("Probable Renter", "T");
        homeownerRenterHandler.put("Renter", "R");
        homeownerRenterHandler.put(" ", "U");

        String recordHomeownerRenter = homeownerRenterHandler.get(selectedHomeownerRenter);

        //handle presence of children attribute

        String recordPresenceOfChildren;

        switch (recordNumberOfChildren) {
            case "":
                recordPresenceOfChildren = "U";
                break;
            case "0":
                recordPresenceOfChildren = "N";
                break;
            default:
                recordPresenceOfChildren = "Y";
                break;
        }

        //handle est income attribute

        String recordEstHouseholdIncome;
        Integer incomeValue;

        if ( recordIncome.equals(""))
            incomeValue = -1;

        else incomeValue = parseInt(recordIncome);

        if ( incomeValue >= 0 && incomeValue < 15000 )
            recordEstHouseholdIncome = "A";
        else if ( incomeValue >= 15000 && incomeValue < 25000)
            recordEstHouseholdIncome = "B";
        else if ( incomeValue >= 25000 && incomeValue < 35000)
            recordEstHouseholdIncome = "C";
        else if ( incomeValue >= 35000 && incomeValue < 50000)
            recordEstHouseholdIncome = "D";
        else if ( incomeValue >= 50000 && incomeValue < 75000)
            recordEstHouseholdIncome = "E";
        else if ( incomeValue >= 75000 && incomeValue < 100000)
            recordEstHouseholdIncome = "F";
        else if ( incomeValue >= 100000 && incomeValue < 125000)
            recordEstHouseholdIncome = "G";
        else if ( incomeValue >= 125000 && incomeValue < 150000)
            recordEstHouseholdIncome = "H";
        else if ( incomeValue >= 150000 && incomeValue < 175000)
            recordEstHouseholdIncome = "I";
        else if ( incomeValue >= 175000 && incomeValue < 200000)
            recordEstHouseholdIncome = "J";
        else if ( incomeValue >= 200000 && incomeValue < 250000)
            recordEstHouseholdIncome = "K";
        else if ( incomeValue >= 250000 )
            recordEstHouseholdIncome = "L";
        else
            recordEstHouseholdIncome = "U";

        //build query hash map

        queryRecord = new HashMap<>();
        queryRecord.put("Gender Code", recordGender);
        queryRecord.put("Area Code", recordAreaCode);
        queryRecord.put("Home Year Built", recordHomeYearBuilt);
        queryRecord.put("Ethnic Insight - Grouping Code", recordEthnicity);
        queryRecord.put("Dwelling Type", recordDwellingType);
        queryRecord.put("Presence of Children Type", recordPresenceOfChildren);
        queryRecord.put("Person Marital Status", recordMaritalStatus);
        queryRecord.put("Person Exact Age", recordAge);
        queryRecord.put("Number of Children in LU", recordNumberOfChildren);
        queryRecord.put("CAPE: Inc: HH: Median Family Household Income", recordIncome);
        queryRecord.put("Combined Homeowner/Renter", recordHomeownerRenter);
        queryRecord.put("Est. Household Income V5", recordEstHouseholdIncome);
        queryRecord.put("Person Education", recordEducation);
        queryRecord.put("Person Title of Respect", recordTitle);
        queryRecord.put("Drive Type", selectedDriveType);
        queryRecord.put("Vehicle Type",selectedvehicleType);
        queryRecord.put("Make", selectedMake);


        return queryRecord;
    }

}