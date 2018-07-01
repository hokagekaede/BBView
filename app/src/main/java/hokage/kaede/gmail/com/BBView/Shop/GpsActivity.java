package hokage.kaede.gmail.com.BBView.Shop;

import hokage.kaede.gmail.com.BBView.Shop.DistanceSetTask.OnEndTaskListener;
import hokage.kaede.gmail.com.BBView3.R;
import hokage.kaede.gmail.com.BBViewLib.Android.BaseActivity;

import java.io.IOException;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GpsActivity extends BaseActivity implements OnItemClickListener, LocationListener, OnEndTaskListener, OnCancelListener {

	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;
	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;

	private TextView mAddressTextView;
	private ShopDatabase mShopDatabase;
	private PlaceAdapter mPlaceAdapter;
	private ProgressDialog mGpsDialog;
	private String mAddressName;

	/**
	 * 起動時の処理を行う。
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(getString(R.string.app_name) + " (現在位置から検索する)");

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

		mAddressTextView = new TextView(this);
		mAddressTextView.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		
		layout_all.addView(location_list);
		layout_all.addView(mAddressTextView);

		LocationManager location_manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		location_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 1, this);
		
		setContentView(layout_all);
		
		// 進捗ダイアログを表示する
		mGpsDialog = new ProgressDialog(this);
		mGpsDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mGpsDialog.setMessage("現在位置を取得中");
		mGpsDialog.setOwnerActivity(this);
		mGpsDialog.setOnCancelListener(this);
		mGpsDialog.show();
	}
	
	/**
	 * 終了時にGPS動作を停止させる。
	 */
	@Override
	protected void onDestroy() {
		LocationManager location_manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		location_manager.removeUpdates(this);

		mShopDatabase.setKeyword(null);
		
		super.onDestroy();
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
	 * GPSから位置情報を取得した場合の処理。位置情報を基に店舗情報を更新する。
	 */
	@Override
	public void onLocationChanged(Location location) {
		mGpsDialog.dismiss();
		
		Geocoder coder = new Geocoder(this);
		try {
			List<Address> address_list = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			
			if(address_list != null) {
				// 一致した最初の住所データを取得し、そのデータの都道府県をデータベースに設定する。
				Address address = address_list.get(0);
				mShopDatabase.readLocationData(address.getAdminArea());
				
				// 住所を取得する。最初の「日本」は不要のため、1から開始する。
				String name = "";
				for(int i=1; i<50; i++) {
					String buf = address.getAddressLine(i);
					if(buf != null) {
						name = name + address.getAddressLine(i);
					}
					else {
						break;
					}
				}
				
				mAddressTextView.setText(name);
				mAddressName = address.getLocality();
				setTitle(getString(R.string.app_name) + " (" + mAddressName + "の検索結果)");
				
				DistanceSetTask task = new DistanceSetTask(this, address, this);
				task.execute();
			}
			else {
				Toast.makeText(this, "指定の場所は見つかりませんでした", Toast.LENGTH_SHORT).show();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// GPSを停止させる
		LocationManager location_manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		location_manager.removeUpdates(this);
	}

	/**
	 * GPSが使用できなくなった場合の処理。トースト通知を行う。
	 */
	@Override
	public void onProviderDisabled(String arg0) {
		Toast.makeText(this, "GPSは現在使用できません。", Toast.LENGTH_SHORT).show();
	}

	/**
	 * GPSが使用できるようになった場合の処理。何も行わない。
	 */
	@Override
	public void onProviderEnabled(String provider) {
		// Do Nothing
	}

	/**
	 * GPSの状態が変わった場合の処理。何も行わない。
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// Do Nothing
	}

	/**
	 * 店舗リスト更新処理が終わった場合の処理。店舗リストを更新する。
	 */
	@Override
	public void onEndTask() {
		mPlaceAdapter.setDistanceFlag(true);
		mPlaceAdapter.notifyDataSetChanged();

		setTitle(getString(R.string.app_name) + " (" + mAddressName + "の店舗一覧/" + String.valueOf(mShopDatabase.getCount()) + "店舗)");
	}

	/**
	 * GPSから情報取得中にバックキーが押された場合の処理。本画面を終了する。
	 */
	@Override
	public void onCancel(DialogInterface dialog) {
		finish();
	}
}
