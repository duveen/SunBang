package kr.o3selab.sunbang.Layout;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.o3selab.sunbang.R;

/**
 * Created by duvee on 2016-08-02.
 */
public class MainRoomLinearLayout extends LinearLayout {
    public MainRoomLinearLayout(Context context, String roomTitle) {
        super(context);
        View view = inflate(context, R.layout.activity_main_room_list_row, this);

        TextView title = (TextView) view.findViewById(R.id.main_room_layout_title_view);
        title.setText(roomTitle);
    }
}
