package hokage.kaede.gmail.com.BBView.Shop;

import hokage.kaede.gmail.com.BBView.Shop.DistanceSetTask.OnEndTaskListener;
import hokage.kaede.gmail.com.BBView3.R;
import hokage.kaede.gmail.com.BBViewLib.Android.BaseActivity;

import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class PointActivity extends BaseActivity implements OnClickListener, OnItemClickListener, OnEndTaskListener {

	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;
	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;

	private EditText mAddressEditText;
	private Button mFilterBtn;
	private ShopDatabase mShopDatabase;
	private PlaceAdapter mPlaceAdapter;
	private String mFeatureName;
	
	/**
	 * 起動時の処理を行う。
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTitle(getString(R.string.app_name) + " (建物・地名から検索する)");

		mShopDatabase = ShopDatabase.getShopDatabase();
		mShopDatabase.clear();
        
		// 全体レイアウト設定
		LinearLayout layout_all = new LinearLayout(this);
		layout_all.setOrientation(LinearLayout.VERTICAL);
		layout_all.setLayoutParams(new LinearLayout.LayoutParams(FP, FP));
		layout_all.setGravity(Gravity.LEFT | Gravity.TOP);
		
		mPlaceAdapter = new PlaceAdapter(this, ShopDatabase.getShopDatabase());
		
		ListView location_list = new ListView(this);
		location_list.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		location_list.setOnItemClickListener(this);
		location_list.setAdapter(mPlaceAdapter);

    	LinearLayout layout_btm = new LinearLayout(this);
    	layout_btm.setOrientation(LinearLayout.HORIZONTAL);
    	layout_btm.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
    	layout_btm.setGravity(Gravity.LEFT | Gravity.TOP);
    	
    	mAddressEditText = new EditText(this);
    	mAddressEditText.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
    	
    	mFilterBtn = new Button(this);
    	mFilterBtn.setText("検索");
    	mFilterBtn.setOnClickListener(this);

    	layout_btm.addView(mAddressEditText);
    	layout_btm.addView(mFilterBtn);
		
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
     * 検索ボタンが押された場合の処理。地名情報から店舗を検索する。
     */
	@Override
	public void onClick(View v) {
		String point_str = mAddressEditText.getText().toString();
		Address address = ShopDatabase.getAddress(this, point_str);
		if(address != null) {
			mShopDatabase.readLocationData(address.getAdminArea());
			mFeatureName = address.getFeatureName();
			setTitle(getString(R.string.app_name) + " (" + mFeatureName + "の検索結果)");
			
			DistanceSetTask task = new DistanceSetTask(this, address, this);
			task.execute();
		}
		else {
			Toast.makeText(this, "指定の場所は見つかりませんでした", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 選択した店舗をマップアプリで表示する。
	 */
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

	/**
	 * 店舗リスト更新処理が終わった場合の処理。店舗リストを更新する。
	 */
	@Override
	public void onEndTask() {
		mPlaceAdapter.setDistanceFlag(true);
		mPlaceAdapter.notifyDataSetChanged();

		setTitle(getString(R.string.app_name) + " (" + mFeatureName + "の店舗一覧/" + String.valueOf(mShopDatabase.getCount()) + "店舗)");
	}
}
