package hokage.kaede.gmail.com.BBView3;

import hokage.kaede.gmail.com.BBView3.R;
import hokage.kaede.gmail.com.BBViewLib.Android.BBViewSettingManager;
import hokage.kaede.gmail.com.BBViewLib.Android.BaseActivity;
import hokage.kaede.gmail.com.Lib.Java.FileIO;

import java.io.InputStream;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ManualActivity extends BaseActivity
{
	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;

	/**
	 * 画面生成時の処理を行う。
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
		// aboutデータの読み込み
		Resources res = this.getResources();
		InputStream is = res.openRawResource(R.raw.about_text);
		String about_text = FileIO.readInputStream(is, FileIO.ENCODE_UTF8);
		
		setTitle(getTitle() + " (BBViewについて)");
		
		// 全体レイアウト設定
		LinearLayout layout_all = new LinearLayout(this);
		layout_all.setLayoutParams(new LinearLayout.LayoutParams(FP, FP));
        layout_all.setOrientation(LinearLayout.VERTICAL);
		layout_all.setGravity(Gravity.TOP);
		
		TextView about_text_view = new TextView(this);
		about_text_view.setText(about_text);
		about_text_view.setTextSize(BBViewSettingManager.getTextSize(this, BBViewSettingManager.FLAG_TEXTSIZE_SMALL));
		
		ScrollView sv = new ScrollView(this);
		sv.addView(about_text_view);
		sv.setLayoutParams(new LinearLayout.LayoutParams(FP, WC, 1));
		
		layout_all.addView(sv);
		
        
        setContentView(layout_all);
    }
}
