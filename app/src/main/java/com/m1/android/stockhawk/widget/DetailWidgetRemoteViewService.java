package com.m1.android.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.m1.android.stockhawk.R;
import com.m1.android.stockhawk.data.QuoteColumns;
import com.m1.android.stockhawk.data.QuoteDatabase;
import com.m1.android.stockhawk.data.QuoteProvider;

public class DetailWidgetRemoteViewService extends RemoteViewsService
{
    private static final String[] QUOTE_COLUMNS = {
            QuoteDatabase.QUOTES + "." + QuoteColumns._ID,
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.CHANGE,
            QuoteColumns.ISUP
    };

    static final int INDEX_STOCK_ID = 0;
    static final int INDEX_SYMBOL = 1;
    static final int INDEX_BIDPRICE = 2;
    static final int INDEX_PERCENT_CHANGE = 3;
    static final int INDEX_CHANGE = 4;
    static final int INDEX_ISUP = 5;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor cursor=null;
            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if(cursor!=null)
                {
                    cursor.close();
                }
                final long identityToken= Binder.clearCallingIdentity();
                cursor=getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,QUOTE_COLUMNS,QuoteColumns.ISCURRENT
                + "=?",new String[] {"1"},null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if(cursor!=null)
                {
                    cursor.close();
                    cursor=null;
                }
            }

            @Override
            public int getCount() {
                return cursor==null ? 0 : cursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if(position== AdapterView.INVALID_POSITION || cursor==null || !cursor.moveToPosition(position))
                {
                    return null;
                }
                RemoteViews remoteViews=new RemoteViews(getPackageName(), R.layout.widget_layout);
                int weatherId=cursor.getInt(INDEX_STOCK_ID);
                String symbol=cursor.getString(INDEX_SYMBOL);
                long bidPrice=cursor.getLong(INDEX_BIDPRICE);
                double change=cursor.getDouble(INDEX_CHANGE);

                remoteViews.setTextViewText(R.id.widget_stock, symbol);
                remoteViews.setTextViewText(R.id.widget_bid_price, bidPrice + "");
                remoteViews.setTextViewText(R.id.widget_change, String.valueOf(change));

                final Intent finalIntent=new Intent();
                finalIntent.setData(QuoteProvider.Quotes.withSymbol(symbol));
                remoteViews.setOnClickFillInIntent(R.id.widget_list,finalIntent);
                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(),R.layout.widget_layout);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if(cursor.moveToPosition(position))
                    return cursor.getInt(INDEX_STOCK_ID);
                    return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}

