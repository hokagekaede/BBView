package hokage.kaede.gmail.com.BBViewLib.Android.PurchaseLib;

import hokage.kaede.gmail.com.BBViewLib.Android.CommonLib.BBArrayAdapter;
import hokage.kaede.gmail.com.BBViewLib.Android.CommonLib.BBDataAdapterItem;
import hokage.kaede.gmail.com.BBViewLib.Java.BBData;
import hokage.kaede.gmail.com.StandardLib.Android.SettingManager;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;

/**
 * 「購入リスト追加」画面のアイテム一覧を生成するクラス。
 */
public class PurchaseAdapter extends BBArrayAdapter {
	
	private ArrayList<BBData> mSelectedItemList;

	public PurchaseAdapter(ArrayList<BBData> list) {
		super(list);
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
		BBDataAdapterItem view = (BBDataAdapterItem)super.getView(position, convertView, parent);
		BBData target_data = super.getItem(position);
		
		view.setBackgroundColor(SettingManager.getColorBlack());
		
		int size = mSelectedItemList.size();
		for(int i=0; i<size; i++) {
			BBData data = mSelectedItemList.get(i);
			if(data.id == target_data.id) {
				view.setBackgroundColor(SettingManager.getColorGray());
				break;
			}
		}
		
		return view;
	}
}
