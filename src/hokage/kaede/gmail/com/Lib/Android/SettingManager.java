package hokage.kaede.gmail.com.Lib.Android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.preference.PreferenceManager;

/**
 * Androidアプリ共通データを管理するクラス
 */
public class SettingManager {

	//----------------------------------------------------------
	// フォントサイズのデータ管理
	//----------------------------------------------------------
	
	public static final String TEXT_SIZE_KEY = "FONTSIZE_KEY";
	
	private static final double TEXT_SIZE_BASE = 15;
	private static final int TEXT_SIZE_DEFAULT_INDEX = 3;
	private static final double[] TEXT_SIZE_LIST = { 1.6, 1.3, 1.0, 0.8, 0.6 };

	public static final String[] STR_TEXT_SIZE_LIST = { "特大", "大", "中", "小", "極小" };
	public static final String STR_TEXT_SIZE_DEFAULT = STR_TEXT_SIZE_LIST[TEXT_SIZE_DEFAULT_INDEX];
	
	public static final int FLAG_TEXTSIZE_NORMAL = 0;
	public static final int FLAG_TEXTSIZE_LARGE  = 1;
	public static final int FLAG_TEXTSIZE_SMALL  = 2;
	
	/**
	 * テキストのサイズを取得する。
	 * @param context
	 * @param base_size
	 * @param flag
	 * @return
	 */
	public static float getTextSize(Context context, int flag) {
		double ret = TEXT_SIZE_LIST[TEXT_SIZE_DEFAULT_INDEX];
		int size = STR_TEXT_SIZE_LIST.length;

		// 現在のフォントサイズの設定値を取得する
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String text_size_str = preferences.getString(TEXT_SIZE_KEY, STR_TEXT_SIZE_DEFAULT);
		
		for(int i=0; i<size; i++) {
			if(text_size_str.equals(STR_TEXT_SIZE_LIST[i])) {
				ret = TEXT_SIZE_LIST[i];
				break;
			}
		}
		
		// フラグに応じて拡大縮小を行う
		if(flag == FLAG_TEXTSIZE_NORMAL) {
			ret = TEXT_SIZE_BASE * ret;
		}
		else if(flag == FLAG_TEXTSIZE_LARGE) {
			ret = TEXT_SIZE_BASE * ret * 1.3;
		}
		else if(flag == FLAG_TEXTSIZE_SMALL) {
			ret = TEXT_SIZE_BASE * ret * 0.7;
		}
		else {
			ret = TEXT_SIZE_BASE * ret;
		}
		
		return (float)(ret);
	}
	
	//----------------------------------------------------------
	// テーマの設定管理
	//----------------------------------------------------------
	
	public static final String THEME_KEY = "THEME_KEY";
	public static final String[] THEME_LIST = { "Default", "Light", "Black" };
	public static final String THEME_DEFAULT = "Default";
	public static final int THEME_DEFAULT_ID = -1;
	public static int sThemeID = THEME_DEFAULT_ID;
	
	protected static int loadThemeID(Context context) {
		int ret = THEME_DEFAULT_ID;
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String theme_value = preferences.getString(THEME_KEY, THEME_DEFAULT);
		
		if(theme_value.equals("Light")) {
			ret = android.R.style.Theme_Holo_Light;
		}
		else if(theme_value.equals("Black")) {
			ret = android.R.style.Theme_Holo;
		}
		
		return ret;
	}
	
	public static int COLOR_BASE = 0;
	public static int COLOR_YELLOW = 1;
	public static int COLOR_RED = 2;
	public static int COLOR_BLUE = 3;
	
	private static int[] THEME_HOLO_PATTERN = {Color.WHITE, Color.YELLOW, Color.MAGENTA, Color.CYAN};
	private static int[] THEME_HOLO_LIGHT_PATTERN = {Color.BLACK, Color.rgb(0xFF, 0xA5, 0x00), Color.RED, Color.BLUE};
	
	
	/**
	 * 指定の文字色について、背景色に応じた色を返す。
	 * @return 文字色
	 */
	public static int getColor(int color_id) {
		if(sThemeID == android.R.style.Theme_Holo) {
			return THEME_HOLO_PATTERN[color_id];
		}
		else if(sThemeID == android.R.style.Theme_Holo_Light) {
			return THEME_HOLO_LIGHT_PATTERN[color_id];
		}
		
		return THEME_HOLO_PATTERN[color_id];
	}
	
	/**
	 * 白の色の設定値を取得する。
	 * @return 文字色
	 */
	public static int getColorWhite() {
		if(sThemeID == android.R.style.Theme_Holo) {
			return Color.WHITE;
		}
		else if(sThemeID == android.R.style.Theme_Holo_Light) {
			return Color.BLACK;
		}
		
		return Color.WHITE;
	}
	
	/**
	 * 黒の色の設定値を取得する。
	 * @return 文字色
	 */
	public static int getColorBlack() {
		if(sThemeID == android.R.style.Theme_Holo) {
			return Color.BLACK;
		}
		else if(sThemeID == android.R.style.Theme_Holo_Light) {
			return Color.WHITE;
		}
		
		return Color.BLACK;
	}
	
	/**
	 * 青の色の設定値を取得する。
	 * @return 文字色
	 */
	public static int getColorBlue() {
		if(sThemeID == android.R.style.Theme_Holo) {
			return Color.CYAN;
		}
		else if(sThemeID == android.R.style.Theme_Holo_Light) {
			return Color.BLUE;
		}
		
		return Color.CYAN;
	}
	
	//----------------------------------------------------------
	// バージョン情報管理
	//----------------------------------------------------------

	/**
	 * バージョン情報を管理する設定キー
	 */
	private static final String SETTING_IS_LAST_VERSIONCODE = "SETTING_IS_LAST_VERSIONCODE";
	
	/**
	 * 初回起動フラグを取得する。
	 * @return 初回起動の場合はtrueを返し、初回起動でない場合はfalseを返す。
	 */
	public static boolean isFirstFlag(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		int last_code = sp.getInt(SETTING_IS_LAST_VERSIONCODE, 0);
		
		if(last_code == 0) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * アップデートフラグを取得する。
	 * @param context 
	 * @return アップデート後の場合はtrueを返し、そうでない場合はfalseを返す。
	 */
	public static boolean isUpdateFlag(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		int last_code = sp.getInt(SETTING_IS_LAST_VERSIONCODE, 0);
		int recent_code = getVersionCode(context);
		
		if(last_code == recent_code) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 初回起動フラグ（現在のバージョンコード）を設定する。
	 */
	public static void setVersionCode(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		sp.edit().putInt(SETTING_IS_LAST_VERSIONCODE, getVersionCode(context)).commit();
	}
	
	/**
	 * バージョンコードを取得する。
	 * @return バージョンコード
	 */
	public static int getVersionCode(Context context) {
		int ret;
		
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
			ret = pi.versionCode;
			
		} catch (NameNotFoundException e) {
			ret = -1;
		}
		
		return ret;
	}
}
