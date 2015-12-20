package hokage.kaede.gmail.com.Lib.Android;

import hokage.kaede.gmail.com.BBView3.R;
import hokage.kaede.gmail.com.Lib.Android.PreferenceIO;
import hokage.kaede.gmail.com.Lib.Java.EncryptionUtillity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * ログインダイアログを表示する機能。
 * PreferenceIOクラス必須。
 */
public class UidManager implements OnClickListener {

	private static final String KEY_UID  = "uid";
	private static final String KEY_PASS = "pass";
	
	private Activity mActivity;
	
	private int mUidEditTextId;
	private int mPassEditTextId;
	
	private String mUid;
	private String mPassword;
	private byte mEnckey;

	private OnInputPassListener mListener;
	
	/**
	 * 初期化を行う。
	 * @param activity
	 */
	public UidManager(Activity activity) {
		mActivity = activity;
		
		Resources res = activity.getResources();
		mEnckey = (byte)res.getInteger(R.integer.enc_key);
		
		mUid = PreferenceIO.readString(activity, KEY_UID, "");
		String sav_pass = PreferenceIO.readString(activity, KEY_PASS, "");
		mPassword = EncryptionUtillity.decode(sav_pass, mEnckey);
	}
	
	/**
	 * ダイアログを表示する。
	 */
	public void showDialog() {
		TableLayout layout = new TableLayout(mActivity);
		layout.setGravity(Gravity.CENTER);
		layout.setStretchAllColumns(true);
		layout.setPadding(10, 10, 10, 10);
		
		TextView id_text_view = new TextView(mActivity);
		id_text_view.setText("SEGA ID");
		
		EditText id_edit_text = new EditText(mActivity);
		id_edit_text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		id_edit_text.setText(mUid);

		mUidEditTextId = id_edit_text.hashCode();
		id_edit_text.setId(mUidEditTextId);
		
		TextView ps_text_view = new TextView(mActivity);
		ps_text_view.setText("PASSWORD");
		
		EditText ps_edit_text = new EditText(mActivity);
		ps_edit_text.setText(mPassword);
		ps_edit_text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

		mPassEditTextId = ps_edit_text.hashCode();
		ps_edit_text.setId(mPassEditTextId);
		
		TableRow id_row = new TableRow(mActivity);
		id_row.addView(id_text_view);
		id_row.addView(id_edit_text);
		
		TableRow ps_row = new TableRow(mActivity);
		ps_row.addView(ps_text_view);
		ps_row.addView(ps_edit_text);
		
		layout.addView(id_row);
		layout.addView(ps_row);

		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle("ログイン");
		builder.setView(layout);
		builder.setPositiveButton("OK", this);
		
		Dialog dialog = builder.create();
		dialog.setOwnerActivity(mActivity);
		dialog.show();
	}

	/**
	 * OKボタン押下時の処理を行う。
	 */
	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		AlertDialog login_dialog = (AlertDialog)arg0;
		
		EditText id_edit_text = (EditText)login_dialog.findViewById(mUidEditTextId);
		mUid = id_edit_text.getText().toString();
		
		EditText pass_edit_text = (EditText)login_dialog.findViewById(mPassEditTextId);
		mPassword = pass_edit_text.getText().toString();

		// IDとパスを保存する
		PreferenceIO.write(mActivity, KEY_UID, mUid);
		PreferenceIO.write(mActivity, KEY_PASS, EncryptionUtillity.encode(mPassword, mEnckey));
		
		if(mListener != null) {
			mListener.InputPass();
		}
	}
	
	/**
	 * UIDを取得する。
	 */
	public String getUid() {
		return mUid;
	}
	
	/**
	 * パスワードを取得する。
	 * @return
	 */
	public String getPassword() {
		return mPassword;
	}
	
	/**
	 * パス入力完了通知リスナーを設定する。
	 * @param listener
	 */
	public void setOnInputPassListener(OnInputPassListener listener) {
		mListener = listener;
	}
	
	/**
	 * パス入力完了通知。
	 */
	public interface OnInputPassListener {
		public void InputPass();
	}
}
