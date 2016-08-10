package kr.o3selab.sunbang.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import kr.o3selab.sunbang.Layout.NoticeListInitView;
import kr.o3selab.sunbang.Layout.NoticeListRowLayout;
import kr.o3selab.sunbang.Layout.NoticeListRowLine;
import kr.o3selab.sunbang.Layout.NoticecPageFrame;
import kr.o3selab.sunbang.R;

public class NoticeListActivity extends AppCompatActivity {

    // UI 변수
    public ProgressDialog pd; // 로딩 창
    public LinearLayout pageLayout;
    public LinearLayout tableLayout;
    public ImageView noticeUndo;

    // DB 변수
    public int document_count;
    public int currentPage = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_list);

        // DB 정보 업데이트
        DB.context = this;
        DB.activity = this;


        // UI 컨텐츠 로딩..
        noticeUndo = (ImageView) findViewById(R.id.notice_activity_ic_undo);
        pageLayout = (LinearLayout) findViewById(R.id.notice_activity_page_layout);
        tableLayout = (LinearLayout) findViewById(R.id.notice_list_table_layout);


        // 로딩창 생성
        pd = new SunbangProgress(this);


        // 데이터 수신
        Thread getNoticeListCount = new Thread(new GetNoticeListCount());
        getNoticeListCount.start();

        Thread getNoticeListData = new Thread(new GetNoticeListData(0));
        getNoticeListData.start();

        Thread[] noticeListGroup = {getNoticeListCount, getNoticeListData};

        ThreadGroupHandler threadGroupHandler = new ThreadGroupHandler(noticeListGroup, pd);
        threadGroupHandler.start();


        // 버튼 핸들러
        noticeUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoticeListActivity.this.finish();
            }
        });

    }

    // =======================================
    //  게시물 수 불러오기
    // =======================================
    public class GetNoticeListCount implements Runnable {
        @Override
        public void run() {
            try {
                String param = URLP.PARAM_MODULE_SRL + DB.NOTICE_MODULE;
                String result = new JsonHandler(URLP.NOTICE_LIST_DOCUMENT_COUNT, param).execute().get();
                document_count = Integer.parseInt(result.substring(0, result.length() - 2));
            } catch (Exception e) {
                DB.sendToast("에러: " + e.getMessage(), 2);
            }
        }
    }


    // =======================================
    //  지정 된 만큼의 게시물 불러오기
    // =======================================
    public class GetNoticeListData implements Runnable {

        public int min = 0;

        public GetNoticeListData(int min) {
            this.min = min * 10;
        }

        @Override
        public void run() {
            try {
                String param = URLP.PARAM_MODULE_SRL + DB.NOTICE_MODULE + "&min=" + min + "&max=10";
                final String result = new JsonHandler(URLP.NOTICE_LIST_DOCUMENT_LIMIT, param).execute().get();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        tableLayout.removeAllViews();

                        NoticeListInitView nli = new NoticeListInitView(NoticeListActivity.this);
                        tableLayout.addView(nli);

                        FrameLayout fla = new NoticeListRowLine(NoticeListActivity.this);
                        tableLayout.addView(fla);

                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray jsonArray = jsonObject.getJSONArray("result");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                final String document_id = obj.getString("document_srl");
                                final String title = obj.getString("title");
                                final String nick_name = obj.getString("nick_name");
                                final String regdate = obj.getString("regdate");

                                final String convertRegdate = regdate.substring(4, 6) + "-" + regdate.substring(6, 8);

                                LinearLayout tr = new NoticeListRowLayout(NoticeListActivity.this, document_id, title, nick_name, convertRegdate);
                                tr.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(NoticeListActivity.this, NoticeActivity.class);
                                        intent.putExtra("document_id", document_id);
                                        startActivity(intent);
                                    }
                                });
                                tableLayout.addView(tr);

                                FrameLayout fl = new NoticeListRowLine(NoticeListActivity.this);
                                tableLayout.addView(fl);
                            }
                        } catch (Exception e) {
                            DB.sendToast("에러: " + e.getMessage(), 2);
                        }
                    }
                });

                Thread getNoticePageData = new Thread(new GetNoticecPageData(currentPage));
                getNoticePageData.start();

            } catch (Exception e) {
                DB.sendToast("에러: " + e.getMessage(), 2);
            }
        }

    }


    // =======================================
    //  페이징 처리 불러오기
    // =======================================
    public class GetNoticecPageData implements Runnable {

        public int currentPage;

        public GetNoticecPageData(int currentPage) {
            this.currentPage = currentPage;
        }

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pageLayout.removeAllViews();
                }
            });
            int page = 0;

            if (document_count % 10 == 0) {
                page = document_count / 10;
            } else {
                page = (document_count / 10) + 1;
            }

            for (int i = 0; i < page; i++) {
                final int finalPage = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FrameLayout layout = new NoticecPageFrame(NoticeListActivity.this, finalPage + 1);
                        if ((finalPage + 1) == currentPage) {
                            layout.setEnabled(false);
                        } else {
                            layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    NoticeListActivity.this.currentPage = finalPage + 1;

                                    Thread getNoticeListData = new Thread(new GetNoticeListData(finalPage));
                                    getNoticeListData.start();
                                }
                            });
                        }
                        pageLayout.addView(layout);
                    }
                });
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        // DB 정보 업데이트
        DB.context = this;
        DB.activity = this;
    }
}
