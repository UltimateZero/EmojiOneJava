package com.uz.emojione.fx;

import com.uz.emojione.Emoji;
import com.uz.emojione.EmojiOne;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Queue;

/**
 * Created by UltimateZero on 9/12/2016.
 */
public class EmojiConversionController {

	@FXML
	private TextArea txtInput;
	@FXML
	private TextFlow flowOutput;


	@FXML
	void initialize() {
		flowOutput.setPadding(new Insets(10));
		txtInput.setFont(Font.font(16));
		txtInput.textProperty().addListener(e-> {
			flowOutput.getChildren().clear();
			String text = txtInput.getText();
			Queue<Object> obs = EmojiOne.getInstance().toEmojiAndText(text);
			while(!obs.isEmpty()) {
				Object ob = obs.poll();
				if(ob instanceof String) {
					addText((String)ob);
				}
				else if(ob instanceof Emoji) {
					Emoji emoji = (Emoji) ob;
					flowOutput.getChildren().add(createEmojiNode(emoji));
				}
			}
		});

		txtInput.setText("Hey :D\n" +
				"is this :pray: working?\n" +
				"Cool. :thumbsup:\n" +
				"See ya later ☺");
	}

	private Node createEmojiNode(Emoji emoji) {
		StackPane stackPane = new StackPane();
		stackPane.setPadding(new Insets(3));
		ImageView imageView = new ImageView();
		imageView.setFitWidth(32);
		imageView.setFitHeight(32);
		imageView.setImage(ImageCache.getInstance().getImage(getEmojiImagePath(emoji.getHex())));
		stackPane.getChildren().add(imageView);

		Tooltip tooltip = new Tooltip(emoji.getShortname());
		Tooltip.install(stackPane, tooltip);
		stackPane.setCursor(Cursor.HAND);
		stackPane.setOnMouseEntered(e-> {
			stackPane.setStyle("-fx-background-color: #a6a6a6; -fx-background-radius: 3;");
		});
		stackPane.setOnMouseExited(e-> {
			stackPane.setStyle("");
		});
		return stackPane;
	}

	private String getEmojiImagePath(String hexStr) {
		return getClass().getResource("png_40/" + hexStr + ".png").toExternalForm();
	}

	private void addText(String text) {
		Text textNode = new Text(text);
		textNode.setFont(Font.font(16));
		flowOutput.getChildren().add(textNode);
	}
}
