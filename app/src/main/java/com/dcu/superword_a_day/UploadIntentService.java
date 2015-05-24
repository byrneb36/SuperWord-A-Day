package com.dcu.superword_a_day;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class UploadIntentService extends IntentService {
    private static final String ACTION_UPLOAD_TO_SERVER = "com.dcu.superword_a_day.action.UPLOAD_TO_SERVER";

    public UploadIntentService() {
        super("UploadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final ArrayList<String> resultsData = intent.getStringArrayListExtra("resultsData");
            handleUpload(resultsData);
        }
    }

    private void handleUpload(ArrayList<String> resultsData) {
        String MY_DOMAIN_URL = "http://superwordaday.com/brendan.php";
        Log.i("UploadIntentService resultsData: ", resultsData.toString());

        String postReceiverUrl = MY_DOMAIN_URL;
        Log.v("TAG", "postURL: " + postReceiverUrl);

        // HttpClient
        HttpClient httpClient = new DefaultHttpClient();

        // post header
        HttpPost httpPost = new HttpPost(postReceiverUrl);

        // add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        for(int i = 0; i < resultsData.size(); i++) {
            nameValuePairs.add(new BasicNameValuePair(resultsData.remove(0), resultsData.remove(0)));
        }
        nameValuePairs.add(new BasicNameValuePair("nimrod", "passed"));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // execute HTTP post request
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();

            if (resEntity != null) {
                String responseStr = EntityUtils.toString(resEntity).trim();
                Log.v("TAG", "Response: " +  responseStr);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Upload Finished", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
