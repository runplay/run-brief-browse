package run.brief.util.explore;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

import run.brief.b.BRefreshable;
import run.brief.b.Device;
import run.brief.browse.R;
import run.brief.util.Files;
import run.brief.util.explore.fm.FileManager;
import run.brief.util.log.BLog;
//import run.brief.beans.Email;
//import run.brief.email.EmailSendFragment;


public class ExploreRenameDialog extends Dialog {
	private Dialog thisDialog;
	private File usefile;
	private Activity activity;
	public static boolean shouldRefresh=false;
	private BRefreshable refreshFragment;
	private EditText renameText;
	private FileManager fm;
    private Button bim;
	public File getUsefile() {
		return usefile;
	}
	public ExploreRenameDialog(Activity activity, File usefile, BRefreshable refreshFragment, FileManager filemanager) {
		super(activity);
		this.refreshFragment=refreshFragment;
		
		this.usefile = usefile;
		
		this.activity=activity;
		fm=filemanager;

		this.setContentView(R.layout.explore_rename_dialog);
        //TextView dialogTitle = (TextView) this.findViewById(android.R.id.title);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(this.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes(lp);
        //dialogTitle.setText(activity.getResources().getString(R.string.label_rename));
        //((TextView) this.findViewById(dialogTitle)).setTypeface(B.getTypeFaceBold());
        //this.setView(title);
		renameText = (EditText) this.findViewById(R.id.file_name_edit);
        renameText.setText(Files.getFilenameLessExtension(usefile.getName()));
        renameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (renameText.getText().toString().matches("[^-_.A-Za-z0-9]")) {
                    BLog.e("disable");
                    bim.setEnabled(false);
                    bim.setAlpha(0.5f);
                } else {
                    BLog.e("enable");
                    bim.setEnabled(true);
                    bim.setAlpha(1f);
                }
            }
        });
        Device.setKeyboard(activity, renameText, true);

        //this.setIcon(R.drawable.icon);

		bim = (Button) this.findViewById(R.id.file_explore_rename_now);
		bim.setOnClickListener(onRenameClick);
        bim.setEnabled(false);
        bim.setAlpha(0.5f);
        TextView txt = (TextView) this.findViewById(R.id.file_name_ext);
        txt.setText(Files.getExtension(usefile.getName()));

        Button close = (Button) this.findViewById(R.id.dialog_cancel);
        close.setOnClickListener(onCloseClick);

		thisDialog=this;
		this.setOnDismissListener(onDismiss);
		


		//context.getMenuInflater().inflate(R.menu.notes_home_popup, popupMenu.getMenu());
	}


	public OnDismissListener onDismiss = new OnDismissListener() {
		@Override
		public void onDismiss(DialogInterface intf) {
			//BLog.e("DISMISS", "Called");
			if(ExploreRenameDialog.shouldRefresh)
				refreshFragment.refresh();
		}
	};
	public Button.OnClickListener onRenameClick = new Button.OnClickListener() {
		@Override
		public void onClick(View view) {
            File to = new File(usefile.getParentFile().getAbsolutePath() + File.separator + renameText.getText().toString()+Files.getExtension(usefile.getName()));
            BLog.e("renameing to: " + to.getAbsolutePath());
            usefile.renameTo(to);
            shouldRefresh=true;
			thisDialog.dismiss();
		}
	};
    public Button.OnClickListener onCloseClick = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            shouldRefresh=false;
            thisDialog.dismiss();
        }
    };


}
