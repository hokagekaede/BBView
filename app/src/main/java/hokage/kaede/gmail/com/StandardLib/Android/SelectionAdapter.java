package hokage.kaede.gmail.com.StandardLib.Android;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * 選択式のリストを実現するクラス
 */
public class SelectionAdapter extends NormalAdapter implements ISelectionAdapter {

    // 選択していない場合の位置
    private static final int UNSELECTED_POSITION = -1;

    // 選択中のアイテムの位置
    private int mSelectedPosition;

    /**
     * 初期化する。
     */
    public SelectionAdapter() {
        super();
        mSelectedPosition = UNSELECTED_POSITION;
    }

    /**
     * 指定のアイテムを選択状態にする。
     * @param position 選択するアイテムの位置
     */
    @Override
    public void select(int position) {
        mSelectedPosition = position;
    }

    /**
     * 選択状態を解除する。
     */
    @Override
    public void unselect() {
        mSelectedPosition = UNSELECTED_POSITION;
    }

    /**
     * 選択状態のアイテムを取得する。
     * @return 選択状態のアイテム
     */
    @Override
    public Object getSelectionItem() {
        return get(mSelectedPosition);
    }

    /**
     * 指定位置のViewを返す。
     * @param position 取得する位置
     * @param view 取得するView
     * @param viewGroup 取得するViewGroup
     * @return viewのインスタンスを返す。
     */
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Context context = viewGroup.getContext();
        Object data = get(position);
        SelectionAdapterItem item_view;

        if(view == null) {
            item_view = new SelectionAdapterItem(context);
            item_view.setTextSize(mTextSize);
            item_view.setTextColor(mTextColor);
            item_view.setBackGroundColor(mBackGroundColor);
        }
        else {
            item_view = (SelectionAdapterItem)view;
        }

        if(mSelectedPosition == position) {
            item_view.setSelected(true);
        }
        else {
            item_view.setSelected(false);
        }

        item_view.setData(data);
        item_view.updateView();

        return item_view;
    }
}
