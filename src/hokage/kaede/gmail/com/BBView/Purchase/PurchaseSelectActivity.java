package hokage.kaede.gmail.com.BBView.Purchase;

import hokage.kaede.gmail.com.BBView.Item.InfoActivity;
import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataFilter;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.BBViewSetting;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBAdapterCmdManager;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBAdapterFilterManager;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBSelectDataAdapter;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBAdapterCmdManager.OnClickIndexButtonInterface;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBAdapterCmdManager.OnExecuteInterface;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBAdapterFilterManager.OnOKFilterDialogListener;
import hokage.kaede.gmail.com.BBViewLib.Android.BaseActivity;
import hokage.kaede.gmail.com.BBViewLib.Android.IntentManager;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class PurchaseSelectActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener, OnOKFilterDialogListener, OnExecuteInterface, OnClickIndexButtonInterface, OnClickListener {
	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;
	
	private BBDataManager mDataManager;
	private BBSelectDataAdapter mAdapter;
	private BBDataFilter mFilter;
	
	private BBAdapterFilterManager mFilterManager;
	private BBAdapterCmdManager mCmdDialog;
	
	private boolean mIsNotHavingOnly = true;   // 未所持のみ表示
	
	// メニュー番号
	private static final int MENU_ITEM1 = 1;
	private static final int MENU_ITEM2 = 2;
	
	// コマンド制御ダイアログ関連の定義
	private static final String DIALOG_LIST_ITEM_INFO   = "詳細を表示する";
	private static final int DIALOG_LIST_IDX_INFO = 0;
	private static final String[] DIALOG_LIST_ITEMS_LISTMODE = { DIALOG_LIST_ITEM_INFO };
	
	/**
	 * 画面生成時の処理を行う。
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle(getTitle() + " (購入リストに追加)");

		// 表示項目のフラグを設定
		mDataManager = BBDataManager.getInstance();
		mDataManager.setSortKey(null);
		
		mFilter = new BBDataFilter();
		mFilter.setPartsType(BBDataManager.BLUST_PARTS_LIST);
		mFilter.setBlustType(BBDataManager.BLUST_TYPE_LIST);
		mFilter.setWeaponType(BBDataManager.WEAPON_TYPE_LIST);
		mFilter.setHavingShow(!mIsNotHavingOnly);
		
		mFilterManager = new BBAdapterFilterManager(this, mFilter);
		mFilterManager.setOnOKFilterDialogListener(this);
		
		// コマンド制御ダイアログを初期化する
		initCmdListDialog();
		
		// 画面を生成する
		createView();
	}
	
	/**
	 * コマンド制御ダイアログを初期化する
	 */
	private void initCmdListDialog() {
		mCmdDialog = new BBAdapterCmdManager(DIALOG_LIST_ITEMS_LISTMODE);
		mCmdDialog.setOnClickIndexButtonInterface(this);
		mCmdDialog.setOnExecuteInterface(this);
		
		// 設定に応じてボタンを非表示にする
		if(!BBViewSetting.IS_LISTBUTTON_SHOWINFO) {
			mCmdDialog.setHiddenTarget(DIALOG_LIST_IDX_INFO);
		}
	}
	
	/**
	 * 画面生成処理を行う。
	 */
	private void createView() {
		// 全体レイアウト設定
		LinearLayout layout_all = new LinearLayout(this);
		layout_all.setLayoutParams(new LinearLayout.LayoutParams(FP, FP));
        layout_all.setOrientation(LinearLayout.VERTICAL);
		layout_all.setGravity(Gravity.TOP);

		// リスト設定
		ListView list_view = new ListView(this);
		list_view.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		list_view.setOnItemClickListener(this);
		list_view.setOnItemLongClickListener(this);
		layout_all.addView(list_view);
		
		// 決定ボタンを設定
		Button btn_ok = new Button(this);
		btn_ok.setLayoutParams(new LayoutParams(FP, WC));
		btn_ok.setText("決定");
		btn_ok.setOnClickListener(this);
		layout_all.addView(btn_ok);
		
		// リストの生成
		ArrayList<BBData> datalist = mDataManager.getList(mFilter);
		mAdapter = new BBSelectDataAdapter(this, datalist);
		mAdapter.setBaseItem(null);
		list_view.setAdapter(mAdapter);

		if(BBViewSetting.IS_SHOW_LISTBUTTON) {
			mAdapter.setBBAdapterCmdManager(mCmdDialog);
		}

		setContentView(layout_all);
	}
	
	private void updateList() {
		mFilter.setHavingShow(!mIsNotHavingOnly);
		
		ArrayList<BBData> datalist = mDataManager.getList(mFilter);
		mAdapter.setList(datalist);
		mAdapter.setBaseItem(null);
		mAdapter.notifyDataSetChanged();
	}
	
	/**
	 * オプションメニュー生成時の処理を行う。
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		menu.add(0, MENU_ITEM1, 0, "絞込み設定").setIcon(android.R.drawable.ic_menu_add);
		menu.add(0, MENU_ITEM2, 0, "全て表示").setIcon(android.R.drawable.ic_menu_add);
		
		return true;
	}
	
	/**
	 * オプションメニュータップ時の処理を行う。
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case MENU_ITEM1:
				mFilterManager.showDialog();
				break;
			case MENU_ITEM2:
				if(mIsNotHavingOnly) {
					item.setTitle("未所持のみ表示");
					mIsNotHavingOnly = false;
				}
				else {
					item.setTitle("全て表示");
					mIsNotHavingOnly = true;
				}
				updateList();
		}
		
		return true;
	}
	
	/**
	 * リストの項目選択時の処理を行う。
	 */
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		BBData data = mAdapter.getItem(position);
		
		if(mAdapter.isSelect(data)) {
			mAdapter.unselect(data);
		}
		else {
			mAdapter.select(data);
		}
		
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 購入プレビュー画面に戻る処理を行う。
	 * @param data 選択したデータ
	 */
	private void backPurchaseView() {
		ArrayList<BBData> list = mAdapter.getSelectedList();
		
		Intent intent = new Intent();
		IntentManager.setSelectedDataArray(intent, list.toArray(new BBData[0]));
		
		setResult(RESULT_OK, intent);
		
		finish();
	}
	
	/**
	 * リストの項目選択時(長)の処理を行う。
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
		BBData to_item = (BBData)(mAdapter.getItem(position));
		mCmdDialog.setTarget(to_item);
		mCmdDialog.showDialog(this);
		
		return false;
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
	 * フィルタの設定を行う。
	 */
	@Override
	public void onOKFilterDialog() {
		mAdapter.setList(mDataManager.getList(mFilter));
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClickIndexButton(int position, int index) {
		BBData to_item = (BBData)(mAdapter.getItem(position));
		mCmdDialog.setTarget(to_item);
	}
	
	/**
	 * 指定された操作を実行する。
	 */
	@Override
	public void onExecute(BBData data, int cmd_idx) {
		if(DIALOG_LIST_ITEMS_LISTMODE[cmd_idx].equals(DIALOG_LIST_ITEM_INFO)) {
			moveInfoActivity(data);
		}
	}

	/**
	 * 決定ボタン押下時の処理を行う。
	 */
	@Override
	public void onClick(View v) {
		backPurchaseView();
	}


}
