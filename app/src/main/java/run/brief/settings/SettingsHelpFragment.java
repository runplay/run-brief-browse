package run.brief.settings;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import run.brief.browse.R;
public class SettingsHelpFragment extends Fragment  {
	private View view;

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view=inflater.inflate(R.layout.settings_help,container, false);

		return view;

	}

	@Override
	public void onResume() {
		super.onResume();

		


	}
	public void refreshData() {
		
	}

	public OnClickListener onEmoCheckboxClicked = new OnClickListener() {
		@Override
		public void onClick(View view) {

			
		}
	};	



}
