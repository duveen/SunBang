package kr.o3selab.sunbang;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import kr.o3selab.sunbang.Activity.AllFindRoomActivity;
import kr.o3selab.sunbang.Activity.LoadingActivity;
import kr.o3selab.sunbang.Activity.NoticeActivity;
import kr.o3selab.sunbang.Activity.NoticeListActivity;
import kr.o3selab.sunbang.Activity.RoomActivity;
import kr.o3selab.sunbang.Activity.SearchActivity;
import kr.o3selab.sunbang.Instance.DB;
import kr.o3selab.sunbang.Instance.JsonHandler;
import kr.o3selab.sunbang.Instance.SunbangProgress;
import kr.o3selab.sunbang.Instance.ThreadGroupHandler;
import kr.o3selab.sunbang.Instance.URLP;
import kr.o3selab.sunbang.Layout.MainNoticeLinearLayout;
import kr.o3selab.sunbang.Layout.MainRoomContentLayout;
import kr.o3selab.sunbang.Layout.MainRoomLinearLayout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public ProgressDialog pd;
    public SliderLayout mainSliderLayout;
    public FrameLayout locationFrame;
    public FrameLayout moneyFrame;
    public LinearLayout mainRoomContentLayout;
    public LinearLayout mainNoticeLayout;
    public LinearLayout mainRoomLayout;
    public TextView mainNoticeTextView;
    public ImageView mainSearchImageView;
    public ImageView footerLogoImageView;


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
        mainRoomContentLayout = (LinearLayout) findViewById(R.id.activity_main_room_content_list);
        mainRoomLayout = (LinearLayout) findViewById(R.id.main_room_layout);
        mainSearchImageView = (ImageView) findViewById(R.id.activity_main_ic_search);
        footerLogoImageView = (ImageView) findViewById(R.id.main_footer_logo_image);


        // 네비게이션 바 핸들러
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageView menuButton = (ImageView) findViewById(R.id.activity_main_ic_menu);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }

            }
        });


        // 데이터 처리
        pd = new SunbangProgress(this);

        Thread getImageListShowThread = new Thread(new GetImageListShowThread());
        getImageListShowThread.start();

        Thread getNoticeListDataThread = new Thread(new GetMainNoticeListData());
        getNoticeListDataThread.start();

        // Thread getRoomListDataThread = new Thread(new GetMainRoomListData());
        // getRoomListDataThread.start();

        Thread getMainRoomContentData = new Thread(new GetMainRoomContentData());
        getMainRoomContentData.start();

        Thread[] mainGroup = {getImageListShowThread, getNoticeListDataThread, getMainRoomContentData};

        ThreadGroupHandler threadGroupHandler = new ThreadGroupHandler(mainGroup, pd);
        threadGroupHandler.start();

        // 핸들러
        locationFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AllFindRoomActivity.class);
                startActivity(intent);
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

        //카카오톡 스킴 체크
        Intent intent = getIntent();
        boolean tf = intent.getBooleanExtra("room_flag", false);
        if (tf) {
            String roomSrl = intent.getStringExtra("room_srl");
            intent = new Intent(MainActivity.this, RoomActivity.class);
            intent.putExtra("srl", roomSrl);
            startActivity(intent);
        }

    }

    // 메인 이미지 슬라이더 로딩 핸들러
    public class GetImageListShowThread implements Runnable {

        @Override
        public void run() {
            final HashMap<String, String> mainImages = DB.mainImages;

            final Iterator<String> itr = mainImages.keySet().iterator();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    while (itr.hasNext()) {
                        final String link = itr.next();
                        DefaultSliderView textSliderView = new DefaultSliderView(MainActivity.this);

                        textSliderView
                                .image(link)
                                .setScaleType(BaseSliderView.ScaleType.Fit)
                                .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                    @Override
                                    public void onSliderClick(BaseSliderView slider) {
                                        String href = mainImages.get(link);
                                        StringTokenizer st = new StringTokenizer(href, ":");

                                        String protocol = st.nextToken();
                                        String value = st.nextToken();

                                        Intent intent;
                                        switch (protocol) {
                                            case "notice":
                                                intent = new Intent(MainActivity.this, NoticeActivity.class);
                                                intent.putExtra("document_id", value);
                                                startActivity(intent);
                                                break;

                                            case "room":
                                                intent = new Intent(MainActivity.this, RoomActivity.class);
                                                intent.putExtra("srl", value);
                                                startActivity(intent);
                                                break;

                                            case "null":
                                                break;

                                            default:
                                                break;
                                        }
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
        }
    }

    // 메인 공지사항 리스트 로딩 핸들러
    public class GetMainNoticeListData implements Runnable {

        @Override
        public void run() {
            try {
                String param = "module_srl=" + DB.NOTICE_MODULE;
                String result = new JsonHandler(URLP.MAIN_NOTCIE_LIST, param).execute().get();

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);

                    final String id = obj.getString("document_id");
                    final String title = obj.getString("title");
                    final String regdate = obj.getString("regdate");
                    final String convertRegdate = regdate.substring(0, 4) + "-" + regdate.substring(4, 6) + "-" + regdate.substring(6, 8);

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
            } catch (Exception e) {
                DB.sendToast("ErrorCode 6:" + e.getMessage(), 2);
            }
        }
    }

    // 메인 최근 등록 원룸 리스트 로딩 핸들러
    /*
    public class GetMainRoomListData implements Runnable {

        @Override
        public void run() {

            try {
                String param = URLP.PARAM_MODULE_SRL + DB.ROOM_MODULE;
                String result = new JsonHandler(URLP.MAIN_ROOM_LIST, param).execute().get();

                JSONObject jsonObject = new JSONObject(result);
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
            } catch (Exception e) {
                DB.sendToast("ErrorCode 7: " + e.getMessage(), 2);
            }
        }
    }
    */

    // 메인 우선순위 별 원룸 리스트 로딩 핸들러
    public class GetMainRoomContentData implements Runnable {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinkedList<LoadingActivity.RoomContent> list = DB.roomList;

                    final float scale = getResources().getDisplayMetrics().density;
                    int m0dp = (int) (0 * scale + 0.5f);

                    LinearLayout parentLayout = null;

                    for (int i = 0; i < list.size(); i = i + 2) {
                        parentLayout = new LinearLayout(MainActivity.this);
                        parentLayout.setOrientation(LinearLayout.HORIZONTAL);
                        parentLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                        LoadingActivity.RoomContent content = list.get(i);
                        LinearLayout layout = new MainRoomContentLayout(MainActivity.this, content.roomSrl, content.nick_name, content.title, content.image, content.money, content.rate);
                        layout.setLayoutParams(new LinearLayout.LayoutParams(m0dp, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                        layout.setWeightSum(1);
                        parentLayout.addView(layout);

                        try {
                            content = list.get(i + 1);
                            LinearLayout layout2 = new MainRoomContentLayout(MainActivity.this, content.roomSrl, content.nick_name, content.title, content.image, content.money, content.rate);
                            layout2.setLayoutParams(new LinearLayout.LayoutParams(m0dp, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                            layout2.setWeightSum(1);
                            parentLayout.addView(layout2);

                        } catch (IndexOutOfBoundsException e) {
                            LinearLayout layout2 = new LinearLayout(MainActivity.this);
                            layout2.setLayoutParams(new LinearLayout.LayoutParams(m0dp, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
                            layout2.setWeightSum(1);
                            parentLayout.addView(layout2);
                        }
                        mainRoomContentLayout.addView(parentLayout);
                    }
                }
            });
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.nav_notice:
                intent = new Intent(MainActivity.this, NoticeListActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_location:
                intent = new Intent(MainActivity.this, AllFindRoomActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_money:
                DB.sendToast("개발 중...", 1);
                break;

            case R.id.nav_search:
                intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_kakao:
                String url = "https://open.kakao.com/o/sy4tHmm";
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;

            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}