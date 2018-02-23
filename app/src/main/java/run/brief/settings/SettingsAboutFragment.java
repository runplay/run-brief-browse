package run.brief.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import run.brief.browse.R;
import run.brief.util.BriefActivityManager;

public class SettingsAboutFragment extends Fragment {
	View view;

	private Activity activity;
    //private ThemeDialog dialog;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view=inflater.inflate(R.layout.settings_about,container, false);
        activity=getActivity();
		return view;

	}

	@Override
	public void onResume() {
		super.onResume();
		TextView wwwlink = (TextView) view.findViewById(R.id.www_link);
		wwwlink.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BriefActivityManager.openAndroidBrowserUrl(getActivity(), "http://www.brief.ink");
			}
		});
		final TextView emaillink = (TextView) view.findViewById(R.id.email_link);
		emaillink.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(BriefActivityManager.isGmailClientInstalled(getActivity())) {
					BriefActivityManager.openGmailClient(getActivity(),emaillink.getText().toString(),"Feedback for Browse - file manager app");
				} else {
					BriefActivityManager.openAndroidBrowserUrl(getActivity(), "mailto:" + emaillink.getText());
				}
			}
		});
            //TextView textHeadTheme = (TextView) view.findViewById(R.id.settings_brief_head_theme);

            //.addStyleBold(textHeadShow, B.FONT_LARGE);

            //B.addStyleBold(themeText, B.FONT_XLARGE);

        //}

	}







}
