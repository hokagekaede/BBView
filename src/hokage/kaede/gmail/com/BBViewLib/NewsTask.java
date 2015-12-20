package hokage.kaede.gmail.com.BBViewLib;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * サテライトニュースを取得するタスククラス
 */
public class NewsTask extends AsyncTask<Object, Integer, Object> {
	
	private Context mContext;
	private NewsData mNewsData;
	private OnGetNewsCompletedListener mListener;
	
	/**
	 * 初期化処理を行う。
	 * @param context 表示する画面
	 * @param mNewsText ニュースの文字列
	 */
	public NewsTask(Context context, String dir_path) {
		mContext = context;
		mNewsData = new NewsData(dir_path);
	}

	/**
	 * ニュース取得完了時に実行するリスナーを登録する。
	 * @param listener
	 */
	public void setCompletedListener(OnGetNewsCompletedListener listener) {
		mListener = listener;
	}

	/**
	 * 事前の処理を行う。ニュース取得中のダイアログを表示する。
	 */
	@Override
	protected void onPreExecute() {
		Toast.makeText(mContext, "ニュース取得開始", Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * バックグラウンドの処理を行う。ニュースデータを取得する。
	 */
	@Override
	protected Object doInBackground(Object... arg0) {
		mNewsData.read();
		return null;
	}
	
	/**
	 * キャンセル時の処理を行う。ニュースの取得処理を中止する。
	 */
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
	
	/**
	 * 終了処理を行う。プログレスダイアログを非表示にし、取得結果のダイアログを表示する。
	 */
	@Override
	protected void onPostExecute(Object arg0) {
		if(mListener != null) {
			mListener.onGetNewsCompleted(mNewsData);
		}
	}
	
	/**
	 * ニュース取得完了時に実行するリスナー
	 */
	public static interface OnGetNewsCompletedListener {
		public void onGetNewsCompleted(NewsData news_data);
	}
}
