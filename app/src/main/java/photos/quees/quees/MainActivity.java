package photos.quees.quees;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.firebase.client.Firebase;

import photos.quees.services.BitmapManipulation;
import photos.quees.services.FirebaseCommunication;
import photos.quees.services.SharedPreferenceService;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private String[] tabs = { "My Questions", "Others" };

    private BitmapManipulation bm = new BitmapManipulation();
    private FirebaseCommunication fc = new FirebaseCommunication();
    private SharedPreferenceService sps = new SharedPreferenceService();
    final private Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);

        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            int selectedItemId = extras.getInt("selectedItemId");

            actionBar.setSelectedNavigationItem(selectedItemId);
            viewPager.setCurrentItem(selectedItemId);

            final String email = extras.getString("email");
            final String token = extras.getString("token");

            if (email != null && token != null) {
                sps.setSharedPreferenceAsString(this, "currentEmail", email);
                sps.setSharedPreferenceAsString(this, "currentToken", token);
            }
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                viewPager.getParent().requestDisallowInterceptTouchEvent(true);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    public void addQuestion(View V) {
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
    }

    public void sendEmail(View V) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:potential_friend@gmail.com"));
        emailIntent.setType("text/plain");
    }

    public void guessPass(View v) {
        AttemptFragment fragment = mAdapter.af;
        addLastSeen(fragment);
        fragment.getQuestion(fragment.currentLastSeen + 1);
    }

    public void guessSubmit(View v) {
        AttemptFragment fragment = mAdapter.af;
        addLastSeen(fragment);
        fragment.submitGuess();
    }

    public void addLastSeen(AttemptFragment af) {
        Long newLastSeen = af.currentLastSeen + 1;
        Firebase lsRef = fc.getRef("lastseens").push();

        lsRef.child("email").setValue(sps.getSharedPreferenceAsString(activity, "currentEmail"));
        lsRef.child("lastSeen").setValue(newLastSeen);
    }
}