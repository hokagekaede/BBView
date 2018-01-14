package hokage.kaede.gmail.com.Lib.Java;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * キーと値によるデータ管理を行うクラス。
 */
public class KeyValueStore {
	private ArrayList<String> mKeys;
	private ArrayList<String> mValues;
	
	public static final String EMPTY_VALUE = "null";
	
	/**
	 * コンストラクタ
	 */
	public KeyValueStore() {
		mKeys   = new ArrayList<String>();
		mValues = new ArrayList<String>();
	}

	/**
	 * データの取得
	 * @param key
	 * @return
	 */
	public String get(String key) {
		String ret = EMPTY_VALUE;

		int idx = mKeys.indexOf(key);
		if(idx >= 0) {
			ret = mValues.get(idx);
		}
		
		return ret;
	}
	
	/**
	 * 指定位置の設定値を取得
	 * @param position
	 * @return
	 */
	public String get(int position) {
		if(position < mValues.size()) {
			return mValues.get(position);
		}
		
		return "";
	}
	
	/**
	 * データの格納
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		int find_idx = mKeys.indexOf(key);
		
		if(find_idx >= 0) {
			mKeys.set(find_idx, key);
			mValues.set(find_idx, value);
		}
		else {
			mKeys.add(key);
			mValues.add(value);
		}
	}
	
	/**
	 * 指定位置のキーを取得
	 * @param position
	 * @return
	 */
	public String getKey(int position) {
		if(position < mKeys.size()) {
			return mKeys.get(position);
		}
		
		return "";
	}

	/**
	 * キー/値の一覧の設定
	 * @param keys
	 * @param values
	 * @return キーと値を設定できた場合はtrueを返す。
	 * キーと値の個数が一致せず、設定できなかった場合はfalseを返す。
	 */
	public boolean setList(ArrayList<String> keys, ArrayList<String> values) {
		if(keys.size() != values.size()) {
			return false;
		}
		
		mKeys = new ArrayList<String>(keys);
		mValues = new ArrayList<String>(values);
		
		return true;
	}
	
	/**
	 * キー/値の一覧を設定する。
	 * @param keys キーの一覧
	 * @param values 値の一覧
	 * @return キーと値を設定できた場合はtrueを返す。
	 * キーと値の個数が一致せず、設定できなかった場合はfalseを返す。
	 */
	public boolean setList(String[] keys, String[] values) {
		if(keys.length != values.length) {
			return false;
		}
		
		mKeys = new ArrayList<String>(Arrays.asList(keys));
		mValues = new ArrayList<String>(Arrays.asList(values));
		
		return true;
	}
	/**
	 * 全キーの取得
	 * @return
	 */
	public ArrayList<String> getKeys() {
		return new ArrayList<String>(mKeys);
	}
	
	/**
	 * 全設定値の取得
	 * @return
	 */
	public ArrayList<String> getValues() {
		return new ArrayList<String>(mValues);
	}
	
	/**
	 * リストのサイズ
	 * @return
	 */
	public int size() {
		return mKeys.size();
	}
	
	/**
	 * データの削除
	 * @param key
	 */
	public void remove(String key) {
		int idx = mKeys.indexOf(key);
		
		if(idx >= 0) {
			mKeys.remove(idx);
			mValues.remove(idx);
		}
	}
	
	/**
	 * データのクリア
	 */
	public void clear() {
		mKeys.clear();
		mValues.clear();
	}
	
	/**
	 * キーの有無を取得
	 * @param key
	 * @return
	 */
	public boolean existKey(String key) {
		return mKeys.contains(key);
	}
	
	/**
	 * キーの格納位置を取得する。
	 * @param key キー。
	 * @return 格納位置。
	 */
	public int indexOf(String key) {
		return mKeys.indexOf(key);
	}

}
