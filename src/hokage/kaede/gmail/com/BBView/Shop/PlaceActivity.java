package hokage.kaede.gmail.com.BBView.Shop;

import hokage.kaede.gmail.com.BBView3.R;
import hokage.kaede.gmail.com.BBViewLib.Android.BaseActivity;

import android.content.Intent;
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

public class PlaceActivity extends BaseActivity implements OnItemClickListener, OnClickListener {
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;
	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	
	private EditText mFilterEditText;
	private Button mFilterBtn;
	private Button mClearBtn;
	private ShopDatabase mShopDatabase;
	private PlaceAdapter mPlaceAdapter;
	
	public static final String AREA_KEY = "AREA_KEY";
	
	/**
	 * 起動時の処理を行う。
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // エリア名を取得する
        String area_name = getAreaName();
        
        if(area_name == null) {
        	finish();
        }
        
		mShopDatabase = ShopDatabase.getShopDatabase();
		mShopDatabase.readLocationData(area_name);
		
        setTitle(getString(R.string.app_name) + " (" + area_name + "の店舗一覧/" + String.valueOf(mShopDatabase.getCount()) + "店舗)");

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
    	
    	mFilterEditText = new EditText(this);
    	mFilterEditText.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
    	
    	mFilterBtn = new Button(this);
    	mFilterBtn.setText("絞込み");
    	mFilterBtn.setOnClickListener(this);
    	
    	mClearBtn = new Button(this);
    	mClearBtn.setText("クリア");
    	mClearBtn.setOnClickListener(this);
    	
    	layout_btm.addView(mFilterEditText);
    	layout_btm.addView(mFilterBtn);
    	layout_btm.addView(mClearBtn);
		
		layout_all.addView(location_list);
		layout_all.addView(layout_btm);
		
        setContentView(layout_all);
    }
    
    /**
     * intentから地域名を取得する
     * @return 地域名。intentがnullの場合、nullを返す。
     */
    private String getAreaName() {
        Intent intent = getIntent();
        
        if(intent == null) {
        	return null;
        }
        
        return intent.getStringExtra(AREA_KEY);
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
     * リスト項目選択時の処理を行う。
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
	 * ボタンタップ時の処理を行う。
	 * @param v タップされたボタンのオブジェクト
	 */
	@Override
	public void onClick(View v) {		
		if(v.equals(mFilterBtn)) {
			String filter_text = mFilterEditText.getText().toString();
			mShopDatabase.setKeyword(filter_text);
		}
		else if(v.equals(mClearBtn)) {
			mShopDatabase.setKeyword(null);
		}
		
		// リスト表示を更新する
		mPlaceAdapter.notifyDataSetChanged();
	}
}
