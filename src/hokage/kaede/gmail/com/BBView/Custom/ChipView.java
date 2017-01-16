package hokage.kaede.gmail.com.BBView.Custom;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataFilter;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.BBViewSetting;
import hokage.kaede.gmail.com.BBViewLib.CustomData;
import hokage.kaede.gmail.com.BBViewLib.CustomDataManager;
import hokage.kaede.gmail.com.BBViewLib.CustomDataWriter;
import hokage.kaede.gmail.com.BBViewLib.SpecValues;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBAdapterValueFilterManager;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBExpandableChipAdapter;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBExpandableChipAdapter.OnChengedChipSetBaseListener;
import hokage.kaede.gmail.com.BBViewLib.Adapter.BBAdapterValueFilterManager.OnClickValueFilterButtonListener;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * チップ画面のレイアウト
 */
public class ChipView extends LinearLayout implements OnClickValueFilterButtonListener {

	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;
	
	private static final int WEIGHT_TEXT_VIEW_ID = 5321;

	private BBDataManager mDataManager;
	private BBAdapterValueFilterManager mFilterManager;
	private CustomData mCustomData;
	private BBDataFilter mFilter;
	
	private BBExpandableChipAdapter mChipListAdapter;

	private ArrayList<BBData> mBeforeChipList;
	
	private boolean mIsChanged;
	
	// チップ登録エラーメッセージ
	private String mErrorMessage;

	public ChipView(Context context) {
		super(context);
		
		mIsChanged = false;
		mErrorMessage = "";

		mDataManager = BBDataManager.getInstance();
		mCustomData = CustomDataManager.getCustomData();

		// 変更前のチップリストを取得する
		mBeforeChipList = mCustomData.getChips();

		// アプリ画面の生成
		createView();
		
		// 容量テキストを更新する
		updateWeightText();
	}
	
	/**
	 * 画面を生成する
	 */
	private void createView() {
		Context context = getContext();
		
		setLayoutParams(new LinearLayout.LayoutParams(FP, FP));
        setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.TOP);
		
		// フィルタをチップのみに設定
		mFilter = new BBDataFilter();
		mFilter.setOtherType(BBDataManager.CHIP_STR);

		// フィルタ設定ダイアログを初期化する
		ArrayList<String> key_list = new ArrayList<String>();
		key_list.add("コスト");
		mFilterManager = new BBAdapterValueFilterManager(mFilter, key_list);
		mFilterManager.setOnClickValueFilterButtonListener(this);
		
		// アダプタを設定する
		mChipListAdapter = new BBExpandableChipAdapter();
		mChipListAdapter.addChildren(mDataManager.getList(mFilter));
		mChipListAdapter.loadCustomData();
		mChipListAdapter.setOnChengedChipSetListener(new OnChengedChipSetListener());
		
		ExpandableListView chip_list_view = new ExpandableListView(context);
		chip_list_view.setAdapter(mChipListAdapter);
		chip_list_view.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		
		// 容量と現在の使用状況を表示するテキストビューを生成する
		TextView weight_text_view = new TextView(context);
		weight_text_view.setTextSize(BBViewSetting.getTextSize(context, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
		weight_text_view.setId(WEIGHT_TEXT_VIEW_ID);

		LinearLayout layout_btm = new LinearLayout(context);
		layout_btm.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		layout_btm.setOrientation(LinearLayout.HORIZONTAL);
		layout_btm.setGravity(Gravity.TOP);
		
		// 決定ボタンを生成する
		Button ok_button = new Button(context);
		ok_button.setText("決定");
		ok_button.setLayoutParams(new LinearLayout.LayoutParams(WC, WC, 1));
		ok_button.setOnClickListener(new OnClickOKButtonListener());
		
		// クリアボタンを生成する
		Button clear_button = new Button(context);
		clear_button.setText("クリア");
		clear_button.setLayoutParams(new LinearLayout.LayoutParams(WC, WC, 1));
		clear_button.setOnClickListener(new OnClickClearButtonListener());

		// 選択中表示ボタンを生成する
		Button view_button = new Button(context);
		view_button.setText("選択中表示");
		view_button.setLayoutParams(new LinearLayout.LayoutParams(WC, WC, 1));
		view_button.setOnClickListener(new OnClickViewButtonListener());
		
		layout_btm.addView(ok_button);
		layout_btm.addView(clear_button);
		layout_btm.addView(view_button);
		
		addView(chip_list_view);
		addView(weight_text_view);
		addView(layout_btm);
	}

	/**
	 * リストが更新された際の処理を行うリスナー。
	 * チップ容量と変更状態を更新する。
	 */
	private class OnChengedChipSetListener implements OnChengedChipSetBaseListener {

		@Override
		public void changed() {
			mIsChanged = true;
			updateWeightText();
		}
	}

	/**
	 * 容量のテキスト情報を更新する
	 */
	private void updateWeightText() {
		View tmp_view = this.findViewById(WEIGHT_TEXT_VIEW_ID);
		checkStatus();

		if(tmp_view instanceof TextView) {
			TextView weight_text_view = (TextView)tmp_view;

			// チップの現在値、最大値を取得する
			int chip_weight = mCustomData.getChipWeight();
			int chip_capacity = SpecValues.castInteger(mCustomData.getChipCapacity());
			
			String now_value = String.valueOf(chip_weight);
			String max_value = String.valueOf(chip_capacity);
			
			weight_text_view.setText("容量：" + now_value + "/" + max_value + " " + mErrorMessage);
			
			// 登録内容に問題がある場合は容量テキストを赤文字にする。
			if(mErrorMessage.equals("")) {
				weight_text_view.setTextColor(SettingManager.getColorWhite());
			}
			else {
				weight_text_view.setTextColor(SettingManager.getColorMazenta());
			}
		}
	}

	/**
	 * チップの状態を確認する。
	 * チップの状態に問題があれば、エラーメッセージを表示する。
	 * @return 選択中のチップの状態に問題がなければtrueを返す。問題があればfalseを返す。
	 */
	private boolean checkStatus() {
		boolean ret = true;
		Context context = getContext();

		// チップの現在値、最大値を取得する
		int chip_weight = mCustomData.getChipWeight();
		int chip_capacity = SpecValues.castInteger(mCustomData.getChipCapacity());
		
		if(chip_weight > chip_capacity) {
			mErrorMessage = "[チップ容量超過]";
			Toast.makeText(context, "チップの容量が超過しています。", Toast.LENGTH_SHORT).show();
			ret = false;
		}
		else if(!mCustomData.judgeActionChip()) {
			mErrorMessage = "[アクションチップ重複]";
			Toast.makeText(context, "アクションチップの設定に誤りがあります。", Toast.LENGTH_SHORT).show();
			ret = false;
		}
		else if(!mCustomData.judgePowerupChip()) {
			mErrorMessage = "[機体強化チップ重複]";
			Toast.makeText(context, "機体強化チップの設定に誤りがあります。", Toast.LENGTH_SHORT).show();
			ret = false;
		}
		else {
			mErrorMessage = "";
		}
		
		return ret;
	}
	
	/**
	 * 決定ボタンを押下した際の処理を行うリスナー。
	 * データチェック後保存、処理をする。
	 */
	private class OnClickOKButtonListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			saveCustomData();
			Toast.makeText(getContext(), "チップを登録しました。", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * クリアボタンを押下した際の処理を行うリスナー。
	 * 選択中のデータをクリアする。
	 */
	private class OnClickClearButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			mCustomData.clearChips();
			mChipListAdapter.clearFlags();
			mChipListAdapter.notifyDataSetChanged();
			mErrorMessage = "";
			updateWeightText();
		}	
	}
	
	/**
	 * 選択中表示ボタンを押下した際の処理を行うリスナー。
	 * 現在選択しているチップを表示するためのダイアログを表示する。
	 */
	private class OnClickViewButtonListener implements OnClickListener, android.content.DialogInterface.OnClickListener, OnMultiChoiceClickListener {

		private ArrayList<BBData> mChipList;
		private boolean[] mChecks;
		
		@Override
		public void onClick(View v) {
			Context context = getContext();
			
			mChipList = mCustomData.getChips();
			int size = mChipList.size();
			
			if(size==0) {
				Toast.makeText(context, "チップが選択されていません", Toast.LENGTH_SHORT).show();
				return;
			}

			String[] selected_chips = new String[size];
			mChecks = new boolean[size];
			
			for(int i=0;i<size;i++) {
				BBData chip = mChipList.get(i);
				selected_chips[i] = chip.get("名称") + " [" + chip.get("コスト") + "]";
				mChecks[i] = true;
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("選択中のチップ一覧");
			builder.setIcon(android.R.drawable.ic_menu_more);
			builder.setMultiChoiceItems(selected_chips, mChecks, this);
			builder.setPositiveButton("OK", this);
			builder.setNegativeButton("Cancel", null);
			
			Dialog dialog = builder.create();
			dialog.show();
		}

		@Override
		public void onClick(DialogInterface arg0, int which) {
			
			int size = mChecks.length;
			for(int i=0; i<size; i++) {
				if(!mChecks[i]) {
					mCustomData.removeChip(mChipList.get(i));
				}
			}
			
			redraw();
			setChanged();
		}

		@Override
		public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
			// 何も処理を行わないが、チェック状態を保持するために本メソッドの定義が必要
		}
	}
	
	/**
	 * カスタマイズデータを保存する
	 */
	private void saveCustomData() {

		// カスタムデータをファイルに書き込む。
		Context context = getContext();
		CustomDataWriter.write(mCustomData, context.getFilesDir().toString());
		
		// 変更前リストを更新し、変更フラグを解除する
		mBeforeChipList = mCustomData.getChips();
		mIsChanged = false;
	}
	
	/**
	 * チップの編集状況をリセットする。画面切り替え時などに使用する。
	 */
	public void reset() {
		if(mIsChanged) {
			mCustomData.clearChips();
			
			int size = mBeforeChipList.size();
			for(int i=0; i<size; i++) {
				mCustomData.addChip(mBeforeChipList.get(i));
			}
		}
	}
	
	/**
	 * 再描画を行う。一旦全チェック状態をリセットして付け直す。
	 */
	public void redraw() {
		mChipListAdapter.clearFlags();
		mChipListAdapter.loadCustomData();
		updateWeightText();
		mChipListAdapter.notifyDataSetChanged();
	}

	/**
	 * 変更状態を変更ありに設定する。
	 * ChipViewの"決定"ボタンからファイル保存したときのみ変更無しにするため、
	 * 本関数では変更状態(true)から変更無し(false)には設定できない。
	 */
	public void setChanged() {
		mIsChanged = true;
	}
	
	/**
	 * フィルタ設定ダイアログを表示する。
	 * @param activity オーナーアクティビティ
	 */
	public void showFilterDialog(Activity activity) {
		mFilterManager.showDialog(activity);
	}
	
	/**
	 * フィルタ設定を実行した場合の処理を行う。
	 */
	@Override
	public void onClickValueFilterButton() {
		mFilter = mFilterManager.getFilter();

		ArrayList<BBData> datalist = mDataManager.getList(mFilter);
		mChipListAdapter.clear();
		mChipListAdapter.addChildren(datalist);
		mChipListAdapter.notifyDataSetChanged();
		
		if(datalist.size() <= 0) {
			Toast.makeText(ChipView.this.getContext(), "条件に一致するチップはありません。", Toast.LENGTH_SHORT).show();
		}
		else {
			mChipListAdapter.loadCustomData();
		}
	}
}
