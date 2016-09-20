package hokage.kaede.gmail.com.BBView.Adapter;

import java.util.ArrayList;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.Android.BBViewSettingManager;
import android.content.Context;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class BBArrayAdapterChipView extends BBArrayAdapterBaseView {
	
	private CheckBox mCheckBox;
	private TextView mExistTextView;
	
	public BBArrayAdapterChipView(Context context, ArrayList<String> keys, boolean is_km_per_hour) {
		super(context, keys, is_km_per_hour);
		
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		this.setPadding(10, 10, 10, 10);
		this.setWeightSum((float)1.0);
	}

	@Override
	public void createView() {
		Context context = getContext();
		mCheckBox = new CheckBox(context);
    	mExistTextView = new TextView(context);
    	
		mCheckBox.setTextSize(BBViewSettingManager.getTextSize(context, BBViewSettingManager.FLAG_TEXTSIZE_NORMAL));
		mCheckBox.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));

        mExistTextView.setTextSize(BBViewSettingManager.getTextSize(context, BBViewSettingManager.FLAG_TEXTSIZE_NORMAL));
        mExistTextView.setGravity(Gravity.RIGHT | Gravity.CENTER);
        mExistTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));

        this.addView(mCheckBox);
        this.addView(mExistTextView);
	}

	@Override
	public void updateView() {
		BBData target_item = getItem();
		mCheckBox.setText(target_item.get("名称") + " [" + target_item.get("コスト") + "]");
    	mExistTextView.setText(createExistText());
	}

	public void setChecked(boolean checked) {
		mCheckBox.setChecked(checked);
	}
	
	public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
		mCheckBox.setOnCheckedChangeListener(listener);
	}
	
	@Override
	public void setId(int id) {
		super.setId(id);
		mCheckBox.setId(id);
	}
}
