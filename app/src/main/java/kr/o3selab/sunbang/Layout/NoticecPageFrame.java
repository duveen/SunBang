package kr.o3selab.sunbang.Layout;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import kr.o3selab.sunbang.R;

/**
 * Created by duvee on 2016-08-01.
 */
public class NoticecPageFrame extends FrameLayout {

    public NoticecPageFrame(Context context, Integer nu) {
        super(context);
        View view = inflate(context, R.layout.activity_notice_page_frame, this);

        TextView number = (TextView) view.findViewById(R.id.notice_page_textview);
        number.setText(nu+"");
    }
}
