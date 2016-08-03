package kr.o3selab.sunbang.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import kr.o3selab.sunbang.Instance.DB;
import kr.o3selab.sunbang.Layout.SearchResultLinearLayout;
import kr.o3selab.sunbang.R;

public class SearchActivity extends AppCompatActivity {

    public LinearLayout resultLayout;
    public EditText textField;
    public GetSearchResult task;
    public Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        DB.context = this;
        DB.activity = this;

        task = new GetSearchResult();
        resultLayout = (LinearLayout) findViewById(R.id.activity_search_result);
        textField = (EditText) findViewById(R.id.activity_search_textfield);
        btn = (Button) findViewById(R.id.activity_search_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetSearchResult().execute(textField.getText().toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        DB.context = this;
        DB.activity = this;
    }

    public class GetSearchResult extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL("http://sunbang.o3selab.kr/script/getSearchResultData.php?id=171&query=" + params[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName("euc-kr")));
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultLayout.removeAllViews();
                    }
                });

                JSONObject jsonObject = new JSONObject(sb.toString());
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
                DB.sendToast("에러 발생!", 2);
            }
            return null;
        }

    }
}
