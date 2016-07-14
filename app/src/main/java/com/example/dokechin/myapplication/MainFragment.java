package com.example.dokechin.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment {

    private AlertDialog mDialog;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
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
                intent.setType("vnd.android.cursor.item/event");
                //スケジュールのタイトル
                intent.putExtra(CalendarContract.Events.TITLE, "出勤");
                //スケジュールの開始時刻 ゼロで現在時刻
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, 0);
                //スケジュールの場所
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "大阪");
                //スケジュールの詳細内容
                intent.putExtra(CalendarContract.Events.DESCRIPTION, "サンプル");
                //スケジュールのアクセスレベル
                intent.putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT);
                //スケジュールの同時持ちの可否
                intent.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);
                //Intentを呼び出す
                startActivity(intent);
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




}
