package application.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 画像ファイルまわりのユーティリティを集めたクラス
 */
public class ImageUtils {
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
	/**
	 * Imageの URL文字列をパスに変更する。（ローカルパス）
	 * @param url	url文字列
	 * @return	path文字列
	 */
	public static String imageUrl2Path(String url) {
		return url.replaceFirst("file:/", "");
	}
	/**
	 * 引数のパスと同じフォルダにある画像を探します。
	 * @param url	検索対象のURL文字列
	 * @param next	次 or 前の画像
	 * @return 探した画像のURL文字列
	 */
	public static String searchImage(String url, boolean next) {
		Path image = Paths.get(imageUrl2Path(url));
		Path parent = image.getParent();
		Comparator<Path> cmparator = next? Comparator.naturalOrder(): Comparator.reverseOrder();
		
		try (Stream<Path> stream = Files.list(parent)){
			List<Path> paths = stream.filter(p -> !Files.isDirectory(p))
				.filter(p -> ImageUtils.checkExtension(p, "bmp", "jpg", "jpeg", "png"))
				.sorted(cmparator).collect(Collectors.toList());
			int index = paths.indexOf(image);
			if (index > -1 && index + 1 < paths.size()) {
				return paths.get(index + 1).toFile().toURI().toURL().toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return url;		
	}
	/**
	 * 指定した画像と同じフォルダ内の前の画像のパスを返す
	 * @param url	調査する画像
	 * @return	前の画像のパス
	 */
	public static String forwardImage(String url) {
		return searchImage(url, false);
	}
	/**
	 * 同じフォルダ内の前の画像のパスを返す
	 * @param url	調査する画像
	 * @return	次の画像のパス
	 */
	public static String nextImage(String url) {
		return searchImage(url, true);
	}
}
