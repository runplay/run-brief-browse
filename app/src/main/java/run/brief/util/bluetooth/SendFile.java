package run.brief.util.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public final class SendFile extends FragmentActivity {
    static final int PICK_CONTACT_REQUEST = 0;
    
  //...
  // duration that the device is discoverable
    private static final int DISCOVER_DURATION = 300;
   
  // our request code (must be greater than zero)
  	private static final int REQUEST_BLU = 1;
  	
    public void enableBlu(){
    	// enable device discovery - this will automatically enable Bluetooth
    	Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
    	 
    	discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
    	                            DISCOVER_DURATION );
    	 
    	startActivityForResult(discoveryIntent, REQUEST_BLU);
    }


    protected void onActivityResult (int requestCode,
            int resultCode,
            Intent data) {

		if (resultCode == DISCOVER_DURATION
			&& requestCode == REQUEST_BLU) {
			Toast.makeText(this, "Bluetooth completed", Toast.LENGTH_SHORT).show();
			// processing code goes here
		} else { // cancelled or error
			Toast.makeText(this, "Bluetooth cancelled", Toast.LENGTH_SHORT).show();
		}
		super.onBackPressed();
	}
}
