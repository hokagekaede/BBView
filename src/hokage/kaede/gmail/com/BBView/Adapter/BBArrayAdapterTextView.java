package hokage.kaede.gmail.com.BBView.Adapter;

import java.util.ArrayList;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.Android.BBViewSettingManager;
import hokage.kaede.gmail.com.Lib.Android.SettingManager;
import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BBArrayAdapterTextView extends BBArrayAdapterBaseView {

	private Context mContext;
	private LinearLayout mMainLayout;
	private TextView mNameTextView;
	private TextView mSubTextView;
	private TextView mExistTextView;
	
	/**
	 * 初期化処理を行う。
	 * LinearLayoutのコンストラクタをコールし、TextViewのオブジェクトを生成する。
	 * @param context リストを表示する画面
	 */
	public BBArrayAdapterTextView(Context context, ArrayList<String> keys, boolean is_km_per_hour) {
		super(context, keys, is_km_per_hour);
		mContext = context;
		
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		this.setPadding(10, 10, 10, 10);
		this.setWeightSum((float)1.0);
	}
	
	/**
	 * ビューを生成する。テキストサイズや文字色の設定を行う。
	 * @param item リストのデータ
	 * @param position リストの位置
	 */
	public void createView() {
        mNameTextView = new TextView(mContext);
        mNameTextView.setTextSize(BBViewSettingManager.getTextSize(mContext, BBViewSettingManager.FLAG_TEXTSIZE_NORMAL));
        
    	mSubTextView = new TextView(mContext);
        mSubTextView.setTextSize(BBViewSettingManager.getTextSize(mContext, BBViewSettingManager.FLAG_TEXTSIZE_SMALL));

    	mExistTextView = new TextView(mContext);
        mExistTextView.setTextSize(BBViewSettingManager.getTextSize(mContext, BBViewSettingManager.FLAG_TEXTSIZE_NORMAL));
        mExistTextView.setGravity(Gravity.RIGHT | Gravity.CENTER);
        mExistTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));

        mMainLayout = new LinearLayout(mContext);
        mMainLayout.setOrientation(LinearLayout.HORIZONTAL);
        mMainLayout.setGravity(Gravity.LEFT | Gravity.CENTER_HORIZONTAL);
        mMainLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        LinearLayout sub_layout = new LinearLayout(mContext);
        sub_layout.setOrientation(LinearLayout.VERTICAL);
        sub_layout.setGravity(Gravity.LEFT | Gravity.CENTER_HORIZONTAL);
        sub_layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
        
        sub_layout.addView(mNameTextView);
        sub_layout.addView(mSubTextView);
        
        mMainLayout.addView(sub_layout);
        mMainLayout.addView(mExistTextView);
        
        this.addView(mMainLayout);
	}
	
	/**
	 * ビューの更新する。
	 * @param base_item 比較対象のデータ
	 */
	public void updateView(BBData base_item) {
		String sub_text = createSubText(base_item);
        mNameTextView.setText(createNameText());
    	mSubTextView.setText(Html.fromHtml(sub_text));
    	mExistTextView.setText(createExistText());

    	// テキストを更新する
        if(sub_text.equals("")) {
        	mSubTextView.setVisibility(View.GONE);
        }
        else {
        	mSubTextView.setVisibility(View.VISIBLE);
        }

        // 選択中のアイテムの場合は文字色を黄色に変更する
        BBData target_item = getItem();
        if(base_item != null && BBDataManager.equalData(target_item, base_item)) {
        	mNameTextView.setTextColor(SettingManager.getColorYellow());
        }
        else {
        	mNameTextView.setTextColor(SettingManager.getColorWhite());
        }
	}
	
	/**
	 * ビューの更新をする。比較対象無し。
	 * @param item リストのデータ。
	 */
	public void updateView() {
		updateView();
	}
}
