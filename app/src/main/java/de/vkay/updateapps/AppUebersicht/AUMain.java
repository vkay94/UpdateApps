package de.vkay.updateapps.AppUebersicht;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import de.vkay.updateapps.AppUebersicht.Feedback.FeedbackActivity;
import de.vkay.updateapps.Datenspeicher.SharedPrefs;
import de.vkay.updateapps.R;
import de.vkay.updateapps.Sonstiges.Const;


public class AUMain extends AppCompatActivity {


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    Bundle bundle;
    SharedPrefs shared;
    AppBarLayout appBar;

    public static boolean fabVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appueber);

        initToolbar();

        appBar = (AppBarLayout) findViewById(R.id.htab_appbar);

        final FloatingActionButton fab_send = (FloatingActionButton) findViewById(R.id.appueber_fab);
        fab_send.setVisibility(View.INVISIBLE);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.appueber_viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        if (shared.getLoggedInStatus()) {
            fab_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
                    intent.putExtra(Const.PAKETNAME, bundle.getString(Const.PAKETNAME));
                    startActivity(intent);
                }
            });

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {

                    switch (position) {
                        case 2:
                            fabVisible = true;
                            fab_send.show();
                            break;

                        default:
                            fabVisible = false;
                            fab_send.hide();
                            break;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.appueber_tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    // Verhindert, dass der Titel verschwindet wenn eingeklappt, ist aber nach Senden ausgeklappt
    @Override
    protected void onRestart() {
        super.onRestart();
        appBar.setExpanded(true, false);
    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bundle = getIntent().getExtras();

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(bundle.getString(Const.NAME));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Window w = getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));

        shared = new SharedPrefs(this);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return AUFragmentUbersicht.newInstance(bundle);
                case 1:
                    return AUFragmentVersionen.newInstance(bundle);
                default:
                    return AUFragmentFeedback.newInstance(bundle);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.aumain_uebersicht);
                case 1:
                    return getString(R.string.aumain_version);
                case 2:
                    return getString(R.string.aumain_feedback);
            }
            return null;
        }
    }
}
