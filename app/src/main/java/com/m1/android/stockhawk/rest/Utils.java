package com.m1.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.m1.android.stockhawk.R;
import com.m1.android.stockhawk.data.QuoteColumns;
import com.m1.android.stockhawk.data.QuoteProvider;
import com.m1.android.stockhawk.service.StockTaskService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
/**
 * Created by sam_chordas on 11/9/15.
 */
public class Utils {


  private static String OWM_MESSAGE_CODE = "cod";
  private static String LOG_TAG = Utils.class.getSimpleName();

  public static boolean showPercent = true;

  public static ArrayList quoteJsonToContentVals(String JSON,Context c){
    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
    JSONObject jsonObject;
    JSONArray resultsArray;
    try{
      jsonObject = new JSONObject(JSON);
      if (jsonObject.has(OWM_MESSAGE_CODE) ) {
        int errorCode=jsonObject.getInt(OWM_MESSAGE_CODE);
        switch (errorCode) {
          case HttpURLConnection.HTTP_OK:
            break;
          case HttpURLConnection.HTTP_NOT_FOUND:
            StockTaskService.setStockStatus(c, StockTaskService.STOCK_STATUS_SERVER_INVALID);
          default:
            StockTaskService.setStockStatus(c,StockTaskService.STOCK_STATUS_SERVER_DOWN);
        }
      }
      if (jsonObject != null && jsonObject.length() != 0){
        jsonObject = jsonObject.getJSONObject("query");
        int count = Integer.parseInt(jsonObject.getString("count"));
        if (count == 1){
          jsonObject = jsonObject.getJSONObject("results")
              .getJSONObject("quote");
          batchOperations.add(buildBatchOperation(jsonObject));
        } else{
          resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

          if (resultsArray != null && resultsArray.length() != 0){
            for (int i = 0; i < resultsArray.length(); i++){
              jsonObject = resultsArray.getJSONObject(i);
              batchOperations.add(buildBatchOperation(jsonObject));
            }
          }
        }
      }
    } catch (JSONException e){
      Log.e(LOG_TAG, "String to JSON failed: " + e);
    }
    return batchOperations;
  }


  public static String truncateBidPrice(String bidPrice){
    bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
    return bidPrice;
  }

  public static String truncateChange(String change, boolean isPercentChange){
    String weight = change.substring(0,1);
    String ampersand = "";
    if (isPercentChange){
      ampersand = change.substring(change.length() - 1, change.length());
      change = change.substring(0, change.length() - 1);
    }
    change = change.substring(1, change.length());
    double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
    change = String.format("%.2f", round);
    StringBuffer changeBuffer = new StringBuffer(change);
    changeBuffer.insert(0, weight);
    changeBuffer.append(ampersand);
    change = changeBuffer.toString();
    return change;
  }

  public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject){
    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
            QuoteProvider.Quotes.CONTENT_URI);
    String bid=null;
    try {
      String change = jsonObject.getString("Change");
      builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol"));
      builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
      builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
          jsonObject.getString("ChangeinPercent"), true));
      builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
      builder.withValue(QuoteColumns.ISCURRENT, 1);
      if (change.charAt(0) == '-'){
        builder.withValue(QuoteColumns.ISUP, 0);
      }else{
        builder.withValue(QuoteColumns.ISUP, 1);
      }
     bid=jsonObject.getString("Bid");
    } catch (JSONException e){
      e.printStackTrace();
    }
    if(bid==null || bid.equals("null"))
    {
      Log.d(LOG_TAG,"bid is null.");
    }
    else
    {
      Log.d(LOG_TAG,"bid is not null." +bid);
    }
    return builder.build();
  }

  @SuppressWarnings("ResourceType")
  static public @StockTaskService.StockStatus
  int getStockStatus(Context c){
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
    return sp.getInt(c.getString(R.string.pref_stock_status_key),StockTaskService.STOCK_STATUS_UNKNOWN);
  }

  /**
   * Resets the stock status.
   * @param c Context used to get the SharedPreferences
   */
  static public void resetStockStatus(Context c){
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
    SharedPreferences.Editor spe = sp.edit();
    spe.putInt(c.getString(R.string.pref_stock_status_key),StockTaskService.STOCK_STATUS_UNKNOWN);
    spe.apply();
  }
}
