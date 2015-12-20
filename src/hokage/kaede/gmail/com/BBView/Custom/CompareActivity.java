package hokage.kaede.gmail.com.BBView.Custom;

import java.util.ArrayList;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataComparator;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.CustomData;
import hokage.kaede.gmail.com.BBViewLib.CustomDataManager;
import hokage.kaede.gmail.com.BBViewLib.CustomDataReader;
import hokage.kaede.gmail.com.BBViewLib.SpecValues;
import hokage.kaede.gmail.com.BBViewLib.Android.BBViewSettingManager;
import hokage.kaede.gmail.com.BBViewLib.Android.BaseActivity;
import hokage.kaede.gmail.com.BBViewLib.Android.ViewBuilder;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;
import hokage.kaede.gmail.com.Lib.Java.FileKeyValueStore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class CompareActivity extends BaseActivity {
	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;

	/**
	 * 比較対象のファイル名指定用のintentキー
	 */
	public static final String INTENTKEY_CMPTO_FILENAME = "INTENTKEY_CMPTO_FILENAME";
	
	private BBDataManager mDataManager;
	private CustomData mCmpFmData;
	private CustomData mCmpToData;
	
	/**
	 * アプリ起動時の処理を行う。
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDataManager = BBDataManager.getInstance();
		mCmpFmData = CustomDataManager.getCustomData();
		mCmpToData = getCmpToCustomData();
		
		if(mCmpFmData == null || mCmpToData == null) {
			Toast.makeText(this, "比較対象のデータに誤りがあります。", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		// アプリ画面の生成
		createView();
	}

	/**
	 * 画面の生成処理を行う。
	 */
	private void createView() {
		// 全体レイアウト設定
		LinearLayout layout_all = new LinearLayout(this);
		layout_all.setOrientation(LinearLayout.VERTICAL);
		layout_all.setGravity(Gravity.LEFT | Gravity.TOP);

		// 名称表示画面
		layout_all.addView(ViewBuilder.createTextView(this, "アセン比較", BBViewSettingManager.FLAG_TEXTSIZE_LARGE));
		
		LinearLayout layout_table = new LinearLayout(this);
		layout_table.setOrientation(LinearLayout.VERTICAL);

		// 兵装スペックを画面に表示する
		layout_table.addView(ViewBuilder.createTextView(this, "兵装スペック", BBViewSettingManager.FLAG_TEXTSIZE_NORMAL, SettingManager.getColor(SettingManager.COLOR_YELLOW)));
		layout_table.addView(createCmpBlustSpeedViews(mCmpFmData, mCmpToData));

		// 総合スペックを画面に表示する
		layout_table.addView(ViewBuilder.createTextView(this, "総合スペック", BBViewSettingManager.FLAG_TEXTSIZE_NORMAL, SettingManager.getColor(SettingManager.COLOR_YELLOW)));
		layout_table.addView(createCmpBlustSpecView(mCmpFmData, mCmpToData));
		
		// パーツスペックを画面に表示する
		layout_table.addView(ViewBuilder.createTextView(this, "パーツスペック", BBViewSettingManager.FLAG_TEXTSIZE_NORMAL, SettingManager.getColor(SettingManager.COLOR_YELLOW)));
		BBData[] parts_fm_list = mCmpFmData.getPartsList();
		BBData[] parts_to_list = mCmpToData.getPartsList();
		layout_table.addView(createCmpItemViews(parts_fm_list, parts_to_list, true));
		
		ScrollView srv = new ScrollView(this);
		srv.addView(layout_table);
		
		layout_all.addView(srv);
		
		// 全体レイアウトの画面表示
		setContentView(layout_all);
	}
	
	/**
	 * 比較対象のカスタムデータを読み込む
	 * @return カスタムデータ。読み込みに失敗した場合はnullを返す。
	 */
	private CustomData getCmpToCustomData() {
		Intent intent = getIntent();
		String select_file_name = "";
		
		if(intent == null) {
			return null;
		}

		// intentからファイル名を読み込む
		select_file_name = intent.getStringExtra(INTENTKEY_CMPTO_FILENAME);
		if(select_file_name == null) {
			return null;
		}
		
		String dir = getFilesDir().toString();
		FileKeyValueStore select_file = new FileKeyValueStore(dir, select_file_name);
		select_file.load();
		
		CustomData custom_data = CustomDataReader.read(select_file, CustomDataManager.getDefaultData(), mDataManager);
		custom_data.setSpeedUnit(BBViewSettingManager.IS_KB_PER_HOUR);
		
		return custom_data;
	}

	/**
	 * アセンの重量と速度を指定の兵装で比較したビューを生成する
	 * @param cmp_fm_data 比較元のデータ
	 * @param cmp_to_data 比較先のデータ
	 * @return 生成したビュー
	 */
	public TableLayout createCmpBlustSpeedViews(CustomData cmp_fm_data, CustomData cmp_to_data) {
		TableLayout table = new TableLayout(this);
		table.setLayoutParams(new TableLayout.LayoutParams(FP, WC));

		String[] blust_list = BBDataManager.BLUST_TYPE_LIST;
		int blust_list_len = blust_list.length;
		
		for(int blust_idx=0; blust_idx<blust_list_len; blust_idx++) {
			String blust_name = blust_list[blust_idx];
			
			ArrayList<TableRow> rows = createCmpBlustSpeedRows(cmp_fm_data, cmp_to_data, blust_name);
			int size = rows.size();
			
			for(int i=0; i<size; i++) {
				table.addView(rows.get(i));
			}
		}
		
		return table;
	}
	
	/**
	 * アセンの重量と速度を指定の兵装で比較したビューを生成する
	 * @param context 表示する画面
	 * @param cmp_fm_data 比較元のデータ
	 * @param cmp_to_data 比較先のデータ
	 * @param blust_name 兵装名
	 * @return 生成したビュー
	 */
	public TableLayout createCmpBlustSpeedView(Context context, CustomData cmp_fm_data, CustomData cmp_to_data, String blust_name) {
		TableLayout table = new TableLayout(context);
		table.setLayoutParams(new TableLayout.LayoutParams(FP, WC));
		
		ArrayList<TableRow> rows = createCmpBlustSpeedRows(cmp_fm_data, cmp_to_data, blust_name);
		int size = rows.size();
		
		for(int i=0; i<size; i++) {
			table.addView(rows.get(i));
		}
		
		return table;
	}

	/**
	 * 兵装依存の指定データリスト
	 */
	public static final String[] BLUST_SPEC_LIST = {
		"総重量", "猶予", "初速", "巡航", "歩速", "低下率"
	};
		
	/**
	 * アセンの重量と速度を指定の兵装で比較した行を生成する
	 * @param context 表示する画面
	 * @param cmp_fm_data 比較元のデータ
	 * @param cmp_to_data 比較先のデータ
	 * @param blust_name 兵装名
	 * @return 生成した行
	 */
	public ArrayList<TableRow> createCmpBlustSpeedRows(CustomData cmp_fm_data, CustomData cmp_to_data, String blust_name) {
		ArrayList<TableRow> rows = new ArrayList<TableRow>();
		
		// タイトル行を生成
		rows.add(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_YELLOW), blust_name, "比較元", "比較先"));
		
		String[] blust_spec_list = BLUST_SPEC_LIST;
		int len = blust_spec_list.length;

		for(int i=0; i<len; i++) {
			String key = blust_spec_list[i];
			double fm_value = cmp_fm_data.getSpecValue(key, blust_name);
			String fm_str = SpecValues.getSpecUnit(fm_value, key, BBViewSettingManager.IS_KB_PER_HOUR);
			
			double to_value = cmp_to_data.getSpecValue(key, blust_name);
			String to_str = SpecValues.getSpecUnit(to_value, key, BBViewSettingManager.IS_KB_PER_HOUR);
			
			int[] colors = getColors(String.format("%.2f", fm_value), String.format("%.2f", to_value), key);
			rows.add(ViewBuilder.createTableRow(this, colors, key, fm_str, to_str));
		}
		
		return rows;
	}
	
	/**
	 * 複数のパーツまたは武器の比較ビューを生成する
	 * @param from_data_list 比較元のデータ一覧
	 * @param to_data_list 比較先のデータ一覧
	 * @return 比較結果を示すビュー
	 */
	public TableLayout createCmpItemViews(BBData[] from_data_list, BBData[] to_data_list, boolean is_parts) {
		TableLayout table = new TableLayout(this);
		table.setLayoutParams(new TableLayout.LayoutParams(FP, WC));
		ArrayList<TableRow> rows;

		int data_len = from_data_list.length;
		
		for(int data_idx=0; data_idx<data_len; data_idx++) {
			BBData from_data = from_data_list[data_idx];
			BBData to_data = to_data_list[data_idx];
			
			if(is_parts) {
				rows = createCmpPartsRows(from_data, to_data);
			}
			else {
				rows = createCmpWeaponRows(from_data, to_data);
			}
			
			int size = rows.size();
			for(int i=0; i<size; i++) {
				table.addView(rows.get(i));
			}
		}
		
		return table;
	}

	/**
	 * パーツの比較行のリストを生成する
	 * @param from_data 比較元のデータ
	 * @param to_data 比較先のデータ
	 * @return 比較結果を示すビュー
	 */
	private ArrayList<TableRow> createCmpPartsRows(BBData from_data, BBData to_data) {
		ArrayList<TableRow> rows = new ArrayList<TableRow>();
		
		String[] cmp_target = BBDataManager.getCmpTarget(from_data);
		int size = cmp_target.length;
		
		rows.add(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_YELLOW), "名称", from_data.get("名称"), to_data.get("名称")));
		
		for(int i=0; i<size; i++) {
			String target_key = cmp_target[i];
			int[] colors = getColors(from_data, to_data, target_key);
			
			String from_point = from_data.get(target_key);
			String to_point = to_data.get(target_key);
			
			if(from_point == null || to_point == null) {
				continue;
			}
			
			String from_str = SpecValues.getSpecUnit(from_data, target_key, BBViewSettingManager.IS_KB_PER_HOUR);
			String to_str = SpecValues.getSpecUnit(to_data, target_key, BBViewSettingManager.IS_KB_PER_HOUR);
			
			if(target_key.equals("重量")) {
				rows.add(ViewBuilder.createTableRow(this, colors, target_key, from_point, to_point));
			}
			else {
				rows.add(ViewBuilder.createTableRow(this, colors, target_key, from_point + " (" + from_str + ")", to_point + " (" + to_str + ")"));
			}
		}
		
		return rows;
	}

	/**
	 * 武器の比較行のリストを生成する
	 * @param from_data 比較元のデータ
	 * @param to_data 比較先のデータ
	 * @return 比較結果を示すビュー
	 */
	private ArrayList<TableRow> createCmpWeaponRows(BBData from_data, BBData to_data) {
		ArrayList<TableRow> rows = new ArrayList<TableRow>();
		
		String[] cmp_target = BBDataManager.getCmpTarget(from_data);
		int size = cmp_target.length;
		
		rows.add(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_YELLOW), "名称", from_data.get("名称"), to_data.get("名称")));
		
		for(int i=0; i<size; i++) {
			String target_key = cmp_target[i];
			String from_str = SpecValues.getSpecUnit(from_data, target_key, BBViewSettingManager.IS_KB_PER_HOUR);
			String to_str = SpecValues.getSpecUnit(to_data, target_key, BBViewSettingManager.IS_KB_PER_HOUR);
		
			int[] colors = getColors(from_data, to_data, target_key);

			if(from_str != null && to_str != null) {
				rows.add(ViewBuilder.createTableRow(this, colors, target_key, from_str, to_str));
			}
		}
		
		return rows;
	}
	
	/**
	 * パーツの基本性能を表示するビューを生成する。
	 * @param cmp_fm_data 比較元のデータ
	 * @param cmp_to_data 比較先のデータ
	 * @return 結果のビュー
	 */
	public TableLayout createCmpBlustSpecView(CustomData cmp_fm_data, CustomData cmp_to_data) {
		TableLayout table = new TableLayout(this);
		table.setLayoutParams(new TableLayout.LayoutParams(FP, WC));
		table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_BASE), "セットボーナス", cmp_fm_data.getSetBonus(), cmp_to_data.getSetBonus()));
		
		String[] spec_key = { "チップ容量", "装甲平均値" };
		
		int size = spec_key.length;
		for(int i=0; i<size; i++) {
			
			String key = spec_key[i];
			double fm_value = cmp_fm_data.getSpecValue(key);
			String fm_str = SpecValues.getSpecUnit(fm_value, key, BBViewSettingManager.IS_KB_PER_HOUR);
			
			double to_value = cmp_to_data.getSpecValue(key);
			String to_str = SpecValues.getSpecUnit(to_value, key, BBViewSettingManager.IS_KB_PER_HOUR);
			
			int[] colors = getColors(String.format("%.2f", fm_value), String.format("%.2f", to_value), key);
			
			table.addView(ViewBuilder.createTableRow(this, colors, key, fm_str, to_str));
		}
		
		return table;
	}

	private int[] getColors(BBData from_data, BBData to_data, String target_key) {
		int[] ret = new int[3];
		
		BBDataComparator cmp_data = new BBDataComparator(target_key, true, true);
		int cmp = cmp_data.compare(from_data, to_data);
		
		if(cmp > 0) {
			ret[0] = SettingManager.getColor(SettingManager.COLOR_BASE);
			ret[1] = SettingManager.getColor(SettingManager.COLOR_BLUE);
			ret[2] = SettingManager.getColor(SettingManager.COLOR_RED);
		}
		else if(cmp < 0) {
			ret[0] = SettingManager.getColor(SettingManager.COLOR_BASE);
			ret[1] = SettingManager.getColor(SettingManager.COLOR_RED);
			ret[2] = SettingManager.getColor(SettingManager.COLOR_BLUE);
		}
		else {
			ret[0] = SettingManager.getColor(SettingManager.COLOR_BASE);
			ret[1] = SettingManager.getColor(SettingManager.COLOR_BASE);
			ret[2] = SettingManager.getColor(SettingManager.COLOR_BASE);
		}
		
		return ret;
	}
	
	private int[] getColors(String from_str, String to_str, String target_key) {
		int[] ret = new int[3];
		
		BBDataComparator cmp_data = new BBDataComparator(target_key, true, true);
		int cmp = cmp_data.compareString(from_str, to_str);
		
		if(cmp > 0) {
			ret[0] = SettingManager.getColor(SettingManager.COLOR_BASE);
			ret[1] = SettingManager.getColor(SettingManager.COLOR_BLUE);
			ret[2] = SettingManager.getColor(SettingManager.COLOR_RED);
		}
		else if(cmp < 0) {
			ret[0] = SettingManager.getColor(SettingManager.COLOR_BASE);
			ret[1] = SettingManager.getColor(SettingManager.COLOR_RED);
			ret[2] = SettingManager.getColor(SettingManager.COLOR_BLUE);
		}
		else {
			ret[0] = SettingManager.getColor(SettingManager.COLOR_BASE);
			ret[1] = SettingManager.getColor(SettingManager.COLOR_BASE);
			ret[2] = SettingManager.getColor(SettingManager.COLOR_BASE);
		}
		
		return ret;
	}
}
