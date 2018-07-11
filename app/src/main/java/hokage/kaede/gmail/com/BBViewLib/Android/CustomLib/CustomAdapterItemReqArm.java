package hokage.kaede.gmail.com.BBViewLib.Android.CustomLib;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import hokage.kaede.gmail.com.BBView3.Custom.SelectActivity;
import hokage.kaede.gmail.com.BBViewLib.Java.BBData;
import hokage.kaede.gmail.com.BBViewLib.Java.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.Java.BBViewSetting;
import hokage.kaede.gmail.com.BBViewLib.Java.CustomData;
import hokage.kaede.gmail.com.BBViewLib.Java.CustomDataManager;
import hokage.kaede.gmail.com.BBViewLib.Android.CustomLib.CustomAdapter.CustomAdapterBaseItem;
import hokage.kaede.gmail.com.BBViewLib.Android.CommonLib.IntentManager;

/**
 * 「アセン」画面の要請兵器の表示内容を生成するクラス。
 */
public class CustomAdapterItemReqArm implements CustomAdapterBaseItem {
	public String title;
	public String summary;

	private BBData mItem;
	private TextView mTitleTextView;
	private TextView mSummaryTextView;
	private Context mContext;
	
	public CustomAdapterItemReqArm(Context context, BBData item) {
		this.title = item.get("名称");
		this.summary = BBDataManager.REQARM_STR;
		this.mContext = context;
		this.mItem = item;
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
		
		mTitleTextView.setPadding(25, 10, 0, 0);
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
		
		intent.putExtra(SelectActivity.INTENTKEY_TITLENAME, summary);
		intent.putExtra(SelectActivity.INTENTKEY_REQARM, summary);
		
		BBData select_item = custom_data.getReqArm();
		IntentManager.setSelectedData(intent, select_item);
		
		mContext.startActivity(intent);
	}
	
	/**
	 * 要請兵器データを取得する。
	 * @return 要請兵器データ
	 */
	@Override
	public BBData getItem() {
		return mItem;
	}
}
