package application.util;

import java.nio.file.Path;

/**
 * ファイルまわりのユーティリティを集めたクラス
 */
public class FileUtils {
	/**
	 * パスに示されたファイルが、指定する拡張子を持つか判定します。
	 * このメソッドは大文字小文字の差を無視します。
	 * @param path	パス
	 * @param exts	判定する拡張子
	 * @return	一致／不一致
	 */
	public static boolean checkExtension(Path path, String... exts) {
		String lowerCase = path.toString().toLowerCase();
		for(String ext: exts) {
			if(lowerCase.endsWith("." + ext.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}
