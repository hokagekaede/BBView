package hokage.kaede.gmail.com.BBView3.Shop;

import hokage.kaede.gmail.com.BBView3.R;
import hokage.kaede.gmail.com.BBViewLib.Android.Common.BaseActivity;
import hokage.kaede.gmail.com.Lib.Android.StringAdapter;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * 店舗検索機能の建物名から検索する画面のアクティビティ。
 */
public class PointActivity extends BaseActivity {

	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;
	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	
	private static final int POINT_EDIT_TEXT_ID = 23122;

	private ShopDatabase mShopDatabase;
	private PlaceAdapter mPlaceAdapter;
	private String mFeatureName = "";
	
	private static final String[] DISTANCE_CAPTIONS = { "距離：1.0 (km)", "距離：1.5 (km)", "距離：2.0 (km)", "距離：4.0 (km)", "距離：6.0 (km)", "距離：10.0 (km)" };
	private static final int[] DISTANCE_VALUES = { 1000, 1500, 2000, 4000, 6000, 10000 };

	private int mDistance = DISTANCE_VALUES[0];
	
	/**
	 * 起動時の処理を行う。
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle(getString(R.string.app_name) + " (建物・地名から検索する)");

		mShopDatabase = ShopDatabase.getShopDatabase();
		mShopDatabase.clear();

		mPlaceAdapter = new PlaceAdapter(this, ShopDatabase.getShopDatabase());
		
		// 全体レイアウト設定
		LinearLayout layout_all = new LinearLayout(this);
		layout_all.setOrientation(LinearLayout.VERTICAL);
		layout_all.setLayoutParams(new LinearLayout.LayoutParams(FP, FP));
		layout_all.setGravity(Gravity.LEFT | Gravity.TOP);
		
		ListView location_list = new ListView(this);
		location_list.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		location_list.setOnItemClickListener(new OnShowMapAppListener());
		location_list.setAdapter(mPlaceAdapter);
		
		LinearLayout layout_btm = new LinearLayout(this);
		layout_btm.setOrientation(LinearLayout.HORIZONTAL);
		layout_btm.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		layout_btm.setGravity(Gravity.LEFT | Gravity.TOP);
		
		EditText address_EditText = new EditText(this);
		address_EditText.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		address_EditText.setId(POINT_EDIT_TEXT_ID);
		
		// 距離を選択するスピナとアダプタ
		StringAdapter distance_adapter = new StringAdapter(this);
		distance_adapter.addArrays(DISTANCE_CAPTIONS);
		
		Spinner distance_spinner = new Spinner(this);
		distance_spinner.setOnItemSelectedListener(new OnSelectedDiscanceListener());
		distance_spinner.setAdapter(distance_adapter);
		distance_spinner.setSelection(0);

		Button filter_btn = new Button(this);
		filter_btn.setText("検索");
		filter_btn.setOnClickListener(new OnStartSearchListener());

		layout_btm.addView(address_EditText);
		layout_btm.addView(distance_spinner);
		layout_btm.addView(filter_btn);
		
		layout_all.addView(location_list);
		layout_all.addView(layout_btm);
		
		setContentView(layout_all);
	}

	/**
	 * 画面破棄時の処理を行う。
	 * キーワード設定を解除する。
	 */
	@Override
	public void onDestroy() {
		mShopDatabase.setKeyword(null);
		
		super.onDestroy();	
	}
	
	/**
	 * 距離を選択するした際の処理を行うリスナー。
	 */
	private class OnSelectedDiscanceListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			mDistance = DISTANCE_VALUES[position];
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			mDistance = DISTANCE_VALUES[0];
		}
	}

	/**
	 * 検索ボタンが押された場合の処理を行うリスナー。
	 * 地名情報から店舗を検索する。
	 */
	private class OnStartSearchListener implements OnClickListener {
		@Override
		public void onClick(View view) {
			
			// EditTextから地名を取得する
			Activity parent_activity = (Activity)(view.getContext());
			EditText point_edit_text = (EditText)(parent_activity.findViewById(POINT_EDIT_TEXT_ID));
			String point_str = ((EditText)point_edit_text).getText().toString();
			
			// 指定地点からの検索を実行する
			Address address = ShopDatabase.getAddress(PointActivity.this, point_str);
			if(address != null) {
				mShopDatabase.readLocationData(address.getAdminArea());
				mFeatureName = address.getFeatureName();
				setTitle(getString(R.string.app_name) + " (" + mFeatureName + "の検索結果)");
				
				DistanceSetTask task = new DistanceSetTask(PointActivity.this, address, new OnEndTaskListener());
				task.setDistance(mDistance);
				task.execute();
			}
			else {
				Toast.makeText(PointActivity.this, "指定の場所は見つかりませんでした", Toast.LENGTH_SHORT).show();
			}
		}	
	}

	/**
	 * 選択した店舗をマップアプリで表示するリスナー。
	 */
	private class OnShowMapAppListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
			// 店舗の情報を取得する
			ShopData shop = mPlaceAdapter.getItem(index);
			
			// Mapsアプリで店舗の住所を検索する
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("geo:0,0?q=" + shop.address));
			
			startActivity(intent);
		}
	}

	/**
	 * 店舗リスト更新処理が終わった場合の処理を行うリスナー。
	 * 店舗リストを更新する。
	 */
	private class OnEndTaskListener implements hokage.kaede.gmail.com.BBView3.Shop.DistanceSetTask.OnEndTaskListener {

		@Override
		public void onEndTask() {
			mPlaceAdapter.setDistanceFlag(true);
			mPlaceAdapter.notifyDataSetChanged();

			setTitle(getString(R.string.app_name) + " (" + mFeatureName + "の店舗一覧/" + String.valueOf(mShopDatabase.getCount()) + "店舗)");
		}
	}
	
}
