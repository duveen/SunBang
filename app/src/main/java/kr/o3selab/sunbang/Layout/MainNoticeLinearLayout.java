package kr.o3selab.sunbang.Layout;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.o3selab.sunbang.R;

/**
 * Created by samgi.park on 2016-07-30.
 */
public class MainNoticeLinearLayout extends LinearLayout {

    public MainNoticeLinearLayout(Context context, String id, String title, String regdate) {
        super(context);
        View view = inflate(context, R.layout.activity_main_notice_list_row, this);

        TextView idView = (TextView) view.findViewById(R.id.main_notice_id);
        TextView titleView = (TextView) view.findViewById(R.id.main_notice_title);
        TextView regdateView = (TextView) view.findViewById(R.id.main_notice_regdate);

        idView.setText(id);
        titleView.setText(title);
        regdateView.setText(regdate);
    }
}
