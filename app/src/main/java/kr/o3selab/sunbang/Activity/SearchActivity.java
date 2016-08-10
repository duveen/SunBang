package kr.o3selab.sunbang.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import kr.o3selab.sunbang.Instance.DB;
import kr.o3selab.sunbang.Instance.JsonHandler;
import kr.o3selab.sunbang.Instance.URLP;
import kr.o3selab.sunbang.Layout.SearchResultLinearLayout;
import kr.o3selab.sunbang.R;

public class SearchActivity extends AppCompatActivity {

    public LinearLayout resultLayout;
    public EditText textField;
    public ImageView undoIc;
    public Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        DB.context = this;
        DB.activity = this;

        resultLayout = (LinearLayout) findViewById(R.id.activity_search_result);
        textField = (EditText) findViewById(R.id.activity_search_textfield);
        btn = (Button) findViewById(R.id.activity_search_button);
        undoIc = (ImageView) findViewById(R.id.activity_search_ic_undo);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getResultData();
            }
        });

        textField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                getResultData();
                return false;
            }
        });

        undoIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.this.finish();
            }
        });
    }

    public void getResultData() {
        boolean check = Pattern.matches("^[ㄱ-ㅎ가-힣0-9a-zA-Z ]*$", textField.getText().toString());
        if (check) {
            Thread th = new Thread(new GetSearchResult(textField.getText().toString()));
            th.start();
        } else {
            DB.sendToast("경고! 한글, 영어, 숫자만 입력 가능합니다!", 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DB.context = this;
        DB.activity = this;
    }

    public class GetSearchResult implements Runnable {

        public String params;

        public GetSearchResult(String params) {
            this.params = params;
        }

        @Override
        public void run() {
            try {
                String query = URLEncoder.encode(params, "utf-8");
                String param = URLP.PARAM_MODULE_SRL + DB.ROOM_MODULE + "&query=" + query;

                String result = new JsonHandler(URLP.SEARCH_RESULT_DATA, param).execute().get();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultLayout.removeAllViews();
                    }
                });

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);

                    final String srl = obj.getString("srl");
                    final String title = obj.getString("title");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayout layout = new SearchResultLinearLayout(SearchActivity.this, title);
                            layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(SearchActivity.this, RoomActivity.class);
                                    intent.putExtra("srl", srl);
                                    startActivity(intent);
                                }
                            });
                            resultLayout.addView(layout);
                        }
                    });
                }
            } catch (Exception e) {
                DB.sendToast("ErrorCode 14: " + e.getMessage(), 2);
            }
        }
    }
}
