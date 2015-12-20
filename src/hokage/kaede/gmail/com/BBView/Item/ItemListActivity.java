package hokage.kaede.gmail.com.BBView.Item;

import hokage.kaede.gmail.com.BBView.Adapter.BBArrayAdapter;
import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataFilter;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.Android.BBViewSettingManager;
import hokage.kaede.gmail.com.BBViewLib.Android.BaseActivity;
import hokage.kaede.gmail.com.BBViewLib.Android.IntentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ItemListActivity extends BaseActivity implements OnItemClickListener, OnClickListener {
	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;	
	
	// 本アプリ共通オブジェクト
	private BBDataManager mDataManager;
	private BBDataFilter mFilter;
	
	// 各画面レイアウトオブジェクト
	private TextView mTitleTextView;
	private Button mMapButton;
	private Button mMedalButton;
	private Button mMaterialButton;
	private ListView mListView;
	private BBArrayAdapter mAdapter;
	
	/**
	 * アプリ起動時の処理を行う。
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	// フィルタを設定する
		mDataManager = BBDataManager.getInstance();
		
		mFilter = new BBDataFilter();
		mFilter.setOtherType("マップ");

		// レイアウトの生成
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.LEFT | Gravity.TOP);
		layout.setLayoutParams(new LinearLayout.LayoutParams(FP, FP));
		
		// タイトルテキストの生成
		mTitleTextView = new TextView(this);
		mTitleTextView.setText("マップ一覧");
		mTitleTextView.setTextSize(BBViewSettingManager.getTextSize(this, BBViewSettingManager.FLAG_TEXTSIZE_LARGE));
		mTitleTextView.setBackgroundColor(Color.rgb(0, 0, 60));
		
		// リストビューの生成
		mAdapter = new BBArrayAdapter(this, mDataManager.getList(mFilter));
		
		mListView = new ListView(this);
		mListView.setOnItemClickListener(this);
		mListView.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		mListView.setAdapter(mAdapter);

		// レイアウトの生成
		LinearLayout layout_btm = new LinearLayout(this);
		layout_btm.setOrientation(LinearLayout.HORIZONTAL);
		layout_btm.setGravity(Gravity.LEFT | Gravity.TOP);
		layout_btm.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		
		mMapButton = new Button(this);
		mMapButton.setText("マップ");
		mMapButton.setOnClickListener(this);
		mMapButton.setLayoutParams(new LinearLayout.LayoutParams(WC, WC, 1));

		mMedalButton = new Button(this);
		mMedalButton.setText("勲章");
		mMedalButton.setOnClickListener(this);
		mMedalButton.setLayoutParams(new LinearLayout.LayoutParams(WC, WC, 1));

		mMaterialButton = new Button(this);
		mMaterialButton.setText("素材");
		mMaterialButton.setOnClickListener(this);
		mMaterialButton.setLayoutParams(new LinearLayout.LayoutParams(WC, WC, 1));
		
		layout_btm.addView(mMapButton);
		layout_btm.addView(mMedalButton);
		layout_btm.addView(mMaterialButton);

		layout.addView(mTitleTextView);
		layout.addView(mListView);
		layout.addView(layout_btm);
		setContentView(layout);
        
    }

    /**
     * リスト項目短押し時の処理を行う。
     * 選択した項目の情報詳細画面に遷移する。
     */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		BBData data = (BBData)(mAdapter.getItem(position));

		Intent intent = new Intent(this, InfoActivity.class);
		IntentManager.setSelectedData(intent, data);
		startActivity(intent);
	}

	/**
	 * ボタン押下時の処理を行う。
	 * 表示するリストを変更する。
	 */
	@Override
	public void onClick(View v) {
		if(v.equals(mMapButton)) {
			mFilter.clear();
			mFilter.setOtherType("マップ");
			mTitleTextView.setText("マップ一覧");
		}
		else if(v.equals(mMedalButton)) {
			mFilter.clear();
			mFilter.setOtherType("勲章");
			mTitleTextView.setText("勲章一覧");
		}
		else if(v.equals(mMaterialButton)) {
			mFilter.clear();
			mFilter.setOtherType("素材");
			mTitleTextView.setText("素材一覧");
		}
		else {
			return;
		}

		mAdapter = new BBArrayAdapter(this, mDataManager.getList(mFilter));
		mListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}
	

}
