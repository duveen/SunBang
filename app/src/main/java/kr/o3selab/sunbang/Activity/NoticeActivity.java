package kr.o3selab.sunbang.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import kr.o3selab.sunbang.Instance.DB;
import kr.o3selab.sunbang.Instance.JsonHandler;
import kr.o3selab.sunbang.Instance.SunbangProgress;
import kr.o3selab.sunbang.Instance.ThreadGroupHandler;
import kr.o3selab.sunbang.Instance.URLP;
import kr.o3selab.sunbang.R;
import me.grantland.widget.AutofitTextView;

public class NoticeActivity extends AppCompatActivity {

    // UI 변수
    public ImageView undoIcon;
    public AutofitTextView noticeTitleView;
    public TextView nickNameView;
    public TextView regdateView;
    public TextView contentView;

    public ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        // DB 정보 업데이트
        DB.context = this;
        DB.activity = this;


        // UI 컨텐츠 로딩...
        undoIcon = (ImageView) findViewById(R.id.notice_activity_ic_undo);
        noticeTitleView = (AutofitTextView) findViewById(R.id.notice_activity_title);
        nickNameView = (TextView) findViewById(R.id.notice_activity_nick_name);
        regdateView = (TextView) findViewById(R.id.notice_activity_regdate);
        contentView = (TextView) findViewById(R.id.notice_activity_content);


        // 인텐트 값 수신
        Intent intent = getIntent();
        String document_srl = intent.getStringExtra("document_id");


        // 로딩 창 생성
        pd = new SunbangProgress(this);


        // 데이터 수신
        Thread getNoticeData = new Thread(new GetNoticeData(document_srl));
        getNoticeData.start();

        Thread[] noticeGroup = {getNoticeData};

        ThreadGroupHandler threadGroupHandler = new ThreadGroupHandler(noticeGroup, pd);
        threadGroupHandler.start();

        // 버튼 핸들러 설정
        undoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoticeActivity.this.finish();
            }
        });
    }

    public class GetNoticeData implements Runnable {

        public String document_srl;

        public GetNoticeData(String document_srl) {
            this.document_srl = document_srl;
        }

        @Override
        public void run() {
            try {
                String param = URLP.PARAM_DOCUMENT_SRL + document_srl;
                String result = new JsonHandler(URLP.NOTICE_DOCUMENT, param).execute().get();

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                JSONObject obj = jsonArray.getJSONObject(0);

                final String title = obj.getString("title");
                final String content = obj.getString("content");
                final String nick_name = obj.getString("nick_name");
                final String regdate = obj.getString("regdate");

                final String regdateConvert = regdate.substring(0, 4)
                        + "-" + regdate.substring(4, 6)
                        + "-" + regdate.substring(6, 8)
                        + " " + regdate.substring(8, 10)
                        + ":" + regdate.substring(10, 12)
                        + ":" + regdate.substring(12, 14);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        noticeTitleView.setText(title);
                        nickNameView.setText(nick_name);
                        regdateView.setText(regdateConvert);
                        contentView.setText(Html.fromHtml(content));
                        contentView.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                });

            } catch (Exception e) {
                DB.sendToast("에러: " + e.getMessage(), 2);
            }
        }
    }


    @Override
    protected void onResume() {

        // DB 정보 업데이트
        DB.context = this;
        DB.activity = this;

        super.onResume();
    }
}
