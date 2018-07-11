package hokage.kaede.gmail.com.BBViewLib.Android.CommonLib;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import hokage.kaede.gmail.com.BBViewLib.Java.BBData;
import hokage.kaede.gmail.com.BBViewLib.Java.BBViewSetting;
import hokage.kaede.gmail.com.StandardLib.Android.SettingManager;

/**
 * リストの左側ボタン生成と長タップ時のダイアログ表示を管理するクラス
 */
public class BBAdapterCmdManager implements android.content.DialogInterface.OnClickListener, OnClickListener {
	
	// コマンドの文字列
	private String[] mCommandList;
	private boolean[] mHiddenList;
	private OnExecuteInterface mListener;
	
	private static final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
	
	private BBData mTarget;
	
	public BBAdapterCmdManager(String[] cmd_str) {
		mCommandList = cmd_str;
		
		int count = mCommandList.length;
		mHiddenList = new boolean[count];
		for(int i=0; i<count; i++) {
			mHiddenList[i] = false;
		}
	}
	
	/**
	 * 非表示にするコマンドボタンを設定する。
	 * @param position コマンドボタンの位置
	 */
	public void setHiddenTarget(int position) {
		if(position < mHiddenList.length) {
			mHiddenList[position] = true;
		}
	}
	
	/**
	 * 実行対象のデータを設定する。
	 * @param data データ
	 */
	public void setTarget(BBData data) {
		mTarget = data;
	}
	
	/**
	 * 操作選択ダイアログの項目選択時およびコマンドボタン押下時の処理を行うリスナーを設定する。
	 * @param listener リスナー
	 */
	public void setOnExecuteInterface(OnExecuteInterface listener) {
		mListener = listener;
	}

	/**
	 * 操作選択ダイアログの項目選択時およびコマンドボタン押下時の処理を管理するインターフェース
	 */
	public interface OnExecuteInterface {
		public void onExecute(BBData data, int cmd_idx);
	}
	
	/**
	 * 長いタップ時の操作選択ダイアログを表示する。
	 * @param activity 操作中のActivity
	 */
	public void showDialog(Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("操作を選択");
		builder.setIcon(android.R.drawable.ic_menu_more);
		builder.setItems(mCommandList, this);

		Dialog dialog = builder.create();
		dialog.setOwnerActivity(activity);
		dialog.show();
	}

	/**
	 * 操作選択ダイアログのコマンド選択時の処理を行う。
	 */
	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		if(mListener != null) {
			mListener.onExecute(mTarget, arg1);
		}
	}
	
	/**
	 * ボタンのビューを生成する。
	 * @param context 対象のContext
	 * @return ボタンのビュー
	 */
	public IndexLayout createButtonView(Context context) {
		return new IndexLayout(context, mCommandList, mHiddenList, this);
	}
	
	/**
	 * コマンドボタン押下時の処理を行う。
	 */
	@Override
	public void onClick(View v) {
		if(v instanceof IndexHandlerInterface) {
			IndexHandlerInterface btn = (IndexHandlerInterface)v;
			
			if(mListener != null) {
				mListener.onExecute(btn.getData(), btn.getIndex());
			}
		}
	}
	
	/**
	 * リストのボタンを搭載するレイアウトクラス
	 */
	public class IndexLayout extends LinearLayout {
		private int mButtonCount;
		private IndexHandlerInterface[] mButtons;

		public IndexLayout(Context context, String[] commands, boolean[] hiddens, OnClickListener listener) {
			super(context);
			this.setOrientation(LinearLayout.HORIZONTAL);
			this.setGravity(Gravity.LEFT | Gravity.CENTER_HORIZONTAL);
			
			mButtonCount  = commands.length;
			
			if(BBViewSetting.IS_LISTBUTTON_TYPETEXT) {
				mButtons = new IndexTextView[mButtonCount];

				for(int i=0; i<mButtonCount; i++) {
					String btn_text = commands[i].substring(0, 1);
					mButtons[i] = new IndexTextView(context, btn_text, listener);
					mButtons[i].setIndex(i);
					
					if(hiddens[i]) {
						mButtons[i].setVisibility(View.GONE);
					}
					
					this.addView((View)mButtons[i]);
				}
			}
			else {
				mButtons = new IndexButton[mButtonCount];

				for(int i=0; i<mButtonCount; i++) {
					String btn_text = commands[i].substring(0, 1);
					mButtons[i] = new IndexButton(context, btn_text, listener);
					mButtons[i].setIndex(i);
					
					if(hiddens[i]) {
						mButtons[i].setVisibility(View.GONE);
					}
					
					this.addView((View)mButtons[i]);
				}
			}
		}
		
		public void update(BBData data) {
			for(int i=0; i<mButtonCount; i++) {
				mButtons[i].setData(data);
			}
		}
	}
	
	/**
	 * リストのボタンのクラス
	 */
	private class IndexButton extends Button implements IndexHandlerInterface {
		private BBData mData;
		private int mIndex;

		public IndexButton(Context context, String btn_text, OnClickListener listener) {
			super(context);
			setText(btn_text);
			setOnClickListener(listener);
			setClickable(true);
			setFocusable(false);
		}

		@Override
		public void setData(BBData data) {
			mData = data;
		}

		@Override
		public BBData getData() {
			return mData;
		}

		@Override
		public void setIndex(int index) {
			mIndex = index;
		}

		@Override
		public int getIndex() {
			return mIndex;
		}

		@Override
		public void setVisibility(int visibility) {
			super.setVisibility(visibility);
		}
	}

	/**
	 * リストのボタンのクラス(TextView版)
	 * Android 5.0でボタンが大きくなる現象への対応
	 */
	private class IndexTextView extends TextView implements IndexHandlerInterface {
		private BBData mData;
		private int mIndex;

		public IndexTextView(Context context, String btn_text, OnClickListener listener) {
			super(context);

			setText(btn_text);
			setTextSize((float)(BBViewSetting.getTextSize(context, BBViewSetting.FLAG_TEXTSIZE_NORMAL) * BBViewSetting.LISTBUTTON_TEXTSIZE));
			setOnClickListener(listener);
			setClickable(true);
			setFocusable(false);
			
			setPadding(10, 10, 10, 10);
			setBackgroundColor(SettingManager.getColorGray());
			
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(WC, WC);
			lp.setMargins(10, 10, 10, 10);
			setLayoutParams(lp);
		}

		
		@Override
		public void setData(BBData data) {
			mData = data;
		}

		@Override
		public BBData getData() {
			return mData;
		}

		@Override
		public void setIndex(int index) {
			mIndex = index;
		}

		@Override
		public int getIndex() {
			return mIndex;
		}

		@Override
		public void setVisibility(int visibility) {
			super.setVisibility(visibility);
		}
	}
	
	/**
	 * ボタンの位置とリスト上の位置を管理するハンドラインターフェース
	 */
	private interface IndexHandlerInterface {
		public void setData(BBData data);
		public BBData getData();
		public void setIndex(int index);
		public int getIndex();
		public void setVisibility(int visibility);
	}
}
