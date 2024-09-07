package application;
	
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {

	/** プロパティファイルパス */
	static String propFilePath = "./app/application.properties";
	/** プロパティ */
	final Properties prop = new Properties();
	/** プロパティキー：初期フォルダ */
	final String DEFAULT_DIR ="app.default.dir";
	/** プロパティキー：高さ */
	final String HIGHT_NAME = "app.hight";
	/** プロパティキー：幅 */
	final String WIDTH_NAME = "app.width";
	/** プロパティキー：縦フィット*/
	final String HORIZONTAL_FIT = "horizontal.fit";
	/** プロパティキー：横フィット*/
	final String VERTICAL_FIT = "vertical.fit";
	/** デフォルトプロパティ値：高さ */
	double DEFAULT_HIGHT = 480;
	/** デフォルトプロパティ値：幅 */
	double DEFAULT_WIDTH = 640;

	/** Scene*/
	Scene scene;

	/**
	 * アプリケーション機動
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
			BorderPane root = (BorderPane)loader.load();
			double hight = Double.parseDouble(prop.getProperty(HIGHT_NAME));
			double width = Double.parseDouble(prop.getProperty(WIDTH_NAME));

			this.scene = new Scene(root, width, hight);
			this.scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			// ControllerでApplication Stageを使えるようにする。
			MainController controller = (MainController)loader.getController();
			controller.setApplication(this);
			controller.setPrimaryStage(primaryStage);
			// 起動引数をコントローラに渡す(List<String>に変換されている)
			controller.setArgs(this.getParameters().getRaw());

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 起動前処理
	 */
	@Override
	public void init() throws Exception {
		Path path = Paths.get(propFilePath);
		if(Files.exists(path)) {
			try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
				prop.load(br);
			}
		}
		String hight = prop.getProperty(HIGHT_NAME);
		String width = prop.getProperty(WIDTH_NAME);
		if (hight == null || hight.trim().equals("")) {
			this.prop.setProperty(HIGHT_NAME, String.valueOf(DEFAULT_HIGHT));
		}
		if (width == null || width.trim().equals("")) {
			this.prop.setProperty(WIDTH_NAME, String.valueOf(DEFAULT_WIDTH));
		}
	}
	/**
	 * 終了時処理
	 */
	@Override
	public void stop() throws Exception {
		System.out.println("application close.");
		this.prop.setProperty(HIGHT_NAME, String.valueOf(this.scene.getHeight()));
		this.prop.setProperty(WIDTH_NAME, String.valueOf(this.scene.getWidth()));
		
		Path path = Paths.get(propFilePath);
		try(BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)){
			LocalDateTime date = LocalDateTime.now();
			String text = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			this.prop.store(bw,text);
		}
	}
	/**
	 * デフォルトディレクトリを取得する。
	 * @return	デフォルトディレクトリのパス
	 */
	public String getDefaultDir() {
		return this.prop.getProperty(DEFAULT_DIR);
	}
	/**
	 * 縦フィット取得
	 * @return
	 */
	public boolean getVerticalFit() {
		String fit = this.prop.getProperty(VERTICAL_FIT);
		return fit != null && fit.equals("1"); 
	}
	/**
	 * 縦フィットを設定
	 * @param fit
	 */
	public void setVerticalFit(boolean fit) {
		this.prop.setProperty(VERTICAL_FIT, fit? "1": "0");
	}
	/**
	 * 横フィット取得
	 * @return
	 */
	public boolean getHorizontalFit() {
		String fit = this.prop.getProperty(HORIZONTAL_FIT);
		return fit != null && fit.equals("1"); 
	}
	/**
	 * 横フィットを設定
	 * @param fit
	 */
	public void setHorizontalFit(boolean fit) {
		this.prop.setProperty(HORIZONTAL_FIT, fit? "1": "0");
	}
	/**
	 * デフォルトディレクトリを設定し、プロパティファイルに書き込む。
	 * @param defaultDir	デフォルトディレクトリのパス
	 */
	public void setDefaultDir(String defaultDir) {
		this.prop.setProperty(DEFAULT_DIR, defaultDir);
		Path path = Paths.get(propFilePath);
		try(BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)){
			LocalDateTime date = LocalDateTime.now();
			String text = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			this.prop.store(bw,text);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	/**
	 * メイン
	 * @param args	起動引数
	 */
	public static void main(String[] args) {
		String classPath = System.getProperty("java.class.path");
		if (classPath != null) {
			for (String path: classPath.split(";", 0)) {
				if (path.endsWith("imageviewer.jar")) {
					File file = new File(path);
					if(file.exists()) {
						String parent = file.getParentFile().getAbsolutePath();
						propFilePath = parent + File.separator + "application.properties";
					}
					break;
				}
			}
		}
		launch(args);
	}
}
