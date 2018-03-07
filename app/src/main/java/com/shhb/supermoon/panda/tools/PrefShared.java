package com.shhb.supermoon.panda.tools;

import android.content.Context;

/**
 * Created by superMoon on 2017/7/31.
 */

public class PrefShared {

	public static void saveString(Context context, String key, String str) {
		context.getSharedPreferences("com.shhb.supermoon.panda", 0).edit()
				.putString(key, str).commit();
	}

	public static String getString(Context context, String key) {
		return context.getSharedPreferences("com.shhb.supermoon.panda", 0)
				.getString(key, null);
	}
	
	public static int saveInt(Context context, String key, int str) {
		context.getSharedPreferences("com.shhb.supermoon.panda", 0).edit()
				.putInt(key, str).commit();
		return str;
	}
	
	public static int getInt(Context context, String key){
		return context.getSharedPreferences("com.shhb.supermoon.panda", 0)
				.getInt(key, 0);
	}

	public static long saveLong(Context context, String key, long str) {
		context.getSharedPreferences("com.shhb.supermoon.panda", 0).edit()
				.putLong(key, str).commit();
		return str;
	}

	public static long getLong(Context context, String key){
		return context.getSharedPreferences("com.shhb.supermoon.panda", 0)
				.getLong(key, 0);
	}

	public static void removeData(Context context, String key) {
		context.getSharedPreferences("com.shhb.supermoon.panda", 0).edit()
				.remove(key).commit();
	}
}
