package com.example.dokechin.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainFragment extends Fragment {

    private AlertDialog mDialog;
    private Context mContext;
    private int mNewEventId;
    private int mLastEventId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mContext = this.getContext();
        mLastEventId = loadFile("event_id.txt");
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
                intent.putExtra(CalendarContract.Events.TITLE, "勤務");
                //スケジュールの開始時刻 ゼロで現在時刻
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, 0);
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
                if (mLastEventId >= 0) {
                    // カレンダーアプリを呼び出すIntentの生成
                    Intent intent = new Intent(Intent.ACTION_EDIT, CalendarContract.Events.CONTENT_URI);
                    //イベントID
                    intent.putExtra(CalendarContract.Events._ID, mLastEventId);
                    //スケジュールの開始時刻 ゼロで現在時刻
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, 0);
                    //Intentを呼び出す
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

        if (requestCode == 0) {

            if (resultCode == 0) {

                int lastEventId = EventUtility.getLastEventId(mContext.getContentResolver());
                if (mNewEventId == lastEventId){
                    mLastEventId = lastEventId;
                    saveFile("event_id.txt",lastEventId);
                }

            }
        }
    }

    // ファイルを保存
    public void saveFile(String file, int eventId) {
        FileOutputStream fileOutputstream = null;

        try {
            fileOutputstream = this.getContext().openFileOutput(file, Context.MODE_PRIVATE);
            fileOutputstream.write(eventId);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // ファイルを保存
    public int loadFile(String file) {
        FileInputStream fileInputstream = null;

        try {
            fileInputstream = this.getContext().openFileInput(file);
            return fileInputstream.read();
        } catch (IOException e) {
            return -1;
        }

    }

}
