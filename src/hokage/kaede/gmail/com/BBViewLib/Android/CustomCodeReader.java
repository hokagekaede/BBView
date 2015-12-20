package hokage.kaede.gmail.com.BBViewLib.Android;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import hokage.kaede.gmail.com.BBView.Custom.CustomMainActivity;
import hokage.kaede.gmail.com.BBViewLib.CustomData;
import hokage.kaede.gmail.com.BBViewLib.CustomDataManager;
import hokage.kaede.gmail.com.Lib.Java.FileIO;
import hokage.kaede.gmail.com.Lib.Java.NetAccess;

/**
 * アセンデータのコードの読み込みを行うクラス
 */
public class CustomCodeReader {
	
	private Context mContext;
	private Intent mIntent;
	
	public CustomCodeReader(Context context, Intent intent) {
		mContext = context;
		mIntent = intent;
	}
	
	public void read() {
		if(mIntent == null) {
			return;
		}
		else if(mIntent.getAction() == null) {
			return;
		}
		else if(mIntent.getAction().equals(Intent.ACTION_SEND)) {
			CustomCodeReaderTask task = new CustomCodeReaderTask();
			task.execute();
		}
	}
	
	private class CustomCodeReaderTask extends AsyncTask<Object, Integer, Integer> {
		
		@Override
		protected Integer doInBackground(Object... arg0) {
			String intent_str = mIntent.getExtras().getCharSequence(Intent.EXTRA_TEXT).toString();
			String code = getCode(intent_str).toString();
			
			if(code == null) {
				code = intent_str;
			}
			
			CustomData custom_data = CustomDataManager.getCustomData();
			return custom_data.setCustomDataID(BBViewSettingManager.getVersionCode(mContext), code);
		}

		/**
		 * Twitter公式アプリからツイート共有した場合のアセンコードを取得する。
		 * ※Twitter公式アプリは、ツイート共有でツイートへのURLを返却する。
		 * @param intent_str インテントの文字列
		 * @return アセンコードを含む文字列
		 */
		public String getCode(String intent_str) {
			int url_idx = intent_str.indexOf("https://");
			if(url_idx == -1) {
				return null;
			}
			String url = intent_str.substring(url_idx);
			String twitter_page = NetAccess.readString(url, "UTF-8");
			
			String path = mContext.getFilesDir().toString() + System.getProperty("file.separator") + "log.txt";
			
			url = "https:" + twitter_page.substring(twitter_page.indexOf("url=") + 5, twitter_page.indexOf(">") - 1);

			twitter_page = NetAccess.readString(url, "UTF-8");
			FileIO.write(path, twitter_page, "Shift_JIS");
			
			int code_head_idx = twitter_page.indexOf("[[BBView:");
			int code_tail_idx = twitter_page.indexOf("]]");
			
			if(code_head_idx == -1 || code_tail_idx == -1) {
				return null;
			}
			
			return twitter_page.substring(code_head_idx, code_tail_idx);
		}
		
		protected void onPostExecute(Integer ret) {

			if(ret == CustomData.RET_SUCCESS) {
				Toast.makeText(mContext, "アセンデータを読み込みました", Toast.LENGTH_SHORT).show();
				Intent next_intent = new Intent(mContext, CustomMainActivity.class);
				mContext.startActivity(next_intent);
			}
			else if(ret == CustomData.ERROR_CODE_TARGET_NOTHING) {
				Toast.makeText(mContext, "アセンデータが存在しません", Toast.LENGTH_SHORT).show();
			}
			else if(ret == CustomData.ERROR_CODE_VERSION_NOT_EQUAL) {
				Toast.makeText(mContext, "バージョン情報が一致していません", Toast.LENGTH_SHORT).show();
			}
			else if(ret == CustomData.ERROR_CODE_ITEM_NOTHING) {
				Toast.makeText(mContext, "アセン情報の読み込みに失敗しました", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
}
