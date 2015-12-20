package hokage.kaede.gmail.com.BBView.Shop;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;

public class DistanceSetTask extends AsyncTask<Object, Integer, Object> {
	private Context mContext;
	private ProgressDialog mDialog;
	private Address mAddress;
	private OnEndTaskListener mEndTask;

	/**
	 * 初期化処理を行う。
	 */
	public DistanceSetTask(Context context, Address address, OnEndTaskListener task) {
		mContext = context;
		mDialog = null;
		mAddress = address;
		mEndTask = task;
	}

	/**
	 * フォアグラウンドの処理を行う。
	 * 店舗データ取得中のダイアログを表示する。
	 */
	@Override
	protected void onPreExecute() {
		mDialog = new ProgressDialog(mContext);
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setMessage("検索中");
		mDialog.setCancelable(false);
		mDialog.show();
	}
	
	/**
	 * バックグラウンドの処理を行う。
	 * 店舗データを公式サイトから取得する。
	 */
	@Override
	protected Object doInBackground(Object... arg0) {
		ShopDatabase shop_database = ShopDatabase.getShopDatabase();
		shop_database.setDistance(mContext, mAddress, 1000);

		return null;
	}
	
	/**
	 * 終了処理を行う。
	 * 次画面への遷移、または店舗情報取得失敗ダイアログを表示する。
	 */
	@Override
	protected void onPostExecute(Object arg0) {
		if(mDialog != null) {
			try {
				mDialog.dismiss();

			} catch(IllegalArgumentException e) {
				// Do Nothing
			}
		}
		
		if(mEndTask != null) {
			mEndTask.onEndTask();
		}
	}
	
	/**
	 * 処理が終了したときに実行するイベントリスナー
	 */
	public static interface OnEndTaskListener {
		public void onEndTask();
	}
}
