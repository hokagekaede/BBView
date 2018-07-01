package hokage.kaede.gmail.com.BBView.Shop;

import hokage.kaede.gmail.com.BBView3.R;
import hokage.kaede.gmail.com.BBViewLib.Android.BaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 地域データをリスト表示するアクティビティ
 * @author kaede
 *
 */
public class AreaActivity extends BaseActivity implements OnItemClickListener {
	
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;
	
	private AreaAdapter mAreaAdapter;
	
	/**
	 * 起動時の処理を行う。
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setTitle(getString(R.string.app_name) + " (都道府県で検索する)");
		
		// 全体レイアウト設定
		LinearLayout layout_all = new LinearLayout(this);
		layout_all.setOrientation(LinearLayout.VERTICAL);
		layout_all.setLayoutParams(new LinearLayout.LayoutParams(FP, FP));
		layout_all.setGravity(Gravity.LEFT | Gravity.TOP);

		mAreaAdapter = new AreaAdapter(this);

		ListView area_list = new ListView(this);
		area_list.setAdapter(mAreaAdapter);
		area_list.setLayoutParams(new LinearLayout.LayoutParams(FP, FP));
		area_list.setOnItemClickListener(this);

		layout_all.addView(area_list);
		
		setContentView(layout_all);
	}

	/**
	 * リスト項目選択時の処理を行う。
	 * 店舗一覧を取得し、次の店舗一覧画面へ遷移する。
	 */
	@Override
	public void onItemClick(AdapterView<?> list, View arg1, int index, long arg3) {
		Intent intent = new Intent(this, PlaceActivity.class);
		intent.putExtra(PlaceActivity.AREA_KEY, mAreaAdapter.getItem(index));
		startActivity(intent);
	}
}
