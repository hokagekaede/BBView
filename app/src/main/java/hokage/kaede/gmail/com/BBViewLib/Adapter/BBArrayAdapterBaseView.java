package hokage.kaede.gmail.com.BBViewLib.Adapter;

import java.util.ArrayList;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataComparator;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.BBNetDatabase;
import hokage.kaede.gmail.com.BBViewLib.BBViewSetting;
import hokage.kaede.gmail.com.BBViewLib.SpecValues;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;
import hokage.kaede.gmail.com.Lib.Java.FileArrayList;
import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class BBArrayAdapterBaseView extends LinearLayout {
	private ArrayList<String> mShownKeys;

	private boolean mIsShowSwitch = false;
	private boolean mIsShowTypeB = false;

	// 表示対象のデータ
	private BBData mTargetData;

	// お気に入りのデータ
	private FileArrayList mFavoriteStore;

	// テキストビューに設定するタグ
	private static final String COLOR_TAG_END    = "</font>";
	private static final String TAG_BR = "<BR>";
	
	/**
	 * 初期化処理を行う。
	 * LinearLayoutのコンストラクタをコールし、TextViewのオブジェクトを生成する。
	 * @param context リストを表示する画面
	 */
	public BBArrayAdapterBaseView(Context context, ArrayList<String> keys) {
		super(context);
		mShownKeys = keys;
		mIsShowTypeB = false;
		
		mFavoriteStore = null;
		
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL );
		this.setPadding(10, 10, 10, 10);
		this.setWeightSum((float)1.0);
	}
	
	/**
	 * お気に入りリストのデータストアを設定する。
	 * @param store ストア
	 */
	public void setFavoriteStore(FileArrayList store) {
		mFavoriteStore = store;
	}

	/**
	 * スイッチ武器の情報を表示するかどうかの設定を行う。
	 * @param is_show_switch スイッチ武器の表示をする場合はtrueを設定する。
	 */
	public void setShowSwitch(boolean is_show_switch) {
		mIsShowSwitch = is_show_switch;
	}
	
	/**
	 * タイプBの情報を表示するかどうかの設定を行う。
	 * @param is_show_typeb trueを設定した場合は表示し、falseを設定した場合はタイプAを表示する。
	 */
	public void setShowTypeB(boolean is_show_typeb) {
		mIsShowTypeB = is_show_typeb;
	}
	
	public void setShownKeys(ArrayList<String> keys) {
		mShownKeys = keys;
	}
	
	/**
	 * ビューを生成する。テキストサイズや文字色の設定を行う。
	 */
	abstract public void createView();

	/**
	 * ビューの更新する。
	 */
	abstract public void updateView();
	
	/**
	 * 表示対象のデータを設定する。
	 * @param item 表示対象のデータ
	 */
	public void setItem(BBData item) {
		mTargetData = item;
	}
	
	/**
	 * 表示対象のデータを取得する。
	 * @return 対象データ
	 */
	public BBData getItem() {
		return mTargetData;
	}
	
	/**
	 * 項目の名前を生成する
	 * @return 項目の名前を生成する。パーツの場合はどの部位のパーツかの情報も追記する。
	 */
	protected String createNameText() {
		String item_name = "";
		String data_name = mTargetData.get("名称");
		
		if(BBDataManager.isParts(mTargetData)) {
			String part_type = BBDataManager.getPartsType(mTargetData).substring(0, 1);
			item_name = data_name + " (" + part_type + ")";
		}
		else {
			item_name = data_name;
		}
		
		// スイッチ武器の場合はタイプ情報を追加表示する
		if(mIsShowSwitch && mTargetData.getTypeB() != null) {
			if(mIsShowTypeB) {
				item_name = item_name + " (タイプB)";
			}
			else {
				item_name = item_name + " (タイプA)";
			}
		}
		
		return item_name;
	}
	
	/**
	 * 追加表示する文字列を生成する
	 * @param base_item 項目
	 * @return 追加表示する文字列
	 */
	protected String createSubText(BBData base_item) {
		String ret = "";
		
		if(base_item == null || mShownKeys == null) {
			return ret;
		}
		
		// 対象のデータを決定 (スイッチ武器)
		BBData from_item = base_item;
		BBData to_item = mTargetData;
		
		if(mIsShowSwitch && mIsShowTypeB) {
			if(from_item.getTypeB() != null) {
				from_item = from_item.getTypeB();
			}
			
			if(to_item.getTypeB() != null) {
				to_item = to_item.getTypeB();
			}
		}
		
		// 表示文字列生成
		int len = mShownKeys.size();
		for(int i=0; i<len; i++) {
			String shown_key = mShownKeys.get(i);
			
			// 現在選択中のパーツとの性能比較を行い、表示色を決定する。
			String color_stag = "";
			String color_etag = "";
			String cmp_str = "";
			if(from_item != null) {
				BBDataComparator cmp_data = new BBDataComparator(shown_key, true, BBViewSetting.IS_KM_PER_HOUR);
				cmp_data.compare(to_item, from_item);
				double cmp = cmp_data.getCmpValue();
				
				if(cmp_data.isCmpOK()) {
					if(cmp > 0) {
						color_stag = SettingManager.getCodeCyan();
						color_etag = COLOR_TAG_END;
						cmp_str = " (" + SpecValues.getSpecUnitCmpArmor(Math.abs(cmp), shown_key, BBViewSetting.IS_KM_PER_HOUR) + "↑)";
					}
					else if(cmp < 0) {
						color_stag = SettingManager.getCodeMagenta();
						color_etag = COLOR_TAG_END;
						cmp_str = " (" + SpecValues.getSpecUnitCmpArmor(Math.abs(cmp), shown_key, BBViewSetting.IS_KM_PER_HOUR) + "↓)";
					}
				}
			}
			
			// 表示する値の文字列を取得する
			String value_str = "";
			if(BBDataComparator.isPointKey(shown_key)) {
				String value = to_item.get(shown_key);
				value_str = value + " (" + SpecValues.getSpecUnit(to_item, shown_key, BBViewSetting.IS_KM_PER_HOUR) + ")";

			}
			else if(shown_key.equals(BBData.ARMOR_BREAK_KEY)) {
				value_str = createArmorBreakString(BBData.ARMOR_BREAK_KEY, to_item);
			}
			else if(shown_key.equals(BBData.ARMOR_DOWN_KEY)) {
				value_str = createArmorBreakString(BBData.ARMOR_DOWN_KEY, to_item);
			}
			else if(shown_key.equals(BBData.ARMOR_KB_KEY)) {
				value_str = createArmorBreakString(BBData.ARMOR_KB_KEY, to_item);
			}
			else if(shown_key.equals(BBData.BULLET_SUM_KEY)) {
				value_str = to_item.get("総弾数") + "=" + SpecValues.getShowValue(to_item, shown_key, BBViewSetting.IS_KM_PER_HOUR);
			}
			else {
				value_str = SpecValues.getShowValue(to_item, shown_key, BBViewSetting.IS_KM_PER_HOUR);
			}
			
			// 文字を結合する
			ret = ret + shown_key + "：" + color_stag + value_str + cmp_str + color_etag + TAG_BR;
		}

		// 末尾の文字を削除
		if(!ret.equals("")) {
			ret = ret.substring(0, ret.length() - TAG_BR.length());
		}
		
		return ret;
	}
	
	/**
	 * 大破判定、転倒判定、KB判定の文字列を生成する。
	 * @param key
	 * @param target_data
	 * @return
	 */
	private String createArmorBreakString(String key, BBData target_data) {
		String ret = "";
		String point = "";

		double min_value = Double.MIN_VALUE;
		double value = target_data.getCalcValue(key);
		
		try {
			min_value = Double.valueOf(SpecValues.ARMOR.get("E-"));

			if(value >= min_value) {
				point = SpecValues.getPoint("装甲", value, true, false);
			}
			else {
				point = SpecValues.NOTHING_STR;
			}
			
		} catch(Exception e) {
			min_value = Double.MIN_VALUE;
			point = SpecValues.NOTHING_STR;
		}

		if(point.equals(SpecValues.NOTHING_STR)) {
			ret = "BS:対象無し";
		}
		else {
			ret = String.format("BS:%s(%s)以下",
					point, SpecValues.getSpecUnit(point, "装甲", true));
		}

		// CS時の情報を追加
		if(target_data.isShotWeapon()) {
			String cs_key = "";
			if(key.equals(BBData.ARMOR_BREAK_KEY)) {
				cs_key = BBData.ARMOR_CS_BREAK_KEY;
			}
			else if(key.equals(BBData.ARMOR_DOWN_KEY)) {
				cs_key = BBData.ARMOR_CS_DOWN_KEY;
			}
			else if(key.equals(BBData.ARMOR_KB_KEY)) {
				cs_key = BBData.ARMOR_CS_KB_KEY;
			}
			else {
				return ret;
			}

			double cs_value = target_data.getCalcValue(cs_key);
			if(cs_value >= min_value) {
				point = SpecValues.getPoint("装甲", cs_value, true, false);
			}
			else {
				point = SpecValues.NOTHING_STR;
			}

			if(point.equals(SpecValues.NOTHING_STR)) {
				ret = ret + " - CS:対象無し";
			}
			else {
				ret = String.format("%s - CS:%s(%s)以下",
						ret,
						SpecValues.getPoint("装甲", cs_value, true, false),
						SpecValues.getSpecUnit(cs_value, "装甲", true));
			}
		}
		
		return ret;
	}
	
	/**
	 * 購入した項目かどうかの文字列を生成する。
	 * @return 購入した武器(開発済みのチップ)かどうかを示す文字列
	 */
	protected String createExistText() {
		String item_name = "";
		String data_name = mTargetData.get("名称");
		
		BBNetDatabase net_database = BBNetDatabase.getInstance();
		if(!net_database.getCardName().equals(BBNetDatabase.NO_CARD_DATA)) {

			if(BBDataManager.isParts(mTargetData)) {
				if(net_database.existParts(mTargetData)) {
					item_name = "(所持)";
				}
				else {
					item_name = "(未購入)";
				}
			}
			else if(BBDataManager.isWeapon(mTargetData)) {
				if(net_database.existWeapon(data_name)) {
					item_name = "(所持)";
				}
				else {
					item_name = "(未購入)";
				}
			}
			else if(mTargetData.existCategory(BBDataManager.CHIP_STR)) {
				if(net_database.existChip(data_name)) {
					item_name = "(所持)";
				}
				else {
					item_name = "(未開発)";
				}
			}
			else if(mTargetData.existCategory(BBDataManager.MATERIAL_STR)) {
				String value = net_database.getMaterials().get(data_name);
				if(value.equals("null")) {
					item_name = "(情報なし)";
				}
				else {
					item_name = value + "個";
				}
			}
			else if(mTargetData.existCategory(BBDataManager.MEDAL_STR)) {
				String value = net_database.getMedals().get(data_name);
				if(value.equals("null")) {
					item_name = "(情報なし)";
				}
				else {
					item_name = value + "個";
				}
			}
			else if(mTargetData.existCategory(BBDataManager.SEED_STR)) {
				String value = net_database.getSeeds().get(data_name);
				if(value.equals("null")) {
					item_name = "(情報なし)";
				}
				else {
					item_name = value + "個";
				}
			}
		}
		
		return item_name;
	}
	
	/**
	 * お気に入りデータに応じてビューの表示を更新する。
	 * @param target_view 対象のビュー。
	 */
	protected void updateFavorite(TextView target_view) {
		if(mFavoriteStore == null) {
			return;
		}
		
		if(mFavoriteStore.exist(mTargetData.get("名称"))) {
			target_view.setTextColor(SettingManager.getColorYellow());
			target_view.setText("[Fav:ON]");
		}
		else {
			target_view.setText("[Fav:OFF]");
			target_view.setTextColor(SettingManager.getColorCyan());
		}
	}
}
