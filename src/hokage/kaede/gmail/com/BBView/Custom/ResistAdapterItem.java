package hokage.kaede.gmail.com.BBView.Custom;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.CustomData;
import hokage.kaede.gmail.com.BBViewLib.Android.BBViewSettingManager;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ResistAdapterItem extends TableLayout {
	public static final int MODE_SHOT      = 1;
	public static final int MODE_EXPLOSION = 2;
	public static final int MODE_SLASH     = 3;

	private TableRow[] mTableRows;
	private TextView[][] mTextViews;

	private int mPatternMode = MODE_SHOT;
	
	private static final int TABLE_ROW_MAX = 7;
	private static final int TABLE_COL_MAX = 5;
	
	private static final String[] TITLE_ROW_STR = { "攻撃方法", "被ダメージ", "破", "転", "仰" };
	
	private static final String[] PATTERN_SHOT = { "CS" };
	private static final String[] PATTERN_CHARGE_SHOT = { "CS(CG0)", "CS(CG1)", "CS(CG2)" };
	private static final String[] PATTERN_SLASH = { "通常", "特殊" };
	private static final String[] PATTERN_CHARGE_SLASH = { "通常(CG0)", "通常(CG1)", "通常(CG2)", "特殊(CG0)", "特殊(CG1)", "特殊(CG2)" };
	private static final String[] PATTERN_EXPLOSION = { "爆発" };
	private static final String[] PATTERN_CHARGE_EXPLOSION = { "爆発(CG0)", "爆発(CG1)", "爆発(CG2)" };

	public ResistAdapterItem(Context context, CustomData custom_data, BBData data, int mode) {
		super(context);
		this.setColumnStretchable(0, true);    // 列幅最大表示
		
		mPatternMode = mode;
		
		mTableRows = new TableRow[TABLE_ROW_MAX];
		mTextViews = new TextView[TABLE_ROW_MAX][];
		
		for(int row=0; row<TABLE_ROW_MAX; row++) {
			mTableRows[row] = new TableRow(context);
			mTextViews[row] = new TextView[TABLE_COL_MAX];
			
			for(int col=0; col<TABLE_COL_MAX; col++) {
				mTextViews[row][col] = new TextView(context);
				mTextViews[row][col].setPadding(5, 5, 5, 5);
				mTextViews[row][col].setGravity(Gravity.RIGHT);
				mTextViews[row][col].setTextSize(BBViewSettingManager.getTextSize(context, BBViewSettingManager.FLAG_TEXTSIZE_SMALL));
				mTableRows[row].addView(mTextViews[row][col]);
			}

			mTextViews[row][0].setGravity(Gravity.LEFT);
			this.addView(mTableRows[row]);
		}
		
		for(int col=1; col<TABLE_COL_MAX; col++) {
			mTextViews[0][col].setText(TITLE_ROW_STR[col]);
		}
		
		update(custom_data, data);
	}
	
	/**
	 * テーブルの内容を更新する
	 * @param data
	 */
	public void update(CustomData custom_data, BBData data) {
		String[] pattern = selectPattern(data);
		int size = pattern.length;
		
		mTextViews[0][0].setText(data.get("名称"));
		
		for(int idx=0; idx<size; idx++) {
			int target_row = idx + 1;
			double damage = getDamage(custom_data, data, pattern[idx]);
			
			mTextViews[target_row][0].setText(pattern[idx]);
			mTextViews[target_row][1].setText(String.format("%.0f", damage));
			mTextViews[target_row][2].setText(getJudgeString(custom_data.isBreak(damage)));
			mTextViews[target_row][3].setText(getJudgeString(custom_data.isDown(damage)));
			mTextViews[target_row][4].setText(getJudgeString(custom_data.isBack(damage)));
			mTableRows[target_row].setVisibility(View.VISIBLE);
		}
		
		for(int row=size+1; row<TABLE_ROW_MAX; row++) {
			mTableRows[row].setVisibility(View.GONE);
		}
	}
	
	/**
	 * テーブルに表示するパターンを選択する
	 * @param data
	 * @return
	 */
	private String[] selectPattern(BBData data) {
		String[] pattern = PATTERN_SHOT;
		
		if(mPatternMode == MODE_SHOT) {
			if(data.isChargeWeapon()) {
				pattern = PATTERN_CHARGE_SHOT;
			}
			else {
				pattern = PATTERN_SHOT;
			}
		}
		else if(mPatternMode == MODE_EXPLOSION) {
			if(data.isChargeWeapon()) {
				pattern = PATTERN_CHARGE_EXPLOSION;
			}
			else {
				pattern = PATTERN_EXPLOSION;
			}
		}
		else if(mPatternMode == MODE_SLASH) {
			if(data.isChargeWeapon()) {
				pattern = PATTERN_CHARGE_SLASH;
			}
			else {
				pattern = PATTERN_SLASH;
			}
		}
		
		return pattern;
	}
	
	/**
	 * ダメージを取得する
	 * @param custom_data
	 * @param data
	 * @param type
	 * @return
	 */
	private double getDamage(CustomData custom_data, BBData data, String type) {
		double ret = 0;
		int charge_level = 0;
		
		// チャージレベルを取得する
		if(type.contains("(CG0)")) {
			charge_level = 0;
		}
		else if(type.contains("(CG1)")) {
			charge_level = 1;
		}
		else if(type.contains("(CG2)")) {
			charge_level = 2;
		}
		
		// 各武器のタイプに応じたダメージ値を取得する
		if(type.contains("CS")) {
			ret = custom_data.getShotDamage(data, BBDataManager.BLUST_PARTS_HEAD, charge_level);
		}
		else if(type.contains("爆発")) {
			ret = custom_data.getExplosionDamage(data, charge_level);
		}
		else if(type.contains("通常")) {
			ret = custom_data.getSlashDamage(data, false, charge_level);
		}
		else if(type.contains("特殊")) {
			ret = custom_data.getSlashDamage(data, true, charge_level);
		}
		
		return ret;
	}
	
	private static String getJudgeString(boolean flag) {
		if(flag) {
			return "×";
		}
		else {
			return "○";
		}
	}
	
	/**
	 * 表示モードを取得する。
	 */
	public int getMode() {
		return mPatternMode;
	}

}
