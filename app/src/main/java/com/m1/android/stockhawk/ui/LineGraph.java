package com.m1.android.stockhawk.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.m1.android.stockhawk.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class LineGraph extends ActionBarActivity
{
    ArrayList<Entry> Entries=new ArrayList<>();
    ArrayList<String> Labels=new ArrayList<String>();
    String url="https://query.yahooapis.com/v1/public/yql";
    String Search="format";
    String SearchVal="json";
    String QueryKey="q";
    String Diag ="diagnostics";
    String DiagVal="true";
    String Env="env";
    String EnvVal="store://datatables.org/alltableswithkeys";
    String Call="callback";
    String CallVal="";
    LineChart lineChart;
    Uri buildUri;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        Intent intent=getIntent();
        String Symbol=intent.getStringExtra("symbol");
        String query="Select * from yahoo.finance.historicaldata where symbol ='"
                + Symbol + "' and startDate = '2016-01-01' and endDate = '2016-01-25'";
        buildUri=Uri.parse(url).buildUpon()
                .appendQueryParameter(QueryKey,query)
                .appendQueryParameter(Search,SearchVal)
                .appendQueryParameter(Diag,DiagVal)
                .appendQueryParameter(Env,EnvVal)
                .appendQueryParameter(Call,CallVal)
                .build();

        lineChart=(LineChart)findViewById(R.id.linechart);
        LineGraphTask lineGraphTask=new LineGraphTask();
        lineGraphTask.execute(buildUri.toString());
    }

    public class LineGraphTask extends AsyncTask<String,String,String>
    {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader=null;
            String JSONResult = null;
            int val=1;
            try {
                URL url=new URL(buildUri.toString());
                urlConnection =(HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream=urlConnection.getInputStream();
                if(inputStream==null)
                {
                    return null;
                }
                bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer=new StringBuffer();
                String line;
                while((line=bufferedReader.readLine())!=null)
                {
                    stringBuffer.append(line +"\n");
                }

                JSONResult=stringBuffer.toString();

                try
                {
                    JSONObject jsonObject=new JSONObject(JSONResult);
                    JSONObject jsonObject1=jsonObject.getJSONObject("query");
                    JSONObject jsonObject3=jsonObject1.getJSONObject("results");
                    JSONArray jsonArray=jsonObject3.getJSONArray("quote");

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonString = jsonArray.getJSONObject(i);
                        Entries.add(new Entry((int) Float.parseFloat(jsonString.getString("Adj_Close")),i+1));
                    }

                    Labels.add("1");
                    Labels.add("2");
                    Labels.add("3");
                    Labels.add("4");
                    Labels.add("5");
                    Labels.add("6");
                    Labels.add("7");
                    Labels.add("8");
                    Labels.add("9");
                    Labels.add("10");
                    Labels.add("11");
                    Labels.add("12");
                    Labels.add("13");
                    Labels.add("14");
                    Labels.add("15");
                    Labels.add("16");
                    Labels.add("17");
                    Labels.add("18");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            LineDataSet dataset = new LineDataSet(Entries, "Stock Values over time");
            dataset.setDrawCircles(true);
            dataset.setDrawValues(true);
            LineData data = new LineData(Labels,dataset);
            lineChart.setDescription("Stock Values");
            lineChart.setData(data);
            lineChart.animateY(2000);
        }
    }
}












