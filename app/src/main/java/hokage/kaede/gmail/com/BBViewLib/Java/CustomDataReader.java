package hokage.kaede.gmail.com.BBViewLib.Java;

import hokage.kaede.gmail.com.StandardLib.Java.FileKeyValueStore;

/**
 * アセンデータをアセンファイルから読み込むクラス。
 */
public class CustomDataReader {

	public static final String SAVEKEY_CHIP_COUNT = "チップ数";
	public static final String SAVEKEY_CHIP = "チップ";
	
	/**
	 * カスタムデータ読み込み
	 * @param file_data ファイルデータ
	 * @param data_mng 各種データ
	 * @return 読み込んだカスタムデータ
	 */
	public static CustomData read(FileKeyValueStore file_data, FileKeyValueStore default_data, BBDataManager data_mng) {
		CustomData custom_data = new CustomData();
		
		// 各パーツデータを読み込む
		String[] parts_type = BBDataManager.BLUST_PARTS_LIST;
		int parts_len = parts_type.length;
		
		for(int i=0; i<parts_len; i++) {
			String parts_name = file_data.get(parts_type[i]);
			BBData parts_data = data_mng.getPartsData(parts_name, parts_type[i]);
			
			// データが見つからなかった場合はデフォルトデータを設定する。
			if(parts_data.id == BBData.ID_ITEM_NOTHING) {
				parts_name = default_data.get(parts_type[i]);
				parts_data = data_mng.getPartsData(parts_name, parts_type[i]);
			}

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
				String name = file_data.get(key);
				BBData weapon_data = data_mng.getWeaponData(name, blust_type[i], weapon_type[j]);
				
				// データが見つからなかった場合はデフォルトデータを設定する。
				if(weapon_data.id == BBData.ID_ITEM_NOTHING) {
					name = default_data.get(key);
					weapon_data = data_mng.getWeaponData(name, blust_type[i], weapon_type[j]);
				}
				
				custom_data.setData(weapon_data);
			}
		}
		
		// チップデータを読み込む
		int size = 0;
		try {
			size = Integer.valueOf(file_data.get(SAVEKEY_CHIP_COUNT));

		} catch(NumberFormatException e) {
			size = 0;
		}
		
		for(int i=0; i<size; i++) {
			String key = SAVEKEY_CHIP + String.format("%02d", i);
			String name = file_data.get(key);
			BBData chip_data = data_mng.getChipData(name);
			
			// チップデータが存在する場合、データを登録する。
			if(chip_data.id != BBData.ID_ITEM_NOTHING) {
				custom_data.addChip(chip_data);
			}
		}
		
		// 要請兵器のデータを読み込む
		String reqarm_name = file_data.get(BBDataManager.REQARM_STR);
		if(reqarm_name.equals(FileKeyValueStore.EMPTY_VALUE)) {
			reqarm_name = "バラム重機砲";  // デフォルト値
		}

		BBData reqarm_data = BBDataManager.getInstance().getReqArmData(reqarm_name);
		
		if(reqarm_data.id == BBData.ID_ITEM_NOTHING) {
			reqarm_name = default_data.get(BBDataManager.REQARM_STR);
			reqarm_data = BBDataManager.getInstance().getReqArmData(reqarm_name);
		}
		
		custom_data.setReqArm(reqarm_data);

		return custom_data;
	}
}
