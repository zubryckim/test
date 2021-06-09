package com.zebra.basicintent1;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class UpdateActivity extends AppCompatActivity {

    DbHelper dataHelper;

    private ProgressBar progressBar;
    private int progressStatus = 0;
    private TextView progressText;
    ArrayList<String> Locations = new ArrayList<String>();
    ArrayList<Ingredient> lista = new ArrayList<Ingredient>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);


        //local dataBase
        dataHelper = new DbHelper(this);

        //progressBar
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressText = (TextView) findViewById(R.id.textView);


        new GetLocation().execute();

    }

    //
    // Get location list from WebService asynchronous
    // step 1
    //
    class GetLocation extends AsyncTask<String, Integer, String> {

        WebService wb = new WebService();

        @Override
        protected String doInBackground(String... params) {
            return wb.GetLocation();
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressText.setText("Pobieranie pól spisowych z serwera...");
        }

        @Override
        protected void onPostExecute(String result) {

            if (result.contains("refused") || result.contains("error")) {
                progressText.setText(result);
            } else {
                //Log.d("result", result);
                dataHelper.truncateLocation();
                Locations = wb.JsonToList(result);
                new SaveLocationsToLocalDatabase().execute();
                //textView.setText("GOTOWE");
                //progressBar.setVisibility(View.INVISIBLE);

            }
        }
    }

    //
    // save the results of the retrieved Locations to the local database asynchronously
    //
    class SaveLocationsToLocalDatabase extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {

            for (int i = 0; i < Locations.size(); i++) {
                boolean results = dataHelper.addToLocation(Locations.get(i));
                //Log.d("LOCATIONS", Locations.get(i));
                progressStatus = 100 - ((Locations.size() - i) * 100) / Locations.size();
                progressBar.setProgress(progressStatus);
                publishProgress(progressStatus);
                //Log.d("Dodane do bazy:", +progressStatus + "/");
            }
            return "ok";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressText.setText("Zapisywanie listy lokalizacji - " + progressStatus + "/" + progressBar.getMax());
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String result) {
            Log.d("SaveLocationsToLocalDatabase: onPostExecute:", "GOTOWE");
            progressBar.setVisibility(View.INVISIBLE);
            progressText.setText("BAZA ZOSTAŁA ZAKTUALIZOWANA.");
            new GetIngredientList().execute();
        }
    }

    //
    // Get ingredient list from WebService asynchronous
    //
    class GetIngredientList extends AsyncTask<String, Integer, String> {

        WebService wb = new WebService();

        @Override
        protected String doInBackground(String... params) {
            return wb.GetAllIngredientList();
        }
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressText.setText("Pobieranie listy składników z serwera...");
        }
        @Override
        protected void onPostExecute(String result) {

            if (result.contains("refused") || result.contains("error")) {
                progressText.setText(result);
            } else {
                //Log.d("result", result);
                dataHelper.truncateIngredient();
                lista = wb.StrJsonToIngredient(result);
                progressBar.setVisibility(View.INVISIBLE);
                new SaveListToLocalDatabase().execute();
            }
        }
    }

    //
    // save the results of the retrieved list (lista) of ingredient to the local database asynchronously
    //
    class SaveListToLocalDatabase extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String sql="INSERT INTO Ingredient (ING_INV, ING_Name, ING_st_koszt, ING_data_likwidacji) VALUES ";

            for (int i = 0; i < lista.size(); i++) {

                boolean results = dataHelper.addToIngredient(lista.get(i).getNrInw(), lista.get(i).getName(), lista.get(i).getCostPosition(), lista.get(i).getDateOfLiquidation());

                progressStatus = 100 - ((lista.size() - i) * 100) / lista.size();
                progressBar.setProgress(progressStatus);
                publishProgress(progressStatus);
                //Log.d("Dodane do bazy:", +progressStatus + "/");
            }

            Log.d("SQL", sql);
            return "ok";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressText.setText("Zapisywanie listy składników - " + progressStatus + "/" + progressBar.getMax());
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("SaveListToLocalDb", "GOTOWE");
            progressBar.setVisibility(View.INVISIBLE);
            progressText.setText("BAZA ZOSTAŁA ZAKTUALIZOWANA.");
            finish();
        }

    }

}
