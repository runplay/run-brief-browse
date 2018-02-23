package run.brief.b;

import android.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

import run.brief.util.explore.ActionModeBack;


public class BFragment extends Fragment {
	protected ActionModeBack amb;
	protected Menu menu;
	@Override
	public void onPause() {
		super.onPause();
		if(amb!=null) {
			amb.done();
			amb.finish();
		}
		System.gc();

		//ViewManagerText.dismissPopup();
		
	}

	public BFragment() {

		this.setHasOptionsMenu(true);

		//BLog.e("CR", "create new BFragment");
	}


	@Override
	public void onResume() {

		super.onResume();
		//ActionBarManager.show((AppCompatActivity) getActivity());
        //BLog.e("BFrag", "resume: "+this.getClass().getName());
		//this.getView().setFocusableInTouchMode(true);

	}

	@Override
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
		//super.onCreateOptionsMenu(menu,inflater);
		this.menu=menu;


	}

}
