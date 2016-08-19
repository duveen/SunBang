package kr.o3selab.sunbang.Layout;

import android.content.Context;
import android.view.View;
import android.widget.ScrollView;

import kr.o3selab.sunbang.R;

/**
 * Created by duvee on 2016-08-15.
 */
public class AllFindRoomListView extends ScrollView {

    public AllFindRoomListView(Context context) {
        super(context);
        View view = inflate(context, R.layout.activity_all_find_room_list, this);
    }
}
