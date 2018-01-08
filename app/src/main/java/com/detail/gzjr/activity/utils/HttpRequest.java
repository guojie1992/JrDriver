package com.detail.gzjr.activity.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jr on 2017/10/14.
 */

public class HttpRequest {
    private static String JsonData;
    public static void RequestData(final String path)throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result="";
                    URL url = new URL(path);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    /*获取URLConnection对象对应的输出流*/
                    PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
                    printWriter.flush();
                    if(httpURLConnection.getResponseCode() == 200){
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));
                        result = br.readLine();
                        JsonData = result;
                    }
                    JSONObject jsonObj = new JSONObject(JsonData);
                    DataParse.version = jsonObj.getString("Version");
                    DataParse.updateUrl = jsonObj.getString("UpdateUrl");
                    DataParse.Isforced = jsonObj.getString("IsForced");
                }catch (Exception e){

                }
            }
        }).start();
    }
}
