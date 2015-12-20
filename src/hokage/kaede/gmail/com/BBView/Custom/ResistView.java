package hokage.kaede.gmail.com.BBView.Custom;

import hokage.kaede.gmail.com.BBViewLib.BBData;
import hokage.kaede.gmail.com.BBViewLib.BBDataFilter;
import hokage.kaede.gmail.com.BBViewLib.BBDataManager;
import hokage.kaede.gmail.com.BBViewLib.CustomData;
import hokage.kaede.gmail.com.BBViewLib.CustomDataManager;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ResistView extends LinearLayout {
	
	private ResistAdapter mAdapter;
	
	public ResistView(Context context) {
		super(context);
	
		// 各管理変数
		CustomData custom_data = CustomDataManager.getCustomData();

		mAdapter = new ResistAdapter(context, custom_data, null);
		updateList("射撃");

		LinearLayout layout_all = new LinearLayout(context);
		layout_all.setOrientation(LinearLayout.VERTICAL);
		layout_all.setGravity(Gravity.LEFT | Gravity.TOP);
		
		ListView listview = new ListView(context);
		listview.setAdapter(mAdapter);
		listview.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));

		LinearLayout layout_btm = new LinearLayout(context);
		layout_btm.setOrientation(LinearLayout.HORIZONTAL);
		layout_btm.setGravity(Gravity.CENTER | Gravity.TOP);
		
		Button set_shot_btn = new Button(context);
		set_shot_btn.setText("射撃武器");
		set_shot_btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
		set_shot_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				updateList("射撃");
			}
		});
		
		Button set_explosion_btn = new Button(context);
		set_explosion_btn.setText("爆発武器");
		set_explosion_btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
		set_explosion_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				updateList("爆発");
			}
		});

		Button set_slash_btn = new Button(context);
		set_slash_btn.setText("近接武器");
		set_slash_btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
		set_slash_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				updateList("近接");
			}
		});
		
		layout_btm.addView(set_shot_btn);
		layout_btm.addView(set_explosion_btn);
		layout_btm.addView(set_slash_btn);
		
		layout_all.addView(listview);
		layout_all.addView(layout_btm);

		addView(layout_all);
	}
	
	private void updateList(String absolute) {
		BBDataManager data_mng = BBDataManager.getInstance();

		BBDataFilter filter = new BBDataFilter();
		filter.setBlustType(BBDataManager.BLUST_TYPE_LIST);
		filter.setWeaponType(BBDataManager.WEAPON_TYPE_LIST);
		filter.setValue("属性", absolute);
		ArrayList<BBData> weapons = data_mng.getList(filter);
		
		if(absolute.equals("射撃")) {
			mAdapter.setMode(ResistAdapterItem.MODE_SHOT);
		}
		else if(absolute.equals("爆発")) {
			mAdapter.setMode(ResistAdapterItem.MODE_EXPLOSION);
		}
		else if(absolute.equals("近接")) {
			mAdapter.setMode(ResistAdapterItem.MODE_SLASH);
		}
		
		mAdapter.setList(weapons);
		mAdapter.notifyDataSetChanged();
	}
}
