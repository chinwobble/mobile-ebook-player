/**
 * Helper utilities to provide more information about this program.
 */
package com.ader.utilities;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * @author Julian Harty
 */
public class About {
	
	private Activity activity;
	private static final String TAG = "About";

	public About(Activity activity) {
		this.activity = activity;
	}

	/**
	 * @return the version name if available, else ??
	 */
	public String getVersionName() {
		String version = "??";
		try {
			PackageInfo pi = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			version = pi.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			Log.e(TAG, "Version name not found in package", e);
		}
		return version;
	}

	/**
	 * @return the version code of the current application; or -1.
	 */
	public int getVersionCode() {
		int version = -1;
		try {
			PackageInfo pi = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			version = pi.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			Log.e(TAG, "Version number not found in package", e);
		}
		return version;
	}
}
