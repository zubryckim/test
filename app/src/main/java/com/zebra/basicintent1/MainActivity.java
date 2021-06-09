package com.zebra.basicintent1;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import android.content.pm.PackageInfo;

import android.view.Menu;


@RequiresApi(api = Build.VERSION_CODES.O)

public class MainActivity extends AppCompatActivity implements modal_info.BottomSheetListener {

    DbHelper dataHelper;
    Globals glob;

    private TextView lblScanData;
    private TextView lblScanData2;
    private TextView lblScanData3;
    private TextView textMobileName;
    private TextView local;
    private String imei;
    private String macAddress;
    private String mobileName;
    private String operator;
    //version
    private Integer versionCode;

    ArrayList<String> Locations = new ArrayList<String>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Send) {
            new sendFileToWebService().execute();
        }
        if (id == R.id.getIngredient) {
            openUpdateActivity();
        }
        if (id == R.id.SetLocation) {
            openLocal2Activity();
        }
        return super.onOptionsItemSelected(item);
    }

    //
    //onCreate
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));

        //SETTINGS
        glob = new Globals();

        imei=glob.getDeviceID(this);
        macAddress = glob.getMacAddress(this);
        mobileName = glob.getSetting("mobileName", getBaseContext());
        versionCode = glob.getVersionCode(this);
        this.setTitle("Skaner ZTM v" + glob.getVersionName(this));

        Log.d("imei:",imei );

        local = (TextView) findViewById(R.id.textView4);
        lblScanData = (TextView) findViewById(R.id.lblScanData);
        lblScanData2 = (TextView) findViewById(R.id.lblScanData2);
        lblScanData3 = (TextView) findViewById(R.id.lblScanData3);
        textMobileName = (TextView) findViewById(R.id.textView0);
        //scanner  receiver
        registerReceiver(myBroadcastReceiver, filter);
        //local database
        dataHelper = new DbHelper(this);

        if (imei != "check Permision") {
            new GetMobileName().execute();
            new GetCurrentVersion().execute();

            if (mobileName.contains("null")) {
                textMobileName.setText("Telefon NIEUPRAWNIONY");
                textMobileName.setTextColor(Color.parseColor("#ff0033"));
                openPermisionActivity();
            }
            else textMobileName.setText(mobileName);

        }
        else {
            lblScanData2.setText("Ustaw uprawnienia aplikacji Skaner ZTM do Telefonu ");
        }

        // otwieranie listy wyboru pola spisowego
        if (dataHelper.locationCount() > 0) {
                openLocal2Activity();
        }
        else openUpdateActivity();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }


    //
    // open new activity when mobile not have a permision
    //
    public void openPermisionActivity(){
        Intent intent = new Intent(this, PermisionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, 1);
    }
    //
    // open new activity to select a location
    //
    public void openLocal2Activity(){
        Intent intent = new Intent(this, Locals2Activity.class);
        intent.putExtra("lokalizacje", Locations);
        startActivityForResult(intent, 1);
    }

    //
    // open new activity to update local database
    //
    public void openUpdateActivity(){
        Intent intent = new Intent(this, UpdateActivity.class);
        //intent.putExtra("aktualizacja", Locations);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);
        if (requestCode==1)
        {
            if (resultCode==RESULT_OK){
                String result = data.getStringExtra("lokalizacja");
                //Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                local.setText(result);
            }
            if (resultCode == RESULT_CANCELED){
                local.setText("-");
            }
        }
    }


    //
    // Scanner receiver registration
    //
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();

            if (action.equals(getResources().getString(R.string.activity_intent_filter_action))) {
                //  Received a barcode scan
                try {
                    displayScanResult(intent, "via Broadcast");
                } catch (Exception e) {
                    //  Catch if the UI does not exist when we receive the broadcast
                }
            }
        }
    };

    //
    // post-scan behavior handling
    //
    @SuppressLint("SetTextI18n")
    private void displayScanResult(Intent initiatingIntent, String howDataReceived) throws InterruptedException {

        if (local.getText().toString().trim().contains("-")) {
            Log.d("skanowanie:",local.getText().toString().trim());
            lblScanData2.setText("przed skanowaniem ustaw pole spisowe!");
        }
        else if (imei=="check Permision")
        {
            lblScanData2.setText("Ustaw uprawnienia aplikacji Skaner ZTM do Telefonu ");
        }
        else {

            String stara_nalepka = "";
            String dataLikwidacji="";
            String nameOfIngredient= "";
            Integer count = 0;
            String decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));

            //sprawdzenie czy nalepka posiada nieaktualny kod GUS

            String nrInv=decodedData.trim();

            if (decodedData.contains("-491-")) {
                nrInv = decodedData.replace("-491-", "-487-");
                Log.d("nrInv:", nrInv);
                Log.d("decodedData:", decodedData);
                stara_nalepka = "tak";
            }
            if (decodedData.contains("-626-")) {
                nrInv = decodedData.replace("-626-", "-623-");
                stara_nalepka = "tak";
            }
            if (decodedData.contains("-808-")) {
                nrInv = decodedData.replace("-808-", "-809-");
                stara_nalepka = "tak";
            }

            lblScanData.setTextColor(Color.parseColor("#16b019"));
            lblScanData.setText(decodedData);
            lblScanData2.setText("");
            lblScanData3.setText("");

            //nameOfIngredient = dataHelper.getIngredient(nrInv).getName();
            //Ingredient ing = dataHelper.getIngredient(decodedData);

            Ingredient ing = dataHelper.getIngredient(nrInv);

            try { nameOfIngredient = ing.getName(); }
            catch (Exception e) { nameOfIngredient =""; };

            try { dataLikwidacji = ing.getDateOfLiquidation().substring(0, 10); }
            catch (Exception e) { dataLikwidacji =""; };

            if (nameOfIngredient != "") {

                Log.d("AfterIF_nameOfIngredient", nameOfIngredient);

                if (dataLikwidacji != "") {
                    Log.d("dataLIkwidacji", dataLikwidacji + "/");
                    lblScanData.setTextColor(Color.parseColor("#ff0033"));
                    lblScanData2.setText(nameOfIngredient);
                    lblScanData3.setTextColor(Color.parseColor("#ff0033"));
                    dataLikwidacji = ing.getDateOfLiquidation().substring(0, 10).toString();
                    Log.d("zlikwidowany w", ing.getDateOfLiquidation());
                    //lblScanData3.setText("zlikwidowany w " + ing.getDateOfLiquidation());
                    //glob.saveToFile(decodedData, imei, operator, Globals.FILE_NAME, local.getText().toString() ,dataLikwidacji, stara_nalepka, getApplicationContext() );

                } else {

                    Log.d("else decodedData:",  decodedData);
                    lblScanData2.setText(nameOfIngredient);
                    lblScanData3.setTextColor(Color.parseColor("#000000"));
                    if (stara_nalepka == "tak") {
                        Log.d("Stara nalepka", decodedData);
                        lblScanData.setTextColor(Color.parseColor("#ff0033"));
                        lblScanData3.setTextColor(Color.parseColor("#ff0033"));
                        lblScanData3.setText("stara nalepka !!!");
                    }
                    dataLikwidacji = "";
                    //glob.saveToFile(decodedData, imei, operator, Globals.FILE_NAME, local.getText().toString() ,dataLikwidacji, stara_nalepka, getApplicationContext() );
                }

            } else {

                lblScanData.setTextColor(Color.parseColor("#ff0033"));
                lblScanData3.setTextColor(Color.parseColor("#ff0033"));
                lblScanData3.setText("BRAK DANYCH W SYSTEMIE SIMPLE!");
                dataLikwidacji = "";
                //glob.saveToFile(decodedData, imei, operator, Globals.FILE_NAME, local.getText().toString() ,dataLikwidacji, stara_nalepka, getApplicationContext() );                                                                                                   );
            }

            glob.saveToFile(decodedData, imei, operator, Globals.FILE_NAME, local.getText().toString(), dataLikwidacji, stara_nalepka, getApplicationContext());

            //MODAL INFO o powtórnym sczytaniu
            count = dataHelper.isSaved(decodedData);
            if (count > 0)
            {
                modal_info modal = new modal_info();
                modal.counter=count;
                modal.show(getSupportFragmentManager(), "modal");
            }
            //lblScanData3.setText( "count: " + dataHelper.isSaved(decodedData));

            //add data scaned to local database
                boolean result = dataHelper.insertData(decodedData.toString(), nameOfIngredient, local.getText().toString(), new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault()).format(new Date()));
                if (result) {
                    //Toast.makeText(getApplicationContext(), "Data inserted succesfully", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Ups.... Data not inserted", Toast.LENGTH_LONG).show();
                }


        }
    }

    @Override
    public void onButtonClicked(String text) {
        //można tu coś zrobić po kliknieciu ok w modalu
        Log.d("from modal button:",  text);

    }


    class GetCurrentVersion extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            WebService wb = new WebService();
            String res = wb.GetCurrentVersion();
            return res;
        }

        @Override
        protected void onPostExecute(String result) {

            Log.d("GetCurrentVersion/onPostExecute/:", result);

            if (result.contains("refused") || result.contains("resolve") ) {
                Toast.makeText(getApplicationContext(), result, 6000).show();
            }
            else if (result!="null") {

                int currentVersion = Integer.parseInt(result);

                Log.d("currentVersion:", ""+currentVersion);
                Log.d("versionCode:", ""+ versionCode);

                if (versionCode < currentVersion) {
                    Log.d("Update", "Dostepna jest nowsza wersja aplikacji");
                    updateAPK();
                }
            }
            else {
                Log.d("GetCurrentVersion result:", result);
            } }


    }

    //
    // get mobile name from WebService
    //
    class GetMobileName extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            WebService wb = new WebService();
            String res = wb.GetMobileName(imei);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {

            Log.d("GetMobileName/onPostExecute/:", result);


            if (result.contains("refused") || result.contains("resolve") ) {
                Toast.makeText(getApplicationContext(), result, 6000).show();
            }
            else if (result!="null") {
                    glob.setSetting("mobileName", result, getBaseContext());
                    mobileName = result;
            }
            else {
               Log.d("GetMobileName result:",result);
            }
        }
    }

    //
    // send the file with inventory data to the WebService
    //
    class sendFileToWebService extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            WebService wb = new WebService();
            String res = wb.PostFile(getBaseContext());
            return res;
        }

        @Override
        protected void onPostExecute(String result) {

            Log.d("sendFileToWebService/onPostExecute/:", result);

            if (result.contains("refused") || result.contains("resolve") ) {
                Toast.makeText(getApplicationContext(), result, 6000).show();
            }
            if (result.contains("OK")) {
                String datetime = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault()).format(new Date());
                glob.renameFile(Globals.FILE_NAME, Globals.FILE_NAME + "_" + datetime, getApplicationContext());
                Toast.makeText(getApplicationContext(), "Dane zostały zapisane na serwerze:" , 3000).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "WebService nie odpowiada", 6000).show();
            }

        }

    }

    public void updateAPK() {

        Snackbar.make(this.findViewById(android.R.id.content), "Dostępna jest nowsza wersja aplikacji", Snackbar.LENGTH_LONG)
                .setAction("Aktualizuj", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = "http://webapp:8080/app.apk";
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                })
                .setActionTextColor(Color.RED)
                .setDuration(30000)
                .show();
    }



}

