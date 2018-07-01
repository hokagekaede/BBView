package hokage.kaede.gmail.com.BBView.Shop;

import hokage.kaede.gmail.com.BBViewLib.Android.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ShopMainActivity extends BaseActivity implements OnClickListener {
	
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;
	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	
	private static final String BTN_TEXT_AREA = "都道府県から検索する";
	private static final String BTN_TEXT_FEATURE = "建物・地名から検索する";
	private static final String BTN_TEXT_POSITION = "現在位置から検索する";
	
	/**
	 * 起動時の処理を行う。
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle(this.getTitle() + " (店舗検索)");
		
		ShopDataTask task = new ShopDataTask(this);
		task.execute();
		
		// 全体レイアウト設定
		LinearLayout layout_all = new LinearLayout(this);
		layout_all.setOrientation(LinearLayout.VERTICAL);
		layout_all.setLayoutParams(new LinearLayout.LayoutParams(FP, FP));
		layout_all.setGravity(Gravity.LEFT | Gravity.TOP);
		
		ScrollView sv = new ScrollView(this);
		sv.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		
		Button area_btn = new Button(this);
		area_btn.setText(BTN_TEXT_AREA);
		area_btn.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		area_btn.setOnClickListener(this);

		Button feature_btn = new Button(this);
		feature_btn.setText(BTN_TEXT_FEATURE);
		feature_btn.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		feature_btn.setOnClickListener(this);

		Button position_btn = new Button(this);
		position_btn.setText(BTN_TEXT_POSITION);
		position_btn.setLayoutParams(new LinearLayout.LayoutParams(FP, WC));
		position_btn.setOnClickListener(this);

		layout_all.addView(sv);
		layout_all.addView(area_btn);
		layout_all.addView(feature_btn);
		layout_all.addView(position_btn);
		
		setContentView(layout_all);
	}

	@Override
	public void onClick(View v) {
		if(v instanceof Button) {
			Button btn = (Button)v;
			
			if(btn.getText().equals(BTN_TEXT_AREA)) {
				Intent intent = new Intent(this, AreaActivity.class);
				startActivity(intent);
			}
			else if(btn.getText().equals(BTN_TEXT_FEATURE)) {
				Intent intent = new Intent(this, PointActivity.class);
				startActivity(intent);
			}
			else if(btn.getText().equals(BTN_TEXT_POSITION)) {
				Intent intent = new Intent(this, GpsActivity.class);
				startActivity(intent);
			}
		}
		
	}

}
