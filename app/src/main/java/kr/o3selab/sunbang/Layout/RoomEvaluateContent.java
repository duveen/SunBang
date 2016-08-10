package kr.o3selab.sunbang.Layout;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import kr.o3selab.sunbang.Activity.RoomActivity;
import kr.o3selab.sunbang.Instance.DB;
import kr.o3selab.sunbang.Instance.JsonHandler;
import kr.o3selab.sunbang.Instance.URLP;
import kr.o3selab.sunbang.R;

/**
 * Created by duvee on 2016-08-05.
 */
public class RoomEvaluateContent extends LinearLayout {

    public Integer id;
    public String name;
    public String data;

    public RoomActivity instance;

    public RoomEvaluateContent(RoomActivity activity, Integer id, String name, String data) {
        super(activity);

        this.id = id;
        this.name = name;
        this.data = data;
        this.instance = activity;

        View view = inflate(activity, R.layout.activity_room_evaluate_content, this);

        TextView nameView = (TextView) view.findViewById(R.id.acivity_room_evaluate_content_id);
        TextView contentView = (TextView) view.findViewById(R.id.acivity_room_evaluate_content_data);
        ImageView imageView = (ImageView) view.findViewById(R.id.acivity_room_evaluate_content_button);

        nameView.setText(this.name.substring(7, 11) + "님");
        contentView.setText(this.data);

        if (DB.phone_number.equals(this.name)) {
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(instance)
                            .setTitle("알림")
                            .setMessage("정말로 댓글을 삭제하시겠습니까?")
                            .setNegativeButton("아니오", null)
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new Thread(new SendDeleteData()).start();
                                }
                            })
                            .show();
                }
            });

        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    // ======================================
    //   댓글 삭제하기
    // =======================================
    public class SendDeleteData implements Runnable {

        @Override
        public void run() {
            try {
                String param = "id=" + id;
                String result = new JsonHandler(URLP.ROOM_SEND_DELETE_COMMENT, param).execute().get();

                if(result.contains("TRUE")) {

                } else {
                    DB.sendToast("ErrorCode 23: " + result, 2);
                }
            } catch (Exception e) {
                DB.sendToast("ErrorCode 23-1:", 2);
            }
            instance.getEvaluateDataMethod();
        }
    }
}
