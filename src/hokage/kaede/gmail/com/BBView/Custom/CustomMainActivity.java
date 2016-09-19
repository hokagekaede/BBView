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
	
	// メイン画面のレイアウト
	private LinearLayout mLayout;
	
	/**
	 * 画面生成時の処理を行う。
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle(this.getTitle() + " (機体カスタマイズ)");
		
		mViewMode = VIEWMODE_STR_CUSTOM;

		// メインの画面
		mLayout = new LinearLayout(this);
		mLayout.setOrientation(LinearLayout.VERTICAL);
		mLayout.setGravity(Gravity.TOP | Gravity.LEFT);
		
		setContentView(mLayout);
	}
	
	/**
	 * 画面再表示時の処理を行う。
	 * 画面の各数値を更新する。
	 */
	@Override
	protected void onResume() {
		super.onResume();
		updateView();
	}
	
	/**
	 * 画面非表示字の処理を行う。
	 * SBや要請兵器の所持状況をリセットする。
	 */
	protected void onPause() {
		super.onPause();
		CustomData custom_data = CustomDataManager.getCustomData();
		custom_data.setHavingExtraItem(CustomData.HAVING_NOTHING);
	}
	
	private void updateView() {
		CustomData custom_data = CustomDataManager.getCustomData();
		custom_data.setHavingExtraItem(CustomData.HAVING_NOTHING);
		
		mLayout.removeAllViews();
		mLayout.addView(createTopLayout());

		if(mViewMode.equals(VIEWMODE_STR_CUSTOM)) {
			mLayout.addView(new CustomView(this, custom_data, mIsShowChips));
		}
		else if(mViewMode.equals(VIEWMODE_STR_CHIP)) {
			mLayout.addView(new ChipView(this));
		}
		else if(mViewMode.equals(VIEWMODE_STR_SPEC)) {
			mLayout.addView(new SpecView(this));
		}
		else if(mViewMode.equals(VIEWMODE_STR_RESIST)) {
			mLayout.addView(new ResistView(this));
		}
		else if(mViewMode.equals(VIEWMODE_STR_FILE)) {
			mLayout.addView(new FileListView(this));
		}
	}
	
	/**
	 * 画面上部のレイアウトを生成する。
	 * @return
	 */
	private LinearLayout createTopLayout() {
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setGravity(Gravity.CENTER);
		layout.setBackgroundColor(SettingManager.getColorGray());
		
		LinearLayout.LayoutParams layout_prm = 
			new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,	1);
		
		int text_color = SettingManager.getColorWhite();
		float text_size = (float)15.0;
		
		TextView custom_text_view = new TextView(this);
		custom_text_view.setTextColor(text_color);
		custom_text_view.setPadding(5, 15, 5, 15);
		custom_text_view.setLayoutParams(layout_prm);
		custom_text_view.setGravity(Gravity.CENTER);
		custom_text_view.setText(VIEWMODE_STR_CUSTOM);
		custom_text_view.setTextSize(text_size);
		custom_text_view.setOnClickListener(new OnClickTopMenuListener(VIEWMODE_STR_CUSTOM));
		layout.addView(custom_text_view);

		TextView chip_text_view = new TextView(this);
		chip_text_view.setTextColor(text_color);
		chip_text_view.setPadding(5, 15, 5, 15);
		chip_text_view.setLayoutParams(layout_prm);
		chip_text_view.setGravity(Gravity.CENTER);
		chip_text_view.setText(VIEWMODE_STR_CHIP);
		chip_text_view.setTextSize(text_size);
		chip_text_view.setOnClickListener(new OnClickTopMenuListener(VIEWMODE_STR_CHIP));
		layout.addView(chip_text_view);

		TextView spec_text_view = new TextView(this);
		spec_text_view.setTextColor(text_color);
		spec_text_view.setPadding(5, 15, 5, 15);
		spec_text_view.setLayoutParams(layout_prm);
		spec_text_view.setGravity(Gravity.CENTER);
		spec_text_view.setText(VIEWMODE_STR_SPEC);
		spec_text_view.setTextSize(text_size);
		spec_text_view.setOnClickListener(new OnClickTopMenuListener(VIEWMODE_STR_SPEC));
		layout.addView(spec_text_view);

		TextView resist_text_view = new TextView(this);
		resist_text_view.setTextColor(text_color);
		resist_text_view.setPadding(5, 15, 5, 15);
		resist_text_view.setLayoutParams(layout_prm);
		resist_text_view.setGravity(Gravity.CENTER);
		resist_text_view.setText(VIEWMODE_STR_RESIST);
		resist_text_view.setTextSize(text_size);
		resist_text_view.setOnClickListener(new OnClickTopMenuListener(VIEWMODE_STR_RESIST));
		layout.addView(resist_text_view);

		TextView file_text_view = new TextView(this);
		file_text_view.setTextColor(text_color);
		file_text_view.setPadding(5, 15, 5, 15);
		file_text_view.setLayoutParams(layout_prm);
		file_text_view.setGravity(Gravity.CENTER);
		file_text_view.setText(VIEWMODE_STR_FILE);
		file_text_view.setTextSize(text_size);
		file_text_view.setOnClickListener(new OnClickTopMenuListener(VIEWMODE_STR_FILE));
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
			updateView();
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
			if(item.isChecked()) {
				mIsShowChips = false;
				item.setChecked(false);
				updateView();
			}
			else {
				mIsShowChips = true;
				item.setChecked(true);
				updateView();
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
				mViewMode = VIEWMODE_STR_CUSTOM;
				updateView();
				return false;
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}
}
