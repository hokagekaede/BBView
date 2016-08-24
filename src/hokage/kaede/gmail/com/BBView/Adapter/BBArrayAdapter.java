package hokage.kaede.gmail.com.BBView.Adapter;

import hokage.kaede.gmail.com.BBView.Adapter.BBAdapterCmdManager.IndexLayout;
import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.Android.BBViewSettingManager;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class BBArrayAdapter extends BaseAdapter {
	private boolean mIsKmPerHour;
	
	private Context mContext;
	
	private ArrayList<BBData> mList;
	private ArrayList<String> mShownKeys;
	private BBData mBaseItem;
	
	private BBAdapterCmdManager mCmdManager;
	
	private boolean mIsShowSwitch = false;
	private boolean mIsShowTypeB = false;
	
	private static final int BUTTON_LAYOUT_INDEX = 0;

	/**
	 * コンストラクタ
	 * @param context
	 * @param list
	 */
	public BBArrayAdapter(Context context, ArrayList<BBData> list) {
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
		this.mIsKmPerHour = preference.getBoolean(BBViewSettingManager.SETTING_SPEED_VIEW_TYPE, false);
		this.mContext = context;
		this.mList = list;
	}
	
	public void setBBAdapterCmdManager(BBAdapterCmdManager manager) {
		mCmdManager = manager;
	}
	
	/**
	 * スイッチ武器の情報を表示するかどうかの設定を行う。
	 * @param is_show_switch スイッチ武器の表示をする場合はtrueを設定する。
	 */
	public void setShowSwitch(boolean is_show_switch) {
		mIsShowSwitch = is_show_switch;
	}
	
	/**
	 * タイプBの性能を表示するかどうかの設定を行う。
	 * @param is_show_typeb タイプBを表示する場合はtrueを設定する。
	 */
	public void setShowTypeB(boolean is_show_typeb) {
		mIsShowTypeB = is_show_typeb;
	}
	
	/**
	 * リストのビューを取得する。
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BBData data = mList.get(position);
		BBArrayAdapterTextView view;

		if(convertView == null) {
			view = new BBArrayAdapterTextView(mContext, mShownKeys, mIsKmPerHour);
			view.setShowSwitch(mIsShowSwitch);
			view.setShowTypeB(mIsShowTypeB);
			view.createView();
			
			// ボタン設定ONの場合、ボタンのビューを追加する
			if(mCmdManager != null) {
				view.addView(mCmdManager.createButtonView(position), BUTTON_LAYOUT_INDEX);
			}
		}
		else {
			view = (BBArrayAdapterTextView)(convertView);
			view.setShowTypeB(mIsShowTypeB);
			view.setShownKeys(mShownKeys);

			if(mCmdManager != null) {
				IndexLayout button_view = (IndexLayout)view.getChildAt(BUTTON_LAYOUT_INDEX);
				button_view.update(position);
			}
		}
		
		view.setItem(data);
		view.updateView(mBaseItem);
		
		return (View)view;
	}
	
	/**
	 * 表示項目のフィルタを設定する
	 * @param filter_key
	 */
	public void setShownKeys(ArrayList<String> filter_key) {
		mShownKeys = filter_key;
	}
	
	/**
	 * 表示項目の一覧を取得する
	 * @return 表示項目の一覧
	 */
	public ArrayList<String> getShownKeys() {
		return mShownKeys;
	}
	
	/**
	 * 現在のリストデータを取得する
	 * @return 取得するリストデータ
	 */
	public ArrayList<BBData> getList() {
		return new ArrayList<BBData>(mList);
	}
	
	/**
	 * 現在のリスト項目の個数を取得する
	 */
	@Override
	public int getCount() {
		return mList.size();
	}

	/**
	 * 指定位置のリスト項目を取得する
	 */
	@Override
	public BBData getItem(int position) {
		if(position < 0 || position >= mList.size()) {
			return null;
		}
		
		return mList.get(position);
	}

	/**
	 * 指定位置のリスト項目IDを取得する
	 */
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	/**
	 * 項目を追加する
	 * @param item 追加する項目
	 */
	public void add(BBData item) {
		mList.add(item);
	}
	
	/**
	 * 項目を削除する
	 * @param item 削除する項目
	 */
	public void remove(BBData item) {
		mList.remove(item);
	}
	
	/**
	 * 項目を全削除する。
	 */
	public void clear() {
		mList.clear();
	}
	
	/**
	 * 項目の位置を取得する
	 * @param item 項目
	 * @return 項目の位置
	 */
	public int getPosition(BBData item) {
		return mList.indexOf(item);
	}
	
	/**
	 * リストを設定する。設定前のリストは破棄する。
	 * @param list 設定するリスト
	 */
	public void setList(ArrayList<BBData> list) {
		this.mList = list;
	}

	/**
	 * 現在選択中のデータを設定する。
	 * 選択中のデータ行はリスト表示時に文字色を変更する。
	 * @param item 現在選択中のデータ
	 */
	public void setBaseItem(BBData item) {
		this.mBaseItem = item;
	}
	
	/**
	 * 現在選択中のデータを取得する。
	 * @return 現在選択中のデータ
	 */
	public BBData getBaseItem() {
		return mBaseItem;
	}
	
	/**
	 * 現在選択中のデータの位置を取得する。
	 * @return 現在選択中のデータの位置
	 */
	public int getBaseItemIndex() {
		return mList.indexOf(mBaseItem);
	}
}
