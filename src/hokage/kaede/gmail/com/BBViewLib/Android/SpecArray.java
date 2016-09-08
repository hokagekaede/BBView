package hokage.kaede.gmail.com.BBViewLib.Android;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataComparator;
import hokage.kaede.gmail.com.BBViewLib.CustomData;
import hokage.kaede.gmail.com.BBViewLib.SpecValues;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;

public class SpecArray {
	
	public static class SpecCol {
		private String[] mValues = new String[3];
		private int[] mColors = new int[3];
		
		public SpecCol(String key, double normal_value, double real_value, boolean is_km_per_hour) {
			mValues[0] = key;
			mValues[1] = SpecValues.getSpecUnit(normal_value, key, is_km_per_hour);
			mValues[2] = SpecValues.getSpecUnit(real_value, key, is_km_per_hour);
			
			BBDataComparator cmp = new BBDataComparator(key, true, BBViewSettingManager.IS_KB_PER_HOUR);
			int ret_cmp = cmp.compareValue(normal_value, real_value);

			if(ret_cmp > 0) {
				mColors[0] = SettingManager.getColor(SettingManager.COLOR_BASE);
				mColors[1] = SettingManager.getColor(SettingManager.COLOR_BLUE);
				mColors[2] = SettingManager.getColor(SettingManager.COLOR_RED);
			}
			else if(ret_cmp < 0) {
				mColors[0] = SettingManager.getColor(SettingManager.COLOR_BASE);
				mColors[1] = SettingManager.getColor(SettingManager.COLOR_RED);
				mColors[2] = SettingManager.getColor(SettingManager.COLOR_BLUE);
			}
			else {
				mColors[0] = SettingManager.getColor(SettingManager.COLOR_BASE);
				mColors[1] = SettingManager.getColor(SettingManager.COLOR_BASE);
				mColors[2] = SettingManager.getColor(SettingManager.COLOR_BASE);
			}
		}
		
		public void setValues(String normal_str, String real_str) {
			mValues[1] = normal_str;
			mValues[2] = real_str;
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
	public static SpecCol getOneShotPowerArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getOneShotPower();
		double real_value = data.getOneShotPower(weapon);
		
		return new SpecCol("単発火力", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	public static SpecCol getCsShotPowerArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getCsShotPower();
		double real_value = data.getCsShotPower(weapon);
		
		return new SpecCol("単発火力(CS時)", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
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

	public static SpecCol getBattlePowerArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getBattlePower();
		double real_value = data.getBattlePower(weapon);

		return new SpecCol("戦術火力", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

	public static SpecCol getReloadTimeArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getReloadTime();
		double real_value = data.getReloadTime(weapon);

		return new SpecCol("リロード時間", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
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

	public static SpecCol getSpChargeTimeArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getSpChargeTime();
		double real_value = data.getSpChargeTime(weapon);

		return new SpecCol("チャージ時間", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
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

	public static SpecCol getMaxRepairArray(CustomData data, BBData weapon) {
		double normal_value = weapon.getMaxRepair();
		double real_value = data.getMaxRepair(weapon);

		return new SpecCol("最大回復量", normal_value, real_value, BBViewSettingManager.IS_KB_PER_HOUR);
	}

}
