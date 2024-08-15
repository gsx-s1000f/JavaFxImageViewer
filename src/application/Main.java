package application;
	
import java.io.BufferedReader;
import java.io.BufferedWriter;
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

	/** プロパティファイル */
	final static String PROP_PATH = "./app/application.properties";
	final Properties prop = new Properties();
	
	Scene scene;
	
	final String HIGHT_NAME = "app.hight";
	final String WIDTH_NAME = "app.width";
	double DEFAULT_HIGHT = 480;
	double DEFAULT_WIDTH = 640;
	
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
			MainController controller = (MainController)loader.getController();
			controller.setStage(primaryStage);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void init() throws Exception {
		Path path = Paths.get(PROP_PATH);
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
	@Override
	public void stop() throws Exception {
		System.out.println("application close.");
		double hight = Double.parseDouble(prop.getProperty(HIGHT_NAME));
		double width = Double.parseDouble(prop.getProperty(WIDTH_NAME));		
		if(this.scene.getHeight() != hight ||
				this.scene.getWidth() != width) {
			this.prop.setProperty(HIGHT_NAME, String.valueOf(this.scene.getHeight()));
			this.prop.setProperty(WIDTH_NAME, String.valueOf(this.scene.getWidth()));
			
			Path path = Paths.get(PROP_PATH);
			try(BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)){
				LocalDateTime date = LocalDateTime.now();
				String text = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
				this.prop.store(bw,text);
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
