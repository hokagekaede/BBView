package hokage.kaede.gmail.com.BBView.Purchase;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataFilter;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.BBNetDatabase;
import hokage.kaede.gmail.com.BBViewLib.BBViewSetting;
import hokage.kaede.gmail.com.BBViewLib.PurchaseManager;
import hokage.kaede.gmail.com.BBViewLib.PurchaseStore;
import hokage.kaede.gmail.com.BBViewLib.Android.BaseActivity;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;
import hokage.kaede.gmail.com.Lib.Java.KeyValueStore;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class PurchasePreviewActivity extends BaseActivity {

	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;
	
	public static final String INTENTKEY_SELECTALL = "INTENTKEY_SELECTALL";
	
	/**
	 * アプリ起動時の処理を行う。
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle(getTitle() + " (購入プレビュー)");
		
		// 画面を生成する
		createView(isSelectAll());
	}
	
	private boolean isSelectAll() {
		Intent intent = getIntent();
		return intent.getBooleanExtra(INTENTKEY_SELECTALL, false);
	}

	/**
	 * 画面の生成処理を行う。
	 */
	private void createView(boolean is_select_all) {
		// 全体レイアウト設定
		LinearLayout layout_all = new LinearLayout(this);
		layout_all.setLayoutParams(new LinearLayout.LayoutParams(FP, FP));
        layout_all.setOrientation(LinearLayout.VERTICAL);
		layout_all.setGravity(Gravity.TOP);

		// 購入リストの読み込み
		BBDataManager data_mng = BBDataManager.getInstance();
		BBDataFilter filter = new BBDataFilter();
		
		ArrayList<BBData> data_list; 
		if(is_select_all) {
			filter.setHavingShow(false);  // 所持済みのパーツは表示しない
			filter.setPartsType(BBDataManager.BLUST_PARTS_LIST);
			filter.setBlustType(BBDataManager.BLUST_TYPE_LIST);
			filter.setWeaponType(BBDataManager.WEAPON_TYPE_LIST);
			data_list = new ArrayList<BBData>(data_mng.getList(filter));
		}
		else {
			PurchaseStore store = new PurchaseStore(this.getFilesDir().toString());
			data_list = store.getPurchaseList();
		}
		
		// 勲章データの取得
		filter.clear();
		filter.setOtherType("勲章");
		ArrayList<BBData> medal_list = new ArrayList<BBData>(data_mng.getList(filter));

		// 素材データの取得
		filter.clear();
		filter.setOtherType("素材");
		ArrayList<BBData> material_list = new ArrayList<BBData>(data_mng.getList(filter));
		
		// 購入リストから必要データを取得する
		PurchaseManager purchase_data = new PurchaseManager(medal_list, material_list);
		
		KeyValueStore had_materials = BBNetDatabase.getInstance().getMaterials();
		KeyValueStore had_medals = BBNetDatabase.getInstance().getMedals();

		// 購入対称から必要な勲章と素材を算出する
		int len_data = data_list.size();
		for(int i=0;i<len_data;i++) {
			BBData target = data_list.get(i);
			purchase_data.setData(target);
		}

		// 勲章/素材の表示
		TableLayout layout_material_table = new TableLayout(this);
		layout_material_table.setColumnStretchable(0, true);
		layout_material_table.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		
		String[] title_row_str = { "勲章/素材", "必要数", "所持数" };
		layout_material_table.addView(CreateTableRow(title_row_str, SettingManager.getColorYellow()));
		
		int medal_max = purchase_data.getMedalCount();		
		for(int i=0; i<medal_max; i++) {
			if(purchase_data.getMedalSum(i) == 0) {
				continue;
			}
			
			String name = purchase_data.getMedalName(i);
			int medal_sum = purchase_data.getMedalSum(i);
			String had_medal_sum = had_medals.get(name);
			
			String[] row_str = {
					name,
					String.valueOf(medal_sum) + "個",
					getNumberString(had_medal_sum)
			};
			layout_material_table.addView(CreateTableRow(row_str, getRowColor(medal_sum, had_medal_sum)));
		}

		int material_max = purchase_data.getMaterialCount();		
		for(int i=0; i<material_max; i++) {
			if(purchase_data.getMaterialSum(i) == 0) {
				continue;
			}
			
			String name = purchase_data.getMaterialName(i);
			int material_sum = purchase_data.getMaterialSum(i);
			String had_material_sum = had_materials.get(name);
			
			String[] row_str = {
					name,
					String.valueOf(material_sum) + "個",
					getNumberString(had_material_sum)
			};
			layout_material_table.addView(CreateTableRow(row_str, getRowColor(material_sum, had_material_sum)));
		}

		// GPの表示
		TextView gp_text = new TextView(this);
		gp_text.setText("合計：" + String.valueOf(purchase_data.getSumGP()) + " GP");
		gp_text.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		gp_text.setTextSize(20);
		gp_text.setTextColor(SettingManager.getColorCyan());
		
		layout_all.addView(layout_material_table);
		layout_all.addView(gp_text);
		
		ScrollView sv = new ScrollView(this);
		sv.setLayoutParams(new LinearLayout.LayoutParams(FP, FP));
		sv.addView(layout_all);
		
		setContentView(sv);
	}
	
	//----------------------------------------------------------
	// 1行追加メソッド
	//----------------------------------------------------------
	private TableRow CreateTableRow(String[] text_list, int color) {
		TableRow table_row = new TableRow(this);
		
		int len = text_list.length;
		for(int i=0; i<len; i++) {
			TextView text_view = new TextView(this);
			text_view.setText(text_list[i]);
			text_view.setTextColor(color);
			text_view.setPadding(5, 0, 5, 0);
			text_view.setTextSize(BBViewSetting.getTextSize(this, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
			table_row.addView(text_view);
		}
		
		return table_row;
	}
	
	/**
	 * 個数の文字列を取得する。
	 * @param value
	 * @return
	 */
	private String getNumberString(String value) {
		if(value.equals(KeyValueStore.EMPTY_VALUE)) {
			return "不明";
		}
		
		return value + "個";
	}
	
	/**
	 * 
	 * @param from_value
	 * @param to_value
	 * @return
	 */
	private int getRowColor(int from_value, String to_value) {
		int ret = SettingManager.getColorCyan();
		int to_value_num = -1;
		
		try {
			to_value_num = Integer.valueOf(to_value);

		} catch(Exception e) {
			to_value_num = -1;
			
		}
		
		if(to_value_num >= 0) {
			if(to_value_num >= from_value) {
				ret = SettingManager.getColorWhite();
			}
			else {
				ret = SettingManager.getColorMazenta();
			}
		}
		
		return ret;
	}
}
