package com.thesis.ClientApp.welcome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import com.google.android.material.tabs.TabLayout;
import com.thesis.ClientApp.R;
import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {
    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    ImageView next,skip;
    int position = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        //ini view
        next = findViewById(R.id.next);
        skip = findViewById(R.id.skip);
        tabIndicator = findViewById(R.id.indicator);

        //Fill list screen
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Keep Posting","Post any issue you may find around\n" +
                " the community",R.drawable.picture1));
        mList.add(new ScreenItem("Interact with Others","Leave comment or upvotes to other \n" +
                "people's post",R.drawable.picture2));
        mList.add(new ScreenItem("Map Visualize","See the locations of the post of\n" +
                "other users in the map",R.drawable.picture3));

        //Setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);

        //setup tabLayout with pagerView
        tabIndicator.setupWithViewPager(screenPager);

        //Skip btn click
        skip.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), IntroLast.class );
            startActivity(intent);
            finish();
        });

        //Next btn click
        next.setOnClickListener(view -> {

            position = screenPager.getCurrentItem();
            if (position < mList.size()){
                position++;
                screenPager.setCurrentItem(position);
            }
            //When reached last
            if (position == mList.size()) {

                loadLastScreen();

            }
        });

        //tabLayout last

        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size()){
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void loadLastScreen() {
        Intent intent = new Intent(getApplicationContext(), IntroLast.class );
        startActivity(intent);
        finish();
    }
}
