package application;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import application.util.FileUtils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainController implements Initializable {

    @FXML
    private ImageView imageView;
    

    @FXML
    private CheckMenuItem menuHorizontalFit;

    @FXML
    private CheckMenuItem menuVerticalFit;

    @FXML
    private BorderPane pain;

    @FXML
    private ScrollPane scroll;

    @FXML
    private Label status;
    
    List<String> args = null;
    
    /** イメージ */
    Image image;
    /** CTRLボタン押下状態 */
    boolean onCtrl = false;
    /** 画像のズーム倍率 */
    int zoom = 100;
    /** アプリケーション */
    Main application;
    /** アプリケーションのプライマリステージ */
    Stage primaryStage;

    @FXML
    void onActionCheckMenuItem(ActionEvent event) {
    	boolean isHorizontalFit = this.menuHorizontalFit.isSelected();
    	boolean isVerticalFit = this.menuVerticalFit.isSelected();
    	double horizontalZoom = (this.scroll.getWidth() - 2) / this.image.getWidth();	// 左右のボーダー分2px引く
    	double verticalZoom = (this.scroll.getHeight() - 2) / this.image.getHeight();	// 上下のボーダー分2px引く
		System.out.println("Image size:" + this.image.getWidth() + "*" + this.image.getHeight());
		System.out.println("ScrollPain size:" + this.scroll.getWidth() + "*" + this.scroll.getHeight());
		double z = 1;
    	if(isHorizontalFit && isVerticalFit) {
    		z = Math.min(horizontalZoom, verticalZoom);
    	} else if(isHorizontalFit) {
    		z = horizontalZoom;
    	} else if(isVerticalFit) {
    		z = verticalZoom;
    	}
		double height = this.image.getHeight() * z;
		double width = this.image.getWidth() * z;
    	this.zoom = (int)Math.floor(Math.min(horizontalZoom, verticalZoom) * 100) ;
		this.imageView.setFitHeight(height);
		this.imageView.setFitWidth(width);
		System.out.println("ImageView size:" + this.imageView.getFitWidth() + "*" + this.imageView.getFitHeight());
		
		this.application.setHorizontalFit(isHorizontalFit);
		this.application.setVerticalFit(isVerticalFit);
		System.out.println(this.zoom);
	}

    /**
     * ペインのドラッグ＆ドロップイベント
     * @param event
     */
    @FXML
    void onDragDropped(DragEvent event) {
		Dragboard board = event.getDragboard();
		if (board.hasFiles()) {
			board.getFiles().forEach(file -> {
				this.changeImage(file);
			});
		}
    }
    /**
     * ボタン押下
     * @param event
     */
    @FXML
    void onKeyPressed(KeyEvent event) {
    	System.out.println("onKeyPressed:" + event.getCode());
    	switch(event.getCode()) {
    	case KeyCode.CONTROL:
    		System.out.println("Ctrl on");
    		this.onCtrl = true;
    		break;
    	case KeyCode.LEFT:
    		System.out.println("Left on");
    		break;
    	case KeyCode.RIGHT:
    		System.out.println("Right on");
    		break;
		default:
    	}
    }
    /**
     * ボタンリリース
     * @param event
     */
    @FXML
    void onKeyReleased(KeyEvent event) {
    	System.out.println("onKeyReleased:" + event.getCode());
    	switch(event.getCode()) {
    	case KeyCode.CONTROL:
    		System.out.println("Ctrl off");
    		this.onCtrl = false;
    		break;
    	case KeyCode.LEFT:
    		System.out.println("Left off");
    		this.forwardImage();
    		break;
    	case KeyCode.RIGHT:
    		System.out.println("Right off");
    		this.nextImage();
    		break;
		default:
    	}
    }
    /**
     * キータイプ（念のためボタンリリースと同じアクション）
     * @param event
     */
    @FXML
    void onKeyTyped(KeyEvent event) {
    	System.out.println("onKeyTyped:" + event.getCode());
    	this.onKeyReleased(event);
    }
    /**
     * ホイール転がし
     * @param event
     */
    @FXML
    void onScroll(ScrollEvent event) {
    	System.out.println("onScroll");
    	// CTRL押下時のみズームする。
    	if(this.onCtrl) {
    		System.out.println("onCtrl");
    		// ホイール転がしで event.getDeltaY() == ±40となったのでそれに合わせた実装(1転がし1%)
    		int z = this.zoom + (int)(event.getDeltaY() / 40);
    		// 原寸10%未満は、10%に固定する。
    		double height = this.image.getHeight() * ((double)z / 100);
    		double width = this.image.getWidth() * ((double)z / 100);
    		// 縦横の何れかが10を切る様なサイズ変更はしない。
    		if(height > 10 && width > 10) {
    			this.zoom = z;
        		System.out.println(this.zoom);
    			this.imageView.setFitHeight(height);
    			this.imageView.setFitWidth(width);
    		}
    		
    	}
    }
    /**
     * メニュー File>Openのアクション
     * @param event	イベント
     */
    @FXML
    void onMenuOpen(ActionEvent event) {

    	FileChooser chooser = new FileChooser();
    	chooser.setTitle("ファイル選択");
    	ObservableList<ExtensionFilter> list = chooser.getExtensionFilters();
    	list.addAll(
    			new FileChooser.ExtensionFilter("All", "*.*"),
    			new FileChooser.ExtensionFilter("BMP", "*.bmp"),
    			new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg"),
    			new FileChooser.ExtensionFilter("PNG", "*.png")
			);
    	String defaultDir = this.application.getDefaultDir();
    	if(defaultDir != null && defaultDir.length() > 0) {
    		File initial = new File(defaultDir);
    		if(initial.exists() && initial.isDirectory()) {
    	    	chooser.setInitialDirectory(initial);
    		}
    	}
    	List<File> files = chooser.showOpenMultipleDialog(this.primaryStage);
    	for(File file: files) {
	    	if(file != null) {
	    		this.changeImage(file);
	    		this.application.setDefaultDir(file.getParent()); 
	    	}
    	}
    }
    /**
     * メニュー File>Closeのアクション
     * @param event	イベント
     */
    @FXML
    void onMenuClose(ActionEvent event) {
    	this.primaryStage.close();
    }
    /**
     * 初期化処理
     */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// ペインのドラッグ＆ドロップイベントを設定
		this.pain.setOnDragOver(event -> {
			Dragboard board = event.getDragboard();
			if( board.hasFiles() ) {
				event.acceptTransferModes(TransferMode.ANY);
			}
		});
	}
	/**
	 * 外部からアプリケーションを設定する。
	 * @param	application
	 */
	public void setApplication(Main application) {
		this.application = application;
		this.menuHorizontalFit.setSelected(this.application.getHorizontalFit());
		this.menuVerticalFit.setSelected(this.application.getVerticalFit());
	}
	/**
	 * 外部からプライマリステージを設定する
	 * @param primaryStage
	 */
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	/**
	 * 関連付けで起動した画像を表示する。
	 * @param args
	 */
	public void setArgs(List<String> args) {
		this.args = args;
		if(args != null && args.size() > 0) {
			this.changeImage(new File(args.get(0).trim()));

		}
	}
	/**
	 * 画像が変更された
	 * @param file
	 */
	void changeImage(File file) {
		if(file.exists() && file.isFile()) {
			this.status.setText(file.getAbsolutePath());
			try {
				this.image = new Image(file.toURI().toURL().toString());
				this.zoom = 100;
				this.imageView.setFitHeight(this.image.getHeight());
				this.imageView.setFitWidth(this.image.getWidth());
				this.imageView.setImage(this.image);
				this.imageView.setVisible(true);		
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			this.onActionCheckMenuItem(null);
		}
	}
	void forwardImage() {
		String nowImage = this.status.getText();
		if(nowImage == null || nowImage.length() < 1) {
			return;
		}
		Path image = Paths.get(nowImage);
		Path parent = image.getParent();
		
		try (Stream<Path> stream = Files.list(parent)){
			List<Path> paths = stream.filter(p -> !Files.isDirectory(p))
				.filter(p -> FileUtils.checkExtension(p, "bmp", "jpg", "jpeg", "png"))
				.sorted(Comparator.reverseOrder()).collect(Collectors.toList());
			int index = paths.indexOf(image);
			if (index > -1 && index + 1 < paths.size()) {
				this.changeImage(paths.get(index + 1).toFile());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void nextImage() {
		String nowImage = this.status.getText();
		if(nowImage == null || nowImage.length() < 1) {
			return;
		}
		Path image = Paths.get(nowImage);
		Path parent = image.getParent();
		
		try (Stream<Path> stream = Files.list(parent)){
			List<Path> paths = stream.filter(p -> !Files.isDirectory(p))
				.filter(p -> FileUtils.checkExtension(p, "bmp", "jpg", "jpeg", "png"))
				.sorted(Comparator.naturalOrder()).collect(Collectors.toList());
			int index = paths.indexOf(image);
			if (index > -1 && index + 1 < paths.size()) {
				this.changeImage(paths.get(index + 1).toFile());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
