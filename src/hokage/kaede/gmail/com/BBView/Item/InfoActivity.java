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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;

public class InfoActivity extends BaseActivity {
	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;
	
	// スペック表示画面のID
	private int sSpecLayoutId = 1000;
	
	private boolean isShowingTypeB = false;
	
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
	 * 画面全体の生成処理を行う。
	 * @param data
	 */
	private void createView(BBData data) {
		LinearLayout layout_all = new LinearLayout(this);
		layout_all.setOrientation(LinearLayout.VERTICAL);
		layout_all.setGravity(Gravity.LEFT | Gravity.TOP);

		LinearLayout layout_spec = new LinearLayout(this);
		layout_spec.setOrientation(LinearLayout.VERTICAL);
		layout_spec.setGravity(Gravity.LEFT | Gravity.TOP);
		layout_spec.setId(sSpecLayoutId);
		
		setSpecView(layout_spec, data);
		
		layout_all.addView(layout_spec);

		// スイッチ武器の場合は切り替えボタンを追加する。
		if(data.getTypeB() != null) {
			Button button = new Button(this);
			button.setText("切り替え");
			button.setOnClickListener(new OnTypeChangeListener());
			layout_all.addView(button);
		}
		
		// 全体レイアウトの画面表示
		setContentView(layout_all);
	} 

	/**
	 * データ表示部分の画面生成処理を行う。
	 * @param data 表示するアイテム
	 */
	private void setSpecView(LinearLayout layout, BBData data) {
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
		layout.addView(ViewBuilder.createTextView(this, title, BBViewSettingManager.FLAG_TEXTSIZE_LARGE));
		layout.addView(ViewBuilder.createTextView(this, category_str, BBViewSettingManager.FLAG_TEXTSIZE_NORMAL));
		layout.addView(createItemInfoTable(data));
	}
	
	private static final String[] CALC_KEYS = {
		BBData.FULL_POWER_KEY,
		BBData.MAGAZINE_POWER_KEY,
		BBData.SEC_POWER_KEY,
		BBData.BATTLE_POWER_KEY,
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
		layout_table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorYellow(), "項目", "説明"));

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
				layout_table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorWhite(), target_key, point + " (" + data_str + ")"));
			}
			else {
				layout_table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorWhite(), target_key, data_str));
			}
		}
		
		// 追加情報の表示
		size = CALC_KEYS.length;
		for(int i=0; i<size ; i++) {
			String key = CALC_KEYS[i];
			double num = data.getCalcValue(key);
			String value_str = SpecValues.getSpecUnit(num, key, BBViewSettingManager.IS_KB_PER_HOUR);
			
			if(num > 0) {
				layout_table.addView(ViewBuilder.createTableRow(this, SettingManager.getColorWhite(), key, value_str));
			}
		}
		
		return layout_table;
	}
	
	/**
	 * 切り替えボタン押下時の処理を行うリスナー。
	 * タイプA/タイプBの切り替えを行う。
	 */
	private class OnTypeChangeListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			View view = InfoActivity.this.findViewById(sSpecLayoutId);
			LinearLayout layout_spec = (LinearLayout)view;
			layout_spec.removeAllViews();
			
			BBData data = IntentManager.getSelectedData(getIntent());
			
			if(isShowingTypeB) {
				isShowingTypeB = false;
			}
			else {
				isShowingTypeB = true;
				data = data.getTypeB();   // スイッチ武器のみボタンを表示するので、nullチェックはしない
			}
			
			setSpecView(layout_spec, data);
		}
		
	}
	
}
