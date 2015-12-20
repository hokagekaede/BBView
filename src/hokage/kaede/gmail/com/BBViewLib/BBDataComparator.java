package hokage.kaede.gmail.com.BBViewLib;

import java.util.Comparator;

/**
 * BBDataの比較処理を行うクラス
 */
public class BBDataComparator implements Comparator<BBData> {
	private String mTargetKey;
	private boolean mIsAsc;
	private boolean mIsKmPerHour;
	
	private double mCmpLastValue;
	private double mMinValue = -10000;
	private boolean mIsCmpOk;

	private int mLastCmpDoubleSize; // 最後の比較結果の倍率
	
	/**
	 * int型を比較する際の倍率の大きさ
	 */
	public static final int CMP_INT_SIZE = 1;
	
	/**
	 * double型を比較する際の倍率の大きさ
	 */
	public static final int CMP_DOUBLE_SIZE = 100;
	
	/**
	 * 性能評価文字でソートする項目
	 */
	private static final String[] SORT_POINT_TARGET = {
		"装甲", 
		"射撃補正",   "索敵",     "ロックオン", "DEF回復", 
		"ブースター", "SP供給率", "エリア移動", "DEF耐久",
		"反動吸収",   "リロード", "武器変更",   "予備弾数",
		"歩行",       "ダッシュ", "重量耐性",   "加速"
	};
	
	private static final String[] SORT_REVERSE_TARGET = {
		"重量",
		"リロード",
		"エリア移動",
		"リロード時間",
		"総重量",
		"DEF回復時間",
		"加速",
		"チャージ時間"
	};

	/**
	 * 初期化処理を行う。
	 * @param target_key 比較するキー。
	 * @param is_asc 昇順の場合はtrueを設定し、降順の場合はfalseを設定する。
	 * @param is_km_per_hour 速度比較時の単位。
	 */
	public BBDataComparator(String target_key, boolean is_asc, boolean is_km_per_hour) {
		this.mTargetKey = target_key;
		this.mIsAsc = is_asc;
		this.mCmpLastValue = 0;
		this.mIsKmPerHour = is_km_per_hour;
		this.mIsCmpOk = false;
		
		if(target_key != null) {
			int len = SORT_REVERSE_TARGET.length;
			for(int i=0; i<len; i++) {
				if(target_key.equals(SORT_REVERSE_TARGET[i])) {
					this.mIsAsc = !is_asc;
					break;
				}
			}
		}
	}
	
	/**
	 * 比較を行う。
	 */
	@Override
	public int compare(BBData arg0, BBData arg1) {
		int ret = 0;
		mCmpLastValue = 0;
		
		if(mTargetKey == null) {
			return 0;
		}
		else if(mTargetKey.equals("威力")) {
			if(mIsAsc) {
				mCmpLastValue = arg0.getOneShotPower() - arg1.getOneShotPower();
			}
			else {
				mCmpLastValue = arg1.getOneShotPower() - arg0.getOneShotPower();
			}
			ret = (int)(mCmpLastValue * CMP_DOUBLE_SIZE);
			mIsCmpOk = true;
		}
		else if(mTargetKey.equals("耐久力")) {
			boolean is_arg0_plane = arg0.existCategory("偵察機系統");
			boolean is_arg1_plane = arg1.existCategory("偵察機系統");
			
			if(is_arg0_plane && is_arg1_plane) {
				ret = 0;
				mIsCmpOk = false;
			}
			else if(is_arg0_plane) {
				ret = 1;
				mIsCmpOk = false;
			}
			else if(is_arg1_plane) {
				ret = -1;
				mIsCmpOk = false;
			}
			else {
				ret = compareString(arg0.get(mTargetKey), arg1.get(mTargetKey));
				mIsCmpOk = true;
			}

			return ret;
		}
		else {
			ret = compareString(arg0.get(mTargetKey), arg1.get(mTargetKey));
		}
		
		return ret;
	}
	
	/**
	 * 比較結果の差分値を返す。
	 * @return 比較結果の差分値。
	 */
	public double getCmpValue() {
		return mCmpLastValue;
	}
	
	/**
	 * 比較に成功したかどうかを返す。
	 * @return 比較に成功した場合はtrueを返し、失敗した場合はfalseを返す。
	 */
	public boolean isCmpOK() {
		return mIsCmpOk;
	}
	
	/**
	 * 比較処理を行う。
	 * @param arg0 比較対象の文字列１
	 * @param arg1 比較対象の文字列２
	 * @return 
	 */
	public int compareString(String arg0, String arg1) {
		mCmpLastValue = 0;
		
		double value0 = SpecValues.getSpecValue(arg0, mTargetKey, mIsKmPerHour);
		double value1 = SpecValues.getSpecValue(arg1, mTargetKey, mIsKmPerHour);
		
		// 数値変換できなかった場合、ポイント自体の値(E-～A+)で比較する
		if(value0 == SpecValues.ERROR_VALUE && value1 == SpecValues.ERROR_VALUE) {
			int len = BBDataManager.SPEC_POINT.length;
			for(int i=0; i<len; i++) {
				if(BBDataManager.SPEC_POINT[i].equals(arg0)) {
					value0 = len - i;
				}
				
				if(BBDataManager.SPEC_POINT[i].equals(arg1)) {
					value1 = len - i;
				}
			}
			
			if(value0 == SpecValues.ERROR_VALUE || value1 == SpecValues.ERROR_VALUE) {
				mIsCmpOk = false;
			}
			else {
				mIsCmpOk = true;
			}
		}
		else if(value0 == SpecValues.ERROR_VALUE) {
			mIsCmpOk = false;
			value0 = mMinValue;
		}
		else if(value1 == SpecValues.ERROR_VALUE) {
			mIsCmpOk = false;
			value1 = mMinValue;
		}
		else {
			mIsCmpOk = true;
		}
		
		if(mIsAsc) {
			mCmpLastValue = value0 - value1;
		}
		else {
			mCmpLastValue = value1 - value0;
		}

		return (int)(mCmpLastValue * CMP_DOUBLE_SIZE);
	}
	
	/**
	 * 性能値がポイント表示の項目かどうかを判別する。
	 * @param key 項目名
	 * @return 性能値がポイント表示の項目の場合はtrueを返し、そうでない場合はfalseを返す。
	 */
	public static boolean isPointKey(String key) {
		int len = SORT_POINT_TARGET.length;
		
		for(int i=0; i<len; i++) {
			if(key.equals(SORT_POINT_TARGET[i])) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 最後の比較の倍率の大きさを取得する。
	 * @return 最後の比較の倍率の大きさ
	 */
	public int getLastCmpSize() {
		return mLastCmpDoubleSize;
	}
}
