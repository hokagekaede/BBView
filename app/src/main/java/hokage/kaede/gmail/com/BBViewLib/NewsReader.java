package hokage.kaede.gmail.com.BBViewLib;

import java.util.ArrayList;

import hokage.kaede.gmail.com.BBViewLib.NewsValues.ITEM;
import hokage.kaede.gmail.com.Lib.Java.NetAccess;

public class NewsReader {

	private static final String NEWS_URL = "http://borderbreak.com/js/news.js";

	// ニュースデータのロードが完了したかどうか
	private boolean mLoadFinish = false;

	// ニュースデータの値を格納したリスト
	private ArrayList<String> mNewsValueList = new ArrayList<String>();
	
	/**
	 * 情報取得済みかどうかを取得する。
	 * @return 情報取得済みの場合はtrueを返し、未取得の場合はfalseを返す。
	 */
	public boolean isFinish() {
		return mLoadFinish;
	}
	
	/**
	 * サテライトニュースの読み込み
	 * @return
	 */
	public void loadNews() {
		String news_data = NetAccess.readString(NEWS_URL, "UTF-8");
		
		if(news_data.equals("")) {
			return;
		}
		
		readReturnValue(news_data);
		
		mLoadFinish = true;
	}
	
	/**
	 * returnに設定されている文字列を全て取得し、リストに格納する。
	 * @param news_text
	 */
	private void readReturnValue(String news_text) {
		int start_idx = 0;
		int end_idx = 0;
		
		String buf = "";
		String return_str = "return";
		
		int size = NewsValues.ITEM.values().length;
		
		for(int i=0; i<size; i++) {
			start_idx = news_text.indexOf(return_str, end_idx);
			end_idx = getEndReturnIndex(news_text, start_idx);
			
			if(start_idx < 0) {
				break;
			}
			else if(start_idx > end_idx) {
				buf = news_text.substring(start_idx + return_str.length());
			}
			else {
				buf = news_text.substring(start_idx + return_str.length(), end_idx);
			}
			
			buf = buf.trim();
			buf = buf.replace("'", "");
			buf = buf.replace(";", "");
			buf = buf.replace(":", "");
			
			mNewsValueList.add(buf);
		}
	}
	
	/**
	 * return文の終端位置を取得する。
	 * @param news_text
	 * @param start_idx
	 * @return
	 */
	private int getEndReturnIndex(String news_text, int start_idx) {
		int ret = news_text.length();

		String[] end_target_str = { "case", "default", ";" };
		int size = end_target_str.length;
		
		for(int i=0; i<size; i++) {
			int tmp = news_text.indexOf(end_target_str[i], start_idx);
			
			if(tmp >= 0 && tmp < ret) {
				ret = tmp;
			}
		}
		
		return ret;
	}
	
	/**
	 * ENUM_NEWS_ITEM列挙子に応じたニュースデータを取得する。
	 * @param item
	 * @return
	 */
	public String get(ITEM item) {
		int size = mNewsValueList.size();
		int idx = NewsValues.indexOf(item, size);
		
		if(idx < 0 || idx >= mNewsValueList.size()) {
			return "";
		}
		
		return mNewsValueList.get(idx);
	}
}
