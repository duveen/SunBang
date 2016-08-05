package kr.o3selab.sunbang.Layout;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import kr.o3selab.sunbang.R;

/**
 * Created by duvee on 2016-08-01.
 */
public class NoticeListInitView extends LinearLayout {

    public NoticeListInitView(Context context) {
        super(context);
        View view = inflate(context, R.layout.activity_notice_list_init, this);
    }
}
