package hokage.kaede.gmail.com.BBView.Custom;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataComparator;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.BBViewSetting;
import hokage.kaede.gmail.com.BBViewLib.SpecValues;
import hokage.kaede.gmail.com.BBViewLib.Android.ViewBuilder;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Paint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * 武器の比較ダイアログ管理クラス
 */
public class CmpWeaponTableBuilder {
	private Activity mActivity;
	private Dialog mDialog;
	private boolean mIsKmPerHour;
	
	private BBData mFromData;
	private BBData mToData;

	private boolean mIsTypeB_FromData = false;;
	private boolean mIsTypeB_ToData = false;;

	private static final int HEADER_COUNT = 3;
	private static final int TABLE_ID = 1000;
	private static final int FROM_DATA_TYPE_ID = 2000;
	private static final int TO_DATA_TYPE_ID = 3000;
	
	/**
	 * 初期化を行う。
	 * @param activity ダイアログのオーナーアクティビティ
	 * @param is_km_per_hour 速度の単位。
	 */
	public CmpWeaponTableBuilder(Activity activity, boolean is_km_per_hour) {
		this.mActivity = activity;
		this.mIsKmPerHour = is_km_per_hour;
	}

	/**
	 * 武器の比較ビューを生成する
	 * @param from_data 比較元のデータ
	 * @param to_data 比較先のデータ
	 * @return 比較結果を示すビュー
	 */
	public void showDialog(BBData from_data, BBData to_data) {
		mFromData = from_data;
		mToData = to_data;
		
		TableLayout table = createCmpTable(from_data, to_data);
		
		// ダイアログを表示
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle("武器性能比較");
		builder.setView(table);
		builder.setPositiveButton("閉じる", null);

		mDialog = builder.create();
		mDialog.setOwnerActivity(mActivity);
		mDialog.show();
	}
	
	/**
	 * 武器の比較結果のテーブルを生成する
	 * @param from_data 比較元のデータ
	 * @param to_data 比較先のデータ
	 */
	private TableLayout createCmpTable(BBData from_data, BBData to_data) {
		TableLayout table = new TableLayout(mActivity);
		table.setId(TABLE_ID);
		
		// タイトル行を設定
		table.addView(ViewBuilder.createTableRow(mActivity, SettingManager.getColorYellow(), "名称", from_data.get("名称"), to_data.get("名称")));
		
		// スイッチ武器行を設定
		BBData from_data_typeB = from_data.getTypeB();
		BBData to_data_typeB = to_data.getTypeB();
		
		if(from_data_typeB != null || to_data_typeB != null) {
			TableRow typename_row = new TableRow(mActivity);
			TableRow typebtn_row = new TableRow(mActivity);
			
			typename_row.addView(ViewBuilder.createTextView(mActivity, "タイプ", BBViewSetting.FLAG_TEXTSIZE_SMALL));
			typebtn_row.addView(ViewBuilder.createTextView(mActivity, "", BBViewSetting.FLAG_TEXTSIZE_SMALL));

			if(from_data_typeB != null) {
				TextView text = ViewBuilder.createTextView(mActivity, "タイプA", BBViewSetting.FLAG_TEXTSIZE_SMALL);
				text.setId(FROM_DATA_TYPE_ID);
				typename_row.addView(text);
				
				TextView btn = ViewBuilder.createTextView(mActivity, "切り替え", BBViewSetting.FLAG_TEXTSIZE_SMALL, SettingManager.getColorYellow());
				btn.setOnClickListener(new TypeChangeListener(FROM_DATA_TYPE_ID));
				btn.setPaintFlags(btn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
				btn.setClickable(true);
				btn.setFocusable(false);
				btn.setPadding(0, 10, 10, 10);
				
				typebtn_row.addView(btn);
			}
			else {
				typename_row.addView(ViewBuilder.createTextView(mActivity, "－", BBViewSetting.FLAG_TEXTSIZE_SMALL));
				typebtn_row.addView(ViewBuilder.createTextView(mActivity, "", BBViewSetting.FLAG_TEXTSIZE_SMALL));
			}
			
			if(to_data_typeB != null) {
				TextView text = ViewBuilder.createTextView(mActivity, "タイプA", BBViewSetting.FLAG_TEXTSIZE_SMALL);
				text.setId(TO_DATA_TYPE_ID);
				typename_row.addView(text);
				
				TextView btn = ViewBuilder.createTextView(mActivity, "切り替え", BBViewSetting.FLAG_TEXTSIZE_SMALL, SettingManager.getColorYellow());
				btn.setOnClickListener(new TypeChangeListener(TO_DATA_TYPE_ID));
				btn.setPaintFlags(btn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
				btn.setClickable(true);
				btn.setFocusable(false);
				btn.setPadding(0, 10, 10, 10);

				typebtn_row.addView(btn);
			}
			else {
				typename_row.addView(ViewBuilder.createTextView(mActivity, "－", BBViewSetting.FLAG_TEXTSIZE_SMALL));
				typebtn_row.addView(ViewBuilder.createTextView(mActivity, "", BBViewSetting.FLAG_TEXTSIZE_SMALL));
			}
			
			table.addView(typename_row);
			table.addView(typebtn_row);
		}
		
		// 比較行を設定
		setCmpWeaponRows(table, from_data, to_data);
		
		return table;
	}

	/**
	 * 武器の比較行をテーブルに設定する。
	 * @param table 比較テーブル
	 * @param from_data 比較元の武器データ
	 * @param to_data 比較先の武器データ
	 */
	private void setCmpWeaponRows(TableLayout table, BBData from_data, BBData to_data) {

		// 比較行を設定
		String[] cmp_target = BBDataManager.getCmpTarget(from_data);
		int size = cmp_target.length;
		
		for(int i=0; i<size; i++) {
			String target_key = cmp_target[i];
			String from_str = SpecValues.getShowValue(from_data, target_key, mIsKmPerHour);
			String to_str = SpecValues.getShowValue(to_data, target_key, mIsKmPerHour);
			
			if(!from_str.equals(BBData.STR_VALUE_NOTHING) && !to_str.equals(BBData.STR_VALUE_NOTHING)) {
				int[] colors = getColors(from_data, to_data, target_key);
				table.addView(ViewBuilder.createTableRow(mActivity, colors, target_key, from_str, to_str));
			}
		}
	}
	
	private int[] getColors(BBData from_data, BBData to_data, String target_key) {
		int[] ret = new int[3];
		
		BBDataComparator cmp_data = new BBDataComparator(target_key, true, true);
		int cmp = cmp_data.compare(from_data, to_data);
		
		if(cmp > 0) {
			ret[0] = SettingManager.getColorWhite();
			ret[1] = SettingManager.getColorCyan();
			ret[2] = SettingManager.getColorMazenta();
		}
		else if(cmp < 0) {
			ret[0] = SettingManager.getColorWhite();
			ret[1] = SettingManager.getColorMazenta();
			ret[2] = SettingManager.getColorCyan();
		}
		else {
			ret[0] = SettingManager.getColorWhite();
			ret[1] = SettingManager.getColorWhite();
			ret[2] = SettingManager.getColorWhite();
		}
		
		return ret;
	}
	
	/**
	 * 切り替えボタン押下時の動作を処理するリスナー
	 */
	private class TypeChangeListener implements OnClickListener {
		private int mTargetId;
		
		public TypeChangeListener(int target_id) {
			mTargetId = target_id;
		}

		@Override
		public void onClick(View arg0) {
			String type_str;
			BBData target_fromdata = mFromData;
			BBData target_todata = mToData;
			
			// フラグの切り替え
			if(mTargetId == FROM_DATA_TYPE_ID) {
				mIsTypeB_FromData = !mIsTypeB_FromData;
				
				if(!mIsTypeB_FromData) {
					type_str = "タイプA";
				}
				else {
					type_str = "タイプB";
				}
			}
			else if(mTargetId == TO_DATA_TYPE_ID) {
				mIsTypeB_ToData = !mIsTypeB_ToData;
				
				if(!mIsTypeB_ToData) {
					type_str = "タイプA";
				}
				else {
					type_str = "タイプB";
				}
			}
			else {
				return;
			}
			
			// タイプ切り替え
			if(mIsTypeB_FromData) {
				target_fromdata = mFromData.getTypeB();
			}

			if(mIsTypeB_ToData) {
				target_todata = mToData.getTypeB();
			}
			
			// 表示するデータの更新
			View view = mDialog.findViewById(mTargetId);
			
			if(view instanceof TextView) {
				((TextView)view).setText(type_str);
			}
			
			TableLayout table = (TableLayout)mDialog.findViewById(TABLE_ID);
			table.removeViews(HEADER_COUNT, table.getChildCount() - HEADER_COUNT);

			// 比較行を設定
			setCmpWeaponRows(table, target_fromdata, target_todata);
		}
		
	}
	
}
