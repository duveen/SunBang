package kr.o3selab.sunbang.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import kr.o3selab.sunbang.R;

public class RoomImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_image);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        ImageView imageView = (ImageView) findViewById(R.id.room_activity_photo_view);
        Glide.with(this).load(url).into(imageView);
    }
}
