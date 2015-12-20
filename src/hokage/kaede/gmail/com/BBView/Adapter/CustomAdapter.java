package hokage.kaede.gmail.com.BBView.Adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class CustomAdapter extends BaseAdapter {
	private ArrayList<CustomAdapterBaseItem> mList;
	private Context mContext;

	public CustomAdapter(Context context) {
		mList = new ArrayList<CustomAdapterBaseItem>();
		mContext = context;
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}
	
	public void addItem(CustomAdapterBaseItem item) {
		mList.add(item);
	}
	
	@Override
	public CustomAdapterBaseItem getItem(int position) {
		return mList.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CustomAdapterBaseItem item = mList.get(position);
		return item.createView(mContext);
	}
	
	/**
	 * 表示インターフェース
	 */
	public static interface CustomAdapterBaseItem {
		public LinearLayout createView(Context context);
		public void updateView();
		public void click();
	}
}
