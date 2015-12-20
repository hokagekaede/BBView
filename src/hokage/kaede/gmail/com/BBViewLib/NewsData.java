package hokage.kaede.gmail.com.BBViewLib;

import java.text.SimpleDateFormat;
import java.util.Date;

import hokage.kaede.gmail.com.Lib.Java.FileKeyValueStore;

public class NewsData {

	// ニュースの時刻フォーマット
	private static final SimpleDateFormat SDF = new SimpleDateFormat("M月d日 H時m分");
	
	private static final String NEWS_FILENAME = "satellite_news.dat";
	
	private static final String SUCCESS_KEY         = "SUCCESS_KEY";
	private static final String GET_DATE_KEY        = "GET_DATE_KEY";
	
	private static final String HIGH_RECENT_MAP_KEY = "HIGH_RECENT_MAP_KEY";
	private static final String HIGH_NEXT_MAP_KEY   = "HIGH_NEXT_MAP_KEY";
	private static final String LOW_RECENT_MAP_KEY  = "LOW_RECENT_MAP_KEY";
	private static final String LOW_NEXT_MAP_KEY    = "LOW_NEXT_MAP_KEY";
	private static final String NEXT_DATE_KEY       = "NEXT_DATE_KEY";
	private static final String NEWS_KEY            = "NEWS_KEY";
	
	private static final String SUCCESS_VALUE = "success";
	
	private FileKeyValueStore mStore;

	/**
	 * 初期化処理を行う。
	 * @param dirname
	 */
	public NewsData(String dirname) {
		mStore = new FileKeyValueStore(dirname, NEWS_FILENAME);
		mStore.setEncode("UTF-8");
		mStore.load();
	}
	
	/*+
	 * サテライトニュースを取得する。ワーカースレッドで実行しないこと。
	 */
	public boolean read() {
		NewsReader news_reader = new NewsReader();
		news_reader.loadNews();
		
		boolean ret = news_reader.isFinish();

		if(ret) {
			mStore.set(GET_DATE_KEY, SDF.format(new Date()));
			mStore.set(SUCCESS_KEY, SUCCESS_VALUE);
			mStore.set(HIGH_RECENT_MAP_KEY, news_reader.get(NewsValues.ITEM.CSTAGEH_0));
			mStore.set(HIGH_NEXT_MAP_KEY, news_reader.get(NewsValues.ITEM.NSTAGEH_0));
			mStore.set(LOW_RECENT_MAP_KEY, news_reader.get(NewsValues.ITEM.CSTAGE_0));
			mStore.set(LOW_NEXT_MAP_KEY, news_reader.get(NewsValues.ITEM.NSTAGE_0));
			mStore.set(NEWS_KEY, news_reader.get(NewsValues.ITEM.NEWS));

			String year  = news_reader.get(NewsValues.ITEM.NYEAR);
			String month = news_reader.get(NewsValues.ITEM.NMONTH);
			String day   = news_reader.get(NewsValues.ITEM.NDAY);
			
			// 日付データがない場合はキーを削除する。
			if(year.equals("0")) {
				mStore.remove(NEXT_DATE_KEY);
			}
			else {
				mStore.set(NEXT_DATE_KEY, year + "/" + month + "/" + day);
			}
			
			mStore.save();
		}
		
		return ret;
	}
	
	public boolean isSuccess() {
		String value = mStore.get(SUCCESS_KEY);
		if(SUCCESS_VALUE.equals(value)) {
			return true;
		}
		
		return false;
	}
	
	public String getDate() {
		return checkValue(mStore.get(GET_DATE_KEY));
	}
	
	public String getHighRecentMapName() {
		return checkValue(mStore.get(HIGH_RECENT_MAP_KEY));
	}

	public String getHighNextMapName() {
		return checkValue(mStore.get(HIGH_NEXT_MAP_KEY));
	}

	public String getLowRecentMapName() {
		return checkValue(mStore.get(LOW_RECENT_MAP_KEY));
	}

	public String getLowNextMapName() {
		return checkValue(mStore.get(LOW_NEXT_MAP_KEY));
	}

	public String getNextDate() {
		if(mStore.get(NEXT_DATE_KEY).equals(FileKeyValueStore.EMPTY_VALUE)) {
			return "未定";
		}
		
		return mStore.get(NEXT_DATE_KEY) + "～";
	}

	public String getNews() {
		return checkValue(mStore.get(NEWS_KEY));
	}
	
	private String checkValue(String value) {
		if(value.equals(FileKeyValueStore.EMPTY_VALUE)) {
			return "データがありません。";
		}

		return value;
	}
}
