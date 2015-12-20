package hokage.kaede.gmail.com.BBView.Adapter;

import hokage.kaede.gmail.com.BBView.Adapter.CustomAdapter.CustomAdapterBaseItem;
import hokage.kaede.gmail.com.BBViewLib.Android.BBViewSettingManager;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomAdapterItemCategory implements CustomAdapterBaseItem {
	public String title;
	
	private TextView mTitleTextView;
	private Context mContext;
	
	public CustomAdapterItemCategory(Context context, String title) {
		this.title = title;
		this.mContext = context;
	}

	@Override
	public LinearLayout createView(Context context) {
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.LEFT);
		layout.setBackgroundColor(Color.rgb(60, 60, 180));
		
		mTitleTextView = new TextView(context);
		layout.addView(mTitleTextView);
		
		updateView();

		return layout;
	}

	@Override
	public void updateView() {
		mTitleTextView.setTextColor(Color.rgb(255, 255, 255));
		mTitleTextView.setPadding(5, 0, 0, 0);
		mTitleTextView.setGravity(Gravity.LEFT);
		mTitleTextView.setText(title);
		mTitleTextView.setTextSize(BBViewSettingManager.getTextSize(mContext, BBViewSettingManager.FLAG_TEXTSIZE_SMALL));
	}

	@Override
	public void click() {
		// Do Nothing
	}
}

