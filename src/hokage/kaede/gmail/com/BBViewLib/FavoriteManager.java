package hokage.kaede.gmail.com.BBViewLib;

import hokage.kaede.gmail.com.Lib.Java.FileArrayList;

public class FavoriteManager {

	public static final String FAVORITE_CATEGORY_NAME = "お気に入り";
	
	private static final String HEAD_FILENAME = "favorite_head.txt";
	private static final String BODY_FILENAME = "favorite_body.txt";
	private static final String ARMS_FILENAME = "favorite_arms.txt";
	private static final String LEGS_FILENAME = "favorite_legs.txt";
	private static final String CHIP_FILENAME = "favorite_chips.txt";
	
	public static FileArrayList sHeadStore;
	public static FileArrayList sBodyStore;
	public static FileArrayList sArmsStore;
	public static FileArrayList sLegsStore;
	public static FileArrayList sChipStore;
	
	public static void init(String dir) {
		sHeadStore = new FileArrayList(dir, HEAD_FILENAME);
		sHeadStore.load();
		
		sBodyStore = new FileArrayList(dir, BODY_FILENAME);
		sBodyStore.load();
		
		sArmsStore = new FileArrayList(dir, ARMS_FILENAME);
		sArmsStore.load();
		
		sLegsStore = new FileArrayList(dir, LEGS_FILENAME);
		sLegsStore.load();
		
		sChipStore = new FileArrayList(dir, CHIP_FILENAME);
		sChipStore.load();
	}
	
	public static FileArrayList getStore(String type) {
		
		if(type.equals(BBDataManager.BLUST_PARTS_HEAD)) {
			return sHeadStore;
		}
		else if(type.equals(BBDataManager.BLUST_PARTS_BODY)) {
			return sBodyStore;
		}
		else if(type.equals(BBDataManager.BLUST_PARTS_ARMS)) {
			return sArmsStore;
		}
		else if(type.equals(BBDataManager.BLUST_PARTS_LEGS)) {
			return sLegsStore;
		}
		else if(type.equals(BBDataManager.CHIP_STR)) {
			return sChipStore;
		}
		
		return null;
	}
}
