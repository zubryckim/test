package com.zebra.basicintent1;
import android.content.Context;
import android.util.Log;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WebService  {

    public String GetCurrentVersion()
    {
        String s="";
        try
        {
            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(Globals.URL+"/GetCurrentVersion");
            //List<NameValuePair> list=new ArrayList<NameValuePair>();
            //list.add(new BasicNameValuePair("version", version));
            //httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse =  httpClient.execute(httpPost);
            s = readResponse(httpResponse);
            s = XmlToJson(s);
        }
        catch(Exception exception) {
            Log.d("GetLocation exeption", exception.getMessage() + "");
            return exception.getMessage();
        }
        return s;
    }

    public String GetMobileName(String imei)
    {
        String s="";
        try
        {
            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(Globals.URL+"/GetMobileName");
            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("imei", imei));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse =  httpClient.execute(httpPost);
            s = readResponse(httpResponse);
            s = XmlToJson(s);
        }
        catch(Exception exception) {
            Log.d("GetLocation exeption", exception.getMessage() + "");
            return exception.getMessage();
        }
        return s;
    }

    public String GetInventory(String[] value) {
        String s="";
        try
        {
            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(Globals.URL+"/GetInfo");

            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("InventoryCode", value[0]));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse =  httpClient.execute(httpPost);
            //HttpEntity httpEntity=httpResponse.getEntity();

            s = readResponse(httpResponse);
            s = XmlToJson(s);
            //s= StrJsonToDataTable(XmlToJson(s)).get(0).getName()+ " "+ StrJsonToDataTable(s).get(0).getCostPosition();
    }
        catch(Exception exception)  {}
        return s;
    }


    public String GetAllIngredientList()
    {
        String s="";
        try
        {
            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(Globals.URL+"/GetAllIngredientList");
            HttpResponse httpResponse =  httpClient.execute(httpPost);
            s = readResponse(httpResponse);
            //Log.d("GetAllIngredientList", httpResponse.getStatusLine()+"");
            s = XmlToJson(s);
        }
        catch(Exception exception) {
            Log.d("GetAllIngredientList:", exception.getMessage() + "");
            return exception.getMessage();
        }

        return s;
    }

    public String GetLocation()
    {
        String s="";
        try
        {
            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(Globals.URL+"/GetLocation");
            HttpResponse httpResponse =  httpClient.execute(httpPost);
            s = readResponse(httpResponse);
            s = XmlToJson(s);
        }
        catch(Exception exception) {
            Log.d("GetLocation exeption", exception.getMessage() + "");
            return exception.getMessage();
        }

        return s;
    }

    public String PostFile(Context context) {
        String s="";
        File file = new File(context.getFilesDir() , Globals.FILE_NAME);

        try
        {
            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost( Globals.URL + "/PutFile");

            String content="{ 'Inventory': [";

            try {
                BufferedReader  br = new BufferedReader (new FileReader(file));
                String line = "";
                while((line = br.readLine()) != null){
                    content += line;
                }
            }
            catch (IOException e) {}

            content +="]}";

            Log.d("POSTFILE:", content);
            //httpPost.setHeader("Content-type", "multipart/form-data");
            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("buffer", content));
            httpPost.setEntity(new UrlEncodedFormEntity(list,"UTF-8"));
            HttpResponse httpResponse =  httpClient.execute(httpPost);
            //HttpEntity httpEntity=httpResponse.getEntity();

            s = readResponse(httpResponse);
            s = XmlToJson(s);

    }
        catch(Exception exception)  {
            Log.d( "Plik nie zapisał się!", exception.getMessage());
            return exception.getMessage();
        }
        return s;
    }

    public String XmlToJson(String xml)
    {
        String returnedString = xml.replace( "<?xml version=\"1.0\" encoding=\"utf-8\"?><string xmlns=\"skladnik\">", "" );
        returnedString = returnedString.replace( "<?xml version=\"1.0\" encoding=\"utf-8\"?><string xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xsi:nil=\"true\" xmlns=\"skladnik\" />","");
        returnedString = returnedString.replace("</string>","");
        return returnedString;

    }


    public String readResponse(HttpResponse res) {
        InputStream is=null;
        String return_text="";
        try {
            is=res.getEntity().getContent();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is));
            String line="";
            StringBuffer sb=new StringBuffer();
            while ((line=bufferedReader.readLine())!=null)
            {
                sb.append(line);
            }
            return_text=sb.toString();
        } catch (Exception e)
        {

        }
        return return_text;
    }

//funkcja przetwarzająca string json na listę objektów Inmgredient (składniki)
    public ArrayList<Ingredient> StrJsonToIngredient(String s){

            //Log.d("StrJsonToDataTable:",s);
            ArrayList<Ingredient> skl = new ArrayList<Ingredient>();

        try {
                JSONObject json = new JSONObject(s);
                JSONArray skladniki = json.getJSONArray("ingredient");
                //JSONObject ingredient = json.getJSONObject("ingredient");skladniki.
                //Log.d( "ingredient", json.getString("ingredient"));

                for(int i=0;i<skladniki.length();i++)
                {
                    //Log.d( "jsonObject:", skladniki.get(i).toString());
                    JSONObject ingredient = new JSONObject(skladniki.get(i).toString());

                    //Log.d( "Ingredient:", ingredient.getString("nazwa"));
                    Ingredient sklad = new Ingredient(ingredient.getString("nr_inwentarzowy"), ingredient.getString("nazwa"), ingredient.getString("st_koszt_idn"), ingredient.getString("data_likwidacji"));
                    skl.add(sklad);
                }

                //String count = Integer.toString(skl.size());
                //Log.d("count:", count);
                //Log.d("Nr:", skl.get(0).getNrInw());


            } catch (JSONException e) {
                // manage exceptions
                e.printStackTrace();
                return skl;
            }

            return skl;
    }


    //funkcja przetwarzająca string json na listę pól spisowych
    public ArrayList<String> JsonToList(String s){

        ArrayList<String> ls = new ArrayList<String>();

        try {
            JSONObject json = new JSONObject(s);
            JSONArray lista = json.getJSONArray("Location");
            for(int i=0;i<lista.length();i++) {
                JSONObject list = new JSONObject(lista.get(i).toString());
                //Log.d("LISTA:", list.getString("locationName"));
                ls.add(list.getString("locationName"));
            }

        }
        catch (JSONException e) {
            //e.printStackTrace();
            return ls;
        }

        return ls;
    }

}

