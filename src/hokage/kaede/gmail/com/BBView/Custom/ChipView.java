package hokage.kaede.gmail.com.BBView.Custom;

import hokage.kaede.gmail.com.BBView.Adapter.BBArrayAdapterChipView;
import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataFilter;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.CustomData;
import hokage.kaede.gmail.com.BBViewLib.CustomDataManager;
import hokage.kaede.gmail.com.BBViewLib.CustomDataWriter;
import hokage.kaede.gmail.com.BBViewLib.SpecValues;
import hokage.kaede.gmail.com.BBViewLib.Android.BBViewSettingManager;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * チップ画面のレイアウト
 */
public class ChipView extends LinearLayout {

	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;
	
	private static final int WEIGHT_TEXT_VIEW_ID = 5321;

	private BBDataManager mDataManager;
	private CustomData mCustomData;
	private BBDataFilter mFilter;
	
	private ChipListAdapter mChipListAdapter;

	private ArrayList<BBData> mBeforeChipList;
	
	private boolean mIsChanged;
	private Context mContext;
	
	// チップ登録エラーメッセージ
	private String mErrorMessage;

	public ChipView(Context context) {
		super(context);
		mIsChanged = false;
		mContext = context;
		mErrorMessage = "";

		mDataManager = BBDataManager.getInstance();
		mCustomData = CustomDataManager.getCustomData();

		// 変更前のチップリストを取得する
		mBeforeChipList = mCustomData.getChips();

		// アプリ画面の生成
		createView();
		
		// カスタマイズデータを読み込む
		loadCustomData();

		// 容量テキストを更新する
		updateWeightText();
	}

	/**
	 * 画面を生成する
	 */
	private void createView() {
		setLayoutParams(new LinearLayout.LayoutParams(FP, FP));
        setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.TOP);
		
		// フィルタをチップのみに設定
		mFilter = new BBDataFilter();
		mFilter.setOtherType(BBDataManager.CHIP_STR);
		
		// アダプタを設定する
		mChipListAdapter = new ChipListAdapter(mContext, mDataManager.getList(mFilter));
		
		ExpandableListView chip_list_view = new ExpandableListView(mContext);
		chip_list_view.setAdapter(mChipListAdapter);
		chip_list_view.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		
		// 容量と現在の使用状況を表示するテキストビューを生成する
		TextView weight_text_view = new TextView(mContext);
		weight_text_view.setTextSize(BBViewSettingManager.getTextSize(mContext, BBViewSettingManager.FLAG_TEXTSIZE_NORMAL));
		weight_text_view.setId(WEIGHT_TEXT_VIEW_ID);

		LinearLayout layout_btm = new LinearLayout(mContext);
		layout_btm.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		layout_btm.setOrientation(LinearLayout.HORIZONTAL);
		layout_btm.setGravity(Gravity.TOP);
		
		// 決定ボタンを生成する
		Button ok_button = new Button(mContext);
		ok_button.setText("決定");
		ok_button.setLayoutParams(new LinearLayout.LayoutParams(WC, WC, 1));
		ok_button.setOnClickListener(new OnClickOKButtonListener());
		
		// クリアボタンを生成する
		Button clear_button = new Button(mContext);
		clear_button.setText("クリア");
		clear_button.setLayoutParams(new LinearLayout.LayoutParams(WC, WC, 1));
		clear_button.setOnClickListener(new OnClickClearButtonListener());

		// 選択中表示ボタンを生成する
		Button view_button = new Button(mContext);
		view_button.setText("選択中表示");
		view_button.setLayoutParams(new LinearLayout.LayoutParams(WC, WC, 1));
		view_button.setOnClickListener(new OnClickViewButtonListener());
		
		layout_btm.addView(ok_button);
		layout_btm.addView(clear_button);
		layout_btm.addView(view_button);
		
		addView(chip_list_view);
		addView(weight_text_view);
		addView(layout_btm);
	}
	
	/**
	 * カスタマイズデータを読み込む
	 */
	private void loadCustomData() {
		ArrayList<BBData> recent_list = mCustomData.getChips();
		
		int size = recent_list.size();
		for(int i=0; i<size; i++) {
			mChipListAdapter.setFlag(recent_list.get(i), true);
		}
	}
	
	/**
	 * 容量のテキスト情報を更新する
	 */
	private void updateWeightText() {
		View tmp_view = this.findViewById(WEIGHT_TEXT_VIEW_ID);

		if(tmp_view instanceof TextView) {
			TextView weight_text_view = (TextView)tmp_view;

			// チップの現在値、最大値を取得する
			int chip_weight = mCustomData.getChipWeight();
			int chip_capacity = SpecValues.castInteger(mCustomData.getChipCapacity());
			
			String now_value = String.valueOf(chip_weight);
			String max_value = String.valueOf(chip_capacity);
			
			weight_text_view.setText("容量：" + now_value + "/" + max_value + " " + mErrorMessage);
			
			// 登録内容に問題がある場合は容量テキストを赤文字にする。
			if(mErrorMessage.equals("")) {
				weight_text_view.setTextColor(SettingManager.getColorWhite());
			}
			else {
				weight_text_view.setTextColor(SettingManager.getColorMazenta());
			}
		}
	}
	
	/**
	 * 決定ボタンを押下した際の処理を行うリスナー。
	 * データチェック後保存、処理をする。
	 */
	private class OnClickOKButtonListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			saveCustomData();
			Toast.makeText(mContext, "チップを登録しました。", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * クリアボタンを押下した際の処理を行うリスナー。
	 * 選択中のデータをクリアする。
	 */
	private class OnClickClearButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			mCustomData.clearChips();
			mChipListAdapter.clearFlag();
			mChipListAdapter.notifyDataSetChanged();
			mErrorMessage = "";
			updateWeightText();
		}	
	}
	
	/**
	 * 選択中表示ボタンを押下した際の処理を行うリスナー。
	 * 現在選択しているチップを表示するためのダイアログを表示する。
	 */
	private class OnClickViewButtonListener implements OnClickListener, android.content.DialogInterface.OnClickListener, OnMultiChoiceClickListener {

		private ArrayList<BBData> mChipList;
		private boolean[] mChecks;
		
		@Override
		public void onClick(View v) {
			mChipList = mCustomData.getChips();
			int size = mChipList.size();
			
			if(size==0) {
				Toast.makeText(mContext, "チップが選択されていません", Toast.LENGTH_SHORT).show();
				return;
			}

			String[] selected_chips = new String[size];
			mChecks = new boolean[size];
			
			for(int i=0;i<size;i++) {
				BBData chip = mChipList.get(i);
				selected_chips[i] = chip.get("名称") + " [" + chip.get("コスト") + "]";
				mChecks[i] = true;
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle("選択中のチップ一覧");
			builder.setIcon(android.R.drawable.ic_menu_more);
			builder.setMultiChoiceItems(selected_chips, mChecks, this);
			builder.setPositiveButton("OK", this);
			builder.setNegativeButton("Cancel", null);
			
			Dialog dialog = builder.create();
			dialog.show();
		}

		@Override
		public void onClick(DialogInterface arg0, int which) {
			
			int size = mChecks.length;
			for(int i=0; i<size; i++) {
				if(!mChecks[i]) {
					mCustomData.removeChip(mChipList.get(i));
				}
			}
			
			redraw();
			setChanged();
		}

		@Override
		public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
			// 何も処理を行わないが、チェック状態を保持するために本メソッドの定義が必要
		}
	}
	
	/**
	 * カスタマイズデータを保存する
	 */
	private void saveCustomData() {

		// カスタムデータをファイルに書き込む。
		CustomDataWriter.write(mCustomData, mContext.getFilesDir().toString());
		
		// 変更前リストを更新し、変更フラグを解除する
		mBeforeChipList = mCustomData.getChips();
		mIsChanged = false;
	}
	
	/**
	 * チップの編集状況をリセットする。画面切り替え時などに使用する。
	 */
	public void reset() {
		if(mIsChanged) {
			mCustomData.clearChips();
			
			int size = mBeforeChipList.size();
			for(int i=0; i<size; i++) {
				mCustomData.addChip(mBeforeChipList.get(i));
			}
		}
	}
	
	/**
	 * 再描画を行う。一旦全チェック状態をリセットして付け直す。
	 */
	public void redraw() {
		mChipListAdapter.clearFlag();
		loadCustomData();
		updateWeightText();
		mChipListAdapter.notifyDataSetChanged();
	}

	/**
	 * 変更状態を変更ありに設定する。
	 * ChipViewの"決定"ボタンからファイル保存したときのみ変更無しにするため、
	 * 本関数では変更状態(true)から変更無し(false)には設定できない。
	 */
	public void setChanged() {
		mIsChanged = true;
	}
	
	/**
	 * チップリストを管理するアダプタ
	 */
	private class ChipListAdapter extends BaseExpandableListAdapter implements OnCheckedChangeListener {

		private ArrayList<String> mGroupList;
		
		private ArrayList<BBData> mSkillChipList;
		private ArrayList<BBData> mPowerupChipList;
		private ArrayList<BBData> mActionChipList;
		
		private ArrayList<Boolean> mSkillChipCheckList;
		private ArrayList<Boolean> mPowerupChipCheckList;
		private ArrayList<Boolean> mActionChipCheckList;
		
		/**
		 * 初期化処理を行う。
		 * @param context アダプタを使用するオブジェクト
		 * @param chip_list チップリスト
		 */
		public ChipListAdapter(Context context, ArrayList<BBData> chip_list) {
			
			mGroupList = new ArrayList<String>();
			
			mSkillChipList = new ArrayList<BBData>();
			mPowerupChipList = new ArrayList<BBData>();
			mActionChipList = new ArrayList<BBData>();
			
			mSkillChipCheckList = new ArrayList<Boolean>();
			mPowerupChipCheckList = new ArrayList<Boolean>();
			mActionChipCheckList = new ArrayList<Boolean>();
			
			mGroupList.add(BBDataManager.SKILL_CHIP_STR);
			mGroupList.add(BBDataManager.POWERUP_CHIP_STR);
			mGroupList.add(BBDataManager.ACTION_CHIP_STR);

			int size = chip_list.size();
			for(int i=0; i<size; i++) {
				BBData buf_chip = chip_list.get(i);
				
				if(buf_chip.existCategory(BBDataManager.SKILL_CHIP_STR)) {
					mSkillChipList.add(buf_chip);
					mSkillChipCheckList.add(false);
				}
				else if(buf_chip.existCategory(BBDataManager.POWERUP_CHIP_STR)) {
					mPowerupChipList.add(buf_chip);
					mPowerupChipCheckList.add(false);
				}
				else if(isActionChip(buf_chip)) {
					mActionChipList.add(buf_chip);
					mActionChipCheckList.add(false);
				}
			}
		}
		
		private boolean isActionChip(BBData chip) {
			if(chip.existCategory(BBDataManager.ACTION_CHIP_STR)) {
				return true;
			}
			else if(chip.existCategory(BBDataManager.ACTION_ACT_CHIP_STR)) {
				return true;
			}
			else if(chip.existCategory(BBDataManager.ACTION_DASH_CHIP_STR)) {
				return true;				
			}
			else if(chip.existCategory(BBDataManager.ACTION_JUMP_CHIP_STR)) {
				return true;				
			}
			
			return false;
		}

		private boolean isActionChip(String type) {
			if(type.equals(BBDataManager.ACTION_CHIP_STR)) {
				return true;
			}
			else if(type.equals(BBDataManager.ACTION_ACT_CHIP_STR)) {
				return true;
			}
			else if(type.equals(BBDataManager.ACTION_DASH_CHIP_STR)) {
				return true;				
			}
			else if(type.equals(BBDataManager.ACTION_JUMP_CHIP_STR)) {
				return true;				
			}
			
			return false;
		}
		
		/**
		 * 子のデータを取得する。
		 * @param groupPosition グループの位置
		 * @param childPosition 子の位置
		 * @return 子のデータ
		 */
		@Override
		public BBData getChild(int groupPosition, int childPosition) {
			if(groupPosition < 0 || groupPosition >= mGroupList.size()) {
				return null;
			}
			
			String type = mGroupList.get(groupPosition);
			ArrayList<BBData> list = getChipList(type);
			
			if(list == null) {
				return null;
			}
			else if(childPosition < 0 || childPosition >= list.size()) {
				return null;
			}
			
			return list.get(childPosition);
		}

		/**
		 * 選択フラグを設定する
		 * @param position フラグを設定する位置
		 * @param flag 設定するフラグ値
		 */
		public void setFlag(int groupPosition, int childPosition, boolean flag) {
			if(groupPosition < 0 || groupPosition >= mGroupList.size()) {
				return;
			}
			
			String type = mGroupList.get(groupPosition);
			ArrayList<Boolean> list = getCheckList(type);
			
			if(childPosition < 0 || childPosition >= list.size()) {
				return;
			}
			
			list.set(childPosition, flag);
		}
		
		/**
		 * 選択フラグを設定する
		 * @param chip フラグを設定するデータ
		 * @param flag 設定するフラグ値
		 */
		public void setFlag(BBData chip, boolean flag) {
			ArrayList<BBData> chip_list = getChipList(chip);
			ArrayList<Boolean> check_list = getCheckList(chip);
			
			if(chip_list != null && check_list != null) {
				int size = chip_list.size();
				for(int i=0; i<size; i++) {
					String tmp_name1 = chip_list.get(i).get("名称");
					String tmp_name2 = chip.get("名称");

					if(tmp_name1.equals(tmp_name2)) {
						check_list.set(i, flag);
						break;
					}
				}
				
			}
		}
		
		/**
		 * チップに対応したデータリストを取得する。
		 * @param chip チップ
		 * @return チップのデータリスト
		 */
		private ArrayList<BBData> getChipList(BBData chip) {
			ArrayList<BBData> ret = null;

			if(chip.existCategory(BBDataManager.SKILL_CHIP_STR)) {
				ret = mSkillChipList;
			}
			else if(chip.existCategory(BBDataManager.POWERUP_CHIP_STR)) {
				ret = mPowerupChipList;
			}
			else if(isActionChip(chip)) {
				ret = mActionChipList;
			}
			
			return ret;
		}

		/**
		 * 種類に対応したデータリストを返す。
		 * @param type チップの種類
		 * @return チップのデータリスト
		 */
		private ArrayList<BBData> getChipList(String type) {
			ArrayList<BBData> ret = null;

			if(type.equals(BBDataManager.SKILL_CHIP_STR)) {
				ret = mSkillChipList;
			}
			else if(type.equals(BBDataManager.POWERUP_CHIP_STR)) {
				ret = mPowerupChipList;
			}
			else if(isActionChip(type)) {
				ret = mActionChipList;
			}
			
			return ret;
		}

		/**
		 * チップに対応したチェックリストを取得する。
		 * @param chip チップ
		 * @return チップのデータリスト
		 */
		private ArrayList<Boolean> getCheckList(BBData chip) {
			ArrayList<Boolean> ret = null;

			if(chip.existCategory(BBDataManager.SKILL_CHIP_STR)) {
				ret = mSkillChipCheckList;
			}
			else if(chip.existCategory(BBDataManager.POWERUP_CHIP_STR)) {
				ret = mPowerupChipCheckList;
			}
			else if(isActionChip(chip)) {
				ret = mActionChipCheckList;
			}
			
			return ret;
		}
		/**
		 * 種類に対応したチェックリストを返す。
		 * @param type チップの種類
		 * @return チップのチェックリスト
		 */
		private ArrayList<Boolean> getCheckList(String type) {
			ArrayList<Boolean> ret = null;

			if(type.equals(BBDataManager.SKILL_CHIP_STR)) {
				ret = mSkillChipCheckList;
			}
			else if(type.equals(BBDataManager.POWERUP_CHIP_STR)) {
				ret = mPowerupChipCheckList;
			}
			else if(isActionChip(type)) {
				ret = mActionChipCheckList;
			}
			
			return ret;
		}

		/**
		 * 子のIDを取得する。
		 * @param groupPosition
		 * @param childPosition
		 * @return
		 */
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		/**
		 * 子のビューを取得する。
		 */
		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
		{
			ArrayList<BBData> chip_list = getChipList(mGroupList.get(groupPosition));
			ArrayList<Boolean> check_list = getCheckList(mGroupList.get(groupPosition));
			
			BBData chipdata = chip_list.get(childPosition);

			BBArrayAdapterChipView checkbox = (BBArrayAdapterChipView)convertView;
			
			if(checkbox == null) {
				checkbox = new BBArrayAdapterChipView(mContext, null, false);
				checkbox.createView();
			}
			
			checkbox.setId(createID(groupPosition, childPosition));
			checkbox.setItem(chipdata);
			checkbox.updateView();
			checkbox.setOnCheckedChangeListener(null);
			checkbox.setChecked(check_list.get(childPosition));
			checkbox.setOnCheckedChangeListener(this);
			
			return checkbox;
		}
		
		/**
		 * グループ位置と子の位置からIDを作成する。
		 * @param groupPosition グループ位置
		 * @param childPosition 子の位置
		 * @return ID値
		 */
		private int createID(int groupPosition, int childPosition) {
			return groupPosition * 1000 + childPosition;
		}
		
		/**
		 * ID値からグループ位置を取得する。
		 * @param id ID値
		 * @return グループ位置
		 */
		private int getGroupPositionFromID(int id) {
			return (int)(id / 1000);
		}
		
		/**
		 * ID値から子の位置を取得する。
		 * @param id ID値
		 * @return 子の位置
		 */
		private int getChildPositionFromID(int id) {
			return id % 1000;
		}

		/**
		 * 子の数を取得する。
		 * @param groupPosition グループの位置
		 * @return 子の数
		 */
		@Override
		public int getChildrenCount(int groupPosition) {
			if(groupPosition < 0 || groupPosition >= mGroupList.size()) {
				return -1;
			}
			
			String type = mGroupList.get(groupPosition);
			ArrayList<BBData> list = getChipList(type);
			
			if(list == null) {
				return -1;
			}
			
			return list.size();
		}

		/**
		 * グループのデータを取得する。
		 */
		@Override
		public String getGroup(int groupPosition) {
			if(groupPosition < 0 || groupPosition >= mGroupList.size())
			{
				return null;
			}
			
			return mGroupList.get(groupPosition);
		}

		/**
		 * グループの数を取得する。
		 */
		@Override
		public int getGroupCount() {
			return mGroupList.size();
		}

		/**
		 * グループのIDを取得する。未使用のため、0を返す。
		 */
		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		/**
		 * グループのビューを取得する。
		 */
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); 
			
			TextView text_view = (TextView)convertView;
			
			if(text_view == null) {
				text_view = new TextView(mContext);
			}
			
			text_view.setText(mGroupList.get(groupPosition));
			text_view.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
			text_view.setTextSize(BBViewSettingManager.getTextSize(mContext, BBViewSettingManager.FLAG_TEXTSIZE_NORMAL));
			text_view.setPadding(0, 15, 0, 15);
			text_view.setTextColor(SettingManager.getColorWhite());
			text_view.setBackgroundColor(SettingManager.getColorBlue());
			text_view.setLayoutParams(lp);
						
			return text_view;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}

		/**
		 * チェックボックスの値を変更した場合の処理を行う。
		 */
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			int id = buttonView.getId();
			int group_position = getGroupPositionFromID(id);
			int child_position = getChildPositionFromID(id);
			
			// フラグ状態を設定する
			setFlag(group_position, child_position, isChecked);
			
			BBData target_item = (BBData)(getChild(group_position, child_position));
			
			if(isChecked) {
				mCustomData.addChip(target_item);
			}
			else {
				mCustomData.removeChip(target_item);
			}

			mIsChanged = true;
			checkStatus();
			updateWeightText();
		}
		
		/**
		 * チップの状態を確認する。
		 * チップの状態に問題があれば、エラーメッセージを表示する。
		 * @return 選択中のチップの状態に問題がなければtrueを返す。問題があればfalseを返す。
		 */
		private boolean checkStatus() {
			boolean ret = true;

			// チップの現在値、最大値を取得する
			int chip_weight = mCustomData.getChipWeight();
			int chip_capacity = SpecValues.castInteger(mCustomData.getChipCapacity());
			
			if(chip_weight > chip_capacity) {
				mErrorMessage = "[チップ容量超過]";
				Toast.makeText(mContext, "チップの容量が超過しています。", Toast.LENGTH_SHORT).show();
				ret = false;
			}
			else if(!mCustomData.judgeActionChip()) {
				mErrorMessage = "[アクションチップ重複]";
				Toast.makeText(mContext, "アクションチップの設定に誤りがあります。", Toast.LENGTH_SHORT).show();
				ret = false;
			}
			else if(!mCustomData.judgePowerupChip()) {
				mErrorMessage = "[機体強化チップ重複]";
				Toast.makeText(mContext, "機体強化チップの設定に誤りがあります。", Toast.LENGTH_SHORT).show();
				ret = false;
			}
			else {
				mErrorMessage = "";
			}
			
			return ret;
		}
		
		/**
		 * フラグを全てクリアする
		 */
		public void clearFlag() {
			int size = mSkillChipCheckList.size();
			for(int i=0; i<size; i++) {
				mSkillChipCheckList.set(i, false);
			}

			size = mPowerupChipCheckList.size();
			for(int i=0; i<size; i++) {
				mPowerupChipCheckList.set(i, false);
			}
			
			size = mActionChipCheckList.size();
			for(int i=0; i<size; i++) {
				mActionChipCheckList.set(i, false);
			}
		}
	}
}
