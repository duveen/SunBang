package kr.o3selab.sunbang.Layout;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.zip.Inflater;

import kr.o3selab.sunbang.R;

/**
 * Created by duvee on 2016-08-04.
 */
public class SearchResultLinearLayout extends LinearLayout {

    public SearchResultLinearLayout(Context context, String title) {
        super(context);
        View view = inflate(context, R.layout.activity_search_result_list, this);

        TextView tv = (TextView) view.findViewById(R.id.activity_search_result_text);
        tv.setText(title);
    }
}
