package hokage.kaede.gmail.com.BBViewLib;

import hokage.kaede.gmail.com.Lib.Java.FileIO;

import java.io.InputStream;

/**
 * BBデータテキストファイルからデータを読み込むクラス。
 */
public class BBDataReader {
	
	private static final String NEWLINE = System.getProperty("line.separator");
	
	private static final String CATEGORY_STR = "■";
	private static final String SUB_CATEGORY_STR = "◆";
	private static final String WEAPON_TYPE_CATEGORY_STR = "●";
	private static final String TITLE_STR = "○";
	private static final String SPLIT_STR = "\t";
	
	/**
	 * 各種データを読み込む。
	 * パーツ、武器、チップ、マップ、素材、勲章のデータを読み込む。
	 * @param is 読み込む入力ストリーム。
	 * @return 
	 */
	public static final void read(InputStream is) {
		BBDataManager data_mng = BBDataManager.getInstance();
		
		String bb_data_str = FileIO.readInputStream(is, FileIO.ENCODE_SJIS);
		String[] bb_data_list = bb_data_str.split(NEWLINE);

		int size = bb_data_list.length;

		String buf = null;
		String category1 = null;
		String category2 = null;
		String category3 = null;
		String[] keys = null;
		String[] values = null;
		int count = 0;

		for(int i=0; i<size; i++) {
			buf = bb_data_list[i];
			
			if(buf.startsWith(CATEGORY_STR)) {
				category1 = buf.substring(1);
				category2 = null;
				category3 = null;
			}
			else if(buf.startsWith(SUB_CATEGORY_STR)) {
				category2 = buf.substring(1);
				category3 = null;
			}
			else if(buf.startsWith(WEAPON_TYPE_CATEGORY_STR)) {
				category3 = buf.substring(1);
			}
			else if(buf.startsWith(TITLE_STR)) {
				buf = buf.substring(1);
				keys = buf.split(SPLIT_STR);
			}
			else if(buf.indexOf(SPLIT_STR) > 0) {
				values = buf.split(SPLIT_STR);
				
				BBData data_buf = new BBData();
				if(category1 != null) { data_buf.addCategory(category1); }
				if(category2 != null) { data_buf.addCategory(category2); }
				if(category3 != null) { data_buf.addCategory(category3); }
				data_buf.setList(keys, values);
				data_buf.id = count;
				count++;
				
				data_mng.add(data_buf);
			}
			else {
				// DO NOTHING
			}
		}
	}
}
