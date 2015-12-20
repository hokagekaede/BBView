package hokage.kaede.gmail.com.BBView.Adapter;

import hokage.kaede.gmail.com.BBViewLib.BBData;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

public class BBSelectDataAdapter extends BBArrayAdapter {
	
	private ArrayList<BBData> mSelectedItemList;

	public BBSelectDataAdapter(Context context, ArrayList<BBData> list) {
		super(context, list);
		mSelectedItemList = new ArrayList<BBData>();
	}
	
	public void select(BBData data) {
		mSelectedItemList.add(data);
	}
	
	public void unselect(BBData data) {
		int size = mSelectedItemList.size();
		for(int i=0; i<size; i++) {
			BBData buf_data = mSelectedItemList.get(i);
			if(buf_data.id == data.id) {
				mSelectedItemList.remove(i);
				break;
			}
		}
	}
	
	public boolean isSelect(BBData data) {
		int size = mSelectedItemList.size();
		for(int i=0; i<size; i++) {
			BBData buf_data = mSelectedItemList.get(i);
			if(buf_data.id == data.id) {
				return true;
			}
		}
		
		return false;
	}

	public ArrayList<BBData> getSelectedList() {
		return mSelectedItemList;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BBArrayAdapterTextView view = (BBArrayAdapterTextView)super.getView(position, convertView, parent);
		BBData target_data = super.getItem(position);
		
		view.setBackgroundColor(Color.argb(0, 0, 0, 0));
		
		int size = mSelectedItemList.size();
		for(int i=0; i<size; i++) {
			BBData data = mSelectedItemList.get(i);
			if(data.id == target_data.id) {
				view.setBackgroundColor(Color.argb(128, 64, 64, 64));
				break;
			}
		}
		
		return view;
	}
}
