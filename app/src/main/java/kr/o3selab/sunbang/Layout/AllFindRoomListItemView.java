package kr.o3selab.sunbang.Layout;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.StringTokenizer;

import kr.o3selab.sunbang.Instance.DB;
import kr.o3selab.sunbang.Instance.URLP;
import kr.o3selab.sunbang.R;

public class AllFindRoomListItemView extends LinearLayout {

    public AllFindRoomListItemView(Context context, String data) {
        super(context);

        StringTokenizer str = new StringTokenizer(data, "$");

        String imageUrlTemp = str.nextToken();
        String imageUrl = imageUrlTemp.substring(2, imageUrlTemp.length());
        String deposit = str.nextToken();
        String moneyType = str.nextToken();
        String money = str.nextToken();
        String roomType = str.nextToken();
        String adminMoney = str.nextToken();
        double lat = Double.parseDouble(str.nextToken());
        double lng = Double.parseDouble(str.nextToken());

        Location hostLocation = new Location("gps");
        hostLocation.setLatitude(lat);
        hostLocation.setLongitude(lng);

        Location roomLocation = new Location("gps");
        roomLocation.setLatitude(DB.sLocationLat.get(DB.defaultBuilding));
        roomLocation.setLongitude(DB.sLocationLng.get(DB.defaultBuilding));

        double distance = hostLocation.distanceTo(roomLocation);

        View view = inflate(context, R.layout.activity_all_find_room_list_item, this);

        ImageView itemImage = (ImageView) view.findViewById(R.id.activity_all_find_list_layout_image_view);
        Glide.with(context).load(URLP.BASE_URL + imageUrl).into(itemImage);

        TextView moneyView = (TextView) view.findViewById(R.id.activity_all_find_list_layout_money);
        moneyView.setText(deposit + " / " + money);

        TextView optionView = (TextView) view.findViewById(R.id.activity_all_find_list_layout_optional);
        optionView.setText(moneyType + " | " + roomType + " | " + adminMoney);

        TextView locationView = (TextView) view.findViewById(R.id.activity_all_find_list_layout_distance);
        locationView.setText(DB.getLocationName(DB.defaultBuilding) +" 까지의 거리 : " + (int) distance + "m");
    }
}
