package hokage.kaede.gmail.com.BBView3;

import hokage.kaede.gmail.com.BBViewLib.Android.BBViewSettingManager;
import hokage.kaede.gmail.com.Lib.Java.FileKeyValueStore;
import hokage.kaede.gmail.com.Lib.Java.FileIO;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SettingActivity extends PreferenceActivity implements OnClickListener {
	private static final String NEWLINE = System.getProperty("line.separator");

	private FileKeyValueStore mSaveData;

	// ダイアログの種類
	private int dialog_type;
	private static final int CMD_IMPORT  = 0;
	private static final int CMD_EXPORT  = 1;

	// メニュー番号
	private static final int MENU_IMPORT = 1;
	private static final int MENU_EXPORT = 2;

	/**
	 * 設定画面起動時の処理を行う
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// テーマを設定する
		/*
		int res_id = BBViewSettingManager.sThemeID;
		
		if(res_id != BBViewSettingManager.THEME_DEFAULT_ID) {
			setTheme(res_id);
		}
		*/
		
		loadSaveFileList();
		createScreen();
	}
	
	/**
	 * セーブデータ管理ファイルを読み込む
	 */
	private void loadSaveFileList() {
		String dir = getFilesDir().toString();
		mSaveData = new FileKeyValueStore(dir, FileKeyValueStore.FILELIST_DAT);
		mSaveData.load();
	}
	
	/**
	 * 設定画面を生成する
	 */
	private void createScreen() {
		PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);
		
		// 全体設定
		PreferenceCategory all_category = new PreferenceCategory(this);
		all_category.setTitle("全体設定");
		screen.addPreference(all_category);

		ListPreference text_size_type = new ListPreference(this);
		text_size_type.setTitle("テキストサイズ");
		text_size_type.setSummary("文字の大きさを変更する");
		text_size_type.setKey(BBViewSettingManager.TEXT_SIZE_KEY);
		text_size_type.setEntries(BBViewSettingManager.STR_TEXT_SIZE_LIST);
		text_size_type.setEntryValues(BBViewSettingManager.STR_TEXT_SIZE_LIST);	
		text_size_type.setDefaultValue(BBViewSettingManager.STR_TEXT_SIZE_DEFAULT);
		screen.addPreference(text_size_type);
		
		ListPreference theme_type = new ListPreference(this);
		theme_type.setTitle("テーマ");
		theme_type.setSummary("画面の背景色などを変更する");
		theme_type.setKey(BBViewSettingManager.THEME_KEY);
		theme_type.setEntries(BBViewSettingManager.THEME_LIST);
		theme_type.setEntryValues(BBViewSettingManager.THEME_LIST);	
		theme_type.setDefaultValue(BBViewSettingManager.THEME_DEFAULT);
		screen.addPreference(theme_type);
		
		CheckBoxPreference speed_view_type = new CheckBoxPreference(this);
		speed_view_type.setTitle("移動速度の単位");
		speed_view_type.setSummary("km/h表示にする");
		speed_view_type.setKey(BBViewSettingManager.SETTING_SPEED_VIEW_TYPE);
		screen.addPreference(speed_view_type);
		
		// アセン画面
		PreferenceCategory custom_category = new PreferenceCategory(this);
		custom_category.setTitle("アセン画面");
		screen.addPreference(custom_category);

		CheckBoxPreference show_column2 = new CheckBoxPreference(this);
		show_column2.setTitle("2列表示");
		show_column2.setSummary("武器を2列表示にする");
		show_column2.setKey(BBViewSettingManager.SETTING_SHOW_COLUMN2);
		show_column2.setChecked(BBViewSettingManager.IS_SHOW_COLUMN2);
		screen.addPreference(show_column2);
		
		CheckBoxPreference show_spec_label = new CheckBoxPreference(this);
		show_spec_label.setTitle("性能表示");
		show_spec_label.setSummary("パーツの下に性能を簡略表示する");
		show_spec_label.setKey(BBViewSettingManager.SETTING_SHOW_SPECLABEL);
		show_spec_label.setChecked(BBViewSettingManager.IS_SHOW_SPECLABEL);
		screen.addPreference(show_spec_label);

		CheckBoxPreference show_type_label = new CheckBoxPreference(this);
		show_type_label.setTitle("武器種類表示");
		show_type_label.setSummary("武器の下に兵装名と武器の種類を表示する");
		show_type_label.setKey(BBViewSettingManager.SETTING_SHOW_TYPELABEL);
		show_type_label.setChecked(BBViewSettingManager.IS_SHOW_TYPELABEL);
		screen.addPreference(show_type_label);
		
		// パーツ/武器選択画面
		PreferenceCategory select_category = new PreferenceCategory(this);
		select_category.setTitle("パーツ/武器選択画面");
		screen.addPreference(select_category);

		CheckBoxPreference show_having_item_only = new CheckBoxPreference(this);
		show_having_item_only.setTitle("パーツ武器表示");
		show_having_item_only.setSummary("所持済みのパーツ/武器のみ表示する");
		show_having_item_only.setKey(BBViewSettingManager.SETTING_SHOW_HAVING_ONLY);
		show_having_item_only.setChecked(BBViewSettingManager.IS_SHOW_HAVING);
		screen.addPreference(show_having_item_only);

		CheckBoxPreference show_listbutton = new CheckBoxPreference(this);
		show_listbutton.setTitle("リストのボタン表示");
		show_listbutton.setSummary("武器選択画面の操作ボタンを表示する");
		show_listbutton.setKey(BBViewSettingManager.SETTING_SHOW_LISTBUTTON);
		show_listbutton.setChecked(BBViewSettingManager.IS_SHOW_LISTBUTTON);
		screen.addPreference(show_listbutton);

		CheckBoxPreference btn_listbutton_typetext = new CheckBoxPreference(this);
		btn_listbutton_typetext.setTitle("リストのボタン変更");
		btn_listbutton_typetext.setSummary("武器選択画面の操作ボタンをテキストにする");
		btn_listbutton_typetext.setKey(BBViewSettingManager.SETTING_LISTBUTTON_TYPETEXT);
		btn_listbutton_typetext.setChecked(BBViewSettingManager.IS_LISTBUTTON_TYPETEXT);
		screen.addPreference(btn_listbutton_typetext);
		
		CheckBoxPreference btn_memory_showitem = new CheckBoxPreference(this);
		btn_memory_showitem.setTitle("表示項目選択状態");
		btn_memory_showitem.setSummary("表示項目選択状態を維持する");
		btn_memory_showitem.setKey(BBViewSettingManager.SETTING_MEMORY_SHOWSPEC);
		btn_memory_showitem.setChecked(BBViewSettingManager.IS_MEMORY_SHOWSPEC);
		screen.addPreference(btn_memory_showitem);

		CheckBoxPreference btn_memory_sort = new CheckBoxPreference(this);
		btn_memory_sort.setTitle("ソート状態");
		btn_memory_sort.setSummary("ソートの状態を維持する");
		btn_memory_sort.setKey(BBViewSettingManager.SETTING_MEMORY_SORT);
		btn_memory_sort.setChecked(BBViewSettingManager.IS_MEMORY_SORT);
		screen.addPreference(btn_memory_sort);

		CheckBoxPreference btn_memory_filter = new CheckBoxPreference(this);
		btn_memory_filter.setTitle("フィルタ状態");
		btn_memory_filter.setSummary("フィルタの状態を維持する");
		btn_memory_filter.setKey(BBViewSettingManager.SETTING_MEMORY_FILTER);
		btn_memory_filter.setChecked(BBViewSettingManager.IS_MEMORY_FILTER);
		screen.addPreference(btn_memory_filter);

		setPreferenceScreen(screen);
	}
	
	/**
	 * 画面に制御が戻った際に、設定を読み込む。
	 */
	protected void onResume() {
		super.onResume();
		BBViewSettingManager.loadSettings(this);
	}
	
	/**
	 * 画面から制御が離れた際に、設定を読み込む。
	 */
	protected void onPause() {
		super.onPause();
		BBViewSettingManager.loadSettings(this);
	}
	
	/**
	 * オプションメニュー生成時の処理を行う。
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		// メニューアイテム(エクスポート)
		MenuItem item2 = menu.add(0, MENU_IMPORT, 0, "インポート");
		item2.setIcon(android.R.drawable.ic_menu_save);

		// メニューアイテム(エクスポート)
		MenuItem item3 = menu.add(0, MENU_EXPORT, 0, "エクスポート");
		item3.setIcon(android.R.drawable.ic_menu_save);

		return true;
	}
	
	/**
	 * オプションメニューの選択時の処理を行う。
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = "";
		String msg = "";
		
		switch(item.getItemId()) {
		
			// ファイルのインポート
			case MENU_IMPORT:
				title = "ファイルのインポート";
				msg = 
					"SDカードのBBViewフォルダからファイルをインポートします。" + NEWLINE +
					"指定のフォルダやファイルが存在しない場合、処理は行われません。" + NEWLINE +
					"アプリ内のデータは削除しないため、不要なファイルが残る可能性があります。";
				dialog_type = CMD_IMPORT;
				ShowDialog(title, msg, true);
				break;
				
			// ファイルのエクスポート
			case MENU_EXPORT:
				title = "ファイルのエクスポート";
				msg = 
					"SDカードのBBViewフォルダへファイルをエクスポートします。" + NEWLINE +
					"フォルダが存在しない場合、自動的に生成します。" + NEWLINE +
					"同名のフォルダ、ファイルが存在する場合は上書きするため、事前に退避してください。";
				dialog_type = CMD_EXPORT;
				ShowDialog(title, msg, true);
				break;

		}

		return true;
	}

	/**
	 * ダイアログのボタンタップ時の処理を行う。
	 */
	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(which != DialogInterface.BUTTON1) {
			return;
		}
		
		if(dialog_type == CMD_IMPORT) {
			importFile();
		}
		else if(dialog_type == CMD_EXPORT) {
			exportFile();
		}
	}

	/**
	 * エクスポート処理
	 */
	private void exportFile() {
		boolean ret = false;
		
		String copy_fm_str = this.getFilesDir().toString();
		File copy_to = Environment.getExternalStorageDirectory();
		String copy_to_str = copy_to.getPath() + File.separator + "BBView";
		
		ret = FileIO.copy(copy_fm_str, copy_to_str);
		
		if(ret) {
			String warning_str = 
				"エクスポートが完了しました" + NEWLINE + 
				"エクスポート先：" + copy_to_str;
			Toast.makeText(this, warning_str, Toast.LENGTH_LONG).show();
		}
		else {
			String warning_str = 
				"エクスポートに失敗しました" + NEWLINE + 
				"エクスポート先：" + copy_to_str;
			Toast.makeText(this, warning_str, Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * インポート処理
	 */
	private void importFile() {
		boolean ret = false;
		
		File copy_fm = Environment.getExternalStorageDirectory();
		String copy_fm_str = copy_fm.getPath() + File.separator + "BBView";
		String copy_to_str = this.getFilesDir().toString();
		
		ret = FileIO.copy(copy_fm_str, copy_to_str);
		
		if(ret) {
			String warning_str = 
				"インポートが完了しました" + NEWLINE + 
				"インポート元：" + copy_fm_str;
			Toast.makeText(this, warning_str, Toast.LENGTH_LONG).show();
		}
		else {
			String warning_str = 
				"インポートに失敗しました" + NEWLINE + 
				"インポート元：" + copy_fm_str;
			Toast.makeText(this, warning_str, Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * ダイアログの生成および表示
	 * @param title
	 * @param msg
	 * @param is_yesno
	 */
	private void ShowDialog(String title, String msg, boolean is_yesno) {
		AlertDialog.Builder cmd_dialog = new AlertDialog.Builder(this);
		cmd_dialog.setIcon(android.R.drawable.ic_menu_more);
		cmd_dialog.setTitle(title);
		cmd_dialog.setMessage(msg);
		if(is_yesno) {
			cmd_dialog.setPositiveButton("Yes", this);
			cmd_dialog.setNegativeButton("No", this);
		}
		else {
			cmd_dialog.setPositiveButton("OK", this);
		}
		cmd_dialog.show();
	}
}

