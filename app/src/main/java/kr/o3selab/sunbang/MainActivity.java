package kr.o3selab.sunbang;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Vector;

import kr.o3selab.sunbang.Activity.NoticeActivity;
import kr.o3selab.sunbang.Activity.NoticeListActivity;
import kr.o3selab.sunbang.Activity.RoomActivity;
import kr.o3selab.sunbang.Activity.SearchActivity;
import kr.o3selab.sunbang.Instance.DB;
import kr.o3selab.sunbang.Layout.MainNoticeLinearLayout;
import kr.o3selab.sunbang.Layout.MainRoomLinearLayout;

public class MainActivity extends AppCompatActivity {
    public ProgressDialog pd;
    public SliderLayout mainSliderLayout;
    public FrameLayout locationFrame;
    public FrameLayout moneyFrame;
    public LinearLayout mainNoticeLayout;
    public LinearLayout mainRoomLayout;
    public TextView mainNoticeTextView;
    public ImageView mainSearchImageView;

    public boolean imageDataFlag = false;
    public boolean noticeDataFlag = false;
    public boolean roomDataFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DB 정보 업데이트
        DB.context = this;
        DB.activity = this;


        // UI 이미지 커넥팅..
        mainSliderLayout = (SliderLayout) findViewById(R.id.main_slider);
        locationFrame = (FrameLayout) findViewById(R.id.main_location_frame);
        moneyFrame = (FrameLayout) findViewById(R.id.main_money_frame);
        mainNoticeLayout = (LinearLayout) findViewById(R.id.main_notice_layout);
        mainNoticeTextView = (TextView) findViewById(R.id.main_notice_view);
        mainRoomLayout = (LinearLayout) findViewById(R.id.main_room_layout);
        mainSearchImageView = (ImageView) findViewById(R.id.activity_main_ic_search);

        // 데이터 처리
        pd = new ProgressDialog(this);
        pd.setTitle("로딩중..");
        pd.setMessage("선방 정보를 가져오고 있습니다!.\n잠시만 기다려주세요!");
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();

        new GetMainImagesSliderData().execute();
        new GetMainNoticeListData().execute();
        new GetMainRoomListData().execute();

        Thread th = new Thread() {
            @Override
            public void run() {
                while(true) {
                    if(imageDataFlag && noticeDataFlag && roomDataFlag) {
                        pd.dismiss();
                        break;
                    }
                }
            }
        };
        th.start();


        // 이미지 핸들러
        locationFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "위치 별 보기 선택", Toast.LENGTH_SHORT).show();
            }
        });

        moneyFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "금액 별 보기 선택", Toast.LENGTH_SHORT).show();
            }
        });

        mainNoticeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NoticeListActivity.class);
                startActivity(intent);
            }
        });

        mainSearchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

    }

    // 메인 이미지 슬라이더 로딩 핸들러
    public class GetMainImagesSliderData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Vector<String> mainImages = DB.mainImages;

                        for (int i = 0; i < mainImages.size(); i++) {

                            DefaultSliderView textSliderView = new DefaultSliderView(MainActivity.this);
                            textSliderView
                                    .image(mainImages.get(i))
                                    .setScaleType(BaseSliderView.ScaleType.Fit)
                                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                        @Override
                                        public void onSliderClick(BaseSliderView slider) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(MainActivity.this, "이미지 사진 클릭", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                            mainSliderLayout.addSlider(textSliderView);
                        }

                        mainSliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
                        mainSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                        mainSliderLayout.setCustomAnimation(new DescriptionAnimation());
                        mainSliderLayout.setDuration(3000);
                        mainSliderLayout.startAutoCycle();
                    }
                });

            } catch (Exception e) {
                DB.sendToast("서버 접속 실패", 2);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            imageDataFlag = true;
        }
    }

    // 메인 공지사항 리스트 로딩 핸들러
    public class GetMainNoticeListData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("http://sunbang.o3selab.kr/script/getMainNoticeListData.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("euc-kr")));

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                JSONObject jsonObject = new JSONObject(sb.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);

                    final String id = obj.getString("document_id");
                    final String title = obj.getString("title");
                    final String regdate = obj.getString("regdate");
                    final String convertRegdate = regdate.substring(0,4) + "-" + regdate.substring(4,6) + "-" + regdate.substring(6,8);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainNoticeLinearLayout linear = new MainNoticeLinearLayout(MainActivity.this, id, title, convertRegdate);
                            linear.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // 공지사항 아이템 선택시 실행
                                    // Toast.makeText(MainActivity.this, id + "번 게시글 클릭", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(MainActivity.this, NoticeActivity.class);
                                    intent.putExtra("document_id", id);
                                    startActivity(intent);

                                }
                            });
                            mainNoticeLayout.addView(linear);
                        }
                    });
                }

                br.close();

            } catch (Exception e) {
                DB.sendToast("에러 발생" + e.getMessage(), 2);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            noticeDataFlag = true;
        }
    }

    // 메인 최근 등록 원룸 리스트 로딩 핸들러
    public class GetMainRoomListData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("http://sunbang.o3selab.kr/script/getMainRoomListData.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("euc-kr")));

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                JSONObject jsonObject = new JSONObject(sb.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);

                    final String title = obj.getString("title");
                    final String document_id = obj.getString("document_id");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainRoomLinearLayout linear = new MainRoomLinearLayout(MainActivity.this, title);
                            linear.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                                    intent.putExtra("srl", document_id);
                                    startActivity(intent);
                                }
                            });
                            mainRoomLayout.addView(linear);
                        }
                    });
                }

                br.close();

            } catch (Exception e) {
                DB.sendToast("에러 발생" + e.getMessage(), 2);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            roomDataFlag = true;
        }
    }

    @Override
    protected void onStop() {
        mainSliderLayout.stopAutoCycle();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // DB 정보 업데이트
        DB.context = this;
        DB.activity = this;

    }
}
