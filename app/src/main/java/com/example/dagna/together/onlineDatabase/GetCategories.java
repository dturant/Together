package com.example.dagna.together.onlineDatabase;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Dagna on 18/04/2016.
 */
public class GetCategories extends AsyncTask<Void, Void, String>
{
    public static String JSON_STRING;
    public static String json_string;

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;

    public GetCategories(AsyncResponse delegate){
        this.delegate = delegate;
    }

    String json_url;

    @Override
    protected void onPreExecute() {
        json_url="https://omega.aizio.net:5678/get_categories.php";
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL url = new URL(json_url);
            HttpURLConnection httpURLConnection= (HttpURLConnection)url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder=new StringBuilder();
            while((JSON_STRING=bufferedReader.readLine())!=null){
                stringBuilder.append(JSON_STRING+ "\n");
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            return stringBuilder.toString().trim();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }



    @Override
    protected void onPostExecute(String result) {
        json_string=result;
        delegate.processFinish(result);
        Log.d("data", result);

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
