package com.dokechin.tasktimer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.ArraySet;

import com.dokechin.tasktimer.com.dokechin.tasktimer.domain.Event;

import java.util.ArrayList;
import java.util.HashSet;

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


    public static Event getEventData(ContentResolver cr, int id){
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI,
                new String [] {CalendarContract.Events.DTSTART, CalendarContract.Events.TITLE},  "_id=" + id, null, null);
        if (cursor.getCount()>0){
            cursor.moveToFirst();
            Event event = new Event();
            event.setStartTimeMills(cursor.getLong(0));
            event.setTitle(cursor.getString(1));
            return event;
        }
        return null;
    }


    public static String[] getTitles(ContentResolver cr){

        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI,
                new String [] {CalendarContract.Events.TITLE},  null, null, null);
        HashSet<String> set = new HashSet<String>();
        while(cursor.moveToNext()){
            String title = cursor.getString(0);
            set.add(title);
        }
        String[] stringArray = set.toArray(new String[0]);
        return stringArray;

    }

}
