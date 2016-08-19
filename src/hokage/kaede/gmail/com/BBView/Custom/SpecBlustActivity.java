package hokage.kaede.gmail.com.BBView.Custom;

import java.util.ArrayList;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.CustomData;
import hokage.kaede.gmail.com.BBViewLib.CustomDataManager;
import hokage.kaede.gmail.com.BBViewLib.SpecValues;
import hokage.kaede.gmail.com.BBViewLib.Android.BBViewSettingManager;
import hokage.kaede.gmail.com.BBViewLib.Android.BaseActivity;
import hokage.kaede.gmail.com.BBViewLib.Android.ViewBuilder;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.LinearLayout.LayoutParams;

public class SpecBlustActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener {

	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;

	private static final int TOGGLE_BUTTON_ASSALT_ID  = 1000;
	private static final int TOGGLE_BUTTON_HEAVY_ID   = 2000;
	private static final int TOGGLE_BUTTON_SNIPER_ID  = 3000;
	private static final int TOGGLE_BUTTON_SUPPORT_ID = 4000;
	private static final int TABLELAYOUT_ID           = 5000;
	
	private String mBlustType;

	/**
	 * アプリ起動時の処理を行う。
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBlustType = BBDataManager.BLUST_TYPE_ASSALT;
		
		// アプリ画面の生成
		createView();
	}
	
	public void createView() {
		// 画面下部のレイアウト
		LinearLayout bottom_layout = new LinearLayout(this);
		bottom_layout.setOrientation(LinearLayout.HORIZONTAL);

		ToggleButton assalt_button = new ToggleButton(this);
		assalt_button.setTextOn("強襲兵装");
		assalt_button.setTextOff("強襲兵装");
		assalt_button.setChecked(true);
		assalt_button.setId(TOGGLE_BUTTON_ASSALT_ID);
		assalt_button.setLayoutParams(new LayoutParams(WC, WC, 1));
		assalt_button.setOnClickListener(this);
		assalt_button.setOnCheckedChangeListener(this);
		bottom_layout.addView(assalt_button);
		
		ToggleButton heavy_button = new ToggleButton(this);
		heavy_button.setTextOn("重火力兵装");
		heavy_button.setTextOff("重火力兵装");
		heavy_button.setChecked(false);
		heavy_button.setId(TOGGLE_BUTTON_HEAVY_ID);
		heavy_button.setLayoutParams(new LayoutParams(WC, WC, 1));
		heavy_button.setOnClickListener(this);
		heavy_button.setOnCheckedChangeListener(this);
		bottom_layout.addView(heavy_button);
		
		ToggleButton sniper_button = new ToggleButton(this);
		sniper_button.setTextOn("遊撃兵装");
		sniper_button.setTextOff("遊撃兵装");
		sniper_button.setChecked(false);
		sniper_button.setId(TOGGLE_BUTTON_SNIPER_ID);
		sniper_button.setLayoutParams(new LayoutParams(WC, WC, 1));
		sniper_button.setOnClickListener(this);
		sniper_button.setOnCheckedChangeListener(this);
		bottom_layout.addView(sniper_button);

		ToggleButton support_button = new ToggleButton(this);
		support_button.setTextOn("支援兵装");
		support_button.setTextOff("支援兵装");
		support_button.setChecked(false);
		support_button.setId(TOGGLE_BUTTON_SUPPORT_ID);
		support_button.setLayoutParams(new LayoutParams(WC, WC, 1));
		support_button.setOnClickListener(this);
		support_button.setOnCheckedChangeListener(this);
		bottom_layout.addView(support_button);
		
		LinearLayout layout_all = new LinearLayout(this);
		layout_all.setOrientation(LinearLayout.VERTICAL);
		layout_all.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		layout_all.setId(TABLELAYOUT_ID);
		layout_all.addView(createSpecView());
		layout_all.addView(bottom_layout);
		
		// 全体レイアウトの画面表示
		setContentView(layout_all);
	}
	
	private View createSpecView() {
		// スペック管理クラスのロード
		CustomData custom_data = CustomDataManager.getCustomData();

		int color = Color.rgb(255, 255, 255);
		int bg_color = Color.rgb(60, 60, 180);

		LinearLayout layout_table = new LinearLayout(this);
		layout_table.setOrientation(LinearLayout.VERTICAL);
		layout_table.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));

		// アセンを画面に表示する
		TextView assemble_view = ViewBuilder.createTextView(this, "アセン", SettingManager.FLAG_TEXTSIZE_SMALL, color, bg_color);
		layout_table.addView(assemble_view);
		layout_table.addView(createAssembleView(custom_data));
		layout_table.addView(createChipTable(custom_data));

		// 機体性能を画面に表示する
		TextView parts_spec_view = ViewBuilder.createTextView(this, "機体性能", SettingManager.FLAG_TEXTSIZE_SMALL, color, bg_color);
		layout_table.addView(parts_spec_view);
		layout_table.addView(createPartsSpecView(custom_data));

		// 武器性能を画面に表示する
		TextView weapon_spec_view = ViewBuilder.createTextView(this, "武器性能", SettingManager.FLAG_TEXTSIZE_SMALL, color, bg_color);
		layout_table.addView(weapon_spec_view);
		layout_table.addView(createWeaponView(custom_data));

		ScrollView data_view = new ScrollView(this);
		data_view.addView(layout_table);
		data_view.setLayoutParams(new LayoutParams(FP, WC, 1));
		
		return data_view;
	}

	/**
	 * トグルボタンの表示を更新する。
	 * @param sb 
	 * @param sbr
	 * @param reqarm
	 */
	private void updateButton(boolean is_assalt, boolean is_heavy, boolean is_sniper, boolean is_support) {
		ToggleButton assalt_button = (ToggleButton)this.findViewById(TOGGLE_BUTTON_ASSALT_ID);
		assalt_button.setChecked(is_assalt);
		
		ToggleButton heavy_button = (ToggleButton)this.findViewById(TOGGLE_BUTTON_HEAVY_ID);
		heavy_button.setChecked(is_heavy);
		
		ToggleButton sniper_button = (ToggleButton)this.findViewById(TOGGLE_BUTTON_SNIPER_ID);
		sniper_button.setChecked(is_sniper);
		
		ToggleButton support_button = (ToggleButton)this.findViewById(TOGGLE_BUTTON_SUPPORT_ID);
		support_button.setChecked(is_support);
	}

	@Override
	public void onClick(View view) {
		if(view instanceof ToggleButton) {
			int id = view.getId();
			String old_blust_type = mBlustType;
			
			if(id == TOGGLE_BUTTON_ASSALT_ID) {
				updateButton(true, false, false, false);
				mBlustType = BBDataManager.BLUST_TYPE_ASSALT;
			}
			else if(id == TOGGLE_BUTTON_HEAVY_ID) {
				updateButton(false, true, false, false);
				mBlustType = BBDataManager.BLUST_TYPE_HEAVY;
			}
			else if(id == TOGGLE_BUTTON_SNIPER_ID) {
				updateButton(false, false, true, false);
				mBlustType = BBDataManager.BLUST_TYPE_SNIPER;
			}
			else if(id == TOGGLE_BUTTON_SUPPORT_ID) {
				updateButton(false, false, false, true);
				mBlustType = BBDataManager.BLUST_TYPE_SUPPORT;
			}
			
			if(!mBlustType.equals(old_blust_type)) {
				LinearLayout layout = (LinearLayout)this.findViewById(TABLELAYOUT_ID);
				layout.removeViews(0, 1);
				layout.addView(createSpecView(), 0);
			}
		}
	}
	
	/**
	 * トグルボタンのチェックが変更された場合の処理。既存の処理の実行を防ぐため、本関数では何も処理を行わない。
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// Do Nothing
	}

	private TableLayout createAssembleView(CustomData custom_data) {
		TableLayout table = new TableLayout(this);
		table.setLayoutParams(new TableLayout.LayoutParams(FP, WC));
		
		table.addView(createPartsRow(custom_data, BBDataManager.BLUST_PARTS_HEAD, BBDataManager.WEAPON_TYPE_MAIN));
		table.addView(createPartsRow(custom_data, BBDataManager.BLUST_PARTS_BODY, BBDataManager.WEAPON_TYPE_SUB));
		table.addView(createPartsRow(custom_data, BBDataManager.BLUST_PARTS_ARMS, BBDataManager.WEAPON_TYPE_SUPPORT));
		table.addView(createPartsRow(custom_data, BBDataManager.BLUST_PARTS_LEGS, BBDataManager.WEAPON_TYPE_SPECIAL));
		
		return table;
	}

	/**
	 * パーツスペックテーブルの行を生成する。
	 * @param data アセンデータ
	 * @param parts_type パーツの種類
	 * @return 指定のパーツ種類に対応する行
	 */
	private TableRow createPartsRow(CustomData custom_data, String parts_key, String weapon_key) {
		int[] colors = { 
				SettingManager.getColor(SettingManager.COLOR_YELLOW),
				SettingManager.getColor(SettingManager.COLOR_BASE),
				SettingManager.getColor(SettingManager.COLOR_YELLOW),
				SettingManager.getColor(SettingManager.COLOR_BASE)
		};
		BBData parts = custom_data.getParts(parts_key);
		BBData weapon = custom_data.getWeapon(mBlustType, weapon_key);
		return ViewBuilder.createTableRow(this, colors, parts_key, parts.get("名称"), weapon_key, weapon.get("名称"));
	}

	/**
	 * チップテーブルを生成する
	 * @param data データ
	 * @return チップデータのテーブル
	 */
	private LinearLayout createChipTable(CustomData custom_data) {
		LinearLayout layout_chip = new LinearLayout(this);
		layout_chip.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		layout_chip.setOrientation(LinearLayout.HORIZONTAL);
		layout_chip.setGravity(Gravity.LEFT | Gravity.TOP);

		ArrayList<BBData> chip_list = custom_data.getChips();
		int size = chip_list.size();
		
		String chip_names = "";
		for(int i=0; i<size; i++) {
			chip_names = chip_names + chip_list.get(i).get("名称");
			if(i<size-1) {
				chip_names = chip_names + ", ";
			}
		}
		
		if(size==0) {
			chip_names = "<チップ未設定>";
		}
		
		TextView chip_text_view = ViewBuilder.createTextView(this, chip_names, SettingManager.FLAG_TEXTSIZE_SMALL);
		
		layout_chip.addView(ViewBuilder.createTextView(this, "チップ", SettingManager.FLAG_TEXTSIZE_SMALL, SettingManager.getColor(SettingManager.COLOR_YELLOW)));
		layout_chip.addView(chip_text_view);
		
		return layout_chip;
	}

	
	private static final String[] BASE_COL_STR = { "装甲平均値", "総重量(猶予)", "低下率", "チップ容量"};
	private static final String[] HEAD_COL_STR = { "射撃補正", "索敵", "ロックオン", "DEF回復" };
	private static final String[] BODY_COL_STR = { "ブースター", "SP供給率", "エリア移動", "DEF耐久" };
	private static final String[] ARMS_COL_STR = { "反動吸収", "リロード", "武器変更", "予備弾倉" };
	private static final String[] LEGS_COL_STR = { "歩行", "ダッシュ", "重量耐性", "加速" };
	
	private TableLayout createPartsSpecView(CustomData custom_data) {
		TableLayout table = new TableLayout(this);
		table.setLayoutParams(new TableLayout.LayoutParams(FP, WC));
		
		String[] base_value_col_str = {
			getSpecString("装甲", custom_data.getArmorAve(mBlustType)),
			String.format("%d (%d)", custom_data.getPartsWeight(), custom_data.getSpacePartsWeight()),
			SpecValues.getSpecUnit(custom_data.getSpeedDownRate(mBlustType), "低下率", BBViewSettingManager.IS_KB_PER_HOUR),
			SpecValues.getSpecUnit(custom_data.getChipCapacity(), "チップ容量", BBViewSettingManager.IS_KB_PER_HOUR)
		};
		
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_YELLOW), BASE_COL_STR));
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_BASE), base_value_col_str));
		
		String[] head_value_col_str = {
			getSpecString("射撃補正", custom_data.getShotBonus(mBlustType)),
			getSpecString("索敵", custom_data.getSearch(mBlustType)),
			getSpecString("ロックオン", custom_data.getRockOn(mBlustType)),
			getSpecString("DEF回復", custom_data.getDefRecover(mBlustType))
		};

		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_YELLOW), HEAD_COL_STR));
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_BASE), head_value_col_str));

		String[] body_value_col_str = {
			getSpecString("ブースター", custom_data.getBoost(mBlustType)),
			getSpecString("SP供給率", custom_data.getSP(mBlustType)),
			getSpecString("エリア移動", custom_data.getAreaMove(mBlustType)),
			getSpecString("DEF耐久", custom_data.getDefGuard(mBlustType))
		};

		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_YELLOW), BODY_COL_STR));
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_BASE), body_value_col_str));

		String[] arms_value_col_str = {
			getSpecString("反動吸収", custom_data.getRecoil(mBlustType)),
			getSpecString("リロード", custom_data.getReload(mBlustType)),
			getSpecString("武器変更", custom_data.getChangeWeapon(mBlustType)),
			getSpecString("予備弾数", custom_data.getSpareBullet(mBlustType))
		};

		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_YELLOW), ARMS_COL_STR));
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_BASE), arms_value_col_str));

		String[] legs_value_col_str = {
			getSpecString("歩行", custom_data.getWalk(mBlustType)),
			getSpecString("ダッシュ", custom_data.getStartDush(mBlustType)),
			getSpecString("重量耐性", custom_data.getAntiWeight(mBlustType)),
			getSpecString("加速", custom_data.getAcceleration(mBlustType))
		};

		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_YELLOW), LEGS_COL_STR));
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_BASE), legs_value_col_str));
		
		return table;
	}
	
	private String getSpecString(String target_key, double value) {
		String blust_point = SpecValues.getPoint(target_key, value, BBViewSettingManager.IS_KB_PER_HOUR);
		String blust_str = SpecValues.getSpecUnit(value, target_key, BBViewSettingManager.IS_KB_PER_HOUR);
		return blust_point + " (" + blust_str + ")";
	}

	/**
	 * 武器スペックテーブルを生成する。(マガジン火力、瞬間火力、戦術火力、リロード時間)
	 * @param data データ
	 * @return 武器スペックテーブル
	 */
	private TableLayout createWeaponView(CustomData data) {
		TableLayout table = new TableLayout(this);
		table.setLayoutParams(new TableLayout.LayoutParams(FP, WC));
		
		int weapon_list_len = BBDataManager.WEAPON_TYPE_LIST.length;
		for(int weapon_idx=0; weapon_idx<weapon_list_len; weapon_idx++) {
			String weapon_type = BBDataManager.WEAPON_TYPE_LIST[weapon_idx];
			BBData weapon = data.getWeapon(mBlustType, weapon_type);

			if(weapon.existKey("リロード時間")) {
				addReloadWeaponRow(table, data, weapon_type, weapon);
			}
			else if(weapon.existCategory("アサルトチャージャー系統")) {
				addACRow(table, data, weapon_type, weapon);
			}
			else if(
				weapon.existCategory("リペアユニット系統") ||
				weapon.existCategory("リペアポスト系統") ||
				weapon.existCategory("リペアショット系統") ||
				weapon.existCategory("リペアフィールド系統") ||
				weapon.existCategory("リペアセントリー系統") ||
				weapon.existCategory("リペアインジェクター系統")) {
				
				addReapirRow(table, data, weapon_type, weapon);
			}
			else if(weapon.existKey("チャージ時間")) {
				addExtraRow(table, data, weapon_type, weapon);
			}
		}
		
		return table;
	}

	private static final String[] WEAPON_MAIN_ROW = { "", "マガジン火力", "瞬間火力", "戦術火力", "リロード時間", "総弾数" };
	
	private void addReloadWeaponRow(TableLayout table, CustomData data, String weapon_type, BBData weapon) {
		double magazine_power = data.getMagazinePower(weapon);
		double sec01_power = data.get1SecPower(weapon);
		//double sec10_power = data.get10SecPower(weapon);
		double battle_power = data.getBattlePower(weapon);
		double reload_time = data.getReloadTime(weapon);
		
		/* 総弾数の文字列を生成する */
		double sum_bullet = data.getBulletSum(weapon);
		double magazine_bullet = weapon.getMagazine();
		double over_bullet = sum_bullet % magazine_bullet;
		double magazine_count = Math.floor(sum_bullet / magazine_bullet);
		
		String bullet_str = "";
		if(magazine_bullet == 1) {
			bullet_str = String.format("1x%.0f", sum_bullet);
		}
		else {
			bullet_str = String.format("%.0fx%.0f +%.0f", magazine_bullet, magazine_count, over_bullet);
		}
		
		String[] cols = { 
				weapon.get("名称"),
				String.format("%.0f", magazine_power), 
				String.format("%.0f", sec01_power), 
				String.format("%.0f", battle_power),
				//String.format("%.0f", sec10_power), 
				String.format("%.1f(秒)", reload_time),
				bullet_str
		};

		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_YELLOW), WEAPON_MAIN_ROW));
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_BASE), cols));
	}

	private static final String[] WEAPON_AC_ROW = { "", "AC速度", "AC戦術速度", "チャージ時間" };
	
	private void addACRow(TableLayout table, CustomData data, String weapon_type, BBData weapon) {
		double ac_speed = data.getACSpeed(weapon);
		double ac_battle_speed = data.getBattleACSpeed(weapon);
		double sp_charge_time = data.getChargeTime(mBlustType, weapon);
		
		String[] cols = {
				weapon.get("名称"),
				// 速度の単位を取得するため、初速のキーを用いる。APIの見直しが必要。
				SpecValues.getSpecUnit(ac_speed, "初速", BBViewSettingManager.IS_KB_PER_HOUR),
				SpecValues.getSpecUnit(ac_battle_speed, "初速", BBViewSettingManager.IS_KB_PER_HOUR),
				SpecValues.getSpecUnit(sp_charge_time, "時間", BBViewSettingManager.IS_KB_PER_HOUR),				
		};

		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_YELLOW), WEAPON_AC_ROW));
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_BASE), cols));
	}

	private static final String[] WEAPON_REPAIR_ROW = { "", "最大回復量", "チャージ時間" };
	
	private void addReapirRow(TableLayout table, CustomData data, String weapon_type, BBData weapon) {
		double max_repair = data.getMaxRepair(weapon);
		double sp_charge_time = data.getChargeTime(mBlustType, weapon);
		
		String[] cols = {
				weapon.get("名称"),
				SpecValues.getSpecUnit(max_repair, "最大回復量", BBViewSettingManager.IS_KB_PER_HOUR),
				SpecValues.getSpecUnit(sp_charge_time, "チャージ時間", BBViewSettingManager.IS_KB_PER_HOUR),				
		};

		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_YELLOW), WEAPON_REPAIR_ROW));
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_BASE), cols));
	}

	private static final String[] WEAPON_EXTRA_ROW = { "", "チャージ時間" };
	
	private void addExtraRow(TableLayout table, CustomData data, String weapon_type, BBData weapon) {
		double sp_charge_time = data.getChargeTime(mBlustType, weapon);
		
		String[] cols = {
				weapon.get("名称"),
				SpecValues.getSpecUnit(sp_charge_time, "チャージ時間", BBViewSettingManager.IS_KB_PER_HOUR),				
		};

		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_YELLOW), WEAPON_EXTRA_ROW));
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_BASE), cols));
	}
}
