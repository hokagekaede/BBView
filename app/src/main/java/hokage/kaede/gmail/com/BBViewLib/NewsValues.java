package hokage.kaede.gmail.com.BBViewLib;

public class NewsValues {

	// 次の戦場の情報がある場合のリスト
	public static enum ITEM {
		CSTAGE_0,
		CSTAGE_1,
		CSTAGE_2,
		CSTAGE_3,
		CSTAGE_ROTS,
		CSTAGE_REQARM,
		CSTAGE_OTHER,
		CSTAGEH_0,
		CSTAGEH_1,
		CSTAGEH_2,
		CSTAGEH_3,
		CSTAGEH_ROTS,
		CSTAGEH_REQARM,
		CSTARGEH_OTHER,
		NSTAGE_0,
		NSTAGE_1,
		NSTAGE_2,
		NSTAGE_3,
		NSTAGE_ROTS,
		NSTAGE_REQARM,
		NSTAGE_OTHER,
		NSTAGEH_0,
		NSTAGEH_1,
		NSTAGEH_2,
		NSTAGEH_3,
		NSTAGEH_ROTS,
		NSTAGEH_REQARM,
		NSTAGEH_OTHER,
		NYEAR,
		NMONTH,
		NDAY,
		NWEEK,
		NEWS
	}

	// 次の戦場の情報がない場合のリスト
	private static enum ITEM_WITH_NO_NEXTMAP {
		CSTAGE_0,
		CSTAGE_1,
		CSTAGE_2,
		CSTAGE_3,
		CSTAGE_ROTS,
		CSTAGE_REQARM,
		CSTAGE_OTHER,
		CSTAGEH_0,
		CSTAGEH_1,
		CSTAGEH_2,
		CSTAGEH_3,
		CSTAGEH_ROTS,
		CSTAGEH_REQARM,
		CSTARGEH_OTHER,
		NSTAGE_OTHER,
		NSTAGEH_OTHER,
		NYEAR,
		NMONTH,
		NDAY,
		NWEEK,
		NEWS
	}
	
	/**
	 * 指定項目の番号を取得する。
	 * news.jsのreturnの数が変更になった場合、列挙パターンを増やし、本関数の処理を変更すること。
	 * @param item 指定項目。ITEMから選択する。
	 * @param size リストのサイズを取得する。
	 * @return 指定項目の番号。
	 */
	public static int indexOf(ITEM item, int size) {
		int ret = -1;
		String name = item.name();
		
		if(size == ITEM.values().length) {
			return item.ordinal();
		}
		else if(size == ITEM_WITH_NO_NEXTMAP.values().length) {
			ITEM_WITH_NO_NEXTMAP[] items = ITEM_WITH_NO_NEXTMAP.values();
			int length = items.length;
			
			for(int i=0; i<length; i++) {
				if(name.equals(items[i].name())) {
					ret = i;
					break;
				}
			}
		}

		return ret;
	}
}
