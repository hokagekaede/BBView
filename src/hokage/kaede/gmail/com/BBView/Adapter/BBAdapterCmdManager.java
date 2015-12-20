package hokage.kaede.gmail.com.BBView.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.Android.BBViewSettingManager;

/**
 * リストの左側ボタン生成と長タップ時のダイアログ表示を管理するクラス
 */
public class BBAdapterCmdManager implements android.content.DialogInterface.OnClickListener, OnClickListener {
	private Activity mActivity;
	
	// コマンドの文字列
	private String[] mCommandList;
	private OnClickIndexButtonInterface mButtonListener;
	private OnExecuteInterface mListener;
	
	private static final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
	
	private BBData mTarget;
	
	public BBAdapterCmdManager(Activity activity, String[] cmd_str) {
		mActivity = activity;
		mCommandList = cmd_str;
	}
	
	public void setTarget(BBData data) {
		mTarget = data;
	}
	
	public void setOnClickIndexButtonInterface(OnClickIndexButtonInterface listener) {
		mButtonListener = listener;
	}
	
	public void setOnExecuteInterface(OnExecuteInterface listener) {
		mListener = listener;
	}
	
	public String[] getCommandList() {
		return mCommandList;
	}

	public void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle("操作を選択");
		builder.setIcon(android.R.drawable.ic_menu_more);
		builder.setItems(mCommandList, this);

		Dialog dialog = builder.create();
		dialog.setOwnerActivity(mActivity);
		dialog.show();
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		if(mListener != null) {
			mListener.onExecute(mTarget, arg1);
		}
	}
	
	public IndexLayout createButtonView(int position) {
		return new IndexLayout(mActivity, mCommandList, position, this);
	}
	
	@Override
	public void onClick(View v) {
		if(v instanceof IndexHandlerInterface) {
			IndexHandlerInterface btn = (IndexHandlerInterface)v;
			
			if(mButtonListener != null) {
				mButtonListener.onClickIndexButton(btn.getPosition(), btn.getIndex());
			}
			
			if(mListener != null) {
				mListener.onExecute(mTarget, btn.getIndex());
			}
		}
	}
	
	public interface OnClickIndexButtonInterface {
		public void onClickIndexButton(int position, int index);
	}
	
	public interface OnExecuteInterface {
		public void onExecute(BBData data, int cmd_idx);
	}
	
	/**
	 * リストのボタンを搭載するレイアウトクラス
	 */
	public class IndexLayout extends LinearLayout {
		private int mButtonCount;
		private IndexHandlerInterface[] mButtons;

		public IndexLayout(Context context, String[] commands, int position, OnClickListener listener) {
			super(context);
			this.setOrientation(LinearLayout.HORIZONTAL);
			this.setGravity(Gravity.LEFT | Gravity.CENTER_HORIZONTAL);
			
			mButtonCount  = commands.length;
			
			if(BBViewSettingManager.IS_LISTBUTTON_TYPETEXT) {
				mButtons = new IndexTextView[mButtonCount];

				for(int i=0; i<mButtonCount; i++) {
					String btn_text = commands[i].substring(0, 1);
					mButtons[i] = new IndexTextView(context, btn_text, listener);
					mButtons[i].setIndex(i);
					mButtons[i].setPosition(position);
					
					this.addView((View)mButtons[i]);
				}
			}
			else {
				mButtons = new IndexButton[mButtonCount];

				for(int i=0; i<mButtonCount; i++) {
					String btn_text = commands[i].substring(0, 1);
					mButtons[i] = new IndexButton(context, btn_text, listener);
					mButtons[i].setIndex(i);
					mButtons[i].setPosition(position);
					
					this.addView((View)mButtons[i]);
				}
			}
		}
		
		public void update(int position) {
			for(int i=0; i<mButtonCount; i++) {
				mButtons[i].setPosition(position);
			}
		}
	}
	
	/**
	 * リストのボタンのクラス
	 */
	private class IndexButton extends Button implements IndexHandlerInterface {
		private int mPosition;
		private int mIndex;

		public IndexButton(Context context, String btn_text, OnClickListener listener) {
			super(context);
			setText(btn_text);
			setOnClickListener(listener);
			setClickable(true);
			setFocusable(false);
		}
		
		public void setPosition(int position) {
			mPosition = position;
		}
		
		public int getPosition() {
			return mPosition;
		}
		
		public void setIndex(int index) {
			mIndex = index;
		}
		
		public int getIndex() {
			return mIndex;
		}
	}

	/**
	 * リストのボタンのクラス(TextView版)
	 * Android 5.0でボタンが大きくなる現象への対応
	 */
	private class IndexTextView extends TextView implements IndexHandlerInterface {
		private int mPosition;
		private int mIndex;

		public IndexTextView(Context context, String btn_text, OnClickListener listener) {
			super(context);

			setText(btn_text);
			setOnClickListener(listener);
			setClickable(true);
			setFocusable(false);
			
			setPadding(10, 10, 10, 10);
			setBackgroundColor(Color.GRAY);
			
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(WC, WC);
			lp.setMargins(10, 10, 10, 10);
			setLayoutParams(lp);
		}
		
		public void setPosition(int position) {
			mPosition = position;
		}
		
		public int getPosition() {
			return mPosition;
		}
		
		public void setIndex(int index) {
			mIndex = index;
		}
		
		public int getIndex() {
			return mIndex;
		}
	}
	
	/**
	 * ボタンの位置とリスト上の位置を管理するハンドラインターフェース
	 */
	private interface IndexHandlerInterface {
		public void setPosition(int position);
		public int getPosition();
		public void setIndex(int index);
		public int getIndex();
	}
}
