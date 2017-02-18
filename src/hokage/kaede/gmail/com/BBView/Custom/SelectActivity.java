package hokage.kaede.gmail.com.BBView.Custom;

import hokage.kaede.gmail.com.BBView.Item.InfoActivity;
import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataFilter;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.BBViewSetting;
import hokage.kaede.gmail.com.BBViewLib.CustomData;
import hokage.kaede.gmail.com.BBViewLib.CustomDataManager;
import hokage.kaede.gmail.com.BBViewLib.CustomDataWriter;
import hokage.kaede.gmail.com.BBViewLib.FavoriteManager;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBAdapterCmdManager;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBAdapterShownKeysManager;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBAdapterSortKeyManager;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBAdapterValueFilterManager;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBArrayAdapter;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBExpandableTextAdapter;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBAdapterCmdManager.OnExecuteInterface;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBAdapterShownKeysManager.OnOKClickListener;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBAdapterSortKeyManager.OnSelectItemListener;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBAdapterValueFilterManager.OnClickValueFilterButtonListener;
import hokage.kaede.gmail.com.BBViewLib.Android.BaseActivity;
import hokage.kaede.gmail.com.BBViewLib.Android.IntentManager;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;
import hokage.kaede.gmail.com.Lib.Java.FileArrayList;
import hokage.kaede.gmail.com.Lib.Java.ListConverter;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class SelectActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener, OnSelectItemListener, OnOKClickListener, OnExecuteInterface, OnClickValueFilterButtonListener {
	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;
	
	private BBDataManager mDataManager;
	private BBArrayAdapter mAdapter;
	private BBExpandableTextAdapter mExAdapter;
	private BBDataFilter mFilter;
	
	private FileArrayList mFavStore;
	
	private BBAdapterSortKeyManager mSortKeyDialog;
	private BBAdapterShownKeysManager mShownKeysDialog;
	private BBAdapterValueFilterManager mFilterManager;
	private BBAdapterCmdManager mCmdDialog;
	
	private CmpPartsTableBuilder mCmpPartsDialog;
	private CmpWeaponTableBuilder mCmpWeaponDialog;
	
	// リスト表示時のタイトル文字列設定用キー
	public static final String INTENTKEY_TITLENAME  = "INTENTKEY_TITLENAME";
	public static final String INTENTKEY_PARTSTYPE  = "INTENTKEY_PARTSTYPE";
	public static final String INTENTKEY_BLUSTTYPE  = "INTENTKEY_BLUSTTYPE";
	public static final String INTENTKEY_WEAPONTYPE = "INTENTKEY_WEAPONTYPE";
	public static final String INTENTKEY_REQARM     = "INTENTKEY_REQARM";
	
	private String[] mSortKeys;

	// メニュー番号
	private static final int MENU_ITEM0 = 0;
	private static final int MENU_ITEM1 = 1;
	private static final int MENU_ITEM2 = 2;
	private static final int MENU_ITEM3 = 3;
	private static final int MENU_ITEM4 = 4;
	private static final int MENU_ITEM5 = 5;
	
	// コマンド制御ダイアログ関連の定義
	private static final String DIALOG_LIST_ITEM_INFO = "詳細を表示する";
	private static final String DIALOG_LIST_ITEM_CMP  = "比較する";
	private static final String DIALOG_LIST_ITEM_FULL = "フルセットを設定する";
	
	private static final int DIALOG_LIST_IDX_INFO = 0;
	private static final int DIALOG_LIST_IDX_CMP  = 1;
	private static final int DIALOG_LIST_IDX_FULL = 2;
	
	private static final String[] DIALOG_LIST_ITEMS_PARTS = { DIALOG_LIST_ITEM_INFO, DIALOG_LIST_ITEM_CMP, DIALOG_LIST_ITEM_FULL };
	private static final String[] DIALOG_LIST_ITEMS_BASE = { DIALOG_LIST_ITEM_INFO, DIALOG_LIST_ITEM_CMP };

	// リストのViewID
	private static final int VIEW_ID_DEFALUT_LIST = 5000;
	private static final int VIEW_ID_EX_LIST = 6000;
	
	// ソート時のタイプB設定
	private boolean mIsSortTypeB = false;

	// リストのカテゴリ表示設定
	private boolean mIsExpandable = false;

	// 所持品のみ表示設定
	private boolean mIsHavingOnly = false;
	
	/**
	 * 画面生成時の処理を行う。
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDataManager = BBDataManager.getInstance();
		mDataManager.setSortKey("");
		
		Intent intent = getIntent();
		
		// ダイアログの初期化を行う
		initDialog(intent);
		
		// 画面を生成する
		createView(intent);
	}
	
	/**
	 * 各ダイアログの初期化を行う
	 * @param intent
	 */
	private void initDialog(Intent intent) {

		// インテントからデータを取得する
		String parts_type  = intent.getStringExtra(INTENTKEY_PARTSTYPE);
		String blust_type  = intent.getStringExtra(INTENTKEY_BLUSTTYPE);
		String weapon_type = intent.getStringExtra(INTENTKEY_WEAPONTYPE);
		String req_arm = intent.getStringExtra(INTENTKEY_REQARM);
		
		BBData recent_data = IntentManager.getSelectedData(intent);
		
		// 表示項目のフラグ設定
		mFilter = new BBDataFilter();
		String shown_save_key = "";
		mSortKeys = BBDataManager.getCmpTarget(recent_data);

		if(parts_type != null) {
			mIsExpandable = BBViewSetting.IS_SHOW_CATEGORYPARTS_INIT;
			mFavStore = FavoriteManager.getStore(parts_type);
			
			mFilter.setPartsType(parts_type);
			shown_save_key = parts_type;
			initCmdListDialog(true);
		}
		if(blust_type != null && weapon_type != null) {
			mIsExpandable = BBViewSetting.IS_SHOW_CATEGORYPARTS_INIT;
			mFavStore = FavoriteManager.getStore(blust_type);
			
			mFilter.setBlustType(blust_type);
			mFilter.setWeaponType(weapon_type);
			shown_save_key = blust_type + ":" + weapon_type;
			initCmdListDialog(false);
		}
		if(req_arm != null) {
			mFilter.setOtherType(req_arm);
			mFilter.setNotHavingShow(true);
			shown_save_key = req_arm;
			initCmdListDialog(false);
		}
		ArrayList<String> key_list = ListConverter.convert(mSortKeys);

		// ソート選択ダイアログを初期化する
		mSortKeyDialog = new BBAdapterSortKeyManager(this, key_list);
		mSortKeyDialog.setSelectItemListener(this);
		
		// ソート設定をロードする
		if(BBViewSetting.IS_MEMORY_SORT) {
			mSortKeyDialog.setSaveKey(shown_save_key);
			mSortKeyDialog.loadSetting();
			mDataManager.setSortKey(mSortKeyDialog.getSortKey());
			mDataManager.setASC(mSortKeyDialog.getAsc());
		}
		
		// 表示項目選択ダイアログを初期化する
		mShownKeysDialog = new BBAdapterShownKeysManager(this, key_list);
		mShownKeysDialog.setOnButtonClickListener(this);
		
		// 表示項目設定をロードする
		if(BBViewSetting.IS_MEMORY_SHOWSPEC) {
			mShownKeysDialog.setSaveKey(shown_save_key);
			mShownKeysDialog.loadSetting();
		}

		// フィルタ設定ダイアログを初期化する
		mFilterManager = new BBAdapterValueFilterManager(mFilter, key_list);
		mFilterManager.setOnClickValueFilterButtonListener(this);
		mFilterManager.setBBData(recent_data);
		
		// フィルタ設定をロードする
		if(BBViewSetting.IS_MEMORY_FILTER) {
			mFilterManager.setSaveKey(shown_save_key);
			mFilterManager.loadSetting(this);
			mFilter = mFilterManager.getFilter();
		}
		
		// アダプタの生成
		ArrayList<BBData> itemlist = mDataManager.getList(mFilter);
		mAdapter = new BBArrayAdapter(itemlist);
		mAdapter.setShowSwitch(true);
		mAdapter.setBaseItem(recent_data);
		mAdapter.setShownKeys(mShownKeysDialog.getShownKeys());
		mAdapter.notifyDataSetChanged();

		if(blust_type != null && weapon_type != null) {
			mExAdapter = new BBExpandableTextAdapter(false);
			mExAdapter.initWeapon(blust_type, weapon_type);
		}
		else {
			mExAdapter = new BBExpandableTextAdapter(true);
			mExAdapter.initParts();
		}
		
		mExAdapter.setFavStore(mFavStore);
		mExAdapter.addChildren(itemlist);
		mExAdapter.setShowSwitch(true);
		mExAdapter.setBaseItem(recent_data);
		mExAdapter.setShownKeys(mShownKeysDialog.getShownKeys());
		mExAdapter.notifyDataSetChanged();

		if(itemlist.size() <= 0) {
			Toast.makeText(this, "条件に一致するパーツはありません。", Toast.LENGTH_SHORT).show();
		}
		
		if(BBViewSetting.IS_SHOW_LISTBUTTON) {
			mExAdapter.setBBAdapterCmdManager(mCmdDialog);
			mAdapter.setBBAdapterCmdManager(mCmdDialog);
		}
		
		// 比較ダイアログを初期化する
		mCmpPartsDialog = new CmpPartsTableBuilder(this, BBViewSetting.IS_KM_PER_HOUR);
		mCmpWeaponDialog = new CmpWeaponTableBuilder(this, BBViewSetting.IS_KM_PER_HOUR);
	}
	
	/**
	 * コマンド制御ダイアログを初期化する。
	 * @param is_parts パーツかどうか。
	 */
	private void initCmdListDialog(boolean is_parts) {
		String[] cmd_list = DIALOG_LIST_ITEMS_BASE;

		if(is_parts) {
			cmd_list = DIALOG_LIST_ITEMS_PARTS;
		}
		
		mCmdDialog = new BBAdapterCmdManager(cmd_list);
		mCmdDialog.setOnExecuteInterface(this);
		
		// 設定に応じてボタンを非表示にする
		if(!BBViewSetting.IS_LISTBUTTON_SHOWINFO) {
			mCmdDialog.setHiddenTarget(DIALOG_LIST_IDX_INFO);
		}

		if(!BBViewSetting.IS_LISTBUTTON_SHOWCMP) {
			mCmdDialog.setHiddenTarget(DIALOG_LIST_IDX_CMP);
		}
		
		if(!BBViewSetting.IS_LISTBUTTON_SHOWFULLSET) {
			mCmdDialog.setHiddenTarget(DIALOG_LIST_IDX_FULL);
		}
	}
	
	/**
	 * 画面生成処理を行う。
	 * @param intent
	 */
	private void createView(Intent intent) {

		// タイトルを取得する
		String title = intent.getStringExtra(INTENTKEY_TITLENAME);
		
		// 全体レイアウト設定
		LinearLayout layout_all = new LinearLayout(this);
		layout_all.setLayoutParams(new LinearLayout.LayoutParams(FP, FP));
        layout_all.setOrientation(LinearLayout.VERTICAL);
		layout_all.setGravity(Gravity.TOP);

		// タイトルを設定
		TextView title_text = new TextView(this);
		title_text.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		title_text.setTextSize(BBViewSetting.getTextSize(this, BBViewSetting.FLAG_TEXTSIZE_LARGE));
		title_text.setGravity(Gravity.CENTER);
		title_text.setTextColor(SettingManager.getColorWhite());
		title_text.setBackgroundColor(SettingManager.getColorDarkGreen());
		title_text.setText(title);
		
		// リスト設定
		ListView list_view = new ListView(this);
		list_view.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		list_view.setOnItemClickListener(this);
		list_view.setOnItemLongClickListener(this);
		list_view.setId(VIEW_ID_DEFALUT_LIST);
		list_view.setAdapter(mAdapter);
		
		// カテゴリリスト設定
		ExpandableListView exlist_view = new ExpandableListView(this);
		exlist_view.setAdapter(mExAdapter);
		exlist_view.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		exlist_view.setOnChildClickListener(new OnClickExItemListener());
		exlist_view.setId(VIEW_ID_EX_LIST);

		if(mIsExpandable) {
			list_view.setVisibility(View.GONE);
		}
		else {
			exlist_view.setVisibility(View.GONE);
		}
		
		// 画面上部のテキストを設定する
		layout_all.addView(title_text);
		layout_all.addView(list_view);
		layout_all.addView(exlist_view);
		
		// リストの位置を選択中のアイテムの位置に変更する
		list_view.setSelection(mAdapter.getBaseItemIndex());
		
		setContentView(layout_all);
	}
	
	/**
	 * オプションメニュー生成時の処理を行う。
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		Intent intent = getIntent();
		String parts_type  = intent.getStringExtra(INTENTKEY_PARTSTYPE);
		String weapon_type = intent.getStringExtra(INTENTKEY_WEAPONTYPE);
		
		menu.add(0, MENU_ITEM0, 0, "ソート設定").setIcon(android.R.drawable.ic_menu_sort_alphabetically);
		menu.add(0, MENU_ITEM2, 0, "表示項目設定").setIcon(android.R.drawable.ic_menu_add);
		menu.add(0, MENU_ITEM1, 0, "フィルタ設定").setIcon(android.R.drawable.ic_menu_add);

		if(parts_type != null || weapon_type != null) {
			MenuItem item = menu.add(0, MENU_ITEM4, 0, "カテゴリ表示").setIcon(android.R.drawable.ic_menu_add);
			item.setCheckable(true);
			item.setChecked(mIsExpandable);
		}

		if(weapon_type != null) {
			MenuItem item = menu.add(0, MENU_ITEM3, 0, "タイプB表示").setIcon(android.R.drawable.ic_menu_add);
			item.setCheckable(true);
			item.setOnMenuItemClickListener(new ClickTypebMenuListener());
		}

		MenuItem item = menu.add(0, MENU_ITEM5, 0, "所持品のみ表示").setIcon(android.R.drawable.ic_menu_add);
		item.setCheckable(true);
		item.setChecked(mIsHavingOnly);
		
		return true;
	}
	
	/**
	 * タイプB表示ボタンを選択した際の処理を行うリスナー。
	 */
	private class ClickTypebMenuListener implements OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			boolean is_checked = !item.isChecked();

			mIsSortTypeB = is_checked;
			item.setChecked(is_checked);

			ArrayList<BBData> datalist = mDataManager.getList(mFilter, mIsSortTypeB);
			mAdapter.setShowTypeB(is_checked);
			mAdapter.setList(datalist);
			mAdapter.notifyDataSetChanged();
			
			return false;
		}
	}
	
	/**
	 * オプションメニュータップ時の処理を行う。
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case MENU_ITEM0:
				mSortKeyDialog.showDialog();
				break;

			case MENU_ITEM1:
				mFilterManager.showDialog(this);
				break;
				
			case MENU_ITEM2:
				mShownKeysDialog.showDialog();
				break;
				
			case MENU_ITEM4:
				changedListVisiblity(item);
				break;

			case MENU_ITEM5:
				mIsHavingOnly = !mIsHavingOnly;
				item.setChecked(mIsHavingOnly);
				updateList();
				break;
		}
		
		return true;
	}
	
	/**
	 * リストの表示状態(通常表示/カテゴリ表示)を変更する。
	 */
	private void changedListVisiblity(MenuItem item) {
		View default_list_view = this.findViewById(VIEW_ID_DEFALUT_LIST);
		View ex_list_view = this.findViewById(VIEW_ID_EX_LIST);
		
		mIsExpandable = !mIsExpandable;
		if(mIsExpandable) {
			default_list_view.setVisibility(View.GONE);
			ex_list_view.setVisibility(View.VISIBLE);
		}
		else {
			default_list_view.setVisibility(View.VISIBLE);
			ex_list_view.setVisibility(View.GONE);
		}

		item.setChecked(mIsExpandable);
	}
	
	/**
	 * リストの項目選択時の処理を行う。
	 */
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		BBData data = (BBData)(mAdapter.getItem(position));
		backCustomView(data);
	}
	
	/**
	 * カテゴリリストのデータ選択時の処理を行うリスナー
	 * データをアセンに設定する。
	 */
	private class OnClickExItemListener implements ExpandableListView.OnChildClickListener {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
			backCustomView(mExAdapter.getChild(groupPosition, childPosition));
			return false;
		}
	}

	/**
	 * 指定位置のパーツを設定し、前画面に戻る。
	 * @param data パーツデータ
	 */
	private void backCustomView(BBData data) {
		
		// カスタムデータに反映する
		CustomData custom_data = CustomDataManager.getCustomData();
		custom_data.setData(data);
		
		// カスタムデータをキャッシュファイルに書き込む。
		CustomDataWriter.write(custom_data, getFilesDir().toString());
		
		// データ変更状態を設定する
		CustomDataManager.setChanged(true);

		finish();
	}

	/**
	 * 指定の位置のパーツでフルセットを設定し、前画面に戻る。
	 * @param data パーツデータ
	 */
	private void backCustomViewFullSetCheck(BBData target_data) {
		String name = target_data.get("名称");
		
		AlertDialog.Builder check_dialog = new AlertDialog.Builder(this);
		check_dialog.setTitle("フルセット設定");
		check_dialog.setPositiveButton("OK", new OnOKFullSetListener(target_data));
		check_dialog.setNegativeButton("Cancel", null);
		check_dialog.setMessage(name + "に設定しますか？");
		check_dialog.show();
	}
	
	/**
	 * フルセット設定OKの場合の処理を行うリスナー。
	 */
	private class OnOKFullSetListener implements OnClickListener {
		private BBData mTarget;
		
		public OnOKFullSetListener(BBData target_data) {
			mTarget = target_data;
		}

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			backCustomViewFullSet(mTarget);
		}
	}

	/**
	 * 指定の位置のパーツでフルセットを設定し、前画面に戻る。
	 * @param data パーツデータ
	 */
	private void backCustomViewFullSet(BBData target_data) {
		
		// 選択したパーツ名を取得し、全部位のパーツをカスタムデータに反映する。
		String name = target_data.get("名称");

		BBDataManager manager = BBDataManager.getInstance();
		CustomData custom_data = CustomDataManager.getCustomData();
		
		int size = BBDataManager.BLUST_PARTS_LIST.length;
		for(int i=0; i<size; i++) {
			BBData data = manager.getPartsData(name, BBDataManager.BLUST_PARTS_LIST[i]);
			custom_data.setData(data);
		}
		
		// カスタムデータをキャッシュファイルに書き込む。
		CustomDataWriter.write(custom_data, getFilesDir().toString());
		
		// データ変更状態を設定する
		CustomDataManager.setChanged(true);

		finish();
	}
	
	/**
	 * リストの項目選択時(長)の処理を行う。
	 * コマンドダイアログに選択したデータを設定し、操作選択ダイアログを表示する。
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
		BBData to_item = (BBData)(mAdapter.getItem(position));
		mCmdDialog.setTarget(to_item);
		mCmdDialog.showDialog(this);
		
		return true;
	}
	
	/**
	 * 詳細画面へ移動する。
	 * @param to_item 詳細画面で表示するデータ
	 */
	private void moveInfoActivity(BBData to_item) {
		Intent intent = new Intent(this, InfoActivity.class);
		IntentManager.setSelectedData(intent, to_item);
		startActivity(intent);
	}
	
	/**
	 * パーツ/武器の比較ダイアログを表示する。
	 * @param to_item 比較対象のパーツ/武器
	 */
	private void showCmpView(BBData to_item) {
		BBData base_item = IntentManager.getSelectedData(getIntent());
		
		if(BBDataManager.isParts(base_item)) {
			mCmpPartsDialog.showDialog(base_item, to_item);
		}
		else {
			mCmpWeaponDialog.showDialog(base_item, to_item);
		}
	}

	/**
	 * ソートキーの設定を行う。
	 */
	@Override
	public void onSelectItem(BBAdapterSortKeyManager manager) {
		String sort_key = manager.getSortKey();
		mDataManager.setSortKey(sort_key);
		mDataManager.setASC(manager.getAsc());
		
		// ソートキー選択状態を記録する
		mSortKeyDialog.updateSetting();
		
		// 表示項目の再設定を行う
		ArrayList<String> recent_key_list = mAdapter.getShownKeys();
		ArrayList<String> new_key_list = new ArrayList<String>();
		
		int size = mSortKeys.length;
		for(int i=0; i<size; i++) {
			String key_buf = mSortKeys[i];
			if(recent_key_list.contains(key_buf)) {
				new_key_list.add(key_buf);
			}
			else if(sort_key.equals(key_buf)) {
				new_key_list.add(key_buf);
			}
		}
		
		mShownKeysDialog.set(sort_key, true);
		
		// 表示項目選択状態を記録する
		if(BBViewSetting.IS_MEMORY_SHOWSPEC) {
			mShownKeysDialog.updateSetting();
		}
		
		ArrayList<BBData> datalist = mDataManager.getList(mFilter, mIsSortTypeB);
		mAdapter.setList(datalist);
		mAdapter.setShownKeys(new_key_list);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 表示項目の設定を行う。
	 */
	@Override
	public void onSelectItem(BBAdapterShownKeysManager manager) {
		mAdapter.setShownKeys(manager.getShownKeys());
		mAdapter.notifyDataSetChanged();

		mExAdapter.setShownKeys(manager.getShownKeys());
		mExAdapter.notifyDataSetChanged();
	}

	/**
	 * コマンドボタン押下または操作選択ダイアログ選択時の処理を行う。
	 */
	@Override
	public void onExecute(BBData data, int cmd_idx) {
		if(cmd_idx == DIALOG_LIST_IDX_INFO) {
			moveInfoActivity(data);
		}
		else if(cmd_idx == DIALOG_LIST_IDX_CMP) {
			showCmpView(data);
		}
		else if(cmd_idx == DIALOG_LIST_IDX_FULL) {
			backCustomViewFullSetCheck(data);
		}
	}
	
	/**
	 * フィルタダイアログのOKボタンが押下された時の処理を行う。
	 */
	@Override
	public void onClickValueFilterButton() {
		mFilterManager.updateSetting(this);
		mFilter = mFilterManager.getFilter();
		
		updateList();
	}
	
	/**
	 * リストを更新する。
	 */
	private void updateList() {
		mFilter.setNotHavingShow(!mIsHavingOnly);
		
		ArrayList<BBData> datalist = mDataManager.getList(mFilter, mIsSortTypeB);
		mAdapter.setList(datalist);
		mAdapter.notifyDataSetChanged();
		
		mExAdapter.clearChildrenAll();
		mExAdapter.addChildren(datalist);
		mExAdapter.notifyDataSetChanged();

		if(datalist.size() == 0) {
			Toast.makeText(this, "条件に一致するパーツはありません。", Toast.LENGTH_SHORT).show();
		}
	}

}
