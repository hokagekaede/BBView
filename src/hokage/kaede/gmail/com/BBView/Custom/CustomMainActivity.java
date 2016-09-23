package hokage.kaede.gmail.com.BBView.Custom;

import hokage.kaede.gmail.com.BBViewLib.CustomData;
import hokage.kaede.gmail.com.BBViewLib.CustomDataManager;
import hokage.kaede.gmail.com.BBViewLib.Android.BBViewSettingManager;
import hokage.kaede.gmail.com.BBViewLib.Android.BaseActivity;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CustomMainActivity extends BaseActivity {

	private String mViewMode;
	private static final String VIEWMODE_STR_CUSTOM = "アセン";
	private static final String VIEWMODE_STR_CHIP   = "チップ";
	private static final String VIEWMODE_STR_SPEC   = "性能";
	private static final String VIEWMODE_STR_RESIST = "耐性";
	private static final String VIEWMODE_STR_FILE   = "データ";

	private static final String MENU_SHOW_CHIPS      = "チップを表示する";
	private static final String MENU_SHARE           = "アセン共有";
	
	private boolean mIsShowChips = false;
	
	// メイン画面のレイアウトID
	private int MAIN_LAYOUT_ID = 1000;
	
	/**
	 * 画面生成時の処理を行う。
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle(this.getTitle() + " (機体カスタマイズ)");
		
		mViewMode = VIEWMODE_STR_CUSTOM;

		// メインの画面
		LinearLayout main_layout = new LinearLayout(this);
		main_layout.setOrientation(LinearLayout.VERTICAL);
		main_layout.setGravity(Gravity.TOP | Gravity.LEFT);
		main_layout.setId(MAIN_LAYOUT_ID);
		
		updateView(main_layout);
		
		setContentView(main_layout);
	}
	
	/**
	 * 画面再表示時の処理を行う。
	 * アセン画面からパーツ/武器を選択し、戻ってきた際に画面を更新する。
	 */
	@Override
	protected void onRestart() {
		super.onRestart();

		LinearLayout main_layout = (LinearLayout)CustomMainActivity.this.findViewById(MAIN_LAYOUT_ID);
		updateView(main_layout);
	}
	
	/**
	 * 画面非表示字の処理を行う。
	 * SBや要請兵器の所持状況をリセットする。
	 * 
	 * onResume()時は所持状況ボタンON/OFF状態を含めて画面表示が保持されているため、
	 * 画面を再構築する必要はない。
	 */
	protected void onPause() {
		super.onPause();
		CustomData custom_data = CustomDataManager.getCustomData();
		custom_data.setMode(CustomData.MODE_NORMAL);
	}
	
	/**
	 * 画面を更新する。
	 * 現状、全てのビューを削除し、新たに生成した画面を追加する方法で実装している。
	 * @param main_layout ビューを載せる対象のレイアウト
	 */
	private void updateView(LinearLayout main_layout) {
		CustomData custom_data = CustomDataManager.getCustomData();
		custom_data.setMode(CustomData.MODE_NORMAL);
		
		main_layout.removeAllViews();
		main_layout.addView(createTopLayout());

		if(mViewMode.equals(VIEWMODE_STR_CUSTOM)) {
			main_layout.addView(new CustomView(this, custom_data, mIsShowChips));
		}
		else if(mViewMode.equals(VIEWMODE_STR_CHIP)) {
			main_layout.addView(new ChipView(this));
		}
		else if(mViewMode.equals(VIEWMODE_STR_SPEC)) {
			main_layout.addView(new SpecView(this));
		}
		else if(mViewMode.equals(VIEWMODE_STR_RESIST)) {
			main_layout.addView(new ResistView(this));
		}
		else if(mViewMode.equals(VIEWMODE_STR_FILE)) {
			main_layout.addView(new FileListView(this));
		}
	}
	
	/**
	 * 画面上部のレイアウトを生成する。
	 * @return
	 */
	private LinearLayout createTopLayout() {
		LinearLayout.LayoutParams layout_prm = 
			new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,	1);
		
		int text_color = SettingManager.getColorWhite();
		float text_size = SettingManager.getTextSize(this);
		
		TextView custom_text_view = new TextView(this);
		custom_text_view.setTextColor(text_color);
		custom_text_view.setPadding(5, 15, 5, 15);
		custom_text_view.setLayoutParams(layout_prm);
		custom_text_view.setGravity(Gravity.CENTER);
		custom_text_view.setText(VIEWMODE_STR_CUSTOM);
		custom_text_view.setTextSize(text_size);
		custom_text_view.setOnClickListener(new OnClickTopMenuListener(VIEWMODE_STR_CUSTOM));

		TextView chip_text_view = new TextView(this);
		chip_text_view.setTextColor(text_color);
		chip_text_view.setPadding(5, 15, 5, 15);
		chip_text_view.setLayoutParams(layout_prm);
		chip_text_view.setGravity(Gravity.CENTER);
		chip_text_view.setText(VIEWMODE_STR_CHIP);
		chip_text_view.setTextSize(text_size);
		chip_text_view.setOnClickListener(new OnClickTopMenuListener(VIEWMODE_STR_CHIP));

		TextView spec_text_view = new TextView(this);
		spec_text_view.setTextColor(text_color);
		spec_text_view.setPadding(5, 15, 5, 15);
		spec_text_view.setLayoutParams(layout_prm);
		spec_text_view.setGravity(Gravity.CENTER);
		spec_text_view.setText(VIEWMODE_STR_SPEC);
		spec_text_view.setTextSize(text_size);
		spec_text_view.setOnClickListener(new OnClickTopMenuListener(VIEWMODE_STR_SPEC));

		TextView resist_text_view = new TextView(this);
		resist_text_view.setTextColor(text_color);
		resist_text_view.setPadding(5, 15, 5, 15);
		resist_text_view.setLayoutParams(layout_prm);
		resist_text_view.setGravity(Gravity.CENTER);
		resist_text_view.setText(VIEWMODE_STR_RESIST);
		resist_text_view.setTextSize(text_size);
		resist_text_view.setOnClickListener(new OnClickTopMenuListener(VIEWMODE_STR_RESIST));

		TextView file_text_view = new TextView(this);
		file_text_view.setTextColor(text_color);
		file_text_view.setPadding(5, 15, 5, 15);
		file_text_view.setLayoutParams(layout_prm);
		file_text_view.setGravity(Gravity.CENTER);
		file_text_view.setText(VIEWMODE_STR_FILE);
		file_text_view.setTextSize(text_size);
		file_text_view.setOnClickListener(new OnClickTopMenuListener(VIEWMODE_STR_FILE));
		
		// 選択中の場合、文字色と背景色を変更して見た目を強調する
		if(mViewMode.equals(VIEWMODE_STR_CUSTOM)) {
			custom_text_view.setTextColor(SettingManager.getColorCyan());
			custom_text_view.setBackgroundColor(SettingManager.getColorBlack());
		}
		else if(mViewMode.equals(VIEWMODE_STR_CHIP)) {
			chip_text_view.setTextColor(SettingManager.getColorCyan());
			chip_text_view.setBackgroundColor(SettingManager.getColorBlack());
		}
		else if(mViewMode.equals(VIEWMODE_STR_SPEC)) {
			spec_text_view.setTextColor(SettingManager.getColorCyan());
			spec_text_view.setBackgroundColor(SettingManager.getColorBlack());
		}
		else if(mViewMode.equals(VIEWMODE_STR_RESIST)) {
			resist_text_view.setTextColor(SettingManager.getColorCyan());
			resist_text_view.setBackgroundColor(SettingManager.getColorBlack());
		}
		else if(mViewMode.equals(VIEWMODE_STR_FILE)) {
			file_text_view.setTextColor(SettingManager.getColorCyan());
			file_text_view.setBackgroundColor(SettingManager.getColorBlack());
		}

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setGravity(Gravity.CENTER);
		layout.setBackgroundColor(SettingManager.getColorGray());
		
		layout.addView(custom_text_view);
		layout.addView(chip_text_view);
		layout.addView(spec_text_view);
		layout.addView(resist_text_view);
		layout.addView(file_text_view);
		
		return layout;
	}
	
	/**
	 * トップメニューを押下した際の処理を行うリスナー。
	 */
	private class OnClickTopMenuListener implements OnClickListener {
		private String mMenuName;
		
		public OnClickTopMenuListener(String menu_name) {
			mMenuName = menu_name;
		}
 
		@Override
		public void onClick(View arg0) {
			mViewMode = mMenuName;
			LinearLayout main_layout = (LinearLayout)CustomMainActivity.this.findViewById(MAIN_LAYOUT_ID);
			updateView(main_layout);
		}
	}
	
	/**
	 * オプションメニュー生成時の処理を行う。
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		MenuItem item = menu.add(MENU_SHOW_CHIPS);
		item.setCheckable(true);
		item.setChecked(mIsShowChips);
		item.setOnMenuItemClickListener(new OnMenuShowChipListener());
		
		menu.add(MENU_SHARE);
		
		return true;
	}
	
	/**
	 * チップを表示するのメニュー選択時の処理を行う。
	 */
	private class OnMenuShowChipListener implements OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			LinearLayout main_layout = (LinearLayout)CustomMainActivity.this.findViewById(MAIN_LAYOUT_ID);
			
			if(item.isChecked()) {
				mIsShowChips = false;
				item.setChecked(false);
				updateView(main_layout);
			}
			else {
				mIsShowChips = true;
				item.setChecked(true);
				updateView(main_layout);
			}
			
			return false;
		}
	}
	
	/**
	 * オプションメニュー選択時の処理を行う。
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String menu_title = item.getTitle().toString();

		if(menu_title.equals(MENU_SHARE)) {
			Toast.makeText(this, "Twitterアプリを選択してください。", Toast.LENGTH_LONG).show();
			
			CustomData custom_data = CustomDataManager.getCustomData();
			
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, custom_data.getCustomDataID(BBViewSettingManager.getVersionCode(this)));
			startActivity(intent);
		}
		
		return true;
	}
	
	/**
	 * Backキーを押下した際の処理を行う。
	 * アセン画面の場合はトップ画面に遷移し、アセン画面以外の場合はアセン画面に遷移する。
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(mViewMode != VIEWMODE_STR_CUSTOM) {
				LinearLayout main_layout = (LinearLayout)CustomMainActivity.this.findViewById(MAIN_LAYOUT_ID);
				
				mViewMode = VIEWMODE_STR_CUSTOM;
				updateView(main_layout);
				return false;
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}
}
