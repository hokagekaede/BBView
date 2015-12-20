package hokage.kaede.gmail.com.BBView3;

import java.util.ArrayList;

import android.util.Log;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;

public class DebugManager {
	
	public static void showAllData() {
		BBDataManager mng = BBDataManager.getInstance();
		ArrayList<BBData> list = mng.getAllList();
		
		int size = list.size();
		for(int i=0; i<size; i++) {
			showBBData(list.get(i));
		}
	}
	
	public static void showBBData(BBData data) {
		ArrayList<String> keys = data.getKeys();
		ArrayList<String> values = data.getValues();
		ArrayList<String> categories = data.getCategorys();

		Log.e("check", "[DATA ID] " + data.id);
		
		int key_size = data.size();
		for(int i=0; i<key_size; i++) {
			Log.e("check", keys.get(i) + "=" + values.get(i));
		}
		
		int cat_size = categories.size();
		for(int i=0; i<cat_size; i++) {
			Log.e("check", "cat" + i + ":" + categories.get(i));
		}
	}

}
