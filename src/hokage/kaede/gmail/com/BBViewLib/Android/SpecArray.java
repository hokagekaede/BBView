package hokage.kaede.gmail.com.BBViewLib.Android;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataComparator;
import hokage.kaede.gmail.com.BBViewLib.CustomData;
import hokage.kaede.gmail.com.BBViewLib.SpecValues;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;

public class SpecArray {
	
	public static class SpecCol {
		private String[] mValues = new String[4];
		private int[] mColors = new int[4];
		
		public static final int KEY_INDEX = 0;
		public static final int KEY_NORMAL = 1;
		public static final int KEY_REAL = 2;
		public static final int KEY_MEMO = 3;
		
		public SpecCol(String key, double normal_value, double real_value, boolean is_km_per_hour) {
			init(key, normal_value, real_value, is_km_per_hour);
			
			mValues[3] = "";
			mColors[3] = SettingManager.getColor(SettingManager.COLOR_BASE);
		}

		public SpecCol(String key, double normal_value, double real_value, double memo_value, boolean is_km_per_hour) {
			init(key, normal_value, real_value, is_km_per_hour);

			BBDataComparator cmp = new BBDataComparator(key, true, BBViewSettingManager.IS_KB_PER_HOUR);
			int ret_cmp = cmp.compareValue(normal_value, memo_value);

			if(ret_cmp > 0) {
				mColors[3] = SettingManager.getColor(SettingManager.COLOR_RED);
			}
			else if(ret_cmp < 0) {
				mColors[3] = SettingManager.getColor(SettingManager.COLOR_BLUE);
			}
			else {
				mColors[3] = SettingManager.getColor(SettingManager.COLOR_BASE);
			}
		}
		
		private void init(String key, double normal_value, double real_value, boolean is_km_per_hour) {
			mValues[KEY_INDEX] = key;
			mValues[1] = SpecValues.getSpecUnit(normal_value, key, is_km_per_hour);
			mValues[2] = SpecValues.getSpecUnit(real_value, key, is_km_per_hour);
			
			BBDataComparator cmp = new BBDataComparator(key, true, BBViewSettingManager.IS_KB_PER_HOUR);
			int ret_cmp = cmp.compareValue(normal_value, real_value);

			if(ret_cmp > 0) {
				mColors[KEY_INDEX] = SettingManager.getColor(SettingManager.COLOR_BASE);
				mColors[KEY_NORMAL] = SettingManager.getColor(SettingManager.COLOR_BLUE);
				mColors[KEY_REAL] = SettingManager.getColor(SettingManager.COLOR_RED);
			}
			else if(ret_cmp < 0) {
				mColors[KEY_INDEX] = SettingManager.getColor(SettingManager.COLOR_BASE);
				mColors[KEY_NORMAL] = SettingManager.getColor(SettingManager.COLOR_RED);
				mColors[KEY_REAL] = SettingManager.getColor(SettingManager.COLOR_BLUE);
			}
			else {
				mColors[KEY_INDEX] = SettingManager.getColor(SettingManager.COLOR_BASE);
				mColors[KEY_NORMAL] = SettingManager.getColor(SettingManager.COLOR_BASE);
				mColors[KEY_REAL] = SettingManager.getColor(SettingManager.COLOR_BASE);
			}
		}

		public void setValues(String normal_str, String real_str) {
			mValues[KEY_NORMAL] = normal_str;
			mValues[KEY_REAL] = real_str;
		}
		
		public void setValues(String normal_str, String real_str, String memo_str) {
			mValues[KEY_NORMAL] = normal_str;
			mValues[KEY_REAL] = real_str;
			mValues[KEY_MEMO] = memo_str;
		}
		
		public void setValues(String target_str, int index) {
			if(index < mValues.length) {
				mValues[index] = target_str;
			}
		}
		
		public String[] getValues() {
			return mValues;
		}
		
		public int[] getColors() {
			return mColors;
		}
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
	public static SpecCol getOneShotPowerArray(CustomData data, BBData weapon) {
		String key = "単発火力";
		double normal_value = weapon.getOneShotPower();
		double real_value = data.getOneShotPower(weapon);

		if(data.existChipGroup("アンチスタビリティ")) {
			double real_value_stn = data.getShotAntiStability(weapon, false);
			String real_value_stn_str = "転倒Dmg値：" + SpecValues.getSpecUnit(real_value_stn, key, BBViewSettingManager.IS_KB_PER_HOUR);
			
			SpecCol col = new SpecCol(key, normal_value, real_value, real_value_stn, BBViewSettingManager.IS_KB_PER_HOUR);
			col.setValues(real_value_stn_str, SpecCol.KEY_MEMO);
			return col;
		}
		// ベースとなる威力自体に補正がかかるため、搭載保留
		/* 
		else if(data.existChipGroup("対物破壊適性")) {
			double real_value_stn = data.getObjectShotPower(weapon);
			String real_value_stn_str = "対施設Dmg値：" + SpecValues.getSpecUnit(real_value_stn, key, BBViewSettingManager.IS_KB_PER_HOUR);
			
			SpecCol col = new SpecCol(key, normal_value, real_value, real_value_stn, BBViewSettingManager.IS_KB_PER_HOUR);
			col.setValues(real_value_stn_str, SpecCol.KEY_MEMO);
			return col;
		}
		*/
		
		return new SpecCol(key, normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	/**
	 * 単発威力(CS時)の配列を生成する。備考欄に転倒ダメージ値を表示する。
	 * @param data アセンデータ
	 * @param weapon 武器データ
	 * @return 配列
	 */
	public static SpecCol getCsShotPowerArray(CustomData data, BBData weapon) {
		String key = "単発火力(CS時)";
		double normal_value = weapon.getCsShotPower();
		double real_value = data.getCsShotPower(weapon);
		
		if(data.existChipGroup("アンチスタビリティ")) {
			double real_value_stn = data.getShotAntiStability(weapon, true);
			String real_value_stn_str = "転倒Dmg値：" + SpecValues.getSpecUnit(real_value_stn, key, BBViewSettingManager.IS_KB_PER_HOUR);
			
			SpecCol col = new SpecCol(key, normal_value, real_value, real_value_stn, BBViewSettingManager.IS_KB_PER_HOUR);
			col.setValues(real_value_stn_str, SpecCol.KEY_MEMO);
			
			return col;
		}

		return new SpecCol(key, normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	public static SpecCol getChargeTimeArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getChargeTime();
		double real_value = data.getChargeTime(weapon);
		
		return new SpecCol("充填時間", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}
	
	//----------------------------------------------------------
	// 主武器のスペック
	//----------------------------------------------------------
	
	public static SpecCol getMagazinePowerArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getMagazinePower();
		double real_value = data.getMagazinePower(weapon);
		
		return new SpecCol("マガジン火力", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}
	
	public static SpecCol getSecPowerArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getSecPower();
		double real_value = data.getSecPower(weapon);

		return new SpecCol("瞬間火力", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	/**
	 * 戦術火力の配列を生成する。
	 * @param data アセンデータ
	 * @param weapon 武器データ
	 * @return 配列
	 */
	public static SpecCol getBattlePowerArray(CustomData data, BBData weapon) {
		String key = "戦術火力";
		double normal_value = weapon.getBattlePower();
		double real_value = data.getBattlePower(weapon);

		if(data.existChipGroup("クイックリロード")) {
			double real_value_quick = data.getBattlePower(weapon, true);
			String real_value_quick_str = "手動リロ時：" + SpecValues.getSpecUnit(real_value_quick, key, BBViewSettingManager.IS_KB_PER_HOUR);
			
			SpecCol col = new SpecCol(key, normal_value, real_value, real_value_quick, BBViewSettingManager.IS_KB_PER_HOUR);
			col.setValues(real_value_quick_str, SpecCol.KEY_MEMO);
			
			return col;
		}

		return new SpecCol(key, normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	/**
	 * リロード時間の配列を生成する。
	 * @param data アセンデータ
	 * @param weapon 武器データ
	 * @return 配列
	 */
	public static SpecCol getReloadTimeArray(CustomData data, BBData weapon) {
		String key = "リロード時間";
		double normal_value = weapon.getReloadTime();
		double real_value = data.getReloadTime(weapon);

		if(data.existChipGroup("クイックリロード")) {
			double real_value_quick = data.getReloadTime(weapon, true);
			String real_value_quick_str = "手動リロ時：" + SpecValues.getSpecUnit(real_value_quick, key, BBViewSettingManager.IS_KB_PER_HOUR);
			
			SpecCol col = new SpecCol(key, normal_value, real_value, real_value_quick, BBViewSettingManager.IS_KB_PER_HOUR);
			col.setValues(real_value_quick_str, SpecCol.KEY_MEMO);
			
			return col;
		}

		return new SpecCol(key, normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}
	
	/**
	 * OH耐性の配列を生成する。
	 * @param data アセンデータ
	 * @param weapon 武器データ
	 * @return 配列
	 */
	public static SpecCol getOverheatTimeArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getOverheatTime();
		double real_value = normal_value;

		return new SpecCol("OH耐性", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	/**
	 * OH復帰時間の配列を生成する。
	 * @param data アセンデータ
	 * @param weapon 武器データ
	 * @return 配列
	 */
	public static SpecCol getOverheatRepairTimeArray(CustomData data, BBData weapon) {
		double normal_value_notoh = weapon.getOverheatRepairTime(false);
		double real_value_notoh = data.getOverheatRepairTime(weapon, false);
		
		double normal_value_oh = weapon.getOverheatRepairTime(true);
		double real_value_oh = data.getOverheatRepairTime(weapon, true);
		
		String key = "OH復帰時間";
		String normal_str = SpecValues.getSpecUnit(normal_value_oh, key, BBViewSettingManager.IS_KB_PER_HOUR) + " ("
				          + SpecValues.getSpecUnit(normal_value_notoh, key, BBViewSettingManager.IS_KB_PER_HOUR) + ")";

		String real_str = SpecValues.getSpecUnit(real_value_oh, key, BBViewSettingManager.IS_KB_PER_HOUR) + " ("
				        + SpecValues.getSpecUnit(real_value_notoh, key, BBViewSettingManager.IS_KB_PER_HOUR) + ")";
		
		SpecCol col = new SpecCol(key, normal_value_notoh, real_value_notoh, BBViewSettingManager.IS_KB_PER_HOUR);
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
	public static SpecCol getMagazineCount(CustomData data, BBData weapon) {
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
		
		SpecCol col = new SpecCol("総弾数", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
		col.setValues(weapon.get("総弾数"), bullet_str);
		
		return col;
	}

	//----------------------------------------------------------
	// 副武器のスペック
	//----------------------------------------------------------

	public static SpecCol getExplosionRangeArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getExplosionRange();
		double real_value = data.getExplosionRange(weapon);

		return new SpecCol("爆発半径", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	//----------------------------------------------------------
	// 補助装備のスペック
	//----------------------------------------------------------

	public static SpecCol getNormalSlashArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getSlashDamage(false);
		double real_value = data.getSlashPower(weapon, false);

		return new SpecCol("通常攻撃(総威力)", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	public static SpecCol getDashSlashArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getSlashDamage(true);
		double real_value = data.getSlashPower(weapon, true);

		return new SpecCol("特殊攻撃(総威力)", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	public static SpecCol getSearchTimeArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getSearchTime();
		double real_value = data.getSearchTime(weapon);

		return new SpecCol("索敵時間", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
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
	public static SpecCol getSpChargeTimeArray(CustomData data, BBData weapon, String blust_type) {
		double normal_value_notoh = weapon.getSpChargeTime(false);
		double real_value_notoh = data.getSpChargeTime(blust_type, weapon, false);

		double normal_value_oh = weapon.getSpChargeTime(true);
		double real_value_oh = data.getSpChargeTime(blust_type, weapon, true);

		String key = "チャージ時間";
		String normal_str = SpecValues.getSpecUnit(normal_value_oh, key, BBViewSettingManager.IS_KB_PER_HOUR) + " ("
				          + SpecValues.getSpecUnit(normal_value_notoh, key, BBViewSettingManager.IS_KB_PER_HOUR) + ")";

		String real_str = SpecValues.getSpecUnit(real_value_oh, key, BBViewSettingManager.IS_KB_PER_HOUR) + " ("
				        + SpecValues.getSpecUnit(real_value_notoh, key, BBViewSettingManager.IS_KB_PER_HOUR) + ")";
		
		SpecCol col = new SpecCol(key, normal_value_notoh, real_value_notoh, BBViewSettingManager.IS_KB_PER_HOUR);
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
	public static SpecCol getAcSpeedArray(CustomData data, BBData weapon) {
		double normal_value = data.getACSpeed(weapon);
		double real_value = normal_value;

		return new SpecCol("AC速度", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	/**
	 * AC戦術速度の配列を生成する。
	 * AC自体は速度値を持たないので、補正前も補正後も同じ値とする。
	 * @param data アセンデータ
	 * @param weapon 武器データ
	 * @return 配列
	 */
	public static SpecCol getAcBattleSpeedArray(CustomData data, BBData weapon) {
		double normal_value = data.getBattleACSpeed(weapon);
		double real_value = normal_value;

		return new SpecCol("AC戦術速度", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	/**
	 * バリア装備の秒間耐久回復量の配列を生成する。
	 * @param data アセンデータ
	 * @param weapon 武器データ
	 * @return 配列
	 */
	public static SpecCol getBattleBarrierGuardArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getBattleBarrierGuard();
		double real_value = data.getBattleBarrierGuard(weapon);

		return new SpecCol("秒間耐久回復量", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	/**
	 * リペア装備の回復量の配列を生成する。
	 * @param data アセンデータ
	 * @param weapon 武器データ
	 * @return 配列
	 */
	public static SpecCol getMaxRepairArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getMaxRepair();
		double real_value = data.getMaxRepair(weapon);

		return new SpecCol("最大回復量", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

}
