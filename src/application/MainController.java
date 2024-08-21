package application;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import javafx.stage.Stage;

public class MainController implements Initializable {

    @FXML
    private ImageView imageView;

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
    /** アプリケーションのプライマリステージ */
    Stage primaryStage;
    
    /**
     * ペインのドラッグ＆ドロップイベント
     * @param event
     */
    @FXML
    void onDragDropped(DragEvent event) {
		Dragboard board = event.getDragboard();
		if (board.hasFiles()) {
			board.getFiles().forEach(file -> {
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
			File file = new File(args.get(0).trim());
			if(file.exists()) {
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
			}
		}
	}
}
