package run.brief.settings;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class SettingsHomeTabbedAdapter extends FragmentStatePagerAdapter {
 
    public SettingsHomeTabbedAdapter(FragmentManager fm) {
        super(fm);

    }
    public int getCount() {
        return 3;
    }

    public Fragment getItem(int position) {
    	Fragment f=null;
    	//View view=null;
    	switch(position) {
    		case 0: 
    			f=new SettingsGeneralFragment();
    			break;
    		case 1:
    			f=new SettingsHelpFragment();
    			break;
            case 2:
                f=new SettingsAboutFragment();
                break;

    	}

        return f;
    }

    public int getItemPosition(Object item) {
        return POSITION_NONE;
    }

}