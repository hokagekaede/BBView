package hokage.kaede.gmail.com.BBView.Item;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.BBViewSetting;
import hokage.kaede.gmail.com.BBViewLib.CustomData;
import hokage.kaede.gmail.com.BBViewLib.CustomDataManager;
import hokage.kaede.gmail.com.BBViewLib.SpecValues;
import hokage.kaede.gmail.com.BBViewLib.Android.ViewBuilder;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

public class WeaponSimView extends LinearLayout implements OnClickListener {

	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;
	
	private static final int VIEWID_ONESHOT_BASE      = 1000;
	private static final int VIEWID_ONESHOT_CS_BASE   = 2000;
	private static final int VIEWID_MAGAZINE_BASE     = 3000;
	private static final int VIEWID_1SEC_BASE         = 4000;
	private static final int VIEWID_BATTLE_BASE       = 5000;

	private static final int VIEWID_OFFSET_DAMAGE     = 0;
	private static final int VIEWID_OFFSET_KB         = 1;
	private static final int VIEWID_OFFSET_DOWN       = 2;
	private static final int VIEWID_OFFSET_BREAK      = 3;
	
	private static final int VIEWID_SPEEDUP_BASE      = 6000;
	private static final int VIEWID_NEWDUP_BASE       = 7000;
	private static final int VIEWID_PRECISE_BASE      = 8000;
	private static final int VIEWID_FATAL_BASE        = 9000;
	
	private static final int VIEWID_REDUCE_BREAK_BASE = 10000;
	
	private static final int VIEWID_OFFSET_CHIPI      = 0;
	private static final int VIEWID_OFFSET_CHIPII     = 1;
	private static final int VIEWID_OFFSET_CHIPIII    = 2;
	
	private static final int VIEWID_CHARGE_LEVEL_BASE = 10000;
	
	private static final int VIEWID_DEF_LIFE          = 20000;
	private static final int VIEWID_DEF_NDEF          = 20001;

	private static final int VIEWID_ARMOR_BASE        = 30000;
	private static final int VIEWID_OFFSET_ARMOR_HEAD = 0;
	private static final int VIEWID_OFFSET_ARMOR_BODY = 1;
	private static final int VIEWID_OFFSET_ARMOR_ARMS = 2;
	private static final int VIEWID_OFFSET_ARMOR_LEGS = 3;

	// シミュレーションの武器データ
	private BBData mTargetData;
	private int mChargeLevel = 0;
	
	// 攻撃側のアセン情報
	private CustomData mAttackBlust;
	private int mFatalChipLv = 0;
	
	// 防御側のアセン情報
	private CustomData mDefenceBlust;
	private int mDefenceLife = SpecValues.BLUST_LIFE_MAX;
	private String[] mArmorArray = { "C+", "C+", "C+", "C+"};
	
	// チップデータ
	private ArrayList<BBData> mSpeedUpChips = new ArrayList<BBData>();
	private ArrayList<BBData> mNewdUpChips = new ArrayList<BBData>();
	private ArrayList<BBData> mPriciseChips = new ArrayList<BBData>();
	private ArrayList<BBData> mFatalChips = new ArrayList<BBData>();

	/**
	 * 初期化を行う。
	 * @param context コンテキスト
	 * @param target 対象の武器データ
	 */
	public WeaponSimView(Context context, BBData target) {
		super(context);
		
		initData(target);
		
		createView(context, target);
		updateView();
	}
	
	/**
	 * 各データを初期化する。
	 * @param target シミュレーション対象の武器
	 */
	private void initData(BBData target) {
		mTargetData = target;
		mAttackBlust = CustomDataManager.getDefaultCustomData();
		mDefenceBlust = CustomDataManager.getDefaultCustomData();
		
		BBDataManager data_mng = BBDataManager.getInstance();
		
		mSpeedUpChips = data_mng.getChipSeries("実弾速射");
		mNewdUpChips = data_mng.getChipSeries("ニュード威力上昇");
		mPriciseChips = data_mng.getChipSeries("プリサイスショット");
		mFatalChips = data_mng.getChipSeries("フェイタルアタック");
	}
	
	/**
	 * 画面に表示するビューを生成する。
	 * @param context コンテキスト
	 * @param data 対象の武器
	 */
	private void createView(Context context, BBData data) {
		this.setLayoutParams(new LinearLayout.LayoutParams(FP, FP));
		this.setOrientation(LinearLayout.VERTICAL);
		this.setGravity(Gravity.LEFT | Gravity.TOP);
		
		this.addView(createPowerTable(context, data));
		this.addView(createSettingTable(context));
	}
	
	/**
	 * 火力を表示するテーブルを生成する。
	 * @param context コンテキスト
	 * @param data 対象の武器
	 * @return テーブルデータ
	 */
	private TableLayout createPowerTable(Context context, BBData data) {
		String title = data.get("名称");
		float fontsize = BBViewSetting.getTextSize(context, BBViewSetting.FLAG_TEXTSIZE_NORMAL);
		
		TableLayout table_power = new TableLayout(context);
		table_power.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		table_power.addView(ViewBuilder.createTableRow(context, SettingManager.getColorYellow(), fontsize, title, "ダメージ", "よろけ", "転倒", "大破"));
		table_power.addView(createPowerRow(context, "単発火力(BS)", VIEWID_ONESHOT_BASE));
		table_power.addView(createPowerRow(context, "単発火力(CS)", VIEWID_ONESHOT_CS_BASE));
		table_power.addView(createPowerRow(context, "マガジン火力", VIEWID_MAGAZINE_BASE));
		table_power.addView(createPowerRow(context, "瞬間火力", VIEWID_1SEC_BASE));
		table_power.addView(createPowerRow(context, "戦術火力", VIEWID_BATTLE_BASE));
		
		return table_power;
	}
	
	/**
	 * 設定テーブルを生成する。
	 * @param context コンテキスト
	 * @return テーブルデータ
	 */
	private TableLayout createSettingTable(Context context) {
		TableLayout table_setting = new TableLayout(context);
		table_setting.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		table_setting.addView(createChipRow(context, "実弾速射", VIEWID_SPEEDUP_BASE, 3));
		table_setting.addView(createChipRow(context, "ニュード強化", VIEWID_NEWDUP_BASE, 3));
		table_setting.addView(createChipRow(context, "プリサイス", VIEWID_PRECISE_BASE, 3));
		table_setting.addView(createChipRow(context, "フェイタル", VIEWID_FATAL_BASE, 2));
		
		if(mTargetData.isChargeWeapon()) {
			table_setting.addView(createChipRow(context, "チャージLv", VIEWID_CHARGE_LEVEL_BASE, 2));
		}

		table_setting.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		table_setting.addView(createLifeRow(context));
		/*
		table_setting.addView(createArmorRow(context, "頭部", VIEWID_OFFSET_ARMOR_HEAD));
		table_setting.addView(createArmorRow(context, "胴部", VIEWID_OFFSET_ARMOR_BODY));
		table_setting.addView(createArmorRow(context, "腕部", VIEWID_OFFSET_ARMOR_ARMS));
		table_setting.addView(createArmorRow(context, "脚部", VIEWID_OFFSET_ARMOR_LEGS));
		table_setting.addView(createChipRow(context, "大破抑制", VIEWID_REDUCE_BREAK_BASE, 1));
		*/
		
		return table_setting;	
	}
	
	/**
	 * 火力を表示するテキストビュー行を生成する。
	 * @param context コンテキスト
	 * @param title タイトル
	 * @param id1 ベースID
	 * @return 行のView
	 */
	private TableRow createPowerRow(Context context, String title, int base_id) {
		TableRow row = new TableRow(context);
		
		TextView title_text_view = new TextView(context);
		title_text_view.setText(title);
		title_text_view.setTextSize(BBViewSetting.getTextSize(context, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
		title_text_view.setTextColor(SettingManager.getColorWhite());

		TextView text_view1 = new TextView(context);
		text_view1.setId(base_id + VIEWID_OFFSET_DAMAGE);
		text_view1.setTextSize(BBViewSetting.getTextSize(context, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
		text_view1.setTextColor(SettingManager.getColorWhite());

		TextView text_view2 = new TextView(context);
		text_view2.setId(base_id + VIEWID_OFFSET_KB);
		text_view2.setTextSize(BBViewSetting.getTextSize(context, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
		text_view2.setTextColor(SettingManager.getColorWhite());

		TextView text_view3 = new TextView(context);
		text_view3.setId(base_id + VIEWID_OFFSET_DOWN);
		text_view3.setTextSize(BBViewSetting.getTextSize(context, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
		text_view3.setTextColor(SettingManager.getColorWhite());

		TextView text_view4 = new TextView(context);
		text_view4.setId(base_id + VIEWID_OFFSET_BREAK);
		text_view4.setTextSize(BBViewSetting.getTextSize(context, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
		text_view4.setTextColor(SettingManager.getColorWhite());
		
		row.addView(title_text_view);
		row.addView(text_view1);
		row.addView(text_view2);
		row.addView(text_view3);
		row.addView(text_view4);
		
		return row;
	}
	
	/**
	 * チップを表示するボタン行を生成する。
	 * @param context コンテキスト
	 * @param title タイトル
	 * @param base_id ベースID
	 * @return 行のView
	 */
	private TableRow createChipRow(Context context, String title, int base_id, int count) {
		TableRow row = new TableRow(context);

		TextView title_text_view = new TextView(context);
		title_text_view.setText(title);
		title_text_view.setTextSize(BBViewSetting.getTextSize(context, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
		title_text_view.setTextColor(SettingManager.getColorWhite());
		row.addView(title_text_view);

		ToggleButton tgl_button1 = new ToggleButton(context);
		tgl_button1.setId(base_id + VIEWID_OFFSET_CHIPI);
		tgl_button1.setOnClickListener(this);
		tgl_button1.setTextSize(BBViewSetting.getTextSize(context, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
		tgl_button1.setText("I");
		tgl_button1.setTextOn("I");
		tgl_button1.setTextOff("I");
		row.addView(tgl_button1);

		if(count >= 2) {
			ToggleButton tgl_button2 = new ToggleButton(context);
			tgl_button2.setId(base_id + VIEWID_OFFSET_CHIPII);
			tgl_button2.setOnClickListener(this);
			tgl_button2.setTextSize(BBViewSetting.getTextSize(context, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
			tgl_button2.setText("II");
			tgl_button2.setTextOn("II");
			tgl_button2.setTextOff("II");
			row.addView(tgl_button2);
		}

		if(count >= 3) {
			ToggleButton tgl_button3 = new ToggleButton(context);
			tgl_button3.setId(base_id + VIEWID_OFFSET_CHIPIII);
			tgl_button3.setOnClickListener(this);
			tgl_button3.setTextSize(BBViewSetting.getTextSize(context, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
			tgl_button3.setText("III");
			tgl_button3.setTextOn("III");
			tgl_button3.setTextOff("III");
			row.addView(tgl_button3);
		}
		
		return row;
	}
	
	/**
	 * 耐久力のシークバー行を生成する。
	 * @param context コンテキスト
	 */
	private TableRow createLifeRow(Context context) {
		TableRow row = new TableRow(context);
		
		TextView title_text_view = new TextView(context);
		title_text_view.setId(VIEWID_DEF_LIFE);
		title_text_view.setText("耐久値(" + mDefenceLife + ")");
		title_text_view.setTextSize(BBViewSetting.getTextSize(context, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
		title_text_view.setTextColor(SettingManager.getColorWhite());
		row.addView(title_text_view);

	    TableRow.LayoutParams seekbar_row_layout = new TableRow.LayoutParams();
	    seekbar_row_layout.span = 3;
		
		SeekBar bar = new SeekBar(context);
		bar.setMax(SpecValues.BLUST_LIFE_MAX);
		bar.setProgress(SpecValues.BLUST_LIFE_MAX);
		bar.setOnSeekBarChangeListener(new OnLifeSeekBarChangeListener());
		row.addView(bar, seekbar_row_layout);
		
		return row;
	}
	
	/**
	 * 耐久値のシークバーが変化した時の処理を行うリスナー
	 */
	private class OnLifeSeekBarChangeListener implements OnSeekBarChangeListener {

		/**
		 * シークバーが変化した時の処理を行う。
		 * @param seekbar シークバー
		 * @param progress シークバーの位置
		 * @param from_user ユーザー操作によるものかどうか
		 */
		@Override
		public void onProgressChanged(SeekBar seekbar, int progress, boolean from_user) {
			mDefenceLife = progress;
			
			TextView title_text_view = (TextView)WeaponSimView.this.findViewById(VIEWID_DEF_LIFE);
			title_text_view.setText("耐久値(" + mDefenceLife + ")");
			
			updateView();
		}

		/**
		 * スライド開始した時の処理を行う。
		 * @param seekbar シークバー
		 */
		@Override
		public void onStartTrackingTouch(SeekBar seekbar) {
			// 何もしない
		}

		/**
		 * スライド終了した時の処理を行う。火力および大破計算を再実行する。
		 * @param seekbar シークバー
		 */
		@Override
		public void onStopTrackingTouch(SeekBar seekbar) {
			// 何もしない
		}
	}

	/**
	 * 装甲値のシークバーを生成する。
	 * @param context コンテキスト
	 */
	private TableRow createArmorRow(Context context, String title, int offset) {
		int id = VIEWID_ARMOR_BASE + offset;

		TableRow row = new TableRow(context);
		
		TextView title_text_view = new TextView(context);
		title_text_view.setId(id);
		title_text_view.setText(title + "(" + mArmorArray[offset] + ")");
		title_text_view.setTextSize(BBViewSetting.getTextSize(context, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
		title_text_view.setTextColor(SettingManager.getColorWhite());
		row.addView(title_text_view);
		
	    TableRow.LayoutParams seekbar_row_layout = new TableRow.LayoutParams();
	    seekbar_row_layout.span = 3;
		
		SeekBar bar = new SeekBar(context);
		bar.setMax(BBDataManager.SPEC_POINT.length - 1);
		bar.setProgress(BBDataManager.SPEC_POINT.length - 8);  // C+の設定
		bar.setOnSeekBarChangeListener(new OnArmorSeekBarChangeListener(title, id, offset));
		row.addView(bar, seekbar_row_layout);
		
		return row;
	}
	
	/**
	 * 装甲値のシークバーが変化した時の処理を行うリスナー。
	 */
	private class OnArmorSeekBarChangeListener implements OnSeekBarChangeListener {
		
		private String mTitle = "";
		private int mId = 0;
		private int mOffset = 0;
		
		/**
		 * 初期化処理を行う。テキストビューのIDを設定する。
		 * @param title テキストビューに表示するタイトル
		 * @param text_view_id テキストビューID
		 */
		public OnArmorSeekBarChangeListener(String title, int id, int offset) {
			mTitle = title;
			mId = id;
			mOffset = offset;
		}

		/**
		 * シークバーが変化した時の処理を行う。
		 * @param seekbar シークバー
		 * @param progress シークバーの位置
		 * @param from_user ユーザー操作によるものかどうか
		 */
		@Override
		public void onProgressChanged(SeekBar seekbar, int progress, boolean from_user) {
			String selected_armor = BBDataManager.SPEC_POINT[BBDataManager.SPEC_POINT.length - progress - 1];
			mArmorArray[mOffset] = selected_armor;
			
			TextView title_text_view = (TextView)WeaponSimView.this.findViewById(mId);
			title_text_view.setText(mTitle + "(" + selected_armor + ")");
			
			updateView();
		}

		/**
		 * スライド開始した時の処理を行う。
		 * @param seekbar シークバー
		 */
		@Override
		public void onStartTrackingTouch(SeekBar seekbar) {
			// 何もしない
		}

		/**
		 * スライド終了した時の処理を行う。火力および大破計算を再実行する。
		 * @param seekbar シークバー
		 */
		@Override
		public void onStopTrackingTouch(SeekBar seekbar) {
			// 何もしない
		}
	}
	
	/**
	 * 対象の武器データを設定する。
	 * @param target 主武器データ
	 */
	public void setData(BBData target) {
		mTargetData = target;
		updateView();
	}
	
	/**
	 * シミュレーション結果を更新する。
	 */
	private void updateView() {
		
		// 火力を再計算して表示する
		double one_shot_damage    = mAttackBlust.getOneShotPowerMain(mTargetData, mChargeLevel, false, true);
		double one_shot_cs_damage = mAttackBlust.getOneShotPowerMain(mTargetData, mChargeLevel, true, true);
		double magazine_damage    = mAttackBlust.getMagazinePower(mTargetData);
		double sec_damage         = mAttackBlust.get1SecPower(mTargetData);
		double battle_damage      = mAttackBlust.getBattlePower(mTargetData);
		
		String one_shot_damage_str    = SpecValues.getSpecUnit(one_shot_damage, "威力", false);
		String one_shot_cs_damage_str = SpecValues.getSpecUnit(one_shot_cs_damage, "威力", false);
		String magazine_damage_str    = SpecValues.getSpecUnit(magazine_damage, "威力", false);
		String sec_damage_str         = SpecValues.getSpecUnit(sec_damage, "威力", false);
		String battle_damage_str      = SpecValues.getSpecUnit(battle_damage, "威力", false);
		
		updateTextView(VIEWID_ONESHOT_BASE, VIEWID_OFFSET_DAMAGE, one_shot_damage_str);
		updateTextView(VIEWID_ONESHOT_CS_BASE, VIEWID_OFFSET_DAMAGE, one_shot_cs_damage_str);
		updateTextView(VIEWID_MAGAZINE_BASE, VIEWID_OFFSET_DAMAGE, magazine_damage_str);
		updateTextView(VIEWID_1SEC_BASE, VIEWID_OFFSET_DAMAGE, sec_damage_str);
		updateTextView(VIEWID_BATTLE_BASE, VIEWID_OFFSET_DAMAGE, battle_damage_str);
		
		// ノックバック有無を再計算して表示する
		boolean one_shot_kb = mAttackBlust.isBack(one_shot_damage);
		boolean one_shot_cs_kb = mAttackBlust.isBack(one_shot_damage);
		
		updateTextView(VIEWID_ONESHOT_BASE, VIEWID_OFFSET_KB, getJudgeString(one_shot_kb));
		updateTextView(VIEWID_ONESHOT_CS_BASE, VIEWID_OFFSET_KB, getJudgeString(one_shot_cs_kb));
		updateTextView(VIEWID_MAGAZINE_BASE, VIEWID_OFFSET_KB, "－");
		updateTextView(VIEWID_1SEC_BASE, VIEWID_OFFSET_KB, "－");
		updateTextView(VIEWID_BATTLE_BASE, VIEWID_OFFSET_KB, "－");

		// 転倒有無を再計算して表示する
		boolean one_shot_down = mAttackBlust.isDown(one_shot_damage);
		boolean one_shot_cs_down = mAttackBlust.isDown(one_shot_cs_damage);

		updateTextView(VIEWID_ONESHOT_BASE, VIEWID_OFFSET_DOWN, getJudgeString(one_shot_down));
		updateTextView(VIEWID_ONESHOT_CS_BASE, VIEWID_OFFSET_DOWN, getJudgeString(one_shot_cs_down));
		updateTextView(VIEWID_MAGAZINE_BASE, VIEWID_OFFSET_DOWN, "－");
		updateTextView(VIEWID_1SEC_BASE, VIEWID_OFFSET_DOWN, "－");
		updateTextView(VIEWID_BATTLE_BASE, VIEWID_OFFSET_DOWN, "－");

		// 大破有無を再計算して表示する
		boolean one_shot_break = mAttackBlust.isBreak(one_shot_damage, mDefenceLife, mFatalChipLv);
		boolean one_shot_cs_break = mAttackBlust.isBreak(one_shot_cs_damage, mDefenceLife, mFatalChipLv);

		updateTextView(VIEWID_ONESHOT_BASE, VIEWID_OFFSET_BREAK, getJudgeString(one_shot_break));
		updateTextView(VIEWID_ONESHOT_CS_BASE, VIEWID_OFFSET_BREAK, getJudgeString(one_shot_cs_break));
		updateTextView(VIEWID_MAGAZINE_BASE, VIEWID_OFFSET_BREAK, "－");
		updateTextView(VIEWID_1SEC_BASE, VIEWID_OFFSET_BREAK, "－");
		updateTextView(VIEWID_BATTLE_BASE, VIEWID_OFFSET_BREAK, "－");

	}
	
	/**
	 * シミュレーション結果を表示するテキストビューを取得する。
	 * @param base_id ベースID
	 * @param offset オフセット値
	 * @return テキストビュー
	 */
	private void updateTextView(int base_id, int offset, String text) {
		TextView text_view = (TextView)this.findViewById(base_id + offset);
		text_view.setText(text);
	}
	
	/**
	 * KB, 転倒, 大破の判定結果から表示する文字列を取得する。
	 * @param status 判定結果
	 * @return 表示する文字列
	 */
	private String getJudgeString(boolean status) {
		if(status) {
			return "する";
		}
		else {
			return "しない";
		}
	}

	/**
	 * ボタンが押下された時の処理を行う。
	 */
	@Override
	public void onClick(View view) {
		int id = view.getId();
		boolean checked = !((ToggleButton)view).isChecked();  // 既に変更された後の値が設定されるので反転する
		
		updateSetting(id, checked);
		updateView();
	}
	
	/**
	 * 設定状態を更新する。
	 * @param id 対象のID
	 * @param checked ボタンのチェック状態
	 */
	private void updateSetting(int id, boolean checked) {

		switch(id) {
		
			// 実弾速射
			case VIEWID_SPEEDUP_BASE + VIEWID_OFFSET_CHIPI:
				updateButtonLamp(VIEWID_SPEEDUP_BASE, VIEWID_OFFSET_CHIPI, checked);
				updateChip("実弾速射", mSpeedUpChips, VIEWID_OFFSET_CHIPI, checked);
				break;

			case VIEWID_SPEEDUP_BASE + VIEWID_OFFSET_CHIPII:
				updateButtonLamp(VIEWID_SPEEDUP_BASE, VIEWID_OFFSET_CHIPII, checked);
				updateChip("実弾速射", mSpeedUpChips, VIEWID_OFFSET_CHIPII, checked);
				break;

			case VIEWID_SPEEDUP_BASE + VIEWID_OFFSET_CHIPIII:
				updateButtonLamp(VIEWID_SPEEDUP_BASE, VIEWID_OFFSET_CHIPIII, checked);
				updateChip("実弾速射", mSpeedUpChips, VIEWID_OFFSET_CHIPIII, checked);
				break;

			// ニュード威力上昇
			case VIEWID_NEWDUP_BASE + VIEWID_OFFSET_CHIPI:
				updateButtonLamp(VIEWID_NEWDUP_BASE, VIEWID_OFFSET_CHIPI, checked);
				updateChip("ニュード威力上昇", mNewdUpChips, VIEWID_OFFSET_CHIPI, checked);
				break;

			case VIEWID_NEWDUP_BASE + VIEWID_OFFSET_CHIPII:
				updateButtonLamp(VIEWID_NEWDUP_BASE, VIEWID_OFFSET_CHIPII, checked);
				updateChip("ニュード威力上昇", mNewdUpChips, VIEWID_OFFSET_CHIPII, checked);
				break;

			case VIEWID_NEWDUP_BASE + VIEWID_OFFSET_CHIPIII:
				updateButtonLamp(VIEWID_NEWDUP_BASE, VIEWID_OFFSET_CHIPIII, checked);
				updateChip("ニュード威力上昇", mNewdUpChips, VIEWID_OFFSET_CHIPIII, checked);
				break;

			// プリサイスショット
			case VIEWID_PRECISE_BASE + VIEWID_OFFSET_CHIPI:
				updateButtonLamp(VIEWID_PRECISE_BASE, VIEWID_OFFSET_CHIPI, checked);
				updateChip("プリサイスショット", mPriciseChips, VIEWID_OFFSET_CHIPI, checked);
				break;

			case VIEWID_PRECISE_BASE + VIEWID_OFFSET_CHIPII:
				updateButtonLamp(VIEWID_PRECISE_BASE, VIEWID_OFFSET_CHIPII, checked);
				updateChip("プリサイスショット", mPriciseChips, VIEWID_OFFSET_CHIPII, checked);
				break;

			case VIEWID_PRECISE_BASE + VIEWID_OFFSET_CHIPIII:
				updateButtonLamp(VIEWID_PRECISE_BASE, VIEWID_OFFSET_CHIPIII, checked);
				updateChip("プリサイスショット", mPriciseChips, VIEWID_OFFSET_CHIPIII, checked);
				break;

			// フェイタルアタック
			case VIEWID_FATAL_BASE + VIEWID_OFFSET_CHIPI:
				updateButtonLamp(VIEWID_FATAL_BASE, VIEWID_OFFSET_CHIPI, checked);
				updateFatalChip(1, checked);
				break;

			case VIEWID_FATAL_BASE + VIEWID_OFFSET_CHIPII:
				updateButtonLamp(VIEWID_FATAL_BASE, VIEWID_OFFSET_CHIPII, checked);
				updateFatalChip(2, checked);
				break;

			
			// チャージレベル
			case VIEWID_CHARGE_LEVEL_BASE + VIEWID_OFFSET_CHIPI:
				updateButtonLamp(VIEWID_CHARGE_LEVEL_BASE, VIEWID_OFFSET_CHIPI, checked);
				updateChargeLevel(1, checked);
				break;

			case VIEWID_CHARGE_LEVEL_BASE + VIEWID_OFFSET_CHIPII:
				updateButtonLamp(VIEWID_CHARGE_LEVEL_BASE, VIEWID_OFFSET_CHIPII, checked);
				updateChargeLevel(2, checked);
				break;

		}
	}

	/**
	 * 対象ベースIDに属するボタンのチェック状態を変更する。
	 * @param base_id ベースID
	 * @param offset オフセット
	 * @param checked チェック状態
	 */
	private void updateButtonLamp(int base_id, int offset, boolean checked) {
		setCheckedButtonLamp(base_id, VIEWID_OFFSET_CHIPI, false);
		setCheckedButtonLamp(base_id, VIEWID_OFFSET_CHIPII, false);
		setCheckedButtonLamp(base_id, VIEWID_OFFSET_CHIPIII, false);
		
		setCheckedButtonLamp(base_id, offset, !checked);
	}
	
	/**
	 * 指定のボタンのチェック状態を変更する。
	 * @param base_id ベースID
	 * @param offset オフセット
	 * @param checked チェック状態
	 */
	private void setCheckedButtonLamp(int base_id, int offset, boolean checked) {
		
		View btn1 = this.findViewById(base_id + offset);
		if(btn1 == null) {
			return;
		}
		
		((ToggleButton)btn1).setChecked(checked);
	}
	/**
	 * 攻撃側チップ情報を更新する。
	 * @param chip_series チップの系統名
	 * @param chip_list チップリスト
	 * @param level レベル
	 * @param is_setting 現在の設定状態
	 */
	private void updateChip(String chip_series, ArrayList<BBData> chip_list, int level, boolean is_setting) {
		mAttackBlust.removeChipSeries(chip_series);

		if(!is_setting) {
			mAttackBlust.addChip(chip_list.get(level));
		}
		
	}
	
	/**
	 * フェイタルチップの設定状態を更新する。
	 * ※防御側アセンの大破計算の引数として必要であるため、他の攻撃チップとは別枠で設定状態を管理する。
	 * @param level チップレベル
	 * @param checked チェック状態
	 */
	private void updateFatalChip(int level, boolean checked) {
		if(checked) {
			mFatalChipLv = 0;
		}
		else {
			mFatalChipLv = level;
		}
	}
	
	/**
	 * チャージレベルを更新する。
	 * @param level レベル
	 * @param checked 現在の設定状態
	 */
	private void updateChargeLevel(int level, boolean checked) {
		if(checked) {
			mChargeLevel = 0;
		}
		else {
			mChargeLevel = level;
		}
	}
	

}