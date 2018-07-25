package hokage.kaede.gmail.com.BBView3.Custom;

import hokage.kaede.gmail.com.BBViewLib.Java.CustomFileManager;
import hokage.kaede.gmail.com.StandardLib.Android.StringAdapter;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 「データ」画面を表示するクラス。
 */
public class FileListView extends LinearLayout {

	// レイアウト定義
	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;

	private static final String NEWLINE = System.getProperty("line.separator");

	// ファイル一覧データ
	private CustomFileManager mCustomFileManager;
	private StringAdapter mFileListAdapter;
	private String mSelectName;
	private String mNewName;
	
	// ファイル選択後の動作
	private static final String CMD_TITLE = "動作を選択";
	private static final String[] CMD_ITEMS = { "読み込む", "上書きする", "名称を変更する", "削除する", "アセン比較する" };
	private static final int CMD_READ    = 0;
	private static final int CMD_UPDATE  = 1;
	private static final int CMD_CHANGE  = 2;
	private static final int CMD_DELETE  = 3;
	private static final int CMD_COMPARE = 4;

	// ボタンテキスト
	private static final String BTN_NEW_FILE = "新規保存";

	/**
	 * 初期化を行う。
	 * @param context コンテキスト
	 */
	public FileListView(Context context) {
		super(context);

		String file_dir = context.getFilesDir().toString();
		mCustomFileManager = CustomFileManager.getInstance(file_dir);

		createView();
	}

	/**
	 * 画面を生成する。
	 */
	private void createView() {
		Context context = getContext();

		// 画面レイアウトの生成
		setLayoutParams(new LinearLayout.LayoutParams(FP, FP));
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.LEFT | Gravity.TOP);

		// リストビューの生成
		mFileListAdapter = new StringAdapter(context);
		ArrayList<String> tag_list = mCustomFileManager.getTagList();
		int size = tag_list.size();

		for(int i=0; i<size; i++) {
			mFileListAdapter.add(tag_list.get(i));
		}

		ListView listview = new ListView(context);
		listview.setOnItemClickListener(new OnSelectFileNameListener());
		listview.setAdapter(mFileListAdapter);
		listview.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));

		// 新規作成ボタンの生成
		Button newfile_btn = new Button(context);
		newfile_btn.setText(BTN_NEW_FILE);
		newfile_btn.setTag(BTN_NEW_FILE);
		newfile_btn.setOnClickListener(new OnClickCreateFileButtonListener());

		addView(listview);
		addView(newfile_btn);
	}

	/**
	 * ファイル一覧から指定のファイル名をタップした時の処理を行う。
	 */
	private class OnSelectFileNameListener implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			// 選択されたファイルタイトルからファイル名を取得
			mSelectName = mFileListAdapter.getItem(position);

			// ダイアログの表示
			AlertDialog.Builder cmd_dialog = new AlertDialog.Builder(getContext());
			cmd_dialog.setIcon(android.R.drawable.ic_menu_more);
			cmd_dialog.setTitle(CMD_TITLE);
			cmd_dialog.setItems(CMD_ITEMS, new OnSelectCommandListener());
			cmd_dialog.show();
		}
	}

	/**
	 * ファイルに対する操作を選択した時の処理を行うリスナー。
	 */
	private class OnSelectCommandListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialogInterface, int which) {
			if(which == CMD_READ) {
				showReadDialog();
			}
			else if(which == CMD_UPDATE) {
				showUpdateFileDialog();
			}
			else if(which == CMD_CHANGE) {
				showChangeDialog();
			}
			else if(which == CMD_DELETE) {
				deleteFile();
			}
			else if(which == CMD_COMPARE) {
				compareCustomData();
			}
		}
	}

	/**
	 * 新規作成ボタンを押下した時の処理を行うリスナー。
	 */
	private class OnClickCreateFileButtonListener implements OnClickListener {
		@Override
		public void onClick(View view) {
			String tag = view.getTag().toString();

			if(tag.equals(BTN_NEW_FILE)) {
				saveNewFileDiaglog();
			}
		}
	}

	/**
	 * ファイルの読み込みを確認するダイアログを表示する。
	 */
	private void showReadDialog() {

		// データ変更時のみダイアログを表示する
		if(mCustomFileManager.isCacheChanged()) {
			AlertDialog.Builder read_dialog = new AlertDialog.Builder(getContext());
			read_dialog.setTitle("データの読み込み");
			read_dialog.setPositiveButton("OK", new OnClickReadListener());
			read_dialog.setNegativeButton("Cancel", null);
			read_dialog.setMessage("編集中のデータを保存せずに読み込みしますか？");
			read_dialog.show();
		}
		else {
			readFile();
		}
	}

	/**
	 * ファイルの読み込みを実行するリスナー
	 */
	private class OnClickReadListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			readFile();
		}
	}
	
	/**
	 * ファイルの読み込み処理を行う。
	 */
	private void readFile() {
		mCustomFileManager.readFile(mSelectName);
	}

	/**
	 * ファイルの新規保存ダイアログを表示する。
	 */
	private void saveNewFileDiaglog() {
		Context context = getContext();

		EditText mDialogText = new EditText(context);
		mDialogText.setInputType(InputType.TYPE_CLASS_TEXT);
		mDialogText.addTextChangedListener(new OnUpdateFileNameTextListener());
		
		AlertDialog.Builder alt_dialog = new AlertDialog.Builder(context);
		alt_dialog.setTitle("データの新規保存");
		alt_dialog.setView(mDialogText);
		alt_dialog.setPositiveButton("OK", new OnInputNewFileNameListener());
		alt_dialog.show();
	}

	/**
	 * ファイルの新規作成ダイアログで名前を入力した後に処理を行うリスナー。
	 */
	private class OnInputNewFileNameListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialogInterface, int i) {
			createFile();
		}
	}

	/**
	 * ファイルを新規保存する。
	 */
	private void createFile() {

		// ファイルが既に存在する場合は、処理を終了する
		if(!mCustomFileManager.createFile(mNewName)) {
			String warning_str = "既にデータが存在しています。" + NEWLINE + "別の名前を入力してください。";
			Toast.makeText(getContext(), warning_str, Toast.LENGTH_LONG).show();
			return;
		}

		// リストに追加し、再描画する
		mFileListAdapter.add(mNewName);
		mFileListAdapter.notifyDataSetChanged();
	}
	
	/**
	 * ファイルを上書き保存する。
	 */
	private void showUpdateFileDialog() {
		AlertDialog.Builder upd_dialog = new AlertDialog.Builder(getContext());
		upd_dialog.setTitle("データの上書き");
		upd_dialog.setPositiveButton("OK", new OnClickUpdateListener());
		upd_dialog.setNegativeButton("Cancel", null);
		upd_dialog.setMessage("データを上書きしますか？");
		upd_dialog.show();
	}
	
	/**
	 * ファイルの上書き保存を実行するリスナー
	 */
	private class OnClickUpdateListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			mCustomFileManager.updateFile(mSelectName);
		}
	}

	/**
	 * ファイル名変更ダイアログを表示する。
	 */
	private void showChangeDialog() {
		Context context = getContext();

		EditText mDialogText = new EditText(context);
		mDialogText.setInputType(InputType.TYPE_CLASS_TEXT);
		mDialogText.addTextChangedListener(new OnUpdateFileNameTextListener());

		AlertDialog.Builder alt_dialog = new AlertDialog.Builder(context);
		alt_dialog.setTitle("データ名の変更");
		alt_dialog.setView(mDialogText);
		alt_dialog.setPositiveButton("OK", new OnInputAfterFileNameListener());
		alt_dialog.show();
	}

	/**
	 * ファイル名変更における変更後のファイル名を入力した後に処理を行うリスナー。
	 */
	private class OnInputAfterFileNameListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialogInterface, int i) {
			changeFile();
		}
	}

	/**
	 * ファイル名を変更する。
	 */
	private void changeFile() {
		// 存在する場合、エラーダイアログを表示し、処理を終了する
		if(!mCustomFileManager.change(mSelectName, mNewName)) {
			String warning_str = "既にデータが存在しています。" + NEWLINE + "別の名前を入力してください。";
			Toast.makeText(getContext(), warning_str, Toast.LENGTH_LONG).show();
			return;
		}
		
		// リストを再描画する
		mFileListAdapter.replaceItem(mSelectName, mNewName);
		mFileListAdapter.notifyDataSetChanged();
	}
	
	/**
	 * ファイルを削除する。
	 */
	private void deleteFile() {
		AlertDialog.Builder del_dialog = new AlertDialog.Builder(getContext());
		del_dialog.setTitle("データの削除");
		del_dialog.setPositiveButton("OK", new OnClickDeleteListener());
		del_dialog.setNegativeButton("Cancel", null);
		del_dialog.setMessage("データを削除しますか？");
		del_dialog.show();
	}

	/**
	 * ファイルの削除を実行するリスナー
	 */
	private class OnClickDeleteListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// ファイルを削除する
			mCustomFileManager.delete(mSelectName);

			// リストを再描画する
			mFileListAdapter.removeItem(mSelectName);
			mFileListAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * アセンの比較画面に移動する。
	 */
	private void compareCustomData() {
		Context context = getContext();
		Intent intent = new Intent(context, CompareActivity.class);
		intent.putExtra(CompareActivity.INTENTKEY_CMPTO_FILENAME, mSelectName);

		context.startActivity(intent);
	}

	/**
	 * EditTextの入力文字列を更新するリスナー。
	 */
	private class OnUpdateFileNameTextListener implements TextWatcher {
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			// Do Nothing
		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			mNewName = charSequence.toString();
		}

		@Override
		public void afterTextChanged(Editable editable) {
			// Do Nothing
		}
	}
}
