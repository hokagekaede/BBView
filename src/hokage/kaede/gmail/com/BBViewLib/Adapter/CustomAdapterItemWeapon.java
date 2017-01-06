package hokage.kaede.gmail.com.BBViewLib.Adapter;

import hokage.kaede.gmail.com.BBView.Custom.SelectActivity;
import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBViewSetting;
import hokage.kaede.gmail.com.BBViewLib.CustomData;
import hokage.kaede.gmail.com.BBViewLib.CustomDataManager;
import hokage.kaede.gmail.com.BBViewLib.Adapter.CustomAdapter.CustomAdapterBaseItem;
import hokage.kaede.gmail.com.BBViewLib.Android.IntentManager;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomAdapterItemWeapon implements CustomAdapterBaseItem {
	public String title;
	public String summary;
	public String blust;
	public String type;
	
	private TextView mTitleTextView;
	private TextView mSummaryTextView;
	private Context mContext;
	
	public CustomAdapterItemWeapon(Context context, String title, String blust, String type) {
		this.title = title;
		this.summary = blust + ":" + type;
		this.blust = blust;
		this.type = type;
		this.mContext = context;
	}

	@Override
	public LinearLayout createView(Context context) {
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.LEFT);
		
		mTitleTextView = new TextView(context);
		layout.addView(mTitleTextView);

		mSummaryTextView = new TextView(context);
		if(BBViewSetting.IS_SHOW_TYPELABEL) {
			layout.addView(mSummaryTextView);
		}
		
		updateView();
		
		return layout;
	}

	@Override
	public void updateView() {
		
		if(BBViewSetting.IS_SHOW_TYPELABEL) {
			mTitleTextView.setPadding(25, 10, 0, 0);
		}
		else {
			mTitleTextView.setPadding(25, 10, 0, 10);
		}

		mTitleTextView.setGravity(Gravity.LEFT);
		mTitleTextView.setText(title);
		mTitleTextView.setTextSize(BBViewSetting.getTextSize(mContext, BBViewSetting.FLAG_TEXTSIZE_NORMAL));

		mSummaryTextView.setPadding(25, 0, 0, 10);
		mSummaryTextView.setGravity(Gravity.LEFT);
		mSummaryTextView.setText(summary);
		mSummaryTextView.setTextSize(BBViewSetting.getTextSize(mContext, BBViewSetting.FLAG_TEXTSIZE_SMALL));
	}

	@Override
	public void click() {
		CustomData custom_data = CustomDataManager.getCustomData();
		Intent intent = new Intent(mContext, SelectActivity.class);
	
		String title = blust + "(" + type + ")";
		intent.putExtra(SelectActivity.INTENTKEY_TITLENAME, title);
		intent.putExtra(SelectActivity.INTENTKEY_BLUSTTYPE, blust);
		intent.putExtra(SelectActivity.INTENTKEY_WEAPONTYPE, type);

		BBData select_item = custom_data.getWeapon(blust, type);
		IntentManager.setSelectedData(intent, select_item);
		
		mContext.startActivity(intent);
	}

}
