package com.example.dokechin.myapplication;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.CalendarContract;

/**
 * Created by dokechin on 2016/07/16.
 */
public class EventUtility {

    public static int getNewEventId(ContentResolver cr) {
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI,
                new String [] {"MAX(_id) as max_id"}, null, null, "_id");
        cursor.moveToFirst();
        int max_val = cursor.getInt(cursor.getColumnIndex("max_id"));
        return max_val+1;
    }

    public static int getLastEventId(ContentResolver cr) {
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI,
                new String [] {"MAX(_id) as max_id"}, null, null, "_id");
        cursor.moveToFirst();
        int max_val = cursor.getInt(cursor.getColumnIndex("max_id"));
        return max_val;
    }
}
