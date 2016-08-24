package hokage.kaede.gmail.com.BBViewLib.Android;

import hokage.kaede.gmail.com.Lib.Android.PreferenceIO;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * BBView専用の設定データを管理するクラス。
 */
public class BBViewSettingManager extends SettingManager {
	
	// Xのデータ設定
	//public static boolean IS_VER_X_ON = false;

	/**
	 * 全ての設定値を読み込む。
	 * @param context コンテキスト
	 */
	public static void loadSettings(Context context) {
		IS_KB_PER_HOUR = isKbPerHour(context);
		IS_SHOW_COLUMN2 = isShowCOLUMN2(context);
		IS_SHOW_SPECLABEL = isShowSpecLabel(context);
		IS_SHOW_TYPELABEL = isShowTypeLabel(context);
		IS_SHOW_LISTBUTTON = isShowButton(context);
		IS_LISTBUTTON_TYPETEXT = isListButtonTypeText(context);
		IS_SHOW_HAVING = isHavingOnly(context);
		IS_MEMORY_SHOWSPEC = isMemoryShowSpec(context);
		IS_MEMORY_SORT = isMemorySort(context);
		IS_MEMORY_FILTER = isMemoryFilter(context);
		
		sThemeID = loadThemeID(context);
	}
	
	/**
	 * 移動速度の表示単位。trueの場合はkm/hで表示。falseの場合はm/secで表示。
	 */
	public static boolean IS_KB_PER_HOUR = false;
	public static final String SETTING_SPEED_VIEW_TYPE = "SETTING_SPEED_VIEW_TYPE";

	private static boolean isKbPerHour(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getBoolean(SETTING_SPEED_VIEW_TYPE, false);
	}

	/**
	 * アセン画面の2列表示設定。trueで表示する。
	 */
	public static boolean IS_SHOW_COLUMN2 = true;
	public static final String SETTING_SHOW_COLUMN2 = "SETTING_SHOW_COLUMN2";
	
	private static boolean isShowCOLUMN2(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getBoolean(SETTING_SHOW_COLUMN2, true);
	}

	/**
	 * アセン画面の性能表示設定。trueで表示する。
	 */
	public static boolean IS_SHOW_SPECLABEL = true;
	public static final String SETTING_SHOW_SPECLABEL = "SETTING_SHOW_SPECLABEL";
	
	private static boolean isShowSpecLabel(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getBoolean(SETTING_SHOW_SPECLABEL, true);
	}

	/**
	 * アセン画面の種類表示設定。trueで表示する。
	 */
	public static boolean IS_SHOW_TYPELABEL = true;
	public static final String SETTING_SHOW_TYPELABEL = "SETTING_SHOW_TYPELABEL";
	
	private static boolean isShowTypeLabel(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getBoolean(SETTING_SHOW_TYPELABEL, true);
	}

	/**
	 * パーツ武器選択画面のリストボタン表示設定。trueで表示する。
	 */
	public static boolean IS_SHOW_LISTBUTTON = true;
	public static final String SETTING_SHOW_LISTBUTTON = "SETTING_SHOW_LISTBUTTON";
	
	private static boolean isShowButton(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getBoolean(SETTING_SHOW_LISTBUTTON, true);
	}

	/**
	 * パーツ武器選択画面のリストボタン表示設定。trueで表示する。
	 */
	public static boolean IS_LISTBUTTON_TYPETEXT = false;
	public static final String SETTING_LISTBUTTON_TYPETEXT = "SETTING_LISTBUTTON_TYPETEXT";
	
	private static boolean isListButtonTypeText(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getBoolean(SETTING_LISTBUTTON_TYPETEXT, false);
	}
	
	/**
	 * 所持品表示設定。Trueで購入済み(開発済み)のみを表示する。
	 */
	public static boolean IS_SHOW_HAVING = false;
	public static final String SETTING_SHOW_HAVING_ONLY = "SETTING_SHOW_HAVING_ONLY";
	
	private static boolean isHavingOnly(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getBoolean(SETTING_SHOW_HAVING_ONLY, false);
	}

	/**
	 * 表示項目選択機能の状態維持設定。Trueで状態維持、Falseで状態破棄。
	 */
	public static boolean IS_MEMORY_SHOWSPEC = true;
	public static final String SETTING_MEMORY_SHOWSPEC = "SETTING_MEMORY_SHOWITEM";
	
	private static boolean isMemoryShowSpec(Context context) {
		return PreferenceIO.read(context, SETTING_MEMORY_SHOWSPEC, true);
	}

	/**
	 * ソート機能の状態維持設定。Trueで状態維持、Falseで状態破棄。
	 */
	public static boolean IS_MEMORY_SORT = false;
	public static final String SETTING_MEMORY_SORT = "SETTING_MEMORY_SORT";
	
	private static boolean isMemorySort(Context context) {
		return PreferenceIO.read(context, SETTING_MEMORY_SORT, false);
	}
	
	public static boolean IS_MEMORY_FILTER = false;
	public static final String SETTING_MEMORY_FILTER = "SETTING_MEMORY_FILTER";
	
	private static boolean isMemoryFilter(Context context) {
		return PreferenceIO.read(context, SETTING_MEMORY_FILTER, false);
	}
}
