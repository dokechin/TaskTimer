package com.dokechin.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainFragment extends Fragment {

    private AlertDialog mDialog;
    private Context mContext;
    private int mNewEventId;
    private int mLastEventId = -1;
    private long mStartTimeMills = 0;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mContext = this.getContext();
        try {
            mLastEventId = Integer.parseInt(loadFile("event_id.txt"));
            mStartTimeMills = Long.parseLong(loadFile("start.txt"));
        }
        catch(Exception ex){
            Log.d("rodo", ex.toString());
        }
        //
        View aboutButton = rootView.findViewById(R.id.about_button);
        aboutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.about_title);
                builder.setMessage(R.string.about_text);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.ok_label,null);
                mDialog = builder.show();
            }
        });

        View enterBotton = rootView.findViewById(R.id.enter_button);
        enterBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // カレンダーアプリを呼び出すIntentの生成
                Intent intent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);

                mNewEventId = EventUtility.getNewEventId(mContext.getContentResolver());

                //イベントID
                intent.putExtra(CalendarContract.Events._ID, mNewEventId);
                //スケジュールのタイトル
                intent.putExtra(CalendarContract.Events.TITLE, getString(R.string.labour));

                mStartTimeMills = System.currentTimeMillis();

                //スケジュールの開始時刻 ゼロで現在時刻
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, mStartTimeMills);

                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, mStartTimeMills);

                //スケジュールのアクセスレベル
                intent.putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT);
                //スケジュールの同時持ちの可否
                intent.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);

                //Intentを呼び出す
                startActivityForResult(intent,0);
            }
        });

        View leaveBotton = rootView.findViewById(R.id.leave_button);
        leaveBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastEventId > 0 && EventUtility.hasEventId(mContext.getContentResolver(),mLastEventId)) {

                    Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, mLastEventId);
                    long currentTimeMillis = System.currentTimeMillis();
                    Intent intent = new Intent(Intent.ACTION_EDIT)
                            .setData(uri)
                            .setType("vnd.android.cursor.item/event")
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, mStartTimeMills)
                            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, currentTimeMillis);
                    startActivity(intent);
                }
            }
        });


        return rootView;
    }


    @Override
    public void onPause(){
        super.onPause();
        if(mDialog != null){
            mDialog.dismiss();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("rodo","onActivityResult requestCode" + requestCode + "resultCode" + resultCode);

        if (requestCode == 0) {

            if (resultCode == 0) {

                int lastEventId = EventUtility.getLastEventId(mContext.getContentResolver());

                Log.d("rodo","lastEventId" + lastEventId + "newEventId" + mNewEventId);
                if (mNewEventId == lastEventId){
                    mLastEventId = lastEventId;
                    saveFile("event_id.txt", Integer.toString(lastEventId));
                    saveFile("start.txt", Long.toString(mStartTimeMills));
                }

            }
        }
    }

    // ファイルを保存
    public void saveFile(String file, String data) {
        FileOutputStream fileOutputstream = null;
        Log.d("rodo" , "save data file= " + file + "data " + data);

        try {
            fileOutputstream = this.getContext().openFileOutput(file, Context.MODE_PRIVATE);
            fileOutputstream.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String convertStreamToString(InputStream is) throws Exception{
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    public String loadFile (String file) {
        try {
            Log.d("rodo" , "load data file= " + file );

            FileInputStream fileInputstream = this.getContext().openFileInput(file);
            String ret = convertStreamToString(fileInputstream);
            //Make sure you close all streams.
            fileInputstream.close();
            Log.d("rodo" , "load data data " + ret);

            return ret;
        }
        catch(Exception ex){
            return "0";
        }
    }



}
