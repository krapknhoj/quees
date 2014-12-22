package photos.quees.quees;

import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {
    public AttemptFragment af;

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new ReviewFragment();
            case 1:
                af = new AttemptFragment();
                return af;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
