package com.weather.mini.c15.c15weathermonitoring;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class ActivityPilihan extends AppCompatActivity {
    private SliderPagerAdapter mAdapter;
    private SliderIndicator mIndicator;

    private BannerSlider bannerSlider;
    private LinearLayout mLinearLayout;

    private LinearLayout lnrsuhu,lnrkelembaban,lnrph,lnrnutrisitanaman,lnrttghidroponik,lnrabout,lnrvolumeair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilihan);
        lnrsuhu = findViewById(R.id.suhu);
        lnrkelembaban=findViewById(R.id.kelembaban);
        lnrph=findViewById(R.id.ph);
        lnrnutrisitanaman=findViewById(R.id.nutrisitanaman);
        //lnrttghidroponik=findViewById(R.id.tentanghidroponik);
        lnrabout=findViewById(R.id.about);
        lnrvolumeair=findViewById(R.id.volumeair);


        bannerSlider = findViewById(R.id.sliderView);
        mLinearLayout = findViewById(R.id.pagesContainer);
        setupSlider();

        lnrsuhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivityPilihan.this,Monitoringsuhu.class);
                startActivity(i);
            }
        });

        lnrkelembaban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivityPilihan.this,Kelembaban.class);
                startActivity(i);
            }
        });

        lnrph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivityPilihan.this,Ph.class);
                startActivity(i);
            }
        });

        lnrnutrisitanaman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivityPilihan.this,NutrisiTanaman.class);
                startActivity(i);
            }
        });

        /*lnrttghidroponik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivityPilihan.this,TentangHidroponik.class);
                startActivity(i);
            }
        });*/
        lnrabout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivityPilihan.this,About.class);
                startActivity(i);
            }
        });
        lnrvolumeair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivityPilihan.this, VolumeAirHidroponik.class);
                startActivity(i);
            }
        });
    }
    private void setupSlider() {
        bannerSlider.setDurationScroll(800);
        List<Fragment> fragments = new ArrayList<>();

        //link image
        fragments.add(FragmentSlider.newInstance("https://i.ibb.co/4m1cJ4b/IMG-20200817-WA0026.jpg"));
        fragments.add(FragmentSlider.newInstance("https://i.ibb.co/ZVgyfs0/IMG-20200825-WA0013.jpg"));
        fragments.add(FragmentSlider.newInstance("https://i.ibb.co/sHyh2p4/IMG-20200825-WA0009.jpg"));
        fragments.add(FragmentSlider.newInstance("https://i.ibb.co/CQpXTbd/IMG-20200825-WA0022.jpg"));

        fragments.add(FragmentSlider.newInstance("https://i.ibb.co/qmSwDz0/Whats-App-Image-2020-11-02-at-11-54-12.jpg"));
        fragments.add(FragmentSlider.newInstance("https://i.ibb.co/xhJ4K33/Whats-App-Image-2020-11-02-at-11-50-25.jpg"));


        mAdapter = new SliderPagerAdapter(getSupportFragmentManager(), fragments);
        bannerSlider.setAdapter(mAdapter);
        mIndicator = new SliderIndicator(this, mLinearLayout, bannerSlider, R.drawable.indicator_circle);
        mIndicator.setPageCount(fragments.size());
        mIndicator.show();
    }
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}
