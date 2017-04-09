package hokage.kaede.gmail.com.BBView.Item;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataComparator;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.BBViewSetting;
import hokage.kaede.gmail.com.BBViewLib.SpecValues;
import hokage.kaede.gmail.com.BBViewLib.Android.BaseActivity;
import hokage.kaede.gmail.com.BBViewLib.Android.IntentManager;
import hokage.kaede.gmail.com.BBViewLib.Android.ViewBuilder;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;

public class InfoActivity extends BaseActivity {
	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;
	
	// スペック表示画面のID
	private static final int VIEWID_SHOWSPEC  = 100;
	private static final int VIEWID_WEAPONSIM = 200;

	// オプションメニューのID
	private static final int MENU_ITEM0 = 0;
	private static final int MENU_ITEM1 = 1;
	
	// 表示するデータ
	private BBData mTargetData;
	
	private boolean isShowingTypeB = false;
	private boolean isShowingWeaponSim = false;
	
	
	/**
	 * アプリ起動時の処理を行う。
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BBData data = IntentManager.getSelectedData(getIntent());
		
		setTitle(getTitle() + " (項目詳細情報)");

		// データがnullの場合はアクティビティを終了する
		if(data == null) {
			finish();
		}
		
		mTargetData = data;

		createView(data);
	}

	/**
	 * 画面全体の生成処理を行う。
	 * @param data
	 */
	private void createView(BBData data) {
		LinearLayout layout_all = new LinearLayout(this);
		layout_all.setOrientation(LinearLayout.VERTICAL);
		layout_all.setGravity(Gravity.LEFT | Gravity.TOP);

		LinearLayout layout_spec = new LinearLayout(this);
		layout_spec.setId(VIEWID_SHOWSPEC);
		layout_spec.setOrientation(LinearLayout.VERTICAL);
		layout_spec.setGravity(Gravity.LEFT | Gravity.TOP);
		
		setSpecView(layout_spec, data);
		
		WeaponSimView sim_view = new WeaponSimView(this, mTargetData);
		sim_view.setId(VIEWID_WEAPONSIM);
		sim_view.setVisibility(View.GONE);
		
		layout_all.addView(layout_spec);
		layout_all.addView(sim_view);

		// 全体レイアウトの画面表示
		setContentView(layout_all);
	} 

	/**
	 * データ表示部分の画面生成処理を行う。
	 * @param data 表示するアイテム
	 */
	private void setSpecView(LinearLayout layout, BBData data) {
		String title = data.get("名称");
		
		String category_str = "";
		ArrayList<String> categorys = data.getCategorys();
		int cate_len = categorys.size();
		for(int i=0; i<cate_len; i++) {
			String buf = categorys.get(i);
			if(buf != null) {
				category_str = category_str + " - " + buf + "\n";
			}
		}

		// タイトルとアイテム詳細情報を表示する
		layout.addView(ViewBuilder.createTextView(this, title, BBViewSetting.FLAG_TEXTSIZE_LARGE));
		layout.addView(ViewBuilder.createTextView(this, category_str, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
		layout.addView(createItemInfoTable(data));
	}
	
	private static final String[] CALC_KEYS = {
		BBData.REAL_LIFE_KEY,
		BBData.DEF_RECORVER_TIME_KEY,
		BBData.FULL_POWER_KEY,
		BBData.MAGAZINE_POWER_KEY,
		BBData.SEC_POWER_KEY,
		BBData.BATTLE_POWER_KEY,
		BBData.OH_POWER_KEY,
		BBData.BULLET_SUM_KEY,
		BBData.CARRY_KEY,
		BBData.FLAIGHT_TIME_KEY,
		BBData.SEARCH_SPACE_KEY,
		BBData.SEARCH_SPACE_START_KEY,
		BBData.SEARCH_SPACE_MAX_KEY,
		BBData.SEARCH_SPACE_TIME_KEY
	};

	/**
	 * アイテム詳細情報のテーブルを生成する
	 * @param data アイテム情報
	 * @return アイテム詳細情報のテーブル
	 */
	public TableLayout createItemInfoTable(BBData data) {
		TableLayout layout_table = new TableLayout(this);
		layout_table.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		layout_table.setColumnShrinkable(1, true);    // 右端は表を折り返す

		// 表のタイトルを記載する
		layout_table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorYellow(), "項目", "説明"));

		ArrayList<String> keys = data.getKeys();
		int size = keys.size();

		// 一般情報を表示
		for(int i=0; i<size; i++) {
			String target_key = keys.get(i);
			String point = data.get(target_key);

			if(target_key.equals("名称")) {
				continue;
			}
			else if(point == null) {
				continue;
			}

			String data_str = SpecValues.getSpecUnit(data, target_key, BBViewSetting.IS_KM_PER_HOUR);
			
			if(BBDataComparator.isPointKey(target_key)) {
				layout_table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorWhite(), target_key, point + " (" + data_str + ")"));
			}
			else {
				layout_table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorWhite(), target_key, data_str));
			}
		}
		
		// 追加情報の表示
		size = CALC_KEYS.length;
		for(int i=0; i<size ; i++) {
			String key = CALC_KEYS[i];
			double num = data.getCalcValue(key);
			String value_str = SpecValues.getSpecUnit(num, key, BBViewSetting.IS_KM_PER_HOUR);
			
			if(num > BBData.NUM_VALUE_NOTHING) {
				layout_table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorCyan(), key, value_str));
			}
		}
		
		// セットボーナス情報を表示する
		if(BBDataManager.isParts(data)) {
			String bonus = data.getSetBonus();
			layout_table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorCyan(), "セットボーナス", bonus));
		}
		
		return layout_table;
	}

	/**
	 * オプションメニュー生成時の処理を行う。
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		if(mTargetData.getTypeB() != null) {
			MenuItem item = menu.add(0, MENU_ITEM0, 0, "タイプB表示");
			item.setCheckable(true);
		}
		
		if(mTargetData.existCategory("主武器")) {
			MenuItem item = menu.add(0, MENU_ITEM1, 0, "武器シミュ表示");
			item.setCheckable(true);
		}
		
		return true;
	}

	/**
	 * オプションメニュータップ時の処理を行う。
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case MENU_ITEM0:
				changeShownWeaponType();
				item.setChecked(isShowingTypeB);
				break;
				
			case MENU_ITEM1:
				changeShownWeaponSim();
				item.setChecked(isShowingWeaponSim);
				break;
		}
		
		return true;
	}
	
	/**
	 * タイプA/タイプBの表示切り替えを行う。
	 */
	private void changeShownWeaponType() {
		LinearLayout layout_spec = (LinearLayout)(InfoActivity.this.findViewById(VIEWID_SHOWSPEC));
		WeaponSimView weapon_sim_view = (WeaponSimView)(InfoActivity.this.findViewById(VIEWID_WEAPONSIM));
		
		layout_spec.removeAllViews();
		
		BBData data = mTargetData;
		
		weapon_sim_view.setData(data);
		
		if(isShowingTypeB) {
			isShowingTypeB = false;
			setSpecView(layout_spec, data);
		}
		else {
			isShowingTypeB = true;
			setSpecView(layout_spec, data.getTypeB());	// スイッチ武器のみメニューを表示するので、nullチェックはしない
		}
	}
	
	/**
	 * 武器シミュレータの表示切替を行う。
	 */
	private void changeShownWeaponSim() {
		LinearLayout layout_spec = (LinearLayout)(InfoActivity.this.findViewById(VIEWID_SHOWSPEC));
		WeaponSimView weapon_sim_view = (WeaponSimView)(InfoActivity.this.findViewById(VIEWID_WEAPONSIM));
		
		if(isShowingWeaponSim) {
			isShowingWeaponSim = false;
			layout_spec.setVisibility(View.VISIBLE);
			weapon_sim_view.setVisibility(View.GONE);
		}
		else {
			isShowingWeaponSim = true;
			layout_spec.setVisibility(View.GONE);
			weapon_sim_view.setVisibility(View.VISIBLE);
		}
		
	}
}
