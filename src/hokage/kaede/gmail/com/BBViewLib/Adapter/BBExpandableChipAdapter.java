package hokage.kaede.gmail.com.BBViewLib.Adapter;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.CustomData;
import hokage.kaede.gmail.com.BBViewLib.CustomDataManager;
import hokage.kaede.gmail.com.BBViewLib.FavoriteManager;

/**
 * チップをカテゴリ表示するアダプタクラス
 */
public class BBExpandableChipAdapter extends BBExpandableAdapter {

	private CustomData mCustomData;
	
	// CheckBoxのフラグ管理を行うリスト
	private ArrayList<ArrayList<Boolean>> mGroupCheckList;

	private OnChengedChipSetBaseListener mChengedChipSetListener;
	
	private static final int SKILL_CHIP_CATEGORY_INDEX = 0;
	private static final int POWERUP_CHIP_CATEGORY_INDEX = 1;
	private static final int ACTION_CHIP_CATEGORY_INDEX = 2;
	private static final int FAVORITE_CHIP_CATEGORY_INDEX = 3;
	
	/**
	 * コンストラクタ。リストを初期化する。
	 */
	public BBExpandableChipAdapter() {
		mCustomData = CustomDataManager.getCustomData();
		mGroupCheckList = new ArrayList<ArrayList<Boolean>>();
		
		super.addGroup(BBDataManager.SKILL_CHIP_STR);
		mGroupCheckList.add(new ArrayList<Boolean>());
		
		super.addGroup(BBDataManager.POWERUP_CHIP_STR);
		mGroupCheckList.add(new ArrayList<Boolean>());
		
		super.addGroup(BBDataManager.ACTION_CHIP_STR);
		mGroupCheckList.add(new ArrayList<Boolean>());

		super.addGroup(FavoriteManager.FAVORITE_CATEGORY_NAME);
		mGroupCheckList.add(new ArrayList<Boolean>());
	}

	/**
	 * データのビューを取得する。
	 * @param groupPosition グループの位置
	 * @param childPosition データの位置
	 * @param isLastChild
	 * @param convertView データのビュー
	 * @param parent グループのビュー
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		BBData chipdata = super.getChild(groupPosition, childPosition);

		BBArrayAdapterChipView checkbox = (BBArrayAdapterChipView)convertView;
		
		if(checkbox == null) {
			checkbox = new BBArrayAdapterChipView(parent.getContext(), null);
			checkbox.createView();
			checkbox.setFavoriteStore(FavoriteManager.sChipStore);
		}
		
		checkbox.setId((int)getChildId(groupPosition, childPosition));
		checkbox.setItem(chipdata);
		checkbox.updateView();
		checkbox.setOnCheckedChangeListener(null);
		checkbox.setChecked(getFlag(groupPosition, childPosition));
		checkbox.setOnCheckedChangeListener(new OnCheckedFlagListener());
		
		checkbox.setOnClickFavListener(new OnClickFavListener(chipdata));
		
		return checkbox;
	}
	
	/**
	 * チップのチェックボックスを変更した場合の処理を行うリスナー
	 */
	private class OnCheckedFlagListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			int id = buttonView.getId();
			int group_position = getGroupPositionFromID(id);
			int child_position = getChildPositionFromID(id);
			
			// フラグ状態を設定する
			setFlag(group_position, child_position, isChecked);
			
			BBData target_item = getChild(group_position, child_position);
			
			if(isChecked) {
				mCustomData.addChip(target_item);
			}
			else {
				mCustomData.removeChip(target_item);
			}
			
			mChengedChipSetListener.changed();
			
			// 通常のリストとFavoriteリストのチェック状態を同期させるために、アダプタを更新する。
			BBExpandableChipAdapter.this.notifyDataSetChanged();
		}
	}
	
	/**
	 * チップの設定状態が更新された際の処理を行うリスナー。
	 */
	public abstract interface OnChengedChipSetBaseListener {
		public abstract void changed();
	}
	
	/**
	 * チップの設定状態が更新された際の処理を行うリスナーを設定する。
	 * @param listener リスナーのインスタンス
	 */
	public void setOnChengedChipSetListener(OnChengedChipSetBaseListener listener) {
		mChengedChipSetListener = listener;
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
			
			int index = FavoriteManager.sChipStore.indexOf(name);
			
			if(index > -1) {
				FavoriteManager.sChipStore.remove(index);

				int fav_index = indexOfChild(FAVORITE_CHIP_CATEGORY_INDEX, target);
				removeChild(FAVORITE_CHIP_CATEGORY_INDEX, fav_index);
				mGroupCheckList.get(FAVORITE_CHIP_CATEGORY_INDEX).remove(fav_index);
			}
			else {
				FavoriteManager.sChipStore.add(name);
				
				addChild(FAVORITE_CHIP_CATEGORY_INDEX, target);
				mGroupCheckList.get(FAVORITE_CHIP_CATEGORY_INDEX).add(mCustomData.existChip(name));
			}
			
			FavoriteManager.sChipStore.save();
			
			BBExpandableChipAdapter.this.notifyDataSetChanged();
		}
	}

	/**
	 * 選択フラグを設定する。
	 * @param groupPosition 親のアイテム位置
	 * @param position 子のアイテム位置
	 * @param flag 設定するフラグ値
	 */
	public void setFlag(int groupPosition, int childPosition, boolean flag) {
		BBData changed_chip = super.getChild(groupPosition, childPosition);
		setFlag(changed_chip, flag);
	}
	
	/**
	 * 選択フラグを設定する。
	 * 
	 * お気に入りリストからの変更がある都合により、
	 * 指定位置のチェックを変更する方法ではチェック状況が同期できない。
	 * そのため、ポジション値は完全に無視することとし、
	 * 全リストの中身を確認し、対象のチップを探し出してチェックを変更する方式を採用する。
	 * 
	 * @param changed_chip 対象のチップ
	 * @param flag 設定するフラグ値
	 */
	public void setFlag(BBData changed_chip, boolean flag) {
		int groupCount = super.getGroupCount();
		
		for(int i=0; i<groupCount; i++) {
			int childrenCount = super.getChildrenCount(i);
			
			for(int j=0; j<childrenCount; j++) {
				BBData buf_chip = super.getChild(i, j);

				String tmp_name1 = buf_chip.get("名称");
				String tmp_name2 = changed_chip.get("名称");

				if(tmp_name1.equals(tmp_name2)) {
					mGroupCheckList.get(i).set(j, flag);
					break;
				}
			}
		}
	}

	/**
	 * フラグを取得する。
	 * @param groupPosition グループの位置
	 * @param childPosition データの位置
	 * @return フラグ
	 */
	public boolean getFlag(int groupPosition, int childPosition) {
		return mGroupCheckList.get(groupPosition).get(childPosition);
	}
	
	/**
	 * フラグを全てクリアする。
	 */
	public void clearFlags() {
		int group_size = mGroupCheckList.size();
		
		for(int i=0; i<group_size; i++) {
			ArrayList<Boolean> child_list = mGroupCheckList.get(i);
			int child_size = child_list.size();
			
			for(int j=0; j<child_size; j++) {
				child_list.set(j, false);
			}
		}
	}

	/**
	 * カスタマイズデータを読み込み、フラグに反映する。
	 */
	public void loadCustomData() {
		ArrayList<BBData> recent_list = mCustomData.getChips();
		
		int size = recent_list.size();
		for(int i=0; i<size; i++) {
			setFlag(recent_list.get(i), true);
		}
	}
	
	/**
	 * データを追加する。
	 * @param chip 追加するデータ
	 */
	public void addChild(BBData chip) {
		BBDataManager manager = BBDataManager.getInstance();
		
		if(chip.existCategory(BBDataManager.SKILL_CHIP_STR)) {
			super.addChild(SKILL_CHIP_CATEGORY_INDEX, chip);
			mGroupCheckList.get(SKILL_CHIP_CATEGORY_INDEX).add(false);
		}
		else if(chip.existCategory(BBDataManager.POWERUP_CHIP_STR)) {
			super.addChild(POWERUP_CHIP_CATEGORY_INDEX, chip);
			mGroupCheckList.get(POWERUP_CHIP_CATEGORY_INDEX).add(false);
		}
		else if(manager.isActionChip(chip)) {
			super.addChild(ACTION_CHIP_CATEGORY_INDEX, chip);
			mGroupCheckList.get(ACTION_CHIP_CATEGORY_INDEX).add(false);
		}
		
		// お気に入りリストに格納されているチップを追加する
		if(FavoriteManager.sChipStore.exist(chip.get("名称"))) {
			super.addChild(FAVORITE_CHIP_CATEGORY_INDEX, chip);
			mGroupCheckList.get(FAVORITE_CHIP_CATEGORY_INDEX).add(false);
		}
	}
	
	/**
	 * データのリストを追加する。
	 * @param chiplist チップのリスト
	 */
	public void addChildren(ArrayList<BBData> chiplist) {
		int size = chiplist.size();
		for(int i=0; i<size; i++) {
			addChild(chiplist.get(i));
		}
	}
	
	/**
	 * 項目を全削除する。
	 */
	@Override
	public void clear() {
		super.clear();
		
		int size = mGroupCheckList.size();
		for(int i=0; i<size; i++) {
			mGroupCheckList.get(i).clear();
		}
		
		mGroupCheckList.clear();
	}
}
