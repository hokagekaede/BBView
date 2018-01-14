package hokage.kaede.gmail.com.BBViewLib.Adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBViewSetting;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;

/**
 * カテゴリ表示用のアダプタのベースクラス
 */
public abstract class BBExpandableAdapter extends BaseExpandableListAdapter {

	private ArrayList<String> mGroupNameList;
	private ArrayList<ArrayList<BBData>> mGroupDataList;
	
	private static final int GROUP_ID_BASE = 1000;
	
	/**
	 * 初期化処理を行う。
	 */
	public BBExpandableAdapter() {
		mGroupNameList = new ArrayList<String>();
		mGroupDataList = new ArrayList<ArrayList<BBData>>();
	}
	
	/**
	 * グループのビューを取得する。
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); 
		
		Context context = parent.getContext();
		TextView text_view = (TextView)convertView;
		
		if(text_view == null) {
			text_view = new TextView(context);
		}
		
		text_view.setText(mGroupNameList.get(groupPosition));
		text_view.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		text_view.setTextSize(BBViewSetting.getTextSize(context, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
		text_view.setPadding(0, 15, 0, 15);
		text_view.setTextColor(SettingManager.getColorWhite());
		text_view.setBackgroundColor(SettingManager.getColorBlue());
		text_view.setLayoutParams(lp);

		return text_view;
	}

	/**
	 * データのビューを取得する。
	 */
	@Override
	abstract public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent);
	
	/**
	 * グループを追加する。
	 * @param name グループ名
	 * @return 追加した位置。
	 */
	public void addGroup(String name) {
		mGroupNameList.add(name);
		mGroupDataList.add(new ArrayList<BBData>());
	}
	
	/**
	 * グループの位置を取得する
	 * @param name グループ名
	 * @return グループの位置を返す。グループが存在しない場合は-1を返す。
	 */
	public int indexOfGroup(String name) {
		return mGroupNameList.indexOf(name);
	}
	
	/**
	 * データを追加する。
	 * @param groupPosition グループの位置
	 * @param data 追加するデータ
	 */
	public void addChild(int groupPosition, BBData data) {

		int size = mGroupDataList.size();
		if(groupPosition < 0 || groupPosition >= size) {
			return;
		}

		mGroupDataList.get(groupPosition).add(data);
	}
	
	/**
	 * データの位置を取得する。
	 * @param groupPosition グループの位置
	 * @param data 対象のデータ
	 * @return データの位置を返す。データが存在しない場合は-1を返す。
	 */
	public int indexOfChild(int groupPosition, BBData data) {
		int size = mGroupDataList.size();
		if(groupPosition < 0 || groupPosition >= size) {
			return -1;
		}
		
		return mGroupDataList.get(groupPosition).indexOf(data);
	}

	/**
	 * 項目を削除する。
	 * @param groupPosition グループの位置
	 * @param item 削除する項目
	 */
	public void removeChild(int groupPosition, BBData data) {

		int size = mGroupDataList.size();
		if(groupPosition < 0 || groupPosition >= size) {
			return;
		}
		
		mGroupDataList.get(groupPosition).remove(data);
	}

	/**
	 * 項目を削除する。
	 * @param groupPosition グループの位置
	 * @param childPosition データの位置
	 */
	public void removeChild(int groupPosition, int childPosotion) {
		int size = mGroupDataList.size();
		if(groupPosition < 0 || groupPosition >= size) {
			return;
		}
		
		mGroupDataList.get(groupPosition).remove(childPosotion);
	}
	
	/**
	 * 項目を全削除する。
	 */
	public void clear() {
		int size = mGroupDataList.size();
		for(int i=0; i<size; i++) {
			mGroupDataList.get(i).clear();
		}

		mGroupNameList.clear();
		mGroupDataList.clear();
	}
	
	/**
	 * 全てのグループから項目を全削除する。
	 * グループは削除しない。
	 */
	public void clearChildrenAll() {
		int size = mGroupDataList.size();
		for(int i=0; i<size; i++) {
			mGroupDataList.get(i).clear();
		}
	}
	
	/**
	 * グループの数を取得する。
	 * @return グループの数
	 */
	@Override
	public int getGroupCount() {
		return mGroupDataList.size();
	}

	/**
	 * データの数を取得する。
	 * @param groupPosition グループの位置
	 * @return データの数
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		
		int size = mGroupDataList.size();
		if(groupPosition < 0 || groupPosition >= size) {
			return -1;
		}
		
		return mGroupDataList.get(groupPosition).size();
	}

	/**
	 * グループを取得する。
	 * @return グループ名
	 */
	@Override
	public String getGroup(int groupPosition) {
		return mGroupNameList.get(groupPosition);
	}

	/**
	 * データを取得する。
	 * @param groupPosition グループの位置
	 * @param childPosition データの位置
	 * @return データ
	 */
	@Override
	public BBData getChild(int groupPosition, int childPosition) {

		int size = mGroupDataList.size();
		if(groupPosition < 0 || groupPosition >= size) {
			return null;
		}
		
		return mGroupDataList.get(groupPosition).get(childPosition);
	}

	/**
	 * データのIDを取得する。
	 * @param groupPosition グループの位置
	 * @param childPosition データの位置
	 * @return ID値
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return groupPosition * GROUP_ID_BASE + childPosition;
	}

	/**
	 * グループのIDを取得する。未使用のため、0を返す。
	 * @param groupPosition グループの位置
	 * @return ID値
	 */
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition * GROUP_ID_BASE;
	}

	/**
	 * ID値からグループ位置を取得する。
	 * @param id ID値
	 * @return グループ位置
	 */
	protected int getGroupPositionFromID(int id) {
		return (int)(id / GROUP_ID_BASE);
	}
	
	/**
	 * ID値からデータの位置を取得する。
	 * @param id ID値
	 * @return データの位置
	 */
	protected int getChildPositionFromID(int id) {
		return id % GROUP_ID_BASE;
	}

	/**
	 * アイテムIDが一定かどうか。
	 * @return false固定。(不確定固定)
	 */
	@Override
	public boolean hasStableIds() {
		return false;
	}

	/**
	 * データが選択可能かどうか。
	 * @return false固定。(選択不可固定)
	 */
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
}
