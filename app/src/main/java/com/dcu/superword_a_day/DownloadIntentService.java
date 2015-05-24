package com.dcu.superword_a_day;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DownloadIntentService extends IntentService {
    private static final String ACTION_DOWNLOAD_FROM_SERVER = "com.dcu.superword_a_day.action.DOWNLOAD_FROM_SERVER";

    public static void startActionDownload(Context context) {
        Intent intent = new Intent(context, DownloadIntentService.class);
        intent.setAction(ACTION_DOWNLOAD_FROM_SERVER);
        context.startService(intent);
    }

    public DownloadIntentService() {
        super("DownloadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String [] wordsArr = intent.getStringArrayExtra("wordsArr");
            handleActionDownload(wordsArr);
        }
    }

    private void handleActionDownload(final String [] wordsArr) {
        // Instantiate an HttpClient
        HttpClient httpclient = new DefaultHttpClient();
        String url = "http://superwordaday.com/RetrieveData.php";
        HttpPost httppost = new HttpPost(url);

        // Instantiate a GET HTTP method
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            for(int i = 0; i < wordsArr.length; i++) {
                nameValuePairs.add(new BasicNameValuePair(wordsArr[i], "" + i));
            }
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse responseBody = httpclient.execute(httppost);

            HttpEntity entity = responseBody.getEntity();
            InputStream inputStream = null;
            inputStream = entity.getContent();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            String result = sb.toString();


            // Parse
            Log.i("responseBody", result);
            JSONArray arr = new JSONArray(result);
            Log.i("json to string", arr.toString());

            /*
             * Need to extract the percentages from the JSONArray here
             */

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // for the moment, just using placeholder percentages
        int [] placeholderPercentages = {34, 66, 52, 90, 81, 15, 72, 88, 39, 62, 63, 77, 11, 19, 25};
        Bundle wordsWithPercentages = new Bundle();
        for(int i = 0; i < wordsArr.length; i++) {
            wordsWithPercentages.putInt(wordsArr[i], placeholderPercentages[i]);
        }

        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("difficulty-percentages-ready");
        intent.putExtra("wordsWithPercentages", wordsWithPercentages);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
