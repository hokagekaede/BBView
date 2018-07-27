package hokage.kaede.gmail.com.BBView3.Purchase;

import hokage.kaede.gmail.com.BBView3.Item.InfoActivity;
import hokage.kaede.gmail.com.BBViewLib.Android.CommonLib.BBDataAdapterItemProperty;
import hokage.kaede.gmail.com.BBViewLib.Android.CommonLib.ControlPanel;
import hokage.kaede.gmail.com.BBViewLib.Java.BBData;
import hokage.kaede.gmail.com.BBViewLib.Java.BBDataFilter;
import hokage.kaede.gmail.com.BBViewLib.Java.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.Java.BBViewSetting;
import hokage.kaede.gmail.com.BBViewLib.Android.CommonLib.ControlPanelBuilder;
import hokage.kaede.gmail.com.BBViewLib.Android.PurchaseLib.PurchaseFilterDialog;
import hokage.kaede.gmail.com.BBViewLib.Android.PurchaseLib.PurchaseAdapter;
import hokage.kaede.gmail.com.BBViewLib.Android.PurchaseLib.PurchaseFilterDialog.OnOKFilterDialogListener;
import hokage.kaede.gmail.com.BBViewLib.Android.CommonLib.BaseActivity;
import hokage.kaede.gmail.com.BBViewLib.Android.CommonLib.IntentManager;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

/**
 * 「購入リスト追加」画面を表示するクラス。
 */
public class PurchaseSelectActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener, OnOKFilterDialogListener, OnClickListener {
	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;
	
	private BBDataManager mDataManager;
	private PurchaseAdapter mAdapter;
	private BBDataFilter mFilter;
	
	private PurchaseFilterDialog mFilterManager;
	private ControlPanelBuilder mCmdDialog;
	
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
		
		mFilterManager = new PurchaseFilterDialog(this, mFilter);
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
		mCmdDialog = new ControlPanelBuilder(DIALOG_LIST_ITEMS_LISTMODE, new OnClickControlPanelListener());

		// 設定に応じてボタンを非表示にする
		if(!BBViewSetting.IS_SHOW_LISTBUTTON) {
			mCmdDialog.setHiddenPanel(true);
		}
		else {
			if (!BBViewSetting.IS_LISTBUTTON_SHOWINFO) {
				mCmdDialog.setHiddenButton(DIALOG_LIST_IDX_INFO);
			}
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
		mAdapter = new PurchaseAdapter(new BBDataAdapterItemProperty());
		mAdapter.setList(datalist);
		mAdapter.setBuilder(mCmdDialog);
		list_view.setAdapter(mAdapter);

		setContentView(layout_all);
	}

	/**
	 * リストを更新する。
	 */
	private void updateList() {
		mFilter.setHavingShow(!mIsNotHavingOnly);
		
		ArrayList<BBData> datalist = mDataManager.getList(mFilter);
		mAdapter.setList(datalist);
		mAdapter.notifyDataSetChanged();
	}
	
	/**
	 * オプションメニュー生成時の処理を行う。
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		menu.add(0, MENU_ITEM1, 0, "フィルタ設定");

		MenuItem item = menu.add(0, MENU_ITEM2, 0, "未所持のみ表示");
		item.setCheckable(true);
		item.setChecked(mIsNotHavingOnly);
		
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
				mIsNotHavingOnly = !mIsNotHavingOnly;
				item.setChecked(mIsNotHavingOnly);
				updateList();
		}
		
		return true;
	}
	
	/**
	 * リストの項目選択時の処理を行う。
	 */
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		if(mAdapter.isSelected(position)) {
			mAdapter.setSelect(position, false);
		}
		else {
			mAdapter.setSelect(position, true);
		}
		
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 購入プレビュー画面に戻る処理を行う。
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
		BBData to_item = mAdapter.getItem(position);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("操作を選択");
		builder.setIcon(android.R.drawable.ic_menu_more);
		builder.setItems(DIALOG_LIST_ITEMS_LISTMODE, new OnClickCommandListener(to_item));

		Dialog dialog = builder.create();
		dialog.setOwnerActivity(this);
		dialog.show();

		return false;
	}

	/**
	 * パーツまたは武器に対する処理を選択した場合の処理を行うリスナー
	 */
	private class OnClickCommandListener implements DialogInterface.OnClickListener {
		private BBData mData;

		public OnClickCommandListener(BBData data) {
			mData = data;
		}

		@Override
		public void onClick(DialogInterface dialogInterface, int index) {
			executeCommand(mData, index);
		}
	}

	/**
	 * パーツまたは武器に対する処理を選択した場合の処理を行うリスナー
	 */
	private class OnClickControlPanelListener implements ControlPanel.OnExecuteListenerInterface {

		@Override
		public void onExecute(BBData data, int cmd_idx) {
			executeCommand(data, cmd_idx);
		}
	}

	/**
	 * コマンドボタン押下または操作選択ダイアログ選択時の処理を行う。
	 */
	public void executeCommand(BBData data, int cmd_idx) {
		if(cmd_idx == DIALOG_LIST_IDX_INFO) {
			moveInfoActivity(data);
		}
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

	/**
	 * 決定ボタン押下時の処理を行う。
	 */
	@Override
	public void onClick(View v) {
		backPurchaseView();
	}


}
