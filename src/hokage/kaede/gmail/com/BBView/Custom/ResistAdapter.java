package hokage.kaede.gmail.com.BBView.Custom;

import java.util.ArrayList;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.CustomData;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ResistAdapter extends BaseAdapter {
	private Context mContext;
	private CustomData mCustomData;
	private ArrayList<BBData> mList;
	private int mPatternMode = ResistAdapterItem.MODE_SHOT;
	
	public ResistAdapter(Context context, CustomData custom_data, ArrayList<BBData> list) {
		mContext = context;
		mCustomData = custom_data;
		mList = list;
	}
	
	public void setList(ArrayList<BBData> list) {
		mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ResistAdapterItem item_view = (ResistAdapterItem)arg1;
		
		if(item_view == null) {
			item_view = new ResistAdapterItem(mContext, mCustomData, mList.get(arg0), mPatternMode);
		}
		else {
			
			// 表示モードが異なる場合はインスタンスを再生成する。
			if(item_view.getMode() == mPatternMode) {
				item_view.update(mCustomData, mList.get(arg0));
			}
			else {
				item_view = new ResistAdapterItem(mContext, mCustomData, mList.get(arg0), mPatternMode);
			}
		}
		
		return (View)(item_view);
	}
	
	public void setMode(int mode) {
		mPatternMode = mode;
	}
}
