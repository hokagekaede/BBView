package hokage.kaede.gmail.com.StandardLib.Android;

/**
 * 選択式のリストのインターフェース
 */
public interface ISelectionAdapter<T extends Object> {

    /**
     * 指定のアイテムを選択状態にする。
     * @param position 選択するアイテムの位置
     */
    void select(int position);

    /**
     * 選択状態を解除する。
     */
    void unselect();

    /**
     * 選択状態のアイテムを取得する。
     * @return 選択状態のアイテム
     */
    T getSelectionItem();

}
