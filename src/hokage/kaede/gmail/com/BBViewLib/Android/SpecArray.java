package hokage.kaede.gmail.com.BBViewLib.Android;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataComparator;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.CustomData;
import hokage.kaede.gmail.com.BBViewLib.SpecValues;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;

/**
 * スペック表示行のデータ生成を行うクラス
 */
public class SpecArray {
	
	/**
	 * スペック表示行のデータクラス。
	 * スペック項目とスペック値(補正前、補正後)から表示色を決定する。
	 */
	public static class SpecRow {
		private String[] mValues = new String[4];
		private int[] mColors = new int[4];
		
		public static final int KEY_INDEX = 0;
		public static final int KEY_NORMAL = 1;
		public static final int KEY_REAL = 2;
		public static final int KEY_MEMO = 3;
		
		/**
		 * 初期化を行う。
		 * @param key スペック項目
		 * @param normal_value 補正前の値
		 * @param real_value 補正後の値
		 * @param is_km_per_hour 速度の単位設定
		 */
		public SpecRow(String key, double normal_value, double real_value, boolean is_km_per_hour) {
			init(key, normal_value, real_value, is_km_per_hour);
			
			mValues[KEY_MEMO] = "";
			mColors[KEY_MEMO] = SettingManager.getColorWhite();
		}

		/**
		 * 初期化を行う。
		 * @param key スペック項目
		 * @param normal_value 補正前の値
		 * @param real_value 補正後の値
		 * @param memo_value 追加情報の値。例えば、アンチスタビリティの火力。
		 * @param is_km_per_hour 速度の単位設定
		 */
		public SpecRow(String key, double normal_value, double real_value, double memo_value, boolean is_km_per_hour) {
			init(key, normal_value, real_value, is_km_per_hour);

			BBDataComparator cmp = new BBDataComparator(key, true, BBViewSettingManager.IS_KB_PER_HOUR);
			int ret_cmp = cmp.compareValue(normal_value, memo_value);

			if(ret_cmp > 0) {
				mColors[KEY_MEMO] = SettingManager.getColorMazenta();
			}
			else if(ret_cmp < 0) {
				mColors[KEY_MEMO] = SettingManager.getColorCyan();
			}
			else {
				mColors[KEY_MEMO] = SettingManager.getColorWhite();
			}
		}
		
		/**
		 * 初期化を行う。
		 * 2つのコンストラクタがコールし、追加情報のデータ以外を初期化する。
		 * @param key スペック項目
		 * @param normal_value 補正前の値
		 * @param real_value 補正後の値
		 * @param is_km_per_hour 速度の単位設定
		 */
		private void init(String key, double normal_value, double real_value, boolean is_km_per_hour) {
			mValues[KEY_INDEX] = key;
			mValues[KEY_NORMAL] = SpecValues.getSpecUnit(normal_value, key, is_km_per_hour);
			mValues[KEY_REAL] = SpecValues.getSpecUnit(real_value, key, is_km_per_hour);
			
			mColors = ViewBuilder.getColors(mColors, normal_value, real_value, key);
		}

		/**
		 * 表示する文字列を設定する。
		 * @param normal_str 補正前の表示文字列
		 * @param real_str 補正後の表示文字列
		 */
		public void setValues(String normal_str, String real_str) {
			mValues[KEY_NORMAL] = normal_str;
			mValues[KEY_REAL] = real_str;
		}
		
		/**
		 * 表示する文字列を設定する。
		 * @param normal_str 補正前の表示文字列
		 * @param real_str 補正後の表示文字列
		 * @param memo_str 追加情報の表示文字列
		 */
		public void setValues(String normal_str, String real_str, String memo_str) {
			mValues[KEY_NORMAL] = normal_str;
			mValues[KEY_REAL] = real_str;
			mValues[KEY_MEMO] = memo_str;
		}
		
		/**
		 * 指定の位置の表示文字列を設定する
		 * @param target_str 設定する表示文字列
		 * @param index 設定先。
		 */
		public void setValues(String target_str, int index) {
			if(index < mValues.length) {
				mValues[index] = target_str;
			}
		}
		
		/**
		 * 表示文字列の配列を取得する。
		 * @return 表示文字列の配列
		 */
		public String[] getValues() {
			return mValues;
		}
		
		/**
		 * 表示色の配列を取得する。
		 * @return 表示色の配列
		 */
		public int[] getColors() {
			return mColors;
		}
	}
	
	//----------------------------------------------------------
	// アセン共通のスペック
	//----------------------------------------------------------
	
	/**
	 * パーツデータの配列を生成する。
	 * @param data アセンデータ
	 * @param blust_type 兵装名
	 * @param target_key スペック名
	 * @return 配列
	 */
	public static SpecRow getPartsSpecArray(CustomData data, String blust_type, String target_key) {
		String normal_point = data.getPoint(target_key);
		double normal_value = SpecValues.getSpecValue(normal_point, target_key, BBViewSettingManager.IS_KB_PER_HOUR);
		String normal_value_str = SpecValues.getSpecUnit(normal_value, target_key, BBViewSettingManager.IS_KB_PER_HOUR);
		
		double real_value;
		if(blust_type.equals("")) {
			real_value = data.getSpecValue(target_key);
		}
		else {
			real_value = data.getSpecValue(target_key, blust_type);
		}
		
		String real_point = SpecValues.getPoint(target_key, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
		String real_value_str = SpecValues.getSpecUnit(real_value, target_key, BBViewSettingManager.IS_KB_PER_HOUR);

		// スペックと内部値を結合する
		if(BBDataComparator.isPointKey(target_key)) {
			normal_value_str = normal_point + " (" + normal_value_str + ")";
			real_value_str = real_point + " (" + real_value_str + ")"; 
		}

		// DEF回復の場合、隣に回復時間を併記する
		if(target_key.equals("DEF回復")) {
			BBData head_parts = data.getParts(BBDataManager.BLUST_PARTS_HEAD);
			normal_value_str = String.format("%s (%s)", normal_value_str,
					SpecValues.getSpecUnit(head_parts.getDefRecoverTime(), "回復時間", BBViewSettingManager.IS_KB_PER_HOUR));
			real_value_str = String.format("%s (%s)", real_value_str,
					SpecValues.getSpecUnit(data.getDefRecoverTime(), "回復時間", BBViewSettingManager.IS_KB_PER_HOUR));

		}

		SpecRow col = new SpecRow(target_key, normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
		col.setValues(normal_value_str, real_value_str);
		return col;
	}
	
	//----------------------------------------------------------
	// 武器共通のスペック
	//----------------------------------------------------------
	
	/**
	 * 単発威力の配列を生成する。備考欄に転倒ダメージ値を表示する。
	 * @param data アセンデータ
	 * @param weapon 武器データ
	 * @return 配列
	 */
	public static SpecRow getOneShotPowerArray(CustomData data, BBData weapon) {
		String key = "単発火力";
		double normal_value = weapon.getOneShotPower();
		double real_value = data.getOneShotPower(weapon);

		if(data.existChipGroup("アンチスタビリティ")) {
			double real_value_stn = data.getShotAntiStability(weapon, false);
			String real_value_stn_str = "転倒Dmg値：" + SpecValues.getSpecUnit(real_value_stn, key, BBViewSettingManager.IS_KB_PER_HOUR);
			
			SpecRow col = new SpecRow(key, normal_value, real_value, real_value_stn, BBViewSettingManager.IS_KB_PER_HOUR);
			col.setValues(real_value_stn_str, SpecRow.KEY_MEMO);
			return col;
		}

		return new SpecRow(key, normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	/**
	 * 単発威力(CS時)の配列を生成する。備考欄に転倒ダメージ値を表示する。
	 * @param data アセンデータ
	 * @param weapon 武器データ
	 * @return 配列
	 */
	public static SpecRow getCsShotPowerArray(CustomData data, BBData weapon) {
		String key = "単発火力(CS時)";
		double normal_value = weapon.getCsShotPower();
		double real_value = data.getCsShotPower(weapon);
		
		if(data.existChipGroup("アンチスタビリティ")) {
			double real_value_stn = data.getShotAntiStability(weapon, true);
			String real_value_stn_str = "転倒Dmg値：" + SpecValues.getSpecUnit(real_value_stn, key, BBViewSettingManager.IS_KB_PER_HOUR);
			
			SpecRow col = new SpecRow(key, normal_value, real_value, real_value_stn, BBViewSettingManager.IS_KB_PER_HOUR);
			col.setValues(real_value_stn_str, SpecRow.KEY_MEMO);
			
			return col;
		}

		return new SpecRow(key, normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	public static SpecRow getChargeTimeArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getChargeTime();
		double real_value = data.getChargeTime(weapon);
		
		return new SpecRow("充填時間", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}
	
	//----------------------------------------------------------
	// 主武器のスペック
	//----------------------------------------------------------
	
	public static SpecRow getMagazinePowerArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getMagazinePower();
		double real_value = data.getMagazinePower(weapon);
		
		return new SpecRow("マガジン火力", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}
	
	public static SpecRow getSecPowerArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getSecPower();
		double real_value = data.getSecPower(weapon);

		return new SpecRow("瞬間火力", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	/**
	 * 戦術火力の配列を生成する。
	 * @param data アセンデータ
	 * @param weapon 武器データ
	 * @return 配列
	 */
	public static SpecRow getBattlePowerArray(CustomData data, BBData weapon) {
		String key = "戦術火力";
		double normal_value = weapon.getBattlePower();
		double real_value = data.getBattlePower(weapon);

		if(data.existChipGroup("クイックリロード")) {
			double real_value_quick = data.getBattlePower(weapon, true);
			String real_value_quick_str = "手動リロ時：" + SpecValues.getSpecUnit(real_value_quick, key, BBViewSettingManager.IS_KB_PER_HOUR);
			
			SpecRow col = new SpecRow(key, normal_value, real_value, real_value_quick, BBViewSettingManager.IS_KB_PER_HOUR);
			col.setValues(real_value_quick_str, SpecRow.KEY_MEMO);
			
			return col;
		}

		return new SpecRow(key, normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	/**
	 * リロード時間の配列を生成する。
	 * @param data アセンデータ
	 * @param weapon 武器データ
	 * @return 配列
	 */
	public static SpecRow getReloadTimeArray(CustomData data, BBData weapon) {
		String key = "リロード時間";
		double normal_value = weapon.getReloadTime();
		double real_value = data.getReloadTime(weapon);

		if(data.existChipGroup("クイックリロード")) {
			double real_value_quick = data.getReloadTime(weapon, true);
			String real_value_quick_str = "手動リロ時：" + SpecValues.getSpecUnit(real_value_quick, key, BBViewSettingManager.IS_KB_PER_HOUR);
			
			SpecRow col = new SpecRow(key, normal_value, real_value, real_value_quick, BBViewSettingManager.IS_KB_PER_HOUR);
			col.setValues(real_value_quick_str, SpecRow.KEY_MEMO);
			
			return col;
		}

		return new SpecRow(key, normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}
	
	/**
	 * OH耐性の配列を生成する。
	 * @param data アセンデータ
	 * @param weapon 武器データ
	 * @return 配列
	 */
	public static SpecRow getOverheatTimeArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getOverheatTime();
		double real_value = normal_value;

		return new SpecRow("OH耐性", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	/**
	 * OH復帰時間の配列を生成する。
	 * @param data アセンデータ
	 * @param weapon 武器データ
	 * @return 配列
	 */
	public static SpecRow getOverheatRepairTimeArray(CustomData data, BBData weapon) {
		double normal_value_notoh = weapon.getOverheatRepairTime(false);
		double real_value_notoh = data.getOverheatRepairTime(weapon, false);
		
		double normal_value_oh = weapon.getOverheatRepairTime(true);
		double real_value_oh = data.getOverheatRepairTime(weapon, true);
		
		String key = "OH復帰時間";
		String normal_str = SpecValues.getSpecUnit(normal_value_oh, key, BBViewSettingManager.IS_KB_PER_HOUR) + " ("
				          + SpecValues.getSpecUnit(normal_value_notoh, key, BBViewSettingManager.IS_KB_PER_HOUR) + ")";

		String real_str = SpecValues.getSpecUnit(real_value_oh, key, BBViewSettingManager.IS_KB_PER_HOUR) + " ("
				        + SpecValues.getSpecUnit(real_value_notoh, key, BBViewSettingManager.IS_KB_PER_HOUR) + ")";
		
		SpecRow col = new SpecRow(key, normal_value_notoh, real_value_notoh, BBViewSettingManager.IS_KB_PER_HOUR);
		col.setValues(normal_str, real_str);
		return col;
	}

	/**
	 * 総弾数の配列を生成する。
	 * 1マガジンからあふれた分は"+n"で表示する。
	 * @param data アセンデータ
	 * @param weapon 武器データ
	 * @return 配列
	 */
	public static SpecRow getMagazineCount(CustomData data, BBData weapon) {
		double normal_value = weapon.getBulletSum();
		double real_value = data.getBulletSum(weapon);

		/* 総弾数の文字列を生成する */
		double magazine_bullet = weapon.getMagazine();
		double over_bullet = real_value % magazine_bullet;
		double magazine_count = Math.floor(real_value / magazine_bullet);
		
		String bullet_str = "";
		if(magazine_bullet == 1) {
			bullet_str = String.format("1x%.0f", real_value);
		}
		else {
			bullet_str = String.format("%.0fx%.0f +%.0f", magazine_bullet, magazine_count, over_bullet);
		}
		
		SpecRow col = new SpecRow("総弾数", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
		col.setValues(weapon.get("総弾数"), bullet_str);
		
		return col;
	}

	//----------------------------------------------------------
	// 副武器のスペック
	//----------------------------------------------------------

	public static SpecRow getExplosionRangeArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getExplosionRange();
		double real_value = data.getExplosionRange(weapon);

		return new SpecRow("爆発半径", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	//----------------------------------------------------------
	// 補助装備のスペック
	//----------------------------------------------------------

	public static SpecRow getNormalSlashArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getSlashDamage(false);
		double real_value = data.getSlashPower(weapon, false);

		return new SpecRow("通常攻撃(総威力)", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	public static SpecRow getDashSlashArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getSlashDamage(true);
		double real_value = data.getSlashPower(weapon, true);

		return new SpecRow("特殊攻撃(総威力)", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	public static SpecRow getSearchTimeArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getSearchTime();
		double real_value = data.getSearchTime(weapon);

		return new SpecRow("索敵時間", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	//----------------------------------------------------------
	// 特別装備のスペック
	//----------------------------------------------------------

	/**
	 * 特別装備のチャージ時間の配列を生成する。
	 * 支援兵装強化チップでSP供給が上昇するため、引数に兵装名が必要。
	 * @param data アセンデータ
	 * @param weapon 武器データ
	 * @param blust_type 兵装名
	 * @return 配列
	 */
	public static SpecRow getSpChargeTimeArray(CustomData data, BBData weapon, String blust_type) {
		double normal_value_notoh = weapon.getSpChargeTime(false);
		double real_value_notoh = data.getSpChargeTime(blust_type, weapon, false);

		double normal_value_oh = weapon.getSpChargeTime(true);
		double real_value_oh = data.getSpChargeTime(blust_type, weapon, true);

		String key = "チャージ時間";
		String normal_str = SpecValues.getSpecUnit(normal_value_oh, key, BBViewSettingManager.IS_KB_PER_HOUR) + " ("
				          + SpecValues.getSpecUnit(normal_value_notoh, key, BBViewSettingManager.IS_KB_PER_HOUR) + ")";

		String real_str = SpecValues.getSpecUnit(real_value_oh, key, BBViewSettingManager.IS_KB_PER_HOUR) + " ("
				        + SpecValues.getSpecUnit(real_value_notoh, key, BBViewSettingManager.IS_KB_PER_HOUR) + ")";
		
		SpecRow col = new SpecRow(key, normal_value_notoh, real_value_notoh, BBViewSettingManager.IS_KB_PER_HOUR);
		col.setValues(normal_str, real_str);
		return col;
	}

	/**
	 * AC速度の配列を生成する。
	 * AC自体は速度値を持たないので、補正前も補正後も同じ値とする。
	 * @param data アセンデータ
	 * @param weapon 武器データ
	 * @return 配列
	 */
	public static SpecRow getAcSpeedArray(CustomData data, BBData weapon) {
		double normal_value = data.getACSpeed(weapon);
		double real_value = normal_value;

		return new SpecRow("AC速度", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	/**
	 * AC戦術速度の配列を生成する。
	 * AC自体は速度値を持たないので、補正前も補正後も同じ値とする。
	 * @param data アセンデータ
	 * @param weapon 武器データ
	 * @return 配列
	 */
	public static SpecRow getAcBattleSpeedArray(CustomData data, BBData weapon) {
		double normal_value = data.getBattleACSpeed(weapon);
		double real_value = normal_value;

		return new SpecRow("AC戦術速度", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	/**
	 * バリア装備の秒間耐久回復量の配列を生成する。
	 * @param data アセンデータ
	 * @param weapon 武器データ
	 * @return 配列
	 */
	public static SpecRow getBattleBarrierGuardArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getBattleBarrierGuard();
		double real_value = data.getBattleBarrierGuard(weapon);

		return new SpecRow("秒間耐久回復量", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	/**
	 * リペア装備の回復量の配列を生成する。
	 * @param data アセンデータ
	 * @param weapon 武器データ
	 * @return 配列
	 */
	public static SpecRow getMaxRepairArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getMaxRepair();
		double real_value = data.getMaxRepair(weapon);

		return new SpecRow("最大回復量", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

}
