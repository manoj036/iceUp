package in.iceup.iceup;


import android.content.Context;
import android.os.Build;
import android.provider.Settings;

class Utils {
	static boolean canDrawOverlays(Context context) {
		return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
	}
}
