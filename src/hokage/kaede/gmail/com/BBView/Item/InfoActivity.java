package hokage.kaede.gmail.com.BBView.Item;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataComparator;
import hokage.kaede.gmail.com.BBViewLib.SpecValues;
import hokage.kaede.gmail.com.BBViewLib.Android.BBViewSettingManager;
import hokage.kaede.gmail.com.BBViewLib.Android.BaseActivity;
import hokage.kaede.gmail.com.BBViewLib.Android.IntentManager;
import hokage.kaede.gmail.com.BBViewLib.Android.ViewBuilder;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableLayout;

public class InfoActivity extends BaseActivity {
	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;

	public static BBData sTargetData;
	
	/**
	 * アプリ起動時の処理を行う。
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BBData data = IntentManager.getSelectedData(getIntent());
		
		setTitle(getTitle() + " (項目詳細情報)");

		// データがnullの場合はアクティビティを終了する
		if(data == null) {
			finish();
		}

		createView(data);
	}

	/**
	 * 画面の生成処理を行う。
	 * @param data 表示するアイテム
	 */
	private void createView(BBData data) {
		// 全体レイアウト設定
		LinearLayout layout_all = new LinearLayout(this);
		layout_all.setOrientation(LinearLayout.VERTICAL);
		layout_all.setGravity(Gravity.LEFT | Gravity.TOP);

		String title = data.get("名称");
		
		String category_str = "";
		ArrayList<String> categorys = data.getCategorys();
		int cate_len = categorys.size();
		for(int i=0; i<cate_len; i++) {
			String buf = categorys.get(i);
			if(buf != null) {
				category_str = category_str + " - " + buf + "\n";
			}
		}

		// タイトルとアイテム詳細情報を表示する
		layout_all.addView(ViewBuilder.createTextView(this, title, BBViewSettingManager.FLAG_TEXTSIZE_LARGE));
		layout_all.addView(ViewBuilder.createTextView(this, category_str, BBViewSettingManager.FLAG_TEXTSIZE_NORMAL));
		layout_all.addView(createItemInfoTable(data));
		
		// 全体レイアウトの画面表示
		setContentView(layout_all);
	}
	
	private static final String[] CALC_KEYS = {
		BBData.FULL_POWER_KEY,
		BBData.MAGAZINE_POWER_KEY,
		BBData.SEC_POWER_KEY,
		BBData.BULLET_SUM_KEY,
		BBData.CARRY_KEY,
		BBData.FLAIGHT_TIME_KEY,
		BBData.SEARCH_SPACE_KEY,
		BBData.SEARCH_SPACE_START_KEY,
		BBData.SEARCH_SPACE_MAX_KEY,
		BBData.SEARCH_SPACE_TIME_KEY
	};

	/**
	 * アイテム詳細情報のテーブルを生成する
	 * @param data アイテム情報
	 * @return アイテム詳細情報のテーブル
	 */
	public TableLayout createItemInfoTable(BBData data) {
		TableLayout layout_table = new TableLayout(this);
		layout_table.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		layout_table.setColumnShrinkable(1, true);    // 右端は表を折り返す

		// 表のタイトルを記載する
		layout_table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_YELLOW), "項目", "説明"));

		ArrayList<String> keys = data.getKeys();
		int size = keys.size();

		// 一般情報を表示
		for(int i=0; i<size; i++) {
			String target_key = keys.get(i);
			String point = data.get(target_key);

			if(target_key.equals("名称")) {
				continue;
			}
			else if(point == null) {
				continue;
			}

			String data_str = SpecValues.getSpecUnit(data, target_key, BBViewSettingManager.IS_KB_PER_HOUR);
			
			if(BBDataComparator.isPointKey(target_key)) {
				layout_table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_BASE), target_key, point + " (" + data_str + ")"));
			}
			else {
				layout_table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_BASE), target_key, data_str));
			}
		}
		
		// 追加情報の表示
		size = CALC_KEYS.length;
		for(int i=0; i<size ; i++) {
			String key = CALC_KEYS[i];
			double num = data.getCalcValue(key);
			String value_str = SpecValues.getSpecUnit(num, key, BBViewSettingManager.IS_KB_PER_HOUR);
			
			if(num > 0) {
				layout_table.addView(ViewBuilder.createTableRow(this, SettingManager.getColor(SettingManager.COLOR_BLUE), key, value_str));
			}
		}
		
		return layout_table;
	}
	
}
