package kr.o3selab.sunbang.Layout;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.o3selab.sunbang.R;

/**
 * Created by duvee on 2016-08-01.
 */
public class NoticeListRowLayout extends LinearLayout {
    public NoticeListRowLayout(Context context, final String id, final String title, final String nick, final String regdate) {
        super(context);

        View view = inflate(context, R.layout.activity_notice_list_row, this);

        TextView idView = (TextView) view.findViewById(R.id.notice_list_number);
        TextView titleView = (TextView) view.findViewById(R.id.notice_list_title);
        TextView nickView = (TextView) view.findViewById(R.id.notice_list_nick_name);
        TextView regdateView = (TextView) view.findViewById(R.id.notice_list_regdate);

        idView.setText(id);
        titleView.setText(title);
        nickView.setText(nick);
        regdateView.setText(regdate);
    }
}
