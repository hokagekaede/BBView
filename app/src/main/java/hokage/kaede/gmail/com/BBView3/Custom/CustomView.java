package hokage.kaede.gmail.com.BBView3.Custom;

import hokage.kaede.gmail.com.BBView3.Item.InfoActivity;
import hokage.kaede.gmail.com.BBViewLib.Java.BBData;
import hokage.kaede.gmail.com.BBViewLib.Java.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.Java.BBViewSetting;
import hokage.kaede.gmail.com.BBViewLib.Java.CustomData;
import hokage.kaede.gmail.com.BBViewLib.Java.CustomFileManager;
import hokage.kaede.gmail.com.BBViewLib.Java.SpecValues;
import hokage.kaede.gmail.com.BBViewLib.Android.CustomLib.CustomAdapter;
import hokage.kaede.gmail.com.BBViewLib.Android.CustomLib.CustomAdapterItemCategory;
import hokage.kaede.gmail.com.BBViewLib.Android.CustomLib.CustomAdapterItemParts;
import hokage.kaede.gmail.com.BBViewLib.Android.CustomLib.CustomAdapterItemReqArm;
import hokage.kaede.gmail.com.BBViewLib.Android.CustomLib.CustomAdapterItemWeapon;
import hokage.kaede.gmail.com.BBViewLib.Android.CustomLib.CustomAdapter.CustomAdapterBaseItem;
import hokage.kaede.gmail.com.BBViewLib.Android.CommonLib.IntentManager;
import hokage.kaede.gmail.com.BBViewLib.Android.CommonLib.ViewBuilder;
import hokage.kaede.gmail.com.StandardLib.Android.SettingManager;
import hokage.kaede.gmail.com.StandardLib.Java.FileIO;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * 「アセン」画面を表示するクラス。
 */
public class CustomView extends FrameLayout implements android.widget.AdapterView.OnItemClickListener {

	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;
	
	private static int sLastPosition = -1;
	private static int sLastListTop = -1;
	
	private boolean mShowChips = false;

	private CustomData mCustomData;
	
	public CustomView(Context context, CustomData custom_data, boolean is_show_chips) {
		super(context);

		this.setLayoutParams(new FrameLayout.LayoutParams(FP, FP));
		
		mShowChips = is_show_chips;

		String file_dir = context.getFilesDir().toString();
		CustomFileManager custom_mng = CustomFileManager.getInstance(file_dir);
		mCustomData = custom_mng.getCacheData();

		if(BBViewSetting.IS_SHOW_COLUMN2) {
			createViewColTwo(context, custom_data);
		}
		else {
			createViewColOne(context, custom_data);
		}
		
		// チップ装着情報を表示するかどうか
		if(mShowChips) {
			createChipView(context, custom_data);
		}
	}

	/**
	 * 1列設定時の画面設定を行う。
	 * @param context
	 * @param custom_data
	 */
	private void createViewColOne(Context context, CustomData custom_data) {

		ListView list_view = new ListView(context);
		CustomAdapter adapter = new CustomAdapter(context);
		
		list_view.setAdapter(adapter);
		list_view.setOnItemClickListener(this);

		String base_spec_str = String.format("機体 (装甲：%.1f / チップ容量：%.1f)", 
				custom_data.getArmorAve(),
				custom_data.getChipCapacity()); 
		
		adapter.addItem(new CustomAdapterItemCategory(context, base_spec_str));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_PARTS_HEAD));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_PARTS_BODY));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_PARTS_ARMS));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_PARTS_LEGS));
		
		String assult_spec_str = String.format("%s (積載：%d / 初速：%.2f)", 
				BBDataManager.BLUST_TYPE_ASSALT, 
				custom_data.getSpaceWeight(BBDataManager.BLUST_TYPE_ASSALT), 
				custom_data.getStartDush(BBDataManager.BLUST_TYPE_ASSALT));
		
		adapter.addItem(new CustomAdapterItemCategory(context, assult_spec_str));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_ASSALT, BBDataManager.WEAPON_TYPE_MAIN));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_ASSALT, BBDataManager.WEAPON_TYPE_SUB));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_ASSALT, BBDataManager.WEAPON_TYPE_SUPPORT));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_ASSALT, BBDataManager.WEAPON_TYPE_SPECIAL));

		String heavy_spec_str = String.format("%s (積載：%d / 初速：%.2f)", 
				BBDataManager.BLUST_TYPE_HEAVY, 
				custom_data.getSpaceWeight(BBDataManager.BLUST_TYPE_HEAVY), 
				custom_data.getStartDush(BBDataManager.BLUST_TYPE_HEAVY));
		
		adapter.addItem(new CustomAdapterItemCategory(context, heavy_spec_str));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_HEAVY, BBDataManager.WEAPON_TYPE_MAIN));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_HEAVY, BBDataManager.WEAPON_TYPE_SUB));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_HEAVY, BBDataManager.WEAPON_TYPE_SUPPORT));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_HEAVY, BBDataManager.WEAPON_TYPE_SPECIAL));
	
		String sniper_spec_str = String.format("%s (積載：%d / 初速：%.2f)", 
				BBDataManager.BLUST_TYPE_SNIPER, 
				custom_data.getSpaceWeight(BBDataManager.BLUST_TYPE_SNIPER), 
				custom_data.getStartDush(BBDataManager.BLUST_TYPE_SNIPER));
		
		adapter.addItem(new CustomAdapterItemCategory(context, sniper_spec_str));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_SNIPER, BBDataManager.WEAPON_TYPE_MAIN));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_SNIPER, BBDataManager.WEAPON_TYPE_SUB));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_SNIPER, BBDataManager.WEAPON_TYPE_SUPPORT));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_SNIPER, BBDataManager.WEAPON_TYPE_SPECIAL));

		String support_spec_str = String.format("%s (積載：%d / 初速：%.2f)", 
				BBDataManager.BLUST_TYPE_SUPPORT, 
				custom_data.getSpaceWeight(BBDataManager.BLUST_TYPE_SUPPORT), 
				custom_data.getStartDush(BBDataManager.BLUST_TYPE_SUPPORT));
		
		adapter.addItem(new CustomAdapterItemCategory(context, support_spec_str));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_SUPPORT, BBDataManager.WEAPON_TYPE_MAIN));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_SUPPORT, BBDataManager.WEAPON_TYPE_SUB));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_SUPPORT, BBDataManager.WEAPON_TYPE_SUPPORT));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_SUPPORT, BBDataManager.WEAPON_TYPE_SPECIAL));

		adapter.addItem(new CustomAdapterItemCategory(context, "その他"));
		adapter.addItem(new CustomAdapterItemReqArm(context, custom_data.getReqArm()));

		if(sLastPosition >= 0) {
			list_view.setSelectionFromTop(sLastPosition, sLastListTop);
		}

		this.addView(list_view);
	}

	/**
	 * 2列設定時の画面設定を行う。
	 * @param context
	 * @param custom_data
	 */
	private void createViewColTwo(Context context, CustomData custom_data) {

		LinearLayout base_layout = new LinearLayout(context);
		base_layout.setOrientation(LinearLayout.VERTICAL);
		base_layout.setLayoutParams(new LinearLayout.LayoutParams(FP, FP));
		
		ListView list_view = new ListView(context);
		CustomAdapter adapter = new CustomAdapter(context);
		
		list_view.setAdapter(adapter);
		list_view.setOnItemClickListener(this);

		String base_spec_str = String.format("機体 (装甲：%.1f / チップ容量：%.1f)", 
				custom_data.getArmorAve(),
				custom_data.getChipCapacity()); 
		
		adapter.addItem(new CustomAdapterItemCategory(context, base_spec_str));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_PARTS_HEAD));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_PARTS_BODY));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_PARTS_ARMS));
		adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_PARTS_LEGS));

		adapter.addItem(new CustomAdapterItemCategory(context, "その他"));
		adapter.addItem(new CustomAdapterItemReqArm(context, custom_data.getReqArm()));
		
		// 2段組みのリスト
		GridView grid_view = new GridView(context);
		CustomAdapter grid_adapter = new CustomAdapter(context);
		
		grid_view.setNumColumns(2);
		grid_view.setAdapter(grid_adapter);
		grid_view.setLayoutParams(new TableLayout.LayoutParams(FP, WC, 1));
		grid_view.setOnItemClickListener(this);
		grid_view.setOnItemLongClickListener(new OnWeaponLongClickListener());
		
		String assult_spec_str = String.format("%s (積載：%d / 初速：%.2f)", 
				BBDataManager.BLUST_TYPE_ASSALT, 
				custom_data.getSpaceWeight(BBDataManager.BLUST_TYPE_ASSALT), 
				custom_data.getStartDush(BBDataManager.BLUST_TYPE_ASSALT));

		String heavy_spec_str = String.format("%s (積載：%d / 初速：%.2f)", 
				BBDataManager.BLUST_TYPE_HEAVY, 
				custom_data.getSpaceWeight(BBDataManager.BLUST_TYPE_HEAVY), 
				custom_data.getStartDush(BBDataManager.BLUST_TYPE_HEAVY));

		String sniper_spec_str = String.format("%s (積載：%d / 初速：%.2f)", 
				BBDataManager.BLUST_TYPE_SNIPER, 
				custom_data.getSpaceWeight(BBDataManager.BLUST_TYPE_SNIPER), 
				custom_data.getStartDush(BBDataManager.BLUST_TYPE_SNIPER));

		String support_spec_str = String.format("%s (積載：%d / 初速：%.2f)", 
				BBDataManager.BLUST_TYPE_SUPPORT, 
				custom_data.getSpaceWeight(BBDataManager.BLUST_TYPE_SUPPORT), 
				custom_data.getStartDush(BBDataManager.BLUST_TYPE_SUPPORT));
		
		grid_adapter.addItem(new CustomAdapterItemCategory(context, assult_spec_str));
		grid_adapter.addItem(new CustomAdapterItemCategory(context, sniper_spec_str));
		
		grid_adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_ASSALT, BBDataManager.WEAPON_TYPE_MAIN));
		grid_adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_SNIPER, BBDataManager.WEAPON_TYPE_MAIN));
		
		grid_adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_ASSALT, BBDataManager.WEAPON_TYPE_SUB));
		grid_adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_SNIPER, BBDataManager.WEAPON_TYPE_SUB));
		
		grid_adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_ASSALT, BBDataManager.WEAPON_TYPE_SUPPORT));
		grid_adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_SNIPER, BBDataManager.WEAPON_TYPE_SUPPORT));
		
		grid_adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_ASSALT, BBDataManager.WEAPON_TYPE_SPECIAL));
		grid_adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_SNIPER, BBDataManager.WEAPON_TYPE_SPECIAL));

		grid_adapter.addItem(new CustomAdapterItemCategory(context, heavy_spec_str));
		grid_adapter.addItem(new CustomAdapterItemCategory(context, support_spec_str));
		
		grid_adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_HEAVY, BBDataManager.WEAPON_TYPE_MAIN));
		grid_adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_SUPPORT, BBDataManager.WEAPON_TYPE_MAIN));
		
		grid_adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_HEAVY, BBDataManager.WEAPON_TYPE_SUB));
		grid_adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_SUPPORT, BBDataManager.WEAPON_TYPE_SUB));
		
		grid_adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_HEAVY, BBDataManager.WEAPON_TYPE_SUPPORT));
		grid_adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_SUPPORT, BBDataManager.WEAPON_TYPE_SUPPORT));
		
		grid_adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_HEAVY, BBDataManager.WEAPON_TYPE_SPECIAL));
		grid_adapter.addItem(createItem(context, custom_data, BBDataManager.BLUST_TYPE_SUPPORT, BBDataManager.WEAPON_TYPE_SPECIAL));
		
		base_layout.addView(list_view);
		base_layout.addView(grid_view);
		
		this.addView(base_layout);
	}
	
	private static CustomAdapterBaseItem createItem(Context context, CustomData custom_data, String type) {
		BBData data = custom_data.getParts(type);
		String title = data.get("名称");
		String summary = type;
	
		if(type.equals(BBDataManager.BLUST_PARTS_HEAD)) {
			title = title + " (頭部)";
			summary = String.format("装：%s / 射：%s / 索：%s / ロ：%s / 回：%s", 
					data.get("装甲"),
					data.get("射撃補正"),
					data.get("索敵"),
					data.get("ロックオン"),
					data.get("DEF回復"));
		}
		else if(type.equals(BBDataManager.BLUST_PARTS_BODY)) {
			title = title + " (胴部)";
			summary = String.format("装：%s / ブ：%s / SP：%s / エ：%s / 耐：%s", 
					data.get("装甲"),
					data.get("ブースター"),
					data.get("SP供給率"),
					data.get("エリア移動"),
					data.get("DEF耐久"));
		}
		else if(type.equals(BBDataManager.BLUST_PARTS_ARMS)) {
			title = title + " (腕部)";
			summary = String.format("装：%s / 反：%s / リ：%s / 武：%s / 弾：%s", 
					data.get("装甲"),
					data.get("反動吸収"),
					data.get("リロード"),
					data.get("武器変更"),
					data.get("予備弾数"));
		}
		else if(type.equals(BBDataManager.BLUST_PARTS_LEGS)) {
			title = title + " (脚部)";
			summary = String.format("装：%s / 歩：%s / ダ：%s / 重：%s / 加：%s", 
					data.get("装甲"),
					data.get("歩行"),
					data.get("ダッシュ"),
					data.get("重量耐性"),
					data.get("加速"));
		}
		
		return new CustomAdapterItemParts(context, data, summary, type);
	}

	private static CustomAdapterBaseItem createItem(Context context, CustomData custom_data, String blust_type, String weapon_type) {
		BBData data = custom_data.getWeapon(blust_type, weapon_type);
		return new CustomAdapterItemWeapon(context, data, blust_type, weapon_type);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		sLastPosition = arg0.getFirstVisiblePosition();
		sLastListTop = arg0.getChildAt(0).getTop();
		
		CustomAdapter adapter = (CustomAdapter)arg0.getAdapter();
		CustomAdapterBaseItem base_item = adapter.getItem(position);
		base_item.click();
	}
	
	/**
	 * 武器を長タップした際の処理を行う。
	 */
	private class OnWeaponLongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			CustomAdapter adapter = (CustomAdapter)parent.getAdapter();
			CustomAdapterBaseItem item_view = adapter.getItem(position);
			
			moveInfoActivity(item_view.getItem());
			return false;
		}
	}

	/**
	 * 詳細画面へ移動する。
	 * @param to_item 詳細画面で表示するデータ
	 */
	private void moveInfoActivity(BBData to_item) {
		Context context = this.getContext();
		Intent intent = new Intent(context, InfoActivity.class);
		IntentManager.setSelectedData(intent, to_item);
		context.startActivity(intent);
	}
	
	/**
	 * 装着中のチップ情報を表示するビューを設定する。
	 * @param context 対象の画面
	 * @param custom_data アセンデータ
	 */
	private void createChipView(Context context, CustomData custom_data) {
		ArrayList<BBData> chiplist = custom_data.getChips();
		String chip_cap_str = SpecValues.getSpecUnit(custom_data.getChipCapacity(), "チップ容量", BBViewSetting.IS_KM_PER_HOUR);
		String chip_weight_str = String.valueOf(custom_data.getChipWeight());
		String chip_text_str = "■チップ情報 [" + chip_weight_str + "/" + chip_cap_str + "]" + FileIO.NEWLINE;
		int size = chiplist.size();
		for(int i=0; i<size; i++) {
			chip_text_str = chip_text_str + chiplist.get(i).get("名称") + FileIO.NEWLINE;
		}
		
		TextView chip_text_view = ViewBuilder.createTextView(context, chip_text_str, BBViewSetting.FLAG_TEXTSIZE_SMALL);
		chip_text_view.setLayoutParams(new LayoutParams(WC, WC));
		chip_text_view.setTextColor(SettingManager.getColorWhite());
		chip_text_view.setBackgroundColor(SettingManager.getColorGray());
		
		LinearLayout buf_layout = new LinearLayout(context);
		buf_layout.setLayoutParams(new LayoutParams(FP, WC));
		buf_layout.setGravity(Gravity.RIGHT);
		buf_layout.addView(chip_text_view);
		
		this.addView(buf_layout);
	}
}
