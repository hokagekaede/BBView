package hokage.kaede.gmail.com.BBViewLib.Adapter;

import java.util.ArrayList;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBViewSetting;
import android.content.Context;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * チップのリストに表示するビューのクラス。
 */
public class BBArrayAdapterChipView extends BBArrayAdapterBaseView {
	
	private CheckBox mCheckBox;
	
	private LinearLayout mExtraDataView;
	private TextView mExistTextView;
	private TextView mFavoriteTextView;
	
	public BBArrayAdapterChipView(Context context, ArrayList<String> keys) {
		super(context, keys);
		
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		this.setPadding(10, 10, 10, 10);
		this.setWeightSum((float)1.0);
	}

	/**
	 * 表示するビューを取得する。
	 */
	@Override
	public void createView() {
		Context context = getContext();
		mCheckBox = new CheckBox(context);

		mExtraDataView = new LinearLayout(context);
    	mExistTextView = new TextView(context);
    	mFavoriteTextView = new TextView(context);
    	
		mCheckBox.setTextSize(BBViewSetting.getTextSize(context, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
		mCheckBox.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));

        mExistTextView.setTextSize(BBViewSetting.getTextSize(context, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
        mExistTextView.setGravity(Gravity.RIGHT | Gravity.CENTER);
        mExistTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        mFavoriteTextView.setTextSize(BBViewSetting.getTextSize(context, BBViewSetting.FLAG_TEXTSIZE_NORMAL));
        mFavoriteTextView.setGravity(Gravity.RIGHT | Gravity.CENTER);
        mFavoriteTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mFavoriteTextView.setPadding(10, 0, 10, 0);

        mExtraDataView.setOrientation(LinearLayout.HORIZONTAL);
        mExtraDataView.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        mExtraDataView.setWeightSum((float)1.0);
        mExtraDataView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
        mExtraDataView.addView(mExistTextView);
        mExtraDataView.addView(mFavoriteTextView);
        
        this.addView(mCheckBox);
        this.addView(mExtraDataView);
	}

	/**
	 * 表示するビューを生成する。
	 */
	@Override
	public void updateView() {
		BBData target_item = getItem();
		mCheckBox.setText(target_item.get("名称") + " [" + target_item.get("コスト") + "]");
    	mExistTextView.setText(createExistText());
    	
    	super.updateFavorite(mFavoriteTextView);
	}

	/**
	 * チェックボックスを更新する。
	 * @param checked 設定するチェック状態
	 */
	public void setChecked(boolean checked) {
		mCheckBox.setChecked(checked);
	}
	
	/**
	 * チェックボックスが変更された場合のリスナーを設定する。
	 * @param listener 対象のリスナー
	 */
	public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
		mCheckBox.setOnCheckedChangeListener(listener);
	}
	
	/**
	 * Favoriteボタンがクリックされた場合のリスナーを設定する。
	 * @param listener 対象のリスナー
	 */
	public void setOnClickFavListener(OnClickListener listener) {
		mFavoriteTextView.setOnClickListener(listener);
	}
	
	/**
	 * IDを設定する。
	 */
	@Override
	public void setId(int id) {
		super.setId(id);
		mCheckBox.setId(id);
	}
}
