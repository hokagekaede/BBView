package hokage.kaede.gmail.com.BBView3.Item;

import hokage.kaede.gmail.com.BBView3.Item.CardSelectDialog.OnSelectCardListener;
import hokage.kaede.gmail.com.BBView3.Item.CardDataReadTask.OnPostExecuteListener;
import hokage.kaede.gmail.com.BBViewLib.Java.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.Java.BBNetDatabase;
import hokage.kaede.gmail.com.BBViewLib.Java.BBViewSetting;
import hokage.kaede.gmail.com.BBViewLib.Android.Common.BaseActivity;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;
import hokage.kaede.gmail.com.Lib.Android.UidManager;
import hokage.kaede.gmail.com.Lib.Android.UidManager.OnInputPassListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class CategoryListActivity extends BaseActivity implements OnClickListener, OnPostExecuteListener, OnSelectCardListener, OnInputPassListener {
	
	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;	
	
	private static final String BBNET_UPDATE = "データ更新";
	private static final String BBNET_CARDCHANGE = "カード切替";

	// タイトル名
	private CharSequence mTitle;
	
	private UidManager mUidManager;
	private String mUid;
	private String mPassword;
		
	/**
	 * アプリ起動時の処理を行う。
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// インスタンス生成
		mTitle = getTitle();

		mUidManager = new UidManager(this);
		
		updateTitle();
		
		ScrollView sv = new ScrollView(this);
		sv.setLayoutParams(new LinearLayout.LayoutParams(FP, FP));
		
		// レイアウトの生成
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.LEFT | Gravity.TOP);
		layout.setLayoutParams(new LinearLayout.LayoutParams(FP, FP));
		sv.addView(layout);
		
		// パーツボタンを生成
		TextView parts_text = new TextView(this);
		parts_text.setText("機体パーツ");
		parts_text.setTextColor(SettingManager.getColorWhite());
		parts_text.setTextSize(BBViewSetting.getTextSize(this, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
		layout.addView(parts_text);

		LinearLayout parts_layout = new LinearLayout(this);
		parts_layout.setOrientation(LinearLayout.HORIZONTAL);
		parts_layout.setGravity(Gravity.LEFT | Gravity.TOP);
		parts_layout.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		
		int parts_size = BBDataManager.BLUST_PARTS_LIST.length;
		
		for(int i=0; i<parts_size; i++) {
			Button parts_btn = new Button(this);
			parts_btn.setText(BBDataManager.BLUST_PARTS_LIST[i]);
			parts_btn.setTag("");
			parts_btn.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
			parts_btn.setOnClickListener(this);
			parts_layout.addView(parts_btn);
		}

		layout.addView(parts_layout);
		
		// 武器ボタンを生成
		int blust_size = BBDataManager.BLUST_TYPE_LIST.length;
		
		for(int i=0; i<blust_size; i++) {
			TextView weapon_text = new TextView(this);
			weapon_text.setText(BBDataManager.BLUST_TYPE_LIST[i]);
			weapon_text.setTextColor(SettingManager.getColorWhite());
			weapon_text.setTextSize(BBViewSetting.getTextSize(this, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
			layout.addView(weapon_text);
			
			LinearLayout weapon_layout = new LinearLayout(this);
			weapon_layout.setOrientation(LinearLayout.HORIZONTAL);
			weapon_layout.setGravity(Gravity.LEFT | Gravity.TOP);
			weapon_layout.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));

			int weapon_size = BBDataManager.WEAPON_TYPE_LIST.length;

			for(int j=0; j<weapon_size; j++) {
				Button weapon_btn = new Button(this);
				weapon_btn.setText(BBDataManager.WEAPON_TYPE_LIST[j]);
				weapon_btn.setTag(BBDataManager.BLUST_TYPE_LIST[i]);
				weapon_btn.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
				weapon_btn.setOnClickListener(this);
				weapon_layout.addView(weapon_btn);
			}
			
			layout.addView(weapon_layout);
		};
		
		// その他ボタンを生成
		TextView others_text = new TextView(this);
		others_text.setText("その他");
		others_text.setTextColor(SettingManager.getColorWhite());
		others_text.setTextSize(BBViewSetting.getTextSize(this, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
		layout.addView(others_text);

		LinearLayout others_layout = new LinearLayout(this);
		others_layout.setOrientation(LinearLayout.HORIZONTAL);
		others_layout.setGravity(Gravity.LEFT | Gravity.TOP);
		others_layout.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));

		// チップ
		Button chip_btn = new Button(this);
		chip_btn.setText(BBDataManager.CHIP_STR);
		chip_btn.setTag("");
		chip_btn.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		chip_btn.setOnClickListener(this);
		others_layout.addView(chip_btn);

		// 勲章
		Button medal_btn = new Button(this);
		medal_btn.setText(BBDataManager.MEDAL_STR);
		medal_btn.setTag("");
		medal_btn.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		medal_btn.setOnClickListener(this);
		others_layout.addView(medal_btn);

		// 素材
		Button material_btn = new Button(this);
		material_btn.setText(BBDataManager.MATERIAL_STR);
		material_btn.setTag("");
		material_btn.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		material_btn.setOnClickListener(this);
		others_layout.addView(material_btn);

		// シード
		Button seed_btn = new Button(this);
		seed_btn.setText(BBDataManager.SEED_STR);
		seed_btn.setTag("");
		seed_btn.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		seed_btn.setOnClickListener(this);
		others_layout.addView(seed_btn);
		
		layout.addView(others_layout);

		// BBNETボタンを生成
		TextView bbnet_text = new TextView(this);
		bbnet_text.setText("BB.NET");
		bbnet_text.setTextColor(SettingManager.getColorWhite());
		bbnet_text.setTextSize(BBViewSetting.getTextSize(this, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
		layout.addView(bbnet_text);

		LinearLayout bbnet_layout = new LinearLayout(this);
		bbnet_layout.setOrientation(LinearLayout.HORIZONTAL);
		bbnet_layout.setGravity(Gravity.LEFT | Gravity.TOP);
		bbnet_layout.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));

		// データ更新
		Button update_btn = new Button(this);
		update_btn.setText(BBNET_UPDATE);
		update_btn.setTag("");
		update_btn.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		update_btn.setOnClickListener(this);
		bbnet_layout.addView(update_btn);

		// カード切り替え
		Button change_btn = new Button(this);
		change_btn.setText(BBNET_CARDCHANGE);
		change_btn.setTag("");
		change_btn.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		change_btn.setOnClickListener(this);
		bbnet_layout.addView(change_btn);

		layout.addView(bbnet_layout);
		
		setContentView(sv);
	}

	/**
	 * 各ボタンを押下した時の処理を行う。
	 */
	@Override
	public void onClick(View v) {
		if(v instanceof Button) {
			String btn_text = ((Button)v).getText().toString();
			String tag_text = ((Button)v).getTag().toString();
			
			Intent intent = new Intent(this, BBDataListActivity.class);
			
			if(btn_text.equals(BBNET_UPDATE)) {
				mUidManager.setOnInputPassListener(this);
				mUidManager.showDialog();
			}
			else if(btn_text.equals(BBNET_CARDCHANGE)) {
				CardSelectDialog dialog = new CardSelectDialog(this);
				dialog.setOnSelectCardListener(this);
				dialog.show();
			}
			else {
				if(tag_text.equals("")) {
					intent.putExtra(BBDataListActivity.INTENTEY_FILTER_MAIN, btn_text);
					intent.putExtra(BBDataListActivity.INTENTEY_FILTER_SUB, "");
				}
				else {
					intent.putExtra(BBDataListActivity.INTENTEY_FILTER_MAIN, tag_text);
					intent.putExtra(BBDataListActivity.INTENTEY_FILTER_SUB, btn_text);
				}
				
				startActivity(intent);
			}
		}
	}

	/**
	 * パスワード入力後の処理を行う。
	 */
	@Override
	public void InputPass() {
		mUid = mUidManager.getUid();
		mPassword = mUidManager.getPassword();
		
		CardDataReadTask task = new CardDataReadTask(CategoryListActivity.this, mUid, mPassword);
		task.setOnPostExecuteListener(new AfterGetCardList());
		task.setMode(CardDataReadTask.MODE_GETCARDLIST);
		task.execute();
	}
	
	/**
	 * カード一覧取得後の処理を行う。
	 */
	private class AfterGetCardList implements OnPostExecuteListener {
		@Override
		public void onPostExecute(boolean success) {
			if(success) {
				CardSelectDialog dialog = new CardSelectDialog(CategoryListActivity.this);
				dialog.setOnSelectCardListener(new AfterSelectCard());
				dialog.show();
			}
		}
	}
	
	/**
	 * データ取得を行うカード選択後の処理を行う。
	 */
	private class AfterSelectCard implements OnSelectCardListener {
		@Override
		public void OnSelectCard(int card_index) {
			CardDataReadTask task = new CardDataReadTask(CategoryListActivity.this, mUid, mPassword);
			task.setOnPostExecuteListener(CategoryListActivity.this);
			task.setMode(CardDataReadTask.MODE_GETCARDDATA);
			task.setCardIndex(card_index);
			task.execute();
		}
	}
	
	/**
	 * データ取得完了後の処理を行う。
	 */
	@Override
	public void onPostExecute(boolean success) {
		updateTitle();
	}

	/**
	 * カード選択後、カード名を更新する。
	 */
	@Override
	public void OnSelectCard(int card_index) {
		BBNetDatabase database = BBNetDatabase.getInstance();
		database.setCardNumber(card_index);
		updateTitle();
	}
	
	/**
	 * タイトル名を更新する。
	 */
	private void updateTitle() {
		String card_name = BBNetDatabase.getInstance().getCardName();
		
		if(card_name == null) {
		    setTitle(mTitle + " (所持品確認)");
		}
		else if(card_name.equals(BBNetDatabase.NO_CARD_DATA)) {
		    setTitle(mTitle + " (所持品確認)");
		}
		else if(BBNetDatabase.getInstance().isEmpty()) {
			Toast.makeText(this, "カードデータ未取得です。", Toast.LENGTH_SHORT).show();
		    setTitle(mTitle + " (所持品確認)");
		}
		else {
		    setTitle(mTitle + " (所持品確認/" + card_name + ")");
		}
	}
}
