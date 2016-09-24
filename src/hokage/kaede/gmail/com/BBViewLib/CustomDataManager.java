package hokage.kaede.gmail.com.BBViewLib;

import hokage.kaede.gmail.com.BBViewLib.Android.BBViewSettingManager;
import hokage.kaede.gmail.com.Lib.Java.FileKeyValueStore;

/**
 * 現在編集中のアセンデータを管理するクラス。
 */
public class CustomDataManager {

	private static CustomData sTarget = new CustomData();	
	private static FileKeyValueStore sDefault;
	private static boolean sIsChanged = false;
	
	/**
	 * 現在編集中のカスタムデータを設定する。
	 * @param target 現在編集中のカスタムデータ。
	 */
	public static void setCustomData(CustomData target) {
		CustomDataManager.sTarget = target;
	}
	
	/**
	 * 現在編集中のカスタムデータを返す。
	 * @return 現在編集中のカスタムデータ。
	 */
	public static CustomData getCustomData() {
		boolean speed_unit = BBViewSettingManager.IS_KM_PER_HOUR;
		boolean data_speed_unit = sTarget.getSpeedUnit();
		
		if(speed_unit != data_speed_unit) {
			sTarget.setSpeedUnit(speed_unit);
		}
		return sTarget;
	}
	
	/**
	 * デフォルトのカスタムデータを設定する。
	 * @param custom_data カスタムデータ。
	 */
	public static void setDefaultData(FileKeyValueStore default_data) {
		sDefault = default_data;
	}
	
	/**
	 * デフォルトのカスタムデータを取得する。
	 * @return デフォルトのカスタムデータ。
	 */
	public static FileKeyValueStore getDefaultData() {
		return sDefault;
	}
	
	/**
	 * データを変更したかどうかを取得する。
	 * @return
	 */
	public static boolean isChanged() {
		return sIsChanged;
	}
	
	/**
	 * データを変更したかどうかを設定する。
	 * @param is_changed
	 */
	public static void setChanged(boolean is_changed) {
		sIsChanged = is_changed;
	}
}
