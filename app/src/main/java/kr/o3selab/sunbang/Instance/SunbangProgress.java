package kr.o3selab.sunbang.Instance;

import android.app.ProgressDialog;
import android.content.Context;

public class SunbangProgress extends ProgressDialog {

    public SunbangProgress(Context context) {
        super(context);
        setTitle("선방");
        setMessage("정보를 불러오고 있습니다!");
        setCancelable(false);
        setProgressStyle(STYLE_SPINNER);
    }
}
