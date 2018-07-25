package hokage.kaede.gmail.com.BBView3.Purchase;

import hokage.kaede.gmail.com.BBView3.Item.InfoActivity;
import hokage.kaede.gmail.com.BBViewLib.Android.CommonLib.BBDataAdapter;
import hokage.kaede.gmail.com.BBViewLib.Android.CommonLib.BBDataAdapterItemProperty;
import hokage.kaede.gmail.com.BBViewLib.Android.CommonLib.ControlPanel;
import hokage.kaede.gmail.com.BBViewLib.Android.CommonLib.ControlPanelBuilder;
import hokage.kaede.gmail.com.BBViewLib.Java.BBData;
import hokage.kaede.gmail.com.BBViewLib.Java.BBViewSetting;
import hokage.kaede.gmail.com.BBViewLib.Java.PurchaseStore;
import hokage.kaede.gmail.com.BBViewLib.Android.CommonLib.BaseActivity;
import hokage.kaede.gmail.com.BBViewLib.Android.CommonLib.IntentManager;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 「購入リスト表示」画面を表示するクラス。
 */
public class PurchaseListActivity extends BaseActivity implements OnItemClickListener, OnClickListener, OnItemLongClickListener {
	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;
	
	public static final int INTENTCODE_RET_SELECT_ITEMID = 1;

	private BBDataAdapter mAdapter;
	private ControlPanelBuilder mCmdDialog;
	
	private static final String BTN_TEXT_ADD     = "追加";
	private static final String BTN_TEXT_CLEAR   = "クリア";
	private static final String BTN_TEXT_PREVIEW = "プレビュー";
	
	private static final String MENU_ALL_SELECT = "不足勲章素材の表示";

	// コマンド制御ダイアログ関連の定義
	private static final String DIALOG_LIST_ITEM_INFO   = "詳細を表示する";
	private static final int DIALOG_LIST_IDX_INFO = 0;
	private static final String[] DIALOG_LIST_ITEMS_LISTMODE = { DIALOG_LIST_ITEM_INFO };

	/**
	 * アプリ起動時の処理を行う。
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle(this.getTitle() + " (欲しいものリスト)");

		initCmdListDialog();
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
	 * 画面を生成する。
	 */
	private void createView() {

		// 全体レイアウト設定
		LinearLayout layout_all = new LinearLayout(this);
		layout_all.setLayoutParams(new LinearLayout.LayoutParams(FP, FP));
		layout_all.setOrientation(LinearLayout.VERTICAL);
		layout_all.setGravity(Gravity.TOP);

		// アダプタの設定
		PurchaseStore store = new PurchaseStore(this.getFilesDir().toString());
		ArrayList<BBData> data_list = store.getPurchaseList();
		mAdapter = new BBDataAdapter(new BBDataAdapterItemProperty());
		mAdapter.setBuilder(mCmdDialog);
		mAdapter.setList(data_list);

		// リスト設定
		ListView list_view = new ListView(this);
		list_view.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		list_view.setOnItemClickListener(this);
		list_view.setOnItemLongClickListener(this);
		list_view.setAdapter(mAdapter);
		layout_all.addView(list_view);

		// 画面下部のボタンを配置
		LinearLayout layout_bottom = new LinearLayout(this);
		layout_bottom.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		layout_bottom.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		layout_all.addView(layout_bottom);

		// リスト追加ボタンの追加
		Button btn_add = new Button(this);
		btn_add.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		btn_add.setText(BTN_TEXT_ADD);
		btn_add.setOnClickListener(this);
		layout_bottom.addView(btn_add);

		// リストクリアボタンの追加
		Button btn_clear = new Button(this);
		btn_clear.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		btn_clear.setText(BTN_TEXT_CLEAR);
		btn_clear.setOnClickListener(this);
		layout_bottom.addView(btn_clear);

		// プレビュー表示ボタンの追加
		Button btn_preview = new Button(this);
		btn_preview.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		btn_preview.setText(BTN_TEXT_PREVIEW);
		btn_preview.setOnClickListener(this);
		layout_bottom.addView(btn_preview);

		setContentView(layout_all);
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
	 * オプションメニュー生成時の処理を行う。
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		menu.add(MENU_ALL_SELECT);
		
		return true;
	}
	
	/**
	 * オプションメニュー選択時の処理を行う。
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String menu_title = item.getTitle().toString();
		
		if(menu_title.equals(MENU_ALL_SELECT)) {
			Intent intent = new Intent(this, PurchasePreviewActivity.class);
			
			intent.putExtra(PurchasePreviewActivity.INTENTKEY_SELECTALL, true);
			
			this.startActivity(intent);
		}
		return true;
	}
	
	/**
	 * ボタン押下時の処理を行う。
	 */
	@Override
	public void onClick(View v) {
		if(v instanceof Button) {
			CharSequence btn_text = ((Button)v).getText();
		
			if(btn_text.equals(BTN_TEXT_ADD)) {
				Intent intent = new Intent(this, PurchaseSelectActivity.class);
				startActivityForResult(intent, INTENTCODE_RET_SELECT_ITEMID);
			}
			else if(btn_text.equals(BTN_TEXT_CLEAR)) {
				PurchaseStore store = new PurchaseStore(this.getFilesDir().toString());
				store.clear();
				mAdapter.clear();
				mAdapter.notifyDataSetChanged();
				Toast.makeText(this, "リストをクリアしました", Toast.LENGTH_SHORT).show();
			}
			else if(btn_text.equals(BTN_TEXT_PREVIEW)) {
				Intent intent = new Intent(this, PurchasePreviewActivity.class);
				startActivity(intent);
			}
		}
	}

	/**
	 * リスト項目を短押しした場合の処理を行う。
	 * 短押ししたアイテムの詳細情報を表示する。
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		BBData data = (BBData)(mAdapter.getItem(position));
		
		Intent intent = new Intent(this, InfoActivity.class);
		IntentManager.setSelectedData(intent, data);
		
		startActivity(intent);
	}

	/**
	 * リスト項目を長押しした場合の処理を行う。
	 * 長押ししたアイテムをリストから削除する。
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// リストからデータを取り出す
		BBData data = (BBData)(mAdapter.getItem(position));

		// データを削除し、ファイルに書き込む
		PurchaseStore store = new PurchaseStore(this.getFilesDir().toString());
		store.remove(data);
		
		// データをリストから削除する
		mAdapter.remove(data);
		mAdapter.notifyDataSetChanged();
		
		Toast.makeText(this, "削除しました", Toast.LENGTH_SHORT).show();
		
		return true;
	}
	
	/**
	 * 起動先アクティビティから戻ってきた場合の処理を行う。
	 * intentから選択したパーツまたは武器データを取得する。
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == INTENTCODE_RET_SELECT_ITEMID) {
			
			if(data == null) {
				return;
			}
			
			// intentで指定されたアイテムIDのデータを取り出す
			BBData[] item = IntentManager.getSelectedDataArray(data);
			int size = item.length;
			
			// データを追加し、ファイルに書き込む
			PurchaseStore store = new PurchaseStore(this.getFilesDir().toString());
			
			for(int i=0; i<size; i++) {
				if(store.add(item[i])) {
					mAdapter.add(item[i]);
					mAdapter.notifyDataSetChanged();
				}
				else {
					Toast.makeText(this, item[i].get("名称") + "は既に登録されています。", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}
