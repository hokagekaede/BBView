package hokage.kaede.gmail.com.BBView3.Custom;

import hokage.kaede.gmail.com.BBViewLib.Java.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.Java.CustomData;
import hokage.kaede.gmail.com.BBViewLib.Java.CustomDataManager;
import hokage.kaede.gmail.com.BBViewLib.Java.CustomDataReader;
import hokage.kaede.gmail.com.StandardLib.Android.StringAdapter;
import hokage.kaede.gmail.com.StandardLib.Java.FileKeyValueStore;
import hokage.kaede.gmail.com.StandardLib.Java.FileManager;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 「データ」画面を表示するクラス。
 */
public class FileListView extends LinearLayout implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener, OnClickListener {

	// レイアウト定義
	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;

	private static final String NEWLINE = System.getProperty("line.separator");
	
	// コンテンツデータ
	private EditText mDialogText;
	
	// ファイル一覧データ
	private StringAdapter mFileListAdapter;
	private String mSelectName;
	
	// ファイル選択後の動作
	private static final String CMD_TITLE = "動作を選択";
	private static final String[] CMD_ITEMS = { "読み込む", "上書きする", "名称を変更する", "削除する", "アセン比較する" };
	private static final int CMD_READ    = 0;
	private static final int CMD_UPDATE   = 1;
	private static final int CMD_CHANGE  = 2;
	private static final int CMD_DELETE  = 3;
	private static final int CMD_COMPARE = 4;
	
	// ダイアログの種類
	private int mDialogType;
	private static final int CMD_TYPE    = 0;
	private static final int CMD_CREATE  = 1;
	private static final int CMD_EDIT    = 2;
	
	// ボタンテキスト
	private static final String BTN_NEW_FILE = "新規保存";
	
	private Context mContext;
	
	private static final String FILE_HEAD = "file";
	private static final String FILE_TAIL = ".dat";
	private String mTargetDir;
	private FileManager mFileManager;

	public FileListView(Context context) {
		super(context);
		mContext = context;
		
		mDialogType = CMD_TYPE;
		
		//  データ管理ファイルを読み込む
		mTargetDir = mContext.getFilesDir().toString();
		mFileManager = new FileManager(mTargetDir, FILE_HEAD, FILE_TAIL);

		// 画面レイアウトの生成
		setLayoutParams(new LinearLayout.LayoutParams(FP, FP));
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.LEFT | Gravity.TOP);
		
		// リストビューの生成
		mFileListAdapter = new StringAdapter(mContext);
		ArrayList<String> tag_list = mFileManager.getTagList();
		int size = tag_list.size();

		for(int i=0; i<size; i++) {
			mFileListAdapter.add(tag_list.get(i));
		}
		
		ListView listview = new ListView(mContext);
		listview.setOnItemClickListener(this);
		listview.setAdapter(mFileListAdapter);
		listview.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));

		// 新規作成ボタンの生成
		Button newfile_btn = new Button(mContext);
		newfile_btn.setText(BTN_NEW_FILE);
		newfile_btn.setTag(BTN_NEW_FILE);
		newfile_btn.setOnClickListener(this);
		
		addView(listview);
		addView(newfile_btn);
	}

	/**
	 * リスト項目タップ時の処理を行う。
	 */
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		// 選択されたファイルタイトルからファイル名を取得
		mSelectName = mFileListAdapter.getItem(position);
		
		// ダイアログの表示
		mDialogType = CMD_TYPE;
		showDialog(CMD_TITLE, CMD_ITEMS);
	}
	
	/**
	 * ダイアログの項目タップ時の処理を行う。
	 */
	public void onClick(DialogInterface dialog, int which) {
		if(mDialogType == CMD_TYPE) {
			if(which == CMD_READ) {
				showReadDialog();
			}
			else if(which == CMD_UPDATE) {
				updateFile();
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
			else {
				// DO NOTHING
			}
		}
		else if(mDialogType == CMD_CREATE) {
			createFile();
		}
		else if(mDialogType == CMD_EDIT) {
			changeFile();
		}
	}

	/**
	 * ボタンのタップ時の処理を行う。
	 */
	@Override
	public void onClick(View v) {
		String tag = v.getTag().toString();
		
		if(tag.equals(BTN_NEW_FILE)) {
			saveNewFileDiaglog();
		}
	}
	
	/**
	 * ダイアログを表示する
	 * @param title ダイアログのタイトル
	 * @param items 表示する項目
	 */
	private void showDialog(String title, String[] items) {
		AlertDialog.Builder cmd_dialog = new AlertDialog.Builder(mContext);
		cmd_dialog.setIcon(android.R.drawable.ic_menu_more);
		cmd_dialog.setTitle(title);
		cmd_dialog.setItems(items, this);
		cmd_dialog.show();
	}
	
	/**
	 * ファイルの読み込みを確認するダイアログを表示する。
	 */
	private void showReadDialog() {

		// データ変更時のみダイアログを表示する
		if(CustomDataManager.isChanged()) {
			AlertDialog.Builder read_dialog = new AlertDialog.Builder(mContext);
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
		// ファイルを読み込み、キャッシュデータに反映
		FileKeyValueStore select_file = new FileKeyValueStore(mFileManager.getDirectory(), mFileManager.getFileName(mSelectName));
		select_file.load();
		select_file.save(FileKeyValueStore.CACHE_NAME);
		
		// カスタムデータに反映する
		BBDataManager data_mng = BBDataManager.getInstance();
		CustomData read_data = CustomDataReader.read(select_file, CustomDataManager.getDefaultData(), data_mng);
		
		if(read_data != null) {
			CustomDataManager.setCustomData(read_data);
			
			// データ変更状態を解除する
			CustomDataManager.setChanged(false);
		}
		else {
			Toast.makeText(mContext, mSelectName + "の読み込みに失敗しました。", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * ファイルの新規保存ダイアログを表示する。
	 */
	private void saveNewFileDiaglog() {
		mDialogType = CMD_CREATE;
	
		mDialogText = new EditText(mContext);
		mDialogText.setInputType(InputType.TYPE_CLASS_TEXT);
		
		AlertDialog.Builder alt_dialog = new AlertDialog.Builder(mContext);
		alt_dialog.setTitle("データの新規保存");
		alt_dialog.setView(mDialogText);
		alt_dialog.setPositiveButton("OK", this);
		alt_dialog.show();
	}
	
	/**
	 * ファイルを新規保存する。
	 */
	private void createFile() {

		// ファイルタグを取得する。
		String filetag  = mDialogText.getText().toString();

		// ファイルタグが既に存在する場合、処理を終了する。
		if(!mFileManager.create(filetag)) {
			String warning_str = "既にデータが存在しています。" + NEWLINE + "別の名前を入力してください。";
			Toast.makeText(mContext, warning_str, Toast.LENGTH_LONG).show();
			return;
		}

		// キャッシュのファイルを指定のファイル名で保存する
		FileKeyValueStore file_data = new FileKeyValueStore(mFileManager.getDirectory());
		file_data.load();
		file_data.save(mFileManager.getFileName(filetag));
		file_data = null;
		
		// リストに追加し、再描画する
		mFileListAdapter.add(filetag);
		mFileListAdapter.notifyDataSetChanged();

		// データ変更状態を解除する
		CustomDataManager.setChanged(false);
	}
	
	/**
	 * ファイルを上書き保存する。
	 */
	private void updateFile() {
		AlertDialog.Builder upd_dialog = new AlertDialog.Builder(mContext);
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
			FileKeyValueStore file_data = new FileKeyValueStore(mFileManager.getDirectory());
			file_data.load();
			file_data.save(mFileManager.getFileName(mSelectName));
			
			// データ変更状態を解除する
			CustomDataManager.setChanged(false);
		}
	}
	
	/**
	 * ファイル名変更ダイアログを表示する。
	 */
	private void showChangeDialog() {
		mDialogType = CMD_EDIT;
		
		mDialogText = new EditText(mContext);
		mDialogText.setInputType(InputType.TYPE_CLASS_TEXT);

		AlertDialog.Builder alt_dialog = new AlertDialog.Builder(mContext);
		alt_dialog.setTitle("データ名の変更");
		alt_dialog.setView(mDialogText);
		alt_dialog.setPositiveButton("OK", this);
		alt_dialog.show();
	}

	/**
	 * ファイル名を変更する。
	 */
	private void changeFile() {
		// ファイルタグを取得する。
		String filetag = mDialogText.getText().toString();
		
		// 存在する場合、エラーダイアログを表示し、処理を終了する
		if(!mFileManager.change(mSelectName, filetag)) {
			String warning_str = "既にデータが存在しています。" + NEWLINE + "別の名前を入力してください。";
			Toast.makeText(mContext, warning_str, Toast.LENGTH_LONG).show();
			return;
		}
		
		// リストを再描画する
		mFileListAdapter.replaceItem(mSelectName, filetag);
		mFileListAdapter.notifyDataSetChanged();
	}
	
	/**
	 * ファイルを削除する。
	 */
	private void deleteFile() {
		AlertDialog.Builder del_dialog = new AlertDialog.Builder(mContext);
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
			mFileManager.delete(mSelectName);

			// リストを再描画する
			mFileListAdapter.removeItem(mSelectName);
			mFileListAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * アセンの比較画面に移動する。
	 */
	private void compareCustomData() {
		// リストからファイルデータを取得する。
		String select_file_name = mFileManager.getFileName(mSelectName);

		Intent intent = new Intent(mContext, CompareActivity.class);
		intent.putExtra(CompareActivity.INTENTKEY_CMPTO_FILENAME, select_file_name);
		mContext.startActivity(intent);
	}
}
