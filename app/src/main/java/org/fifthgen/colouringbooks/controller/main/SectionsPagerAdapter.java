package org.fifthgen.colouringbooks.controller.main;

import android.os.Parcelable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by GameGFX Studio on 2015/8/14.
 */
@SuppressWarnings("deprecation")
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
    List<String> tabs;

    public SectionsPagerAdapter(FragmentManager fragmentManager, List<String> tabs) {
        super(fragmentManager);
        this.tabs = tabs;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return ThemeListFragment.getInstance();
        } else {
            return UserFragment.getInstance();
        }
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    public void destroyAllFragment() {
        ThemeListFragment.getInstance().finish();
        UserFragment.getInstance().finish();
    }
}
