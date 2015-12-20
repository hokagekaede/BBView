package hokage.kaede.gmail.com.Lib.Android;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 文字列のリスト向けアダプタクラス
 */
public class StringAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<String> mFileList;
	
	/**
	 * 初期化処理を行う。空リストを生成する。
	 * @param context リストを表示する画面
	 */
	public StringAdapter(Context context) {
		this.mContext = context;
		this.mFileList = new ArrayList<String>();
	}

	/**
	 * リストの登録数を返す。
	 * @return リストの登録数
	 */
	@Override
	public int getCount() {
		return mFileList.size();
	}

	/**
	 * リストのデータを取得する。
	 * @param 取得するデータの位置
	 * @return リストのデータ
	 */
	@Override
	public String getItem(int position) {
		if(position < 0 || position >= mFileList.size()) {
			return null;
		}
		
		return mFileList.get(position);
	}
	
	/**
	 * リストの指定データを削除する。
	 * @param value 削除するデータ
	 */
	public void removeItem(String value) {
		int idx = mFileList.indexOf(value);
		
		if(idx >= 0) {
			mFileList.remove(idx);
		}
	}
	
	/**
	 * リストの指定データを置き換える。
	 * @param from_value 置き換え前のデータ
	 * @param to_value 置き換え後のデータ
	 */
	public void replaceItem(String from_value, String to_value) {
		int idx = mFileList.indexOf(from_value);

		if(idx >= 0) {
			mFileList.set(idx, to_value);
		}
	}

	/**
	 * IDを取得する。
	 */
	@Override
	public long getItemId(int position) {
		return 0;
	}

	/**
	 * ビューを取得する。
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView list_item_view = new TextView(mContext);
		list_item_view.setText(mFileList.get(position));
		list_item_view.setPadding(10, 10, 10, 10);
		list_item_view.setTextSize(SettingManager.getTextSize(mContext, SettingManager.FLAG_TEXTSIZE_NORMAL));
		
		return list_item_view;
	}
	
	/**
	 * ファイル名を追加する
	 * @param filename 追加するファイル名(ファイルタグ)
	 */
	public void add(String filename) {
		mFileList.add(filename);
	}
}
