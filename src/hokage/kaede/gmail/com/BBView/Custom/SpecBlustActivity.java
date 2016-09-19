package hokage.kaede.gmail.com.BBView.Custom;

import java.util.ArrayList;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.CustomData;
import hokage.kaede.gmail.com.BBViewLib.CustomDataManager;
import hokage.kaede.gmail.com.BBViewLib.SpecValues;
import hokage.kaede.gmail.com.BBViewLib.Android.BBViewSettingManager;
import hokage.kaede.gmail.com.BBViewLib.Android.BaseActivity;
import hokage.kaede.gmail.com.BBViewLib.Android.SpecArray;
import hokage.kaede.gmail.com.BBViewLib.Android.ViewBuilder;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;
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

		int color = SettingManager.getColorWhite();
		int bg_color = SettingManager.getColorBlue();

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
				SettingManager.getColorYellow(),
				SettingManager.getColorWhite(),
				SettingManager.getColorYellow(),
				SettingManager.getColorWhite()
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
		
		layout_chip.addView(ViewBuilder.createTextView(this, "チップ", SettingManager.FLAG_TEXTSIZE_SMALL, SettingManager.getColorYellow()));
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
		
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorYellow(), BASE_COL_STR));
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorWhite(), base_value_col_str));
		
		String[] head_value_col_str = {
			getSpecString("射撃補正", custom_data.getShotBonus(mBlustType)),
			getSpecString("索敵", custom_data.getSearch(mBlustType)),
			getSpecString("ロックオン", custom_data.getRockOn(mBlustType)),
			getSpecString("DEF回復", custom_data.getDefRecover(mBlustType))
		};

		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorYellow(), HEAD_COL_STR));
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorWhite(), head_value_col_str));

		String[] body_value_col_str = {
			getSpecString("ブースター", custom_data.getBoost(mBlustType)),
			getSpecString("SP供給率", custom_data.getSP(mBlustType)),
			getSpecString("エリア移動", custom_data.getAreaMove(mBlustType)),
			getSpecString("DEF耐久", custom_data.getDefGuard(mBlustType))
		};

		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorYellow(), BODY_COL_STR));
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorWhite(), body_value_col_str));

		String[] arms_value_col_str = {
			getSpecString("反動吸収", custom_data.getRecoil(mBlustType)),
			getSpecString("リロード", custom_data.getReload(mBlustType)),
			getSpecString("武器変更", custom_data.getChangeWeapon(mBlustType)),
			getSpecString("予備弾数", custom_data.getSpareBullet(mBlustType))
		};

		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorYellow(), ARMS_COL_STR));
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorWhite(), arms_value_col_str));

		String[] legs_value_col_str = {
			getSpecString("歩行", custom_data.getWalk(mBlustType)),
			getSpecString("ダッシュ", custom_data.getStartDush(mBlustType)),
			getSpecString("重量耐性", custom_data.getAntiWeight(mBlustType)),
			getSpecString("加速", custom_data.getAcceleration(mBlustType))
		};

		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorYellow(), LEGS_COL_STR));
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorWhite(), legs_value_col_str));
		
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

			if(mBlustType.equals("強襲兵装")) {
				if(weapon.existCategory("主武器")) {
					addReloadWeaponRow(table, data, weapon);
				}
				else if(weapon.existCategory("副武器")) {
					addSubWeaponRow(table, data, weapon);
				}
				else if(weapon.existCategory("補助装備")) {
					addSlashRow(table, data, weapon);
				}
				else if(weapon.existCategory("特別装備")) {
					addACRow(table, data, weapon);
				}
			}
			else if(mBlustType.equals("重火力兵装")) {
				if(weapon.existCategory("主武器")) {
					addReloadWeaponRow(table, data, weapon);
				}
				else if(weapon.existCategory("副武器")) {
					addSubWeaponRow(table, data, weapon);
				}
				else if(weapon.existCategory("補助装備")) {
					if(weapon.existCategory("パイク系統") || weapon.existCategory("チェーンソー系統")) {
						addSlashRow(table, data, weapon);
					}
					else if(weapon.existCategory("ハウルHSG系統")) {
						addReloadWeaponRow(table, data, weapon);
					}
					else {
						addSupportBombRow(table, data, weapon);
					}
				}
				else if(weapon.existCategory("特別装備")) {
					if(weapon.existCategory("バリアユニット系統")) {
						addBarrierRow(table, data, weapon);
					}
					else {
						addCannonRow(table, data, weapon);
					}
				}
			}
			else if(mBlustType.equals("遊撃兵装")) {
				if(weapon.existCategory("主武器")) {
					addReloadWeaponRow(table, data, weapon);
				}
				else if(weapon.existCategory("副武器")) {
					addReloadWeaponRow(table, data, weapon);
				}
				else if(weapon.existCategory("補助装備")) {
					if(weapon.existCategory("偵察機系統") || weapon.existCategory("レーダーユニット系統") ||
					   weapon.existCategory("NDディテクター系統") || weapon.existCategory("クリアリングソナー系統")) {
						addSearchRow(table, data, weapon);
					}
					else {
						addReloadWeaponRow(table, data, weapon);
					}
				}
				else if(weapon.existCategory("特別装備")) {
					if(weapon.existCategory("EUS系統")) {
						addEUSRow(table, data, weapon);
					}
					else if(weapon.existCategory("シールド系統")) {
						addBarrierRow(table, data, weapon);
					}
					else {
						addExtraRow(table, data, weapon);
					}
				}
			}
			else if(mBlustType.equals("支援兵装")) {
				if(weapon.existCategory("主武器")) {
					addReloadWeaponRow(table, data, weapon);
				}
				else if(weapon.existCategory("副武器")) {
					addSubWeaponRow(table, data, weapon);
				}
				else if(weapon.existCategory("補助装備")) {
					addSearchRow(table, data, weapon);
				}
				else if(weapon.existCategory("特別装備")) {
					addReapirRow(table, data, weapon);
				}
			}
		}
		
		return table;
	}

	/**
	 * 主武器の情報を記載した列を追加する。
	 * @param table 追加先のテーブル
	 * @param data 対象のアセンデータ
	 * @param weapon 対象の武器データ
	 */
	private void addReloadWeaponRow(TableLayout table, CustomData data, BBData weapon) {
		String[] title = { weapon.get("名称"), "補正前", "補正後" };
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorYellow(), title));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getOneShotPowerArray(data, weapon)));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getCsShotPowerArray(data, weapon)));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getMagazinePowerArray(data, weapon)));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getSecPowerArray(data, weapon)));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getBattlePowerArray(data, weapon)));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getReloadTimeArray(data, weapon)));

		if(weapon.existKey("OH耐性")) {
			table.addView(ViewBuilder.createTableRow(this, SpecArray.getOverheatTimeArray(data, weapon)));
		}
		if(weapon.existKey("OH復帰時間")) {
			table.addView(ViewBuilder.createTableRow(this, SpecArray.getOverheatRepairTimeArray(data, weapon)));
		}
		
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getMagazineCount(data, weapon)));
		
		if(weapon.isChargeWeapon()) {
			table.addView(ViewBuilder.createTableRow(this, SpecArray.getChargeTimeArray(data, weapon)));
		}
	}

	/**
	 * 副武器の情報を記載した列を追加する。
	 * @param table 追加先のテーブル
	 * @param data 対象のアセンデータ
	 * @param weapon 対象の武器データ
	 */
	private void addSubWeaponRow(TableLayout table, CustomData data, BBData weapon) {
		String[] title = { weapon.get("名称"), "補正前", "補正後" };
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorYellow(), title));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getOneShotPowerArray(data, weapon)));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getExplosionRangeArray(data, weapon)));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getReloadTimeArray(data, weapon)));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getMagazineCount(data, weapon)));

		if(weapon.isChargeWeapon()) {
			table.addView(ViewBuilder.createTableRow(this, SpecArray.getChargeTimeArray(data, weapon)));
		}
	}

	/**
	 * 近接武器の情報を記載した列を追加する。
	 * @param table 追加先のテーブル
	 * @param data 対象のアセンデータ
	 * @param weapon 対象の武器データ
	 */
	private void addSlashRow(TableLayout table, CustomData data, BBData weapon) {
		String[] title = { weapon.get("名称"), "補正前", "補正後" };
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorYellow(), title));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getNormalSlashArray(data, weapon)));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getDashSlashArray(data, weapon)));
	}

	/**
	 * 補助装備ボムの情報を記載した列を追加する。
	 * @param table 追加先のテーブル
	 * @param data 対象のアセンデータ
	 * @param weapon 対象の武器データ
	 */
	private void addSupportBombRow(TableLayout table, CustomData data, BBData weapon) {
		String[] title = { weapon.get("名称"), "補正前", "補正後" };
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorYellow(), title));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getExplosionRangeArray(data, weapon)));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getReloadTimeArray(data, weapon)));
	}

	/**
	 * 索敵装備の情報を記載した列を追加する。
	 * @param table 追加先のテーブル
	 * @param data 対象のアセンデータ
	 * @param weapon 対象の武器データ
	 */
	private void addSearchRow(TableLayout table, CustomData data, BBData weapon) {
		String[] title = { weapon.get("名称"), "補正前", "補正後" };
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorYellow(), title));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getSearchTimeArray(data, weapon)));
	}

	/**
	 * ACの情報を記載した列を追加する。
	 * @param table 追加先のテーブル
	 * @param data 対象のアセンデータ
	 * @param weapon 対象の武器データ
	 */
	private void addACRow(TableLayout table, CustomData data, BBData weapon) {
		String[] title = { weapon.get("名称"), "補正前", "補正後" };
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorYellow(), title));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getAcSpeedArray(data, weapon)));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getAcBattleSpeedArray(data, weapon)));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getSpChargeTimeArray(data, weapon, mBlustType)));
	}

	/**
	 * 砲撃装備の情報を記載した列を追加する。
	 * @param table 追加先のテーブル
	 * @param data 対象のアセンデータ
	 * @param weapon 対象の武器データ
	 */
	private void addCannonRow(TableLayout table, CustomData data, BBData weapon) {
		String[] title = { weapon.get("名称"), "補正前", "補正後" };
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorYellow(), title));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getOneShotPowerArray(data, weapon)));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getExplosionRangeArray(data, weapon)));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getSpChargeTimeArray(data, weapon, mBlustType)));
	}

	/**
	 * バリア装備の情報を記載した列を追加する。
	 * @param table 追加先のテーブル
	 * @param data 対象のアセンデータ
	 * @param weapon 対象の武器データ
	 */
	private void addBarrierRow(TableLayout table, CustomData data, BBData weapon) {
		String[] title = { weapon.get("名称"), "補正前", "補正後" };
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorYellow(), title));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getBattleBarrierGuardArray(data, weapon)));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getSpChargeTimeArray(data, weapon, mBlustType)));
	}
	
	/**
	 * EUS系統の情報を記載した列を追加する。
	 * @param table 追加先のテーブル
	 * @param data 対象のアセンデータ
	 * @param weapon 対象の武器データ
	 */
	private void addEUSRow(TableLayout table, CustomData data,  BBData weapon) {
		String[] title = { weapon.get("名称"), "補正前", "補正後" };
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorYellow(), title));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getMagazinePowerArray(data, weapon)));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getSecPowerArray(data, weapon)));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getBattlePowerArray(data, weapon)));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getReloadTimeArray(data, weapon)));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getSpChargeTimeArray(data, weapon, mBlustType)));
	}

	/**
	 * リペア装備の情報を記載した列を追加する。
	 * @param table 追加先のテーブル
	 * @param data 対象のアセンデータ
	 * @param weapon 対象の武器データ
	 */
	private void addReapirRow(TableLayout table, CustomData data, BBData weapon) {
		String[] title = { weapon.get("名称"), "補正前", "補正後" };
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorYellow(), title));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getMaxRepairArray(data, weapon)));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getSpChargeTimeArray(data, weapon, mBlustType)));
	}

	/**
	 * 特別装備の基本情報を記載した列を追加する。
	 * @param table 追加先のテーブル
	 * @param data 対象のアセンデータ
	 * @param weapon 対象の武器データ
	 */
	private void addExtraRow(TableLayout table, CustomData data, BBData weapon) {
		String[] title = { weapon.get("名称"), "補正前", "補正後" };
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorYellow(), title));
		table.addView(ViewBuilder.createTableRow(this, SpecArray.getSpChargeTimeArray(data, weapon, mBlustType)));
	}
}
