package hokage.kaede.gmail.com.BBViewLib.Android;

import hokage.kaede.gmail.com.Lib.Android.SettingManager;
import android.content.Context;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * 基本的なビューを生成するクラス
 */
public class ViewBuilder {
	
	/**
	 * テーブルビューの行を生成する
	 * @param mContext 生成する画面
	 * @param args 表示する文字列
	 * @return テーブルビューの行
	 */
	public static TableRow createTableRow(Context context, int color, String... args) {
		TableRow row = new TableRow(context);
		
		int len = args.length;
		for(int i=0; i<len; i++) {
			TextView text = new TextView(context);
			text.setText(args[i]);
			text.setPadding(5, 0, 5, 0);
			text.setTextColor(color);
			text.setTextSize(BBViewSettingManager.getTextSize(context, BBViewSettingManager.FLAG_TEXTSIZE_SMALL));
			row.addView(text);
		}
		
		return row;
	}

	/**
	 * テーブルビューの行を生成する。文字列の配列数と色番号の配列数を合わせること。
	 * @param context 現在表示中の画面
	 * @param colors 各列の色
	 * @param args 表示する文字列
	 * @return テーブルビューの行
	 */
	public static TableRow createTableRow(Context context, int[] colors, String... args) {
		TableRow row = new TableRow(context);

		int len = args.length;
		for(int i=0; i<len; i++) {
			TextView text = new TextView(context);
			text.setText(args[i]);
			text.setPadding(5, 0, 5, 0);
			text.setTextColor(colors[i]);
			text.setTextSize(BBViewSettingManager.getTextSize(context, BBViewSettingManager.FLAG_TEXTSIZE_SMALL));
			row.addView(text);
		}
		
		return row;
	}

	/**
	 * テキストビューを生成する。
	 * @param context 現在表示中の画面
	 * @param text テキスト
	 * @param flag テキストサイズの設定
	 * @param color 文字色
	 * @param bg_color 背景色
	 * @return テキストビュー
	 */
	public static TextView createTextView(Context context, String text, int flag, int color, int bg_color) {
		TextView text_view = new TextView(context);
		text_view.setText(text);
		text_view.setTextSize(BBViewSettingManager.getTextSize(context, flag));
		text_view.setTextColor(color);
		text_view.setBackgroundColor(bg_color);
		
		return text_view;
	}

	/**
	 * テキストビューを生成する。
	 * @param context 現在表示中の画面
	 * @param text テキスト
	 * @param flag テキストサイズの設定
	 * @param color 文字色
	 * @return テキストビュー
	 */
	public static TextView createTextView(Context context, String text, int flag, int color) {
		TextView text_view = new TextView(context);
		text_view.setText(text);
		text_view.setTextSize(BBViewSettingManager.getTextSize(context, flag));
		text_view.setTextColor(color);
		
		return text_view;
	}
	
	/**
	 * テキストビューを生成する
	 * @param context 現在表示中の画面
	 * @param text テキスト
	 * @param flag テキストサイズの設定
	 * @return テキストビュー
	 */
	public static TextView createTextView(Context context, String text, int flag) {
		return createTextView(context, text, flag, SettingManager.getColor(SettingManager.COLOR_BASE));
	}
}
