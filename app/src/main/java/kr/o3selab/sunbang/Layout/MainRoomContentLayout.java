package kr.o3selab.sunbang.Layout;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.StringTokenizer;

import kr.o3selab.sunbang.Activity.RoomActivity;
import kr.o3selab.sunbang.Instance.DB;
import kr.o3selab.sunbang.Instance.JsonHandler;
import kr.o3selab.sunbang.Instance.URLP;
import kr.o3selab.sunbang.R;

/**
 * Created by duvee on 2016-08-11.
 */
public class MainRoomContentLayout extends LinearLayout {

    public MainRoomContentLayout(final Context context, final String roomSrl, String nickName, String title, String image, String money, String rate) {
        super(context);
        View view = inflate(context, R.layout.activity_main_room_content_list, this);

        TextView titleView = (TextView) view.findViewById(R.id.activity_main_room_content_title);
        final ImageView imageView = (ImageView) view.findViewById(R.id.activity_main_room_content_image_view);
        TextView nameView = (TextView) view.findViewById(R.id.activity_main_room_content_name);
        TextView moneyView = (TextView) view.findViewById(R.id.activity_main_room_content_money);
        TextView rateView = (TextView) view.findViewById(R.id.activity_main_room_content_rate);
        TextView shareView = (TextView) view.findViewById(R.id.activity_main_room_content_share);

        titleView.setText(nickName);
        nameView.setText(title);
        Glide.with(context).load(image).into(imageView);
        moneyView.setText(money);
        rateView.setText(rate);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RoomActivity.class);
                intent.putExtra("srl", roomSrl);
                context.startActivity(intent);
            }
        });

        shareView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DB.sendToast("공유버튼 클릭", 1);
            }
        });


    }
}
