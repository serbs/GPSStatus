package mgps.app.gpsstatus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class NetworkManager {
	public static boolean isInetAvailable(Context ctx) {
		return isWiFiEnabled(ctx) || isGPRSEnabled(ctx);
	}

	public static boolean isWiFiEnabled(Context ctx) {
		ConnectivityManager connManager = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			return true;
		} else {
			return false;
		}

	}

	public static boolean isGPRSEnabled(Context ctx) {
		ConnectivityManager connManager1 = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mMobile = connManager1
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (mMobile.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

}
