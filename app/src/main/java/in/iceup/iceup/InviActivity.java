package in.iceup.iceup;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class InviActivity extends Activity {

	private final BroadcastReceiver FinishThisActivity = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
			overridePendingTransition(0, R.anim.fade_out);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invi);
		Log.d("Invi Activity", "onCreate: Activity Created");
//        sendBroadcast(new Intent("FinishMainActivity"));
		registerReceiver(FinishThisActivity, new IntentFilter("FinishInviActivity"));
//		if(MainActivity.opening){
//			finish();
//			MainActivity.opening=false;
//		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}

//    //goes into immersive mode if needed
//    public void toggleHideyBar() {
//        int newUiOptions = this.getWindow().getDecorView().getSystemUiVisibility();
//
//        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
//        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//
//        this.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
//    }

	@Override
	protected void onStop() {
		super.onStop();
		sendBroadcast(new Intent("InviStopped"));

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(FinishThisActivity);
	}
}
