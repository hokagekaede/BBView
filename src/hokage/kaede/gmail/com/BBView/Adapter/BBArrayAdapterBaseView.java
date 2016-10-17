package hokage.kaede.gmail.com.BBView.Adapter;

import java.util.ArrayList;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataComparator;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.BBNetDatabase;
import hokage.kaede.gmail.com.BBViewLib.SpecValues;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;
import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;

public abstract class BBArrayAdapterBaseView extends LinearLayout {
	private ArrayList<String> mShownKeys;
	private boolean mIsKmPerHour;

	private boolean mIsShowSwitch = false;
	private boolean mIsShowTypeB = false;

	// 表示対象のデータ
	private BBData mTargetData;
	
	// テキストビューに設定するタグ
	private static final String COLOR_TAG_END    = "</font>";
	private static final String TAG_BR = "<BR>";
	
	/**
	 * 初期化処理を行う。
	 * LinearLayoutのコンストラクタをコールし、TextViewのオブジェクトを生成する。
	 * @param context リストを表示する画面
	 */
	public BBArrayAdapterBaseView(Context context, ArrayList<String> keys, boolean is_km_per_hour) {
		super(context);
		mShownKeys = keys;
		mIsKmPerHour = is_km_per_hour;
		mIsShowTypeB = false;
		
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL );
		this.setPadding(10, 10, 10, 10);
		this.setWeightSum((float)1.0);
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
	 * @param is_show_typeb
	 */
	public void setShowTypeB(boolean is_show_typeb) {
		mIsShowTypeB = is_show_typeb;
	}
	
	public void setShownKeys(ArrayList<String> keys) {
		mShownKeys = keys;
	}
	
	/**
	 * ビューを生成する。テキストサイズや文字色の設定を行う。
	 * @param item リストのデータ
	 */
	abstract public void createView();

	/**
	 * ビューの更新する。
	 * @param item リストのデータ
	 * @param position リストの位置
	 */
	abstract public void updateView();
	
	/**
	 * 表示対象のデータを設定する。
	 * @param item データ。
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
	 * @param data 項目
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
	 * @param data 項目
	 * @return 追加表示する文字列
	 */
	protected String createSubText(BBData base_item) {
		String ret = "";
		
		if(mShownKeys == null) {
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
			String value = to_item.get(shown_key);
			
			// 現在選択中のパーツとの性能比較を行い、表示色を決定する。
			String color_stag = "";
			String color_etag = "";
			String cmp_str = "";
			if(from_item != null) {
				BBDataComparator cmp_data = new BBDataComparator(shown_key, true, mIsKmPerHour);
				cmp_data.compare(to_item, from_item);
				double cmp = cmp_data.getCmpValue();
				
				if(cmp_data.isCmpOK()) {
					if(cmp > 0) {
						color_stag = SettingManager.getCodeCyan();
						color_etag = COLOR_TAG_END;
						cmp_str = " (" + SpecValues.getSpecUnitCmpArmor(Math.abs(cmp), shown_key, mIsKmPerHour) + "↑)";
					}
					else if(cmp < 0) {
						color_stag = SettingManager.getCodeMagenta();
						color_etag = COLOR_TAG_END;
						cmp_str = " (" + SpecValues.getSpecUnitCmpArmor(Math.abs(cmp), shown_key, mIsKmPerHour) + "↓)";
					}
				}
			}
			
			// 表示する値の文字列を取得する
			String value_str = "";
			if(BBDataComparator.isPointKey(shown_key)) {
				value_str = value + " (" + SpecValues.getSpecUnit(to_item, shown_key, mIsKmPerHour) + ")";

			}
			else if(shown_key.equals(BBData.ARMOR_BREAK_KEY)) {
				value_str = createArmorBreakString(BBData.ARMOR_BREAK_KEY, value);
			}
			else if(shown_key.equals(BBData.ARMOR_DOWN_KEY)) {
				value_str = createArmorBreakString(BBData.ARMOR_DOWN_KEY, value);
			}
			else if(shown_key.equals(BBData.ARMOR_KB_KEY)) {
				value_str = createArmorBreakString(BBData.ARMOR_KB_KEY, value);
			}
			else if(shown_key.equals(BBData.BULLET_SUM_KEY)) {
				value_str = to_item.get("総弾数") + "=" + SpecValues.getShowValue(to_item, shown_key, mIsKmPerHour);
			}
			else {
				value_str = SpecValues.getShowValue(to_item, shown_key, mIsKmPerHour);
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
	 * @param value
	 * @return
	 */
	private String createArmorBreakString(String key, String value) {
		String ret = "";
		String point = SpecValues.getPoint("装甲", value, true);

		if(point.equals(SpecValues.NOTHING_STR)) {
			ret = "BS:対象無し";
		}
		else {
			ret = String.format("BS:%s(%s)以下",
					point, SpecValues.getSpecUnit(point, "装甲", true));
		}

		// CS時の情報を追加
		if(mTargetData.isShotWeapon()) {
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

			double cs_value = mTargetData.getCalcValue(cs_key);
			point = SpecValues.getPoint("装甲", cs_value, true);

			if(point.equals(SpecValues.NOTHING_STR)) {
				ret = ret + " - CS:対象無し";
			}
			else {
				ret = String.format("%s - CS:%s(%s)以下",
						ret,
						SpecValues.getPoint("装甲", cs_value, true),
						SpecValues.getSpecUnit(cs_value, "装甲", true));
			}
		}
		
		return ret;
	}
	
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
    	}
    	
    	return item_name;
	}
}
