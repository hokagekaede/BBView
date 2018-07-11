package hokage.kaede.gmail.com.BBViewLib.Java;

import hokage.kaede.gmail.com.StandardLib.Java.FileKeyValueStore;

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
		boolean speed_unit = BBViewSetting.IS_KM_PER_HOUR;
		boolean data_speed_unit = sTarget.getSpeedUnit();
		
		if(speed_unit != data_speed_unit) {
			sTarget.setSpeedUnit(speed_unit);
		}
		return sTarget;
	}
	
	/**
	 * デフォルトのカスタムファイルを設定する。
	 * @param default_data カスタムファイル。
	 */
	public static void setDefaultData(FileKeyValueStore default_data) {
		sDefault = default_data;
	}
	
	/**
	 * デフォルトのカスタムファイルを取得する。
	 * @return デフォルトのカスタムファイル。
	 */
	public static FileKeyValueStore getDefaultData() {
		return sDefault;
	}
	
	/**
	 * デフォルトのカスタムデータを取得する。
	 * @return デフォルトのカスタムデータ。
	 */
	public static CustomData getDefaultCustomData() {
		BBDataManager data_mng = BBDataManager.getInstance();
		CustomData custom_data = new CustomData();
		
		// 各パーツデータを読み込む
		String[] parts_type = BBDataManager.BLUST_PARTS_LIST;
		int parts_len = parts_type.length;
		
		for(int i=0; i<parts_len; i++) {
			String parts_name = sDefault.get(parts_type[i]);
			BBData parts_data = data_mng.getPartsData(parts_name, parts_type[i]);

			custom_data.setData(parts_data);
		}
		
		// 各武器データを読み込む
		String[] blust_type  = BBDataManager.BLUST_TYPE_LIST;
		String[] weapon_type = BBDataManager.WEAPON_TYPE_LIST;
		int blust_len  = blust_type.length;
		int weapon_len = weapon_type.length;
		
		for(int i=0; i<blust_len; i++) {
			for(int j=0; j<weapon_len; j++) {
				String key = blust_type[i] + ":" + weapon_type[j];
				String name = sDefault.get(key);
				BBData weapon_data = data_mng.getWeaponData(name, blust_type[i], weapon_type[j]);
				
				custom_data.setData(weapon_data);
			}
		}
		
		return custom_data;
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
