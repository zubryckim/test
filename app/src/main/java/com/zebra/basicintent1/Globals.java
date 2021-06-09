package com.zebra.basicintent1;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Globals {

        public static final String FILE_NAME = "inventory.txt";
        public static String URL = "http://172.20.0.90:8080/WebService1.asmx";


        // save scan results to file (cvs format)
        // parameters: data - numer inwentarzowy
        // Globals.FILE_NAME
        // local.getText().toString().getBytes()
        public void saveToFile(String data, String imei, String operator, String fileName, String locales, String dataLikwidacji, String stara_nalepka, Context myContext) {

                //Log.d("SaveToFile:", data);
                String timestamp  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                FileOutputStream fos = null;


                try {

                        Inventory inv = new Inventory(data, locales, timestamp, imei, dataLikwidacji, stara_nalepka);

                        Gson gson = new Gson();
                        String coma = ",";
                        if (existFile(fileName, myContext)) {
                                fos = myContext.openFileOutput(fileName, myContext.MODE_APPEND);
                                fos.write(coma.getBytes());
                        }
                        else fos = myContext.openFileOutput(fileName, myContext.MODE_APPEND);
                        fos.write(gson.toJson(inv).getBytes());

               /*
               generate csv file
                try {

                        String separator = ";";
                        //String operator = getSetting("operator");
                        //String mobileName = getSetting("mobileName");
                        String mobileName = imei;
                        fos = myContext.openFileOutput(fileName, myContext.MODE_APPEND);
                        fos.write(data.getBytes());
                        fos.write(separator.getBytes());
                        fos.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()).getBytes());
                        fos.write(separator.getBytes());
                        fos.write(locales.getBytes());
                        fos.write(separator.getBytes());
                        fos.write(operator.getBytes());
                        fos.write(separator.getBytes());
                        fos.write(mobileName.getBytes());
                        fos.write(separator.getBytes());
                        fos.write(dataLikwidacji.getBytes());
                        fos.write(separator.getBytes());
                        fos.write(stara_nalepka.getBytes());
                        fos.write(separator.getBytes());
                        fos.write(System.getProperty("line.separator").getBytes());

                        //Toast.makeText(myContext, "Save to inventory", Toast.LENGTH_LONG).show();
 */

                } catch (IOException e) {
                        e.printStackTrace();
                }


        }


        public void setSetting(String key, String value, Context myContext) {
                SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(myContext);
                SharedPreferences.Editor editor = spref.edit();
                editor.putString(key, value);
                editor.commit();

        }

        public String getSetting(String key, Context myContext) {
                SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(myContext);
                String name = "";
                if (spref.contains(key)) {
                        name = spref.getString(key, "");
                }
                return name;
        }


        public void renameFile(String oldName, String newName, Context myContext) {

                File from = myContext.getFileStreamPath(oldName);
                File to = new File(from.getParent(), newName);
                if (!from.renameTo(to)) {
                        Log.d("renameFile", "Couldn't rename file " + from.getAbsolutePath() + " to " + to.getAbsolutePath());
                }
        }

        public boolean existFile(String name,  Context myContext) {
                File file = myContext.getFileStreamPath(name);
                if (file.exists()) {
                        return true;
                }
                else return false;
        }


        public String getDeviceID(Context myContext) {

                TelephonyManager telephonyManager = (TelephonyManager) myContext.getSystemService(Context.TELEPHONY_SERVICE);

                if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        //lblScanData3.setText("Uprawnij aplikację SkanerZTM do odczytywania stanu i informacji telefonu");
                        return "check Permision";
                }

                else {

                        String id = telephonyManager.getDeviceId();
                        if (id == null) {
                                id = "not available";
                        }

                        int phoneType = telephonyManager.getPhoneType();
                        switch (phoneType) {
                                case TelephonyManager.PHONE_TYPE_NONE:
                                        return  id;

                                case TelephonyManager.PHONE_TYPE_GSM:
                                        //"GSM: IMEI="
                                        return id;

                                case TelephonyManager.PHONE_TYPE_CDMA:
                                        //"CDMA: MEID/ESN="
                                        return id;

                                /*
                                 *  for API Level 11 or above
                                 *  case TelephonyManager.PHONE_TYPE_SIP:
                                 *   return "SIP";
                                 */

                                default:
                                        return  id;
                        }
                }
        }

        public String getMacAddress(Context myContext) {

                WifiManager manager = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);


                if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return "check Permision";
                } else {
                        WifiInfo info = manager.getConnectionInfo();
                        String mac = info.getMacAddress();
                        if (mac == null) {
                                mac = "not available";
                        }
                        return mac;
                }
        }

        public int getVersionCode(Context context) {

                PackageInfo pi= null;
                try{
                        pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e){

                }
                return  pi!=null ? pi.versionCode:-1;

        }

        public String getVersionName(Context context) {

                PackageInfo pi= null;
                try{
                        pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e){

                }
                return  pi!=null ? pi.versionName:"-1";
        }

}



