package application;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

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

public class MainController implements Initializable {

    @FXML
    private ImageView imageView;

    @FXML
    private BorderPane pain;

    @FXML
    private ScrollPane scroll;

    @FXML
    private Label status;

    Image image;
    boolean onCtrl = false;
    int zoom = 100;
    
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
    
    @FXML
    void onKeyPressed(KeyEvent event) {
    	System.out.println("onKeyPressed:" + event.getCode());
    	switch(event.getCode()) {
    	case KeyCode.CONTROL:
    		this.onCtrl = true;
    		break;
		default:
    	}
    }

    @FXML
    void onKeyReleased(KeyEvent event) {
    	System.out.println("onKeyReleased:" + event.getCode());
    	switch(event.getCode()) {
    	case KeyCode.CONTROL:
    		this.onCtrl = false;
    		break;
		default:
    	}
    }

    @FXML
    void onKeyTyped(KeyEvent event) {
    	System.out.println("onKeyTyped:" + event.getCode());
    	switch(event.getCode()) {
    	case KeyCode.CONTROL:
    		this.onCtrl = false;
    		break;
		default:
    	}
    }

    @FXML
    void onScroll(ScrollEvent event) {
    	System.out.println("onScroll");
    	if(this.onCtrl) {
    		this.zoom = this.zoom - (int)(event.getDeltaY() / 40 * 5);
    		if(this.zoom < 10) this.zoom = 10;
    		System.out.println(this.zoom);
			this.imageView.setFitHeight(this.image.getHeight() * ((double)this.zoom / 100));
			this.imageView.setFitWidth(this.image.getWidth() * ((double)this.zoom / 100));
    	}
    }

    @FXML
    void onScrollFinished(ScrollEvent event) {
    	System.out.println("onScrollFinished");
    }

    @FXML
    void onScrollStarted(ScrollEvent event) {
    	System.out.println("onScrollStarted");
    }


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		this.pain.setOnDragOver(event -> {
			Dragboard board = event.getDragboard();
			if( board.hasFiles() ) {
				event.acceptTransferModes(TransferMode.ANY);
			}
		});
	}
}
