package com.dokechin.tasktimer;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.dokechin.tasktimer.com.dokechin.tasktimer.domain.Counter;
import com.dokechin.tasktimer.com.dokechin.tasktimer.domain.Event;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    private AlertDialog mDialog;
    private Context mContext;
    private int mNewEventId;
    private int mLastEventId = -1;
    private long mStartTimeMills = 0;
    private String mLastEventTitle = "";

    private Timer mainTimer;					//タイマー用
    private MainTimerTask mainTimerTask;		//タイマタスククラス
    private TextView countText;					//テキストビュー
    private long mCount = 0;		            //カウント
    private Handler mHandler = new Handler();   //UI Threadへのpost用ハンドラ
    private AutoCompleteTextView mTextView;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mContext = this.getContext();
        try {
            mLastEventId = Integer.parseInt(loadFile("event_id.txt"));
            mStartTimeMills = Long.parseLong(loadFile("start.txt"));
            mLastEventTitle = loadFile("title.txt");
        }
        catch(Exception ex){
            Log.d(TAG, ex.toString());
        }

        //テキストビュー
        this.countText = (TextView)rootView.findViewById(R.id.timer_text);

        String[] TITLES = EventUtility.getTitles(mContext.getContentResolver());
        Log.d(TAG, TITLES.toString());

        // In the onCreate method
        mTextView = (AutoCompleteTextView) rootView.findViewById(R.id.task_textview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, TITLES);
        mTextView.setAdapter(adapter);
        mTextView.setText(mLastEventTitle);

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
                intent.putExtra(CalendarContract.Events.TITLE, mTextView.getText().toString());

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
                Event event = EventUtility.getEventData(mContext.getContentResolver(),mLastEventId);
                mainTimer.cancel();
                if (mLastEventId > 0 && event != null) {

                    Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, mLastEventId);
                    long currentTimeMillis = System.currentTimeMillis();

                    ContentResolver cr = mContext.getContentResolver();
                    ContentValues values = new ContentValues();

                    values.put(CalendarContract.Events.DTSTART, mStartTimeMills);
                    values.put(CalendarContract.Events.DTEND, currentTimeMillis);
                    int rows = cr.update(uri, values, null, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.finish_title);
                    builder.setMessage(R.string.finish_text);
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.ok_label,null);
                    mDialog = builder.show();

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

        Log.d(TAG, String.format("onActivityResult requestCode=%s resultCode=%s", requestCode, resultCode));

        if (requestCode == 0) {

            if (resultCode == 0) {

                int lastEventId = EventUtility.getLastEventId(mContext.getContentResolver());

                if (mNewEventId == lastEventId){
                    mLastEventId = lastEventId;
                    Event event = EventUtility.getEventData(mContext.getContentResolver(),lastEventId);

                    saveFile("event_id.txt", Integer.toString(lastEventId));
                    saveFile("start.txt", Long.toString(event.getStartTimeMills()));
                    saveFile("title.txt", event.getTitle());

                    // reset count
                    mCount = 0;

                    //タイマーインスタンス生成
                    this.mainTimer = new Timer();
                    //タスククラスインスタンス生成
                    this.mainTimerTask = new MainTimerTask();
                    //タイマースケジュール設定＆開始
                    this.mainTimer.schedule(mainTimerTask, 1000, 1000);

                }

            }
        }
    }

    // ファイルを保存
    public void saveFile(String file, String data) {

        Log.d(TAG , String.format("save data file=%s,data=%s" , file , data));

        FileOutputStream fileOutputstream = null;

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

    public String loadFile (String file) throws Exception{
        try {

            Log.d(TAG , String.format("load data file %s" , file));

            FileInputStream fileInputstream = this.getContext().openFileInput(file);
            String ret = convertStreamToString(fileInputstream);
            //Make sure you close all streams.
            fileInputstream.close();

            Log.d(TAG , String.format("load data data %s" , ret));

            return ret;
        }
        catch(Exception ex){
            throw ex;
        }
    }

    public class MainTimerTask extends TimerTask {
        @Override
        public void run() {
            //ここに定周期で実行したい処理を記述します
            mHandler.post( new Runnable() {
                public void run() {

                    //実行間隔分を加算処理
                    mCount+= 1;
                    Counter counter = new Counter(mCount);
                    //画面にカウントを表示
                    countText.setText(counter.clockFormat());
                }
            });
        }
    }

}
