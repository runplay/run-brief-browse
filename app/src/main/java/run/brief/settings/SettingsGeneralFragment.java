package run.brief.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import run.brief.b.BRefreshable;
import run.brief.b.State;
import run.brief.beans.BriefSettings;
import run.brief.browse.R;
import run.brief.util.BriefActivityManager;


public class SettingsGeneralFragment extends Fragment implements BRefreshable {
	private View view;
	private View darkview;
	private View lightview;

	//private RadioButton pasterename;
	//private RadioButton pasteover;

    //public static final int RESULTCODE_SMS = 0;
	//private CheckBox smscheck;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view=inflater.inflate(R.layout.settings,container, false);

		return view;

	}

	@Override
	public void onResume() {
		super.onResume();

		darkview=(View) view.findViewById(R.id.settings_theme_dark);
		darkview.setOnClickListener(goDarkTheme);
		lightview=(View) view.findViewById(R.id.settings_theme_light);
		lightview.setOnClickListener(goLightTheme);

		//pasterename = (RadioButton) view.findViewById(R.id.settings_paste_rename);
        //pasterename.setOnClickListener(onRadioClicked);
		//pasteover= (RadioButton) view.findViewById(R.id.settings_paste_overwrite);
        //pasteover.setOnClickListener(onRadioClicked);

		refresh();

        //}
	}
	@Override
	public void refresh() {
		//BLog.e("CALL","settings general");
		//ActionBarManager.setActionBarBackOnlyWithLogo(getActivity(), R.drawable.icon_settings, getActivity().getResources().getString(R.string.action_settings), R.menu.settings, R.color.actionbar_general);
	}	
	public void refreshData() {
		
	}
	public OnClickListener goDarkTheme = new OnClickListener() {
		@Override
		public void onClick(View view) {
		    boolean restart=false;
		    BriefSettings settings = State.getSettings();
			if(settings.getBoolean(BriefSettings.BOOL_STYLE_DARK)==Boolean.FALSE) {
				settings.setBoolean(BriefSettings.BOOL_STYLE_DARK, Boolean.TRUE);
				restart=true;
			}
		    settings.save();
		    State.setSettings(settings);
			if(restart)
				BriefActivityManager.closeAndRestartBrief(getActivity());
		}
	};
	public OnClickListener goLightTheme = new OnClickListener() {
		@Override
		public void onClick(View view) {
			boolean restart=false;
			BriefSettings settings = State.getSettings();
			if(settings.getBoolean(BriefSettings.BOOL_STYLE_DARK)==Boolean.TRUE) {
				settings.setBoolean(BriefSettings.BOOL_STYLE_DARK, Boolean.FALSE);
				restart=true;
			}
			settings.save();
			State.setSettings(settings);
			if(restart)
				BriefActivityManager.closeAndRestartBrief(getActivity());

		}
	};
/*
	public OnClickListener onRadioClicked = new OnClickListener() {
		@Override
		public void onClick(View view) {
			boolean checked = ((RadioButton) view).isChecked();
			BriefSettings settings = State.getSettings();
			// Check which radio button was clicked
			switch(view.getId()) {
				case R.id.settings_paste_rename:
					if (checked) {
						//settings.setBoolean(BriefSettings.);
					}
					break;
				case R.id.settings_paste_overwrite:
					if (checked) {

					}
					break;
				default:
					if(checked) {

					}
					break;
			}
			settings.save();
			State.setSettings(settings);

		}
	};

*/


}
