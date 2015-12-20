package hokage.kaede.gmail.com.BBViewLib.Android;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * BBViewの各画面のベースとなるクラス。
 */
public abstract class BaseActivity extends Activity {
	
	public void setContentView(View view) {

		// テーマを設定する
		setTheme();
		
		LinearLayout main_layout = new LinearLayout(this);
		main_layout.setOrientation(LinearLayout.VERTICAL);
		main_layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
		
		view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
		
		TextView sega_text_view = new TextView(this);
		sega_text_view.setText("©SEGA");
		sega_text_view.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		
		main_layout.addView(view);
		main_layout.addView(sega_text_view);
		
		super.setContentView(main_layout);
	}

	private void setTheme() {
		int res_id = BBViewSettingManager.sThemeID;
		
		if(res_id != BBViewSettingManager.THEME_DEFAULT_ID) {
			setTheme(res_id);
		}
	}
}
