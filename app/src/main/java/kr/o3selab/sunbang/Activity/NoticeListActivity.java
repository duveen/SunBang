package kr.o3selab.sunbang.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        pd = new ProgressDialog(this);
        pd.setMessage("목록을 불러오고 있습니다.");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);


        // 이벤트 핸들링

        // 데이터 수신
        new GetNoticeListCount().execute();

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
    public class GetNoticeListCount extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                URL url = new URL("http://sunbang.o3selab.kr/script/getNoticeListCount.php?id=139");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                if (con.getResponseCode() == 200) {
                    InputStream is = con.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("euc-kr")));

                    String line = br.readLine();
                    String count = line.substring(0, line.length() - 2);

                    document_count = Integer.parseInt(count);

                    br.close();
                }

            } catch (Exception e) {
                DB.sendToast(e.getMessage(), 2);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            new GetNoticeListData(0).execute();
        }
    }


    // =======================================
    //  지정 된 만큼의 게시물 불러오기
    // =======================================
    public class GetNoticeListData extends AsyncTask<Void, Void, Void> {

        public int min = 0;

        public GetNoticeListData(int min) {
            this.min = min * 10;
        }

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("http://sunbang.o3selab.kr/script/getNoticeListData.php?id=139&min=" + min + "&max=10");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("euc-kr")));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();

                final String JSONline = sb.toString();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        tableLayout.removeAllViews();

                        NoticeListInitView nli = new NoticeListInitView(NoticeListActivity.this);
                        tableLayout.addView(nli);

                        FrameLayout fla = new NoticeListRowLine(NoticeListActivity.this);
                        tableLayout.addView(fla);

                        try {
                            JSONObject jsonObject = new JSONObject(JSONline);
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
                            DB.sendToast("에러", 2);
                        }
                    }
                });

            } catch (Exception e) {
                DB.sendToast("리스트 불러오기 실패!", 2);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            new GetNoticecPageData(currentPage).execute();
        }
    }


    // =======================================
    //  페이징 처리 불러오기
    // =======================================
    public class GetNoticecPageData extends AsyncTask<Void, Void, Void> {
        public int currentPage;

        public GetNoticecPageData(int currentPage) {
            this.currentPage = currentPage;
        }

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pageLayout.removeAllViews();
                }
            });
            int page = 0;

            if(document_count % 10 == 0) {
                page = document_count / 10;
            } else {
                page = (document_count / 10) + 1;
            }

            for(int i = 0; i < page; i++) {
                final int finalPage = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        FrameLayout layout = new NoticecPageFrame(NoticeListActivity.this, finalPage+1);
                        if((finalPage+1) == currentPage) {
                            layout.setEnabled(false);
                        } else {
                            layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    NoticeListActivity.this.currentPage = finalPage + 1;
                                    new GetNoticeListData(finalPage).execute();
                                }
                            });
                        }
                        pageLayout.addView(layout);
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
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
