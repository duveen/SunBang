package kr.o3selab.sunbang.Layout;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.AppActionInfoBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;

import kr.o3selab.sunbang.Activity.RoomActivity;
import kr.o3selab.sunbang.Instance.DB;
import kr.o3selab.sunbang.R;

/**
 * Created by duvee on 2016-08-11.
 */
public class MainRoomContentLayout extends LinearLayout {

    public MainRoomContentLayout(final Context context, final String roomSrl, String nickName, String title, String image, String money, String rate) {
        super(context);

        try {
            //카카오톡 API
            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(context);
            final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

            String imageSrc = image;
            int width = 400;
            int height = 200;
            kakaoTalkLinkMessageBuilder
                    .addText("선방에서 " + title + "을(를) 공유하셨습니다!")
                    .addImage(imageSrc, width, height)
                    .addAppButton("앱에서 보기!",
                            new AppActionBuilder()
                                    .addActionInfo(AppActionInfoBuilder
                                            .createAndroidActionInfoBuilder()
                                            .setExecuteParam("room_srl="+roomSrl)
                                            .setMarketParam("referrer=" + DB.activity.getPackageName())
                                            .build())
                                    .build()
                    ).build();


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
                    try {
                        kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, context);
                    } catch (Exception e) {
                        DB.sendToast("ErrorCode 24: " + e.getMessage(), 2);
                    }
                }
            });

        } catch (Exception e) {
            DB.sendToast("ErrorCode 23: " + e.getMessage(), 2);
        }
    }
}
