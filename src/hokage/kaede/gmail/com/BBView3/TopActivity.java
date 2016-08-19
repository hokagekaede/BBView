package hokage.kaede.gmail.com.BBView3;

import java.io.InputStream;

import hokage.kaede.gmail.com.BBView.Custom.CustomMainActivity;
import hokage.kaede.gmail.com.BBView.Item.CategoryListActivity;
import hokage.kaede.gmail.com.BBView.Purchase.PurchaseListActivity;
import hokage.kaede.gmail.com.BBView.Shop.ShopMainActivity;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.BBDataReader;
import hokage.kaede.gmail.com.BBViewLib.BBNetDatabase;
import hokage.kaede.gmail.com.BBViewLib.CustomData;
import hokage.kaede.gmail.com.BBViewLib.CustomDataManager;
import hokage.kaede.gmail.com.BBViewLib.CustomDataReader;
import hokage.kaede.gmail.com.BBViewLib.SpecValues;
import hokage.kaede.gmail.com.BBViewLib.Android.BBViewSettingManager;
import hokage.kaede.gmail.com.BBViewLib.Android.BaseActivity;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;
import hokage.kaede.gmail.com.Lib.Java.FileIO;
import hokage.kaede.gmail.com.Lib.Java.FileKeyValueStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class TopActivity extends BaseActivity {
	
	private static final String BTN_TEXT_CUSTOM   = "機体カスタマイズ";
	private static final String BTN_TEXT_ITEM     = "所持品確認";
	private static final String BTN_TEXT_PURCHASE = "購入プレビュー";
	private static final String BTN_TEXT_SHOP     = "店舗検索";
	private static final String BTN_TEXT_BLOG     = "BBView開発室へ";
	
	private final static String MENU_SETTING         = "設定";
	private final static String MENU_OTHER           = "その他";
	private final static String MENU_OFFICIAL        = "BB公式ページを開く";
	private final static String MENU_BBNET           = "BB.NETを開く";
	private final static String MENU_WIKI            = "BB wikiを開く";
	private final static String MENU_ABOUT           = "BB Viewについて";
	
	/**
	 * 画面生成時の処理を行う。
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// バージョンアップデータを使用するかどうかの設定値を初期化
		// BBViewSettingManager.IS_VER_X_ON = false;
		
		// 初期化する
		SpecValues.init();
		loadPartsData();
		initCustomData();
		
		// アプリの設定値を読み込む
		BBViewSettingManager.loadSettings(this);

		// BB.NETのデータを更新する
		BBNetDatabase.getInstance().init(this.getFilesDir().toString());
		
		// 初回起動時は説明ページを起動し、アップデート時は更新情報をダイアログ表示する。
		if(BBViewSettingManager.isFirstFlag(this)) {
			BBViewSettingManager.setVersionCode(this);
			Intent intent = new Intent(this, ManualActivity.class);
			startActivity(intent);
		}
		else if(BBViewSettingManager.isUpdateFlag(this)) {
			BBViewSettingManager.setVersionCode(this);
			showFirstDialog();
		}

		// 共有データを設定する
		//CustomCodeReader reader = new CustomCodeReader(this, getIntent());
		//reader.read();
		setShareData(getIntent());

		// メインのレイアウト
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.TOP | Gravity.LEFT);
		layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		// アプリ更新情報のタイトルレイアウト
		LinearLayout title_layout = new LinearLayout(this);
		title_layout.setOrientation(LinearLayout.HORIZONTAL);
		title_layout.setGravity(Gravity.TOP | Gravity.LEFT);
		title_layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		// アプリ更新情報のタイトルテキスト
		TextView title_view = new TextView(this);
		title_view.setText("アプリ更新情報");
		title_view.setTextSize(BBViewSettingManager.getTextSize(this, BBViewSettingManager.FLAG_TEXTSIZE_NORMAL));
		title_view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
		title_view.setTextColor(SettingManager.getColor(SettingManager.COLOR_BASE));
		title_layout.addView(title_view);
		
		layout.addView(title_layout);
		
		// アプリ更新情報のスクロール
		ScrollView sv = new ScrollView(this);
		sv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
		layout.addView(sv);
		
		// アプリ更新情報のビュー
		TextView appupd_text_view = new TextView(this);
		appupd_text_view.setText(loadUpdateText());
		appupd_text_view.setTextSize(BBViewSettingManager.getTextSize(this, BBViewSettingManager.FLAG_TEXTSIZE_SMALL));
		appupd_text_view.setTextColor(SettingManager.getColor(SettingManager.COLOR_BASE));
		appupd_text_view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
		sv.addView(appupd_text_view);
		
		// 各種ボタン
		Button custom_btn = new Button(this);
		custom_btn.setText(BTN_TEXT_CUSTOM);
		custom_btn.setOnClickListener(new OnMoveCustomListener());
		layout.addView(custom_btn);

		Button item_btn = new Button(this);
		item_btn.setText(BTN_TEXT_ITEM);
		item_btn.setOnClickListener(new OnMoveItemListener());
		layout.addView(item_btn);

		Button purchase_btn = new Button(this);
		purchase_btn.setText(BTN_TEXT_PURCHASE);
		purchase_btn.setOnClickListener(new OnMovePurchaseListener());
		layout.addView(purchase_btn);

		Button shop_btn = new Button(this);
		shop_btn.setText(BTN_TEXT_SHOP);
		shop_btn.setOnClickListener(new OnMoveShopListener());
		layout.addView(shop_btn);
		
		Button blog_btn = new Button(this);
		blog_btn.setText(BTN_TEXT_BLOG);
		blog_btn.setOnClickListener(new OnMoveBlogListener());
		layout.addView(blog_btn);
		
		/* 次回のバージョンアップ用(データ切り替え) */
		/*
		ToggleButton ver_btn = new ToggleButton(this);
		ver_btn.setChecked(BBViewSettingManager.IS_VER_X_ON);
		ver_btn.setText("Xデータ [OFF]");
		ver_btn.setTextOn("Xデータ [ON]");
		ver_btn.setTextOff("Xデータ [OFF]");
		ver_btn.setOnCheckedChangeListener(new OnChangeVersionListener());
		layout.addView(ver_btn);
		*/

		setContentView(layout);
	}
	
	/* 次回のバージョンアップ用(データ切り替え) */
	/*
	private class OnChangeVersionListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			
			if(arg1) {
				BBViewSettingManager.IS_VER_X_ON = true;
				SpecValues.init();
				loadPartsData(R.raw.bb_data_x);
				initCustomData();
			}
			else {
				BBViewSettingManager.IS_VER_X_ON = false;
				SpecValues.init();
				loadPartsData();
				initCustomData();
			}
			
			TopActivity.this.updateVer();
		}
	}
	*/

	/**
	 * 既存のアクティビティを使用する場合の処理を行う。
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		//CustomCodeReader reader = new CustomCodeReader(this, getIntent());
		//reader.read();
		setShareData(getIntent());
	}

	/**
	 * パーツ・武器のデータを読み込む。
	 */
	private void loadPartsData() {
		loadPartsData(R.raw.bb_data);
	}

	/**
	 * パーツ・武器のデータを読み込む。
	 * @param resource パーツ・武器のリソースデータID
	 */
	private void loadPartsData(int resource) {

		BBDataManager data_mng = BBDataManager.getInstance();
		data_mng.init();
		
		try {
			Resources res = this.getResources();
			InputStream is_bbdata = res.openRawResource(resource);  // Resources.NotFoundException
			BBDataReader.read(is_bbdata);

		} catch(Resources.NotFoundException res_e) {
			Toast.makeText(this, "パーツ・武器のデータが見つかりません。", Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * アセンデータの初期化を行う。
	 */
	private void initCustomData() {
		BBDataManager data_mng = BBDataManager.getInstance();
		
		try {
			String filedir = getFilesDir().toString();
			Resources res = this.getResources();
			
			// デフォルトデータを読み込む
			InputStream is_defaultset = res.openRawResource(R.raw.defaultset);  // Resources.NotFoundException
			FileKeyValueStore default_data = new FileKeyValueStore(filedir);
			default_data.load(is_defaultset);
			CustomDataManager.setDefaultData(default_data);

			// カスタムデータの読み込み
			FileKeyValueStore file_data = new FileKeyValueStore(filedir);
			file_data.load();
			CustomData custom_data = CustomDataReader.read(file_data, default_data, data_mng);
			CustomDataManager.setCustomData(custom_data);

		} catch(Resources.NotFoundException res_e) {
			Toast.makeText(this, "リソースデータが見つかりません。", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 初回起動時に表示するダイアログを生成する。
	 * @return 初回起動時に表示するダイアログ
	 */
	private void showFirstDialog() {
		String update_info_str = loadUpdateText();

		// ダイアログを生成する
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("更新情報");
		builder.setMessage(update_info_str);
		builder.setPositiveButton("OK", null);

		Dialog dialog = builder.create();
		dialog.setOwnerActivity(this);
		dialog.show();
	}
	
	/**
	 * 更新情報のテキストを読み込む。
	 * @return 更新情報のテキスト
	 */
	private String loadUpdateText() {
		String update_info_str;

		// 更新情報を読み込む
		try {
			Resources res = this.getResources();
			InputStream update_info = res.openRawResource(R.raw.update_info);  // Resources.NotFoundException
			update_info_str = FileIO.readInputStream(update_info, FileIO.ENCODE_SJIS);

		} catch(Resources.NotFoundException res_e) {
			update_info_str = "更新情報を読み込めませんでした";
		}
		
		return update_info_str;
	}

	/**
	 * 共有アセンデータを設定する。
	 * 共有データが渡されていない場合は何も処理しない。
	 */
	private void setShareData(Intent intent) {
		if(intent == null) {
			return;
		}
		else if(intent.getAction() == null) {
			return;
		}
		
		if(intent.getAction().equals(Intent.ACTION_SEND)) {
			String code = intent.getExtras().getCharSequence(Intent.EXTRA_TEXT).toString();
			CustomData custom_data = CustomDataManager.getCustomData();
			int ret = custom_data.setCustomDataID(BBViewSettingManager.getVersionCode(this), (String) code);
			
			if(ret == CustomData.RET_SUCCESS) {
				Toast.makeText(this, "アセンデータを読み込みました", Toast.LENGTH_SHORT).show();
				Intent next_intent = new Intent(this, CustomMainActivity.class);
				startActivity(next_intent);
			}
			else if(ret == CustomData.ERROR_CODE_TARGET_NOTHING) {
				Toast.makeText(this, "アセンデータが存在しません", Toast.LENGTH_SHORT).show();
			}
			else if(ret == CustomData.ERROR_CODE_VERSION_NOT_EQUAL) {
				Toast.makeText(this, "バージョン情報が一致していません", Toast.LENGTH_SHORT).show();
			}
			else if(ret == CustomData.ERROR_CODE_ITEM_NOTHING) {
				Toast.makeText(this, "アセン情報の読み込みに失敗しました", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**
	 * オプションメニュー生成時の処理を行う。
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		menu.add(MENU_SETTING);
		
		SubMenu sub_menu = menu.addSubMenu(MENU_OTHER);
		sub_menu.add(MENU_OFFICIAL);
		sub_menu.add(MENU_BBNET);
		sub_menu.add(MENU_WIKI);
		sub_menu.add(MENU_ABOUT);
		
		return true;
	}
	
	/**
	 * オプションメニュー選択時の処理を行う。
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String menu_title = item.getTitle().toString();
		
		if(menu_title.equals(MENU_SETTING)) {
			Intent intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
		}
		else if(menu_title.equals(MENU_OFFICIAL)) {
			Uri uri = Uri.parse("http://borderbreak.com/");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
		else if(menu_title.equals(MENU_BBNET)) {
			Uri uri = Uri.parse("https://pc.borderbreak.net/bb_p/login/s3.jsp");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
		else if(menu_title.equals(MENU_WIKI)) {
			Uri uri = Uri.parse("http://www12.atwiki.jp/borderbreak/");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
		else if(menu_title.equals(MENU_ABOUT)) {
			Intent intent = new Intent(this, ManualActivity.class);
			startActivity(intent);
		}
		
		return true;
	}

	/**
	 * 機体カスタマイズ画面へのインテントを発行するリスナー
	 */
	private class OnMoveCustomListener implements OnClickListener {
		
		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(TopActivity.this, CustomMainActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * 所持品確認画面へのインテントを発行するリスナー
	 */
	private class OnMoveItemListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(TopActivity.this, CategoryListActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * 購入プレビュー画面へのインテントを発行するリスナー
	 */
	private class OnMovePurchaseListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(TopActivity.this, PurchaseListActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * 店舗検索画面へのインテントを発行するリスナー
	 */
	private class OnMoveShopListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(TopActivity.this, ShopMainActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * BBView開発室を表示するインテントを発行するリスナー
	 */
	private class OnMoveBlogListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Uri uri = Uri.parse("http://bbview.blog.fc2.com/");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	}

}
