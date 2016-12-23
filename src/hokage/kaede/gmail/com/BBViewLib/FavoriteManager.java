package hokage.kaede.gmail.com.BBViewLib;

import hokage.kaede.gmail.com.Lib.Java.FileArrayList;

public class FavoriteManager {
	
	// お気に入りリストのデータを格納するファイル
	private static final String FAVORITE_CHIP_FILENAME = "favorite_chips.txt";
	public static FileArrayList sFavoriteStore;
	
	public static void init(String dir) {
		sFavoriteStore = new FileArrayList(dir, FAVORITE_CHIP_FILENAME);
		sFavoriteStore.load();
	}
}
