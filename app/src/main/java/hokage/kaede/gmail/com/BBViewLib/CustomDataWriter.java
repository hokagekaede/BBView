package hokage.kaede.gmail.com.BBViewLib;

import java.util.ArrayList;

import hokage.kaede.gmail.com.Lib.Java.FileKeyValueStore;

/**
 * カスタムデータをファイルに書き込むクラス。
 */
public class CustomDataWriter {
	
	public static void write(CustomData data, String directory) {
		FileKeyValueStore file_data = new FileKeyValueStore(directory);
		
		// パーツのデータを設定する。
		int parts_size = BBDataManager.BLUST_PARTS_LIST.length;
		for(int i=0; i<parts_size; i++) {
			String type = BBDataManager.BLUST_PARTS_LIST[i];
			file_data.set(type, data.getParts(type).get("名称"));
		}
		
		// 武器のデータを設定する。
		int blust_size = BBDataManager.BLUST_PARTS_LIST.length;
		int weapon_size = BBDataManager.WEAPON_TYPE_LIST.length;
		
		for(int i=0; i<blust_size; i++) {
			String blust_type = BBDataManager.BLUST_TYPE_LIST[i];
			
			for(int j=0; j<weapon_size; j++) {
				String weapon_type = BBDataManager.WEAPON_TYPE_LIST[j];
				file_data.set(blust_type + ":" + weapon_type, data.getWeapon(blust_type, weapon_type).get("名称"));
			}
		}

		// チップのデータを設定する。
		ArrayList<BBData> chip_list = data.getChips();
		int size = chip_list.size();
		
		for(int i=0; i<size; i++) {
			BBData chip_data = chip_list.get(i);
			String key = CustomDataReader.SAVEKEY_CHIP + String.format("%02d", i);
			String name = chip_data.get("名称");
			file_data.set(key, name);
		}
		
		file_data.set(CustomDataReader.SAVEKEY_CHIP_COUNT, String.valueOf(size));
		
		// 要請兵器のデータを設定する。
		file_data.set(BBDataManager.REQARM_STR, data.getReqArm().get("名称"));

		// ファイルに書き込む。
		file_data.save();
	}

}
