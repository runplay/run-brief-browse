package run.brief.util.explore;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import run.brief.b.BRefreshable;
import run.brief.b.Bgo;
import run.brief.b.State;
import run.brief.browse.R;
import run.brief.util.explore.fm.FileManagerDisk;
//import run.brief.beans.Email;
//import run.brief.email.EmailSendFragment;


public class ExploreDialog extends Dialog {
	private Dialog thisDialog;
	private File usefile;
	private Activity activity;
	public static boolean shouldRefresh=false;
	private BRefreshable refreshFragment;
	private FileManagerDisk fm;
    private ExploreRenameDialog popupRename;

	public File getUsefile() {
		return usefile;
	}
	public ExploreDialog(Activity activity, File usefile, BRefreshable refreshFragment, FileManagerDisk filemanager) {
		super(activity);
		this.refreshFragment=refreshFragment;
		
		this.usefile = usefile;
		
		this.activity=activity;
		fm=filemanager;

		this.setContentView(R.layout.explore_dialog);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(this.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		this.getWindow().setAttributes(lp);
        //TextView dialogTitle = (TextView) this.findViewById(android.R.id.title);

        //dialogTitle.setText(activity.getResources().getString(R.string.file_menu_options));
        //((TextView) this.findViewById(dialogTitle)).setTypeface(B.getTypeFaceBold());
        //this.setView(title);

        //this.setIcon(R.drawable.icon);

		Button bim = (Button) this.findViewById(R.id.dialog_cancel);
		bim.setOnClickListener(onCloseClick);
		Button eim = (Button) this.findViewById(R.id.explore_dialog_open);
		eim.setOnClickListener(onOpenClick);
		Button del = (Button) this.findViewById(R.id.explore_dialog_delete);
		del.setOnClickListener(onDeleteClick);
		Button rename = (Button) this.findViewById(R.id.explore_dialog_rename);
		rename.setOnClickListener(onRenameClick);

		thisDialog=this;
		this.setOnDismissListener(onDismiss);
		
		Button copy = (Button) this.findViewById(R.id.dialog_copy);
		copy.setOnClickListener(onCopyClick);


		//context.getMenuInflater().inflate(R.menu.notes_home_popup, popupMenu.getMenu());
	}
	public Button.OnClickListener onRenameClick = new Button.OnClickListener() {
		@Override
		public void onClick(View view) {

            popupRename = new ExploreRenameDialog(activity,usefile,refreshFragment,fm);

            popupRename.show();
            popupRename.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface intf) {
                    //BLog.e("DISMISS", "Called");
                    if (ExploreRenameDialog.shouldRefresh)
                        refreshFragment.refreshData();
                }
            });


			//fm.addSelectedFile(new FileItem(usefile));
			//State.addCachedFileManager(fm);
			//Bgo.openFragmentBackStack(activity, new FilesDeleteFragment());
			shouldRefresh=false;
			thisDialog.dismiss();

		}
	};
	public Button.OnClickListener onDeleteClick = new Button.OnClickListener() {
		@Override
		public void onClick(View view) {
            fm.addSelectedFile(new FileItem(usefile));
			State.addCachedFileManager(fm);
            Bgo.openFragmentBackStack(activity, new FilesDeleteFragment());
            shouldRefresh=false;
			thisDialog.dismiss();

		}
	};
	public OnDismissListener onDismiss = new OnDismissListener() {
		@Override
		public void onDismiss(DialogInterface intf) {
			//BLog.e("DISMISS", "Called");
			if(ExploreDialog.shouldRefresh)
				refreshFragment.refresh();
		}
	};
	public Button.OnClickListener onCloseClick = new Button.OnClickListener() {
		@Override
		public void onClick(View view) {
			shouldRefresh=false;
			thisDialog.dismiss();
		}
	};
	public Button.OnClickListener onOpenClick = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			

		    shouldRefresh=false;
		    thisDialog.dismiss();
			Bgo.openFile(activity, fm, usefile);
		}
		
	};
	public Button.OnClickListener onCopyClick = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			fm.addSelectedClipboardCopyFile(usefile);
//BLog.e("USEFILE: "+usefile.getAbsolutePath());
		    //Device.copyToClipboard(activity, usefile.getAbsolutePath());
			shouldRefresh=true;
		    thisDialog.dismiss();
		    
		}
		
	};
}
