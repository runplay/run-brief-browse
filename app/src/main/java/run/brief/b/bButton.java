package run.brief.b;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.RelativeLayout;

import run.brief.browse.R;

public class bButton extends Button {

	public bButton(Context context) {
	    super(context);
	    init(context);
	    // TODO Auto-generated constructor stub
	}
	
	public bButton(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    init(context);
	    // TODO Auto-generated constructor stub
	}
	
	public bButton(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    init(context);
	    // TODO Auto-generated constructor stub
    }
	
	private void init(Context context) {
		this.setBackgroundResource(R.drawable.btn_general);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		//params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		params.setMargins(1,1,1,1);
		this.setLayoutParams(params);
		this.setTextSize(16);
		//this.setTextColor(context.getResources().getColor(R.color.black));
		this.setPadding(2, 0, 5,0);
		this.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));

	}
}