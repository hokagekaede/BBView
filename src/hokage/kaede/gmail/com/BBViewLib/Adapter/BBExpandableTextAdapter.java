package hokage.kaede.gmail.com.BBViewLib.Adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.FavoriteManager;
import hokage.kaede.gmail.com.BBViewLib.SpecValues;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBAdapterCmdManager.IndexLayout;
import hokage.kaede.gmail.com.Lib.Java.FileArrayList;

/**
 * パーツをカテゴリ表示するアダプタクラス
 */
public class BBExpandableTextAdapter extends BBExpandableAdapter {

	private ArrayList<String> mShownKeys;
	private BBData mBaseItem;
	
	private BBAdapterCmdManager mCmdManager;	
	private FileArrayList mFavStore;
	
	private boolean mIsShowParts = false;
	private boolean mIsShowSwitch = false;
	private boolean mIsShowTypeB = false;

	private static final int BUTTON_LAYOUT_INDEX = 0;
	
	/**
	 * コンストラクタ。リストを初期化する。
	 */
	public BBExpandableTextAdapter(boolean is_parts) {
		mIsShowParts = is_parts;
	}
	
	/**
	 * パーツカテゴリ表示時の初期化処理を行う。
	 * カテゴリにシリーズを追加する。
	 */
	public void initParts() {
		ArrayList<String> series_list = SpecValues.SETBONUS.getKeys();
		int series_count = series_list.size();
		
		for(int i=0; i<series_count; i++) {
			addGroup(series_list.get(i));
		}
		
		addGroup(FavoriteManager.FAVORITE_CATEGORY_NAME);
	}

	/**
	 * 武器カテゴリ表示時の初期化処理を行う。
	 * カテゴリにシリーズを追加する。
	 * 
	 * 引数が違うのでコンストラクタ内の分岐処理による実装は不可。
	 */
	public void initWeapon(String blust_type, String weapon_type) {
		ArrayList<String> series_list = SpecValues.getWeaponSeiresList(blust_type, weapon_type);
		int series_count = series_list.size();
		
		for(int i=0; i<series_count; i++) {
			addGroup(series_list.get(i));
		}
		
		addGroup(FavoriteManager.FAVORITE_CATEGORY_NAME);
	}
	
	/**
	 * お気に入りリストのストアを設定する。
	 * @param store
	 */
	public void setFavStore(FileArrayList store) {
		mFavStore = store;
	}
	
	/**
	 * データのビューを取得する。
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		Context context = parent.getContext();
		BBData data = super.getChild(groupPosition, childPosition);
		BBArrayAdapterTextView view;

		if(convertView == null) {
			view = new BBArrayAdapterTextView(context, mShownKeys);
			view.setFavoriteStore(mFavStore);
			view.setShowSwitch(mIsShowSwitch);
			view.setShowTypeB(mIsShowTypeB);
			view.createView();
			
			// ボタン設定ONの場合、ボタンのビューを追加する
			if(mCmdManager != null) {
				IndexLayout button_view = mCmdManager.createButtonView(context);
				button_view.update(data);
				view.addView(button_view, BUTTON_LAYOUT_INDEX);
			}
		}
		else {
			view = (BBArrayAdapterTextView)(convertView);
			view.setShowTypeB(mIsShowTypeB);
			view.setShownKeys(mShownKeys);

			if(mCmdManager != null) {
				IndexLayout button_view = (IndexLayout)view.getChildAt(BUTTON_LAYOUT_INDEX);
				button_view.update(data);
			}
		}
		
		view.setItem(data);
		view.setBaseItem(mBaseItem);
		view.updateView();
		view.setOnClickFavListener(new OnClickFavListener(data));
		
		return view;
	}
	
	/**
	 * コマンドボタンを設定する。
	 * @param manager コマンドボタンのデータ
	 */
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
	 * 表示項目のフィルタを設定する。
	 * @param filter_key 表示項目のフィルタ
	 */
	public void setShownKeys(ArrayList<String> filter_key) {
		mShownKeys = filter_key;
	}
	
	/**
	 * 表示項目の一覧を取得する。
	 * @return 表示項目の一覧
	 */
	public ArrayList<String> getShownKeys() {
		return mShownKeys;
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
	 * データを追加する。
	 * @param item 追加するデータ
	 */
	public void addChild(BBData item) {
		if(this.mIsShowParts) {
			addChildParts(item);
		}
		else {
			addChildWeapon(item);
		}
	}
	
	/**
	 * パーツデータを追加する。
	 * @param item 追加するパーツ
	 */
	private void addChildParts(BBData item) {
		String name = item.get("名称");
		
		// ブランド名と同じ位置に格納する。
		int size = getGroupCount();
		for(int i=0; i<size-1; i++) {
			String brand_name = getGroup(i);
			if(name.startsWith(brand_name)) {
				addChild(i, item);
			}
		}
		
		// お気に入りリストに格納されているチップを追加する。
		if(mFavStore != null && mFavStore.exist(name)) {
			addChild(size - 1, item);
		}
	}
	
	/**
	 * 武器データを追加する。
	 * @param item 追加する武器
	 */
	private void addChildWeapon(BBData item) {
		String name = item.get("名称");
		
		// ブランド名と同じ位置に格納する。
		int size = getGroupCount();
		for(int i=0; i<size-1; i++) {
			String series_name = getGroup(i);
			
			if(item.isSwitchWeapon()) {
				series_name = series_name + "(タイプA)";
			}

			if(item.existCategory(series_name)) {
				addChild(i, item);
			}
		}
		
		// お気に入りリストに格納されているチップを追加する。
		if(mFavStore != null && mFavStore.exist(name)) {
			addChild(size - 1, item);
		}
	}
	
	/**
	 * データのリストを追加する。
	 * @param chiplist チップのリスト
	 */
	public void addChildren(ArrayList<BBData> itemlist) {
		int size = itemlist.size();
		for(int i=0; i<size; i++) {
			addChild(itemlist.get(i));
		}
	}
	
	/**
	 * 項目を全削除する。
	 */
	@Override
	public void clear() {
		super.clear();
		mShownKeys.clear();
	}

	/**
	 * データが選択可能かどうか。
	 * @return true固定。(選択可能固定)
	 */
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	/**
	 * Favoriteボタンを押下した場合の処理を行う。
	 * お気に入りの設定状況に応じて、リストへの追加/削除を行う。
	 * また、お気に入りリストのファイルも更新する。
	 */
	private class OnClickFavListener implements OnClickListener {
		private BBData target;
		
		public OnClickFavListener(BBData data) {
			target = data;
		}

		@Override
		public void onClick(View view) {
			String name = target.get("名称");
			
			if(mFavStore == null) {
				return;
			}
			
			int index = mFavStore.indexOf(name);
			
			int fav_group_idx = indexOfGroup(FavoriteManager.FAVORITE_CATEGORY_NAME);
			
			if(index > -1) {
				mFavStore.remove(index);

				int fav_index = indexOfChild(fav_group_idx, target);
				removeChild(fav_group_idx, fav_index);
			}
			else {
				mFavStore.add(name);
				
				addChild(fav_group_idx, target);
			}
			
			mFavStore.save();
			
			BBExpandableTextAdapter.this.notifyDataSetChanged();
		}
	}
}
