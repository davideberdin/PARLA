package davideberdin.goofing;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.view.View.OnClickListener;
import android.media.MediaPlayer;
import android.support.design.widget.TabLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

import davideberdin.goofing.controllers.User;
import davideberdin.goofing.fragments.ListenNative;
import davideberdin.goofing.fragments.ListenUser;
import davideberdin.goofing.libraries.FloatingActionButton;
import davideberdin.goofing.libraries.FloatingActionsMenu;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.IOUtilities;
import davideberdin.goofing.utilities.UserLocalStore;

public class Listening extends AppCompatActivity {

    private UserLocalStore userLocalStore = null;
    private User loggedUser = null;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    public static boolean listeningNative = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // need to change the color - android bug
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fabListening = new FloatingActionButton(getBaseContext());
        fabListening.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
        fabListening.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // play audio
                try {

                    userLocalStore = new UserLocalStore(v.getContext());
                    loggedUser = userLocalStore.getLoggedUser();

                    MediaPlayer mediaPlayer = null;
                    String fileAudio = ((loggedUser.GetCurrentSentence()).toLowerCase()).replace(" ", "_");
                    if( Listening.listeningNative) {
                        if (loggedUser.GetGender().equals("Male"))
                            fileAudio = "m_" + fileAudio;
                        else
                            fileAudio = "f_" + fileAudio;

                        int resID = getResources().getIdentifier(fileAudio, "raw", getPackageName());
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), resID);
                    } else {
                        fileAudio = "recorded_" + fileAudio;
                        String fileAudioPath = IOUtilities.audioFilesPath.get(fileAudio);
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(fileAudioPath));
                        mediaPlayer.setDataSource(fileAudioPath);
                    }

                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        FloatingActionButton fabTest = new FloatingActionButton(getBaseContext());
        fabTest.setImageResource(android.R.drawable.ic_btn_speak_now);
        fabTest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Listening.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        final FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.fabMenu);
        menuMultipleActions.addButton(fabListening);
        menuMultipleActions.addButton(fabTest);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_listening, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //region APP EVENTS
    @Override
    public void onResume() {
        super.onResume();

        try {
            IOUtilities.readUserAudio(this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        try {
            IOUtilities.writeUserAudio(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            IOUtilities.writeUserAudio(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            IOUtilities.writeUserAudio(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //endregion

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
            Fragment f;
            switch (position) {
                case 0:
                    f = new ListenNative();
                    break;
                case 1:
                    f = new ListenUser();
                    break;
                default:
                    f = null;
            }
            return f;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Listen Native";
                case 1:
                    return "Listen Yourself";
            }
            return null;
        }
    }
}
