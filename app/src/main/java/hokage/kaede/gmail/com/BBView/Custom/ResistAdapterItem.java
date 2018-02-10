package hokage.kaede.gmail.com.BBView.Custom;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.BBViewSetting;
import hokage.kaede.gmail.com.BBViewLib.CustomData;
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

	private static final int TABLE_ROW_MAX = 7;

	private int mPatternMode = MODE_SHOT;
	private int mColumnCount = 0;

	private static final String[] TITLE_ROW_STR_DEFAULT = { "攻撃方法", "被ダメージ", "破", "転", "仰" };
	private static final String[] TITLE_ROW_STR_EXPLOSION = { "攻撃方法", "空爆時ダメ", "破", "転", "仰", "地爆時ダメ", "破", "転", "仰" };
	
	private static final String[] PATTERN_SHOT = { "CS" };
	private static final String[] PATTERN_CHARGE_SHOT = { "CS(CG0)", "CS(CG1)", "CS(CG2)" };
	private static final String[] PATTERN_SLASH = { "通常", "特殊" };
	private static final String[] PATTERN_CHARGE_SLASH = { "通常(CG0)", "通常(CG1)", "通常(CG2)", "特殊(CG0)", "特殊(CG1)", "特殊(CG2)" };
	private static final String[] PATTERN_EXPLOSION = { "爆発" };
	private static final String[] PATTERN_CHARGE_EXPLOSION = { "爆発(CG0)", "爆発(CG1)", "爆発(CG2)" };

	public ResistAdapterItem(Context context, int mode) {
		super(context);
		this.setColumnStretchable(0, true);    // 列幅最大表示
		
		mPatternMode = mode;
		String[] target_title;

		if(mPatternMode == MODE_EXPLOSION) {
			mColumnCount = TITLE_ROW_STR_EXPLOSION.length;
			target_title = TITLE_ROW_STR_EXPLOSION;
		}
		else {
			mColumnCount = TITLE_ROW_STR_DEFAULT.length;
			target_title = TITLE_ROW_STR_DEFAULT;
		}

		mTableRows = new TableRow[TABLE_ROW_MAX];
		mTextViews = new TextView[TABLE_ROW_MAX][];
		
		for(int row=0; row<TABLE_ROW_MAX; row++) {
			mTableRows[row] = new TableRow(context);
			mTextViews[row] = new TextView[mColumnCount];
			
			for(int col=0; col<mColumnCount; col++) {
				mTextViews[row][col] = new TextView(context);
				mTextViews[row][col].setPadding(5, 5, 5, 5);
				mTextViews[row][col].setGravity(Gravity.RIGHT);
				mTextViews[row][col].setTextSize(BBViewSetting.getTextSize(context, BBViewSetting.FLAG_TEXTSIZE_SMALL));
				mTableRows[row].addView(mTextViews[row][col]);
			}

			mTextViews[row][0].setGravity(Gravity.LEFT);
			this.addView(mTableRows[row]);
		}

		for(int col=1; col<mColumnCount; col++) {
			mTextViews[0][col].setText(target_title[col]);
		}
	}

	/**
	 * テーブルの内容を更新する
	 * @param custom_data 被弾側のカスタムデータ
	 * @param data 攻撃側の武器データ
	 * @param is_show_typeb 攻撃側の武器をタイプB扱いするかどうか
	 */
	public void update(CustomData custom_data, BBData data, boolean is_show_typeb) {

		if(mPatternMode == MODE_EXPLOSION) {
			update_explosion(custom_data, data, is_show_typeb);
		}
		else {
			update_default(custom_data, data, is_show_typeb);
		}
	}

	/**
	 * テーブルのデータを更新する。(爆発武器以外)
	 * @param custom_data 被弾側のカスタムデータ
	 * @param data 攻撃側の武器データ
	 * @param is_show_typeb 攻撃側の武器をタイプB扱いするかどうか
	 */
	private void update_default(CustomData custom_data, BBData data, boolean is_show_typeb) {

		// タイプB設定が有効の場合は、武器のデータをタイプBに切り替える
		String name = data.getNameWithType(is_show_typeb);
		BBData item = data;
		if(is_show_typeb) {
			BBData item_typeb = item.getTypeB();

			if(item_typeb != null) {
				item = item_typeb;
			}
		}

		String[] pattern = selectPattern(item);
		int size = pattern.length;

		mTextViews[0][0].setText(name);

		for(int idx=0; idx<size; idx++) {
			int target_row = idx + 1;
			double damage = getDamage(custom_data, item, pattern[idx], false);

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
	 * テーブルのデータを更新する。(爆発武器)
	 * @param custom_data 被弾側のカスタムデータ
	 * @param data 攻撃側の武器データ
	 * @param is_show_typeb 攻撃側の武器をタイプB扱いするかどうか
	 */
	private void update_explosion(CustomData custom_data, BBData data, boolean is_show_typeb) {

		// タイプB設定が有効の場合は、武器のデータをタイプBに切り替える
		String name = data.getNameWithType(is_show_typeb);
		BBData item = data;
		if(is_show_typeb) {
			BBData item_typeb = item.getTypeB();

			if(item_typeb != null) {
				item = item_typeb;
			}
		}

		String[] pattern = selectPattern(item);
		int size = pattern.length;

		mTextViews[0][0].setText(name);

		for(int idx=0; idx<size; idx++) {
			int target_row = idx + 1;
			double damage = getDamage(custom_data, item, pattern[idx], true);

			mTextViews[target_row][0].setText(pattern[idx]);
			mTextViews[target_row][1].setText(String.format("%.0f", damage));
			mTextViews[target_row][2].setText(getJudgeString(custom_data.isBreak(damage)));
			mTextViews[target_row][3].setText(getJudgeString(custom_data.isDown(damage)));
			mTextViews[target_row][4].setText(getJudgeString(custom_data.isBack(damage)));

			damage = getDamage(custom_data, item, pattern[idx], false);
			mTextViews[target_row][5].setText(String.format("%.0f", damage));
			mTextViews[target_row][6].setText(getJudgeString(custom_data.isBreak(damage)));
			mTextViews[target_row][7].setText(getJudgeString(custom_data.isDown(damage)));
			mTextViews[target_row][8].setText(getJudgeString(custom_data.isBack(damage)));

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
	 * @param is_headbomb 空爆の時はtrueを設定し、地爆の時はfalseを設定する。(爆発武器時以外は無効)
	 * @return
	 */
	private double getDamage(CustomData custom_data, BBData data, String type, boolean is_headbomb) {
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
			if(is_headbomb) {
				ret = custom_data.getExplosionHeadDamage(data, charge_level);
			}
			else {
				ret = custom_data.getExplosionLegsDamage(data, charge_level);
			}
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
