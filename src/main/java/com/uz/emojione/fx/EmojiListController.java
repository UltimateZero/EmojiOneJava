package com.uz.emojione.fx;

import com.uz.emojione.Emoji;
import com.uz.emojione.EmojiOne;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.Map;


/**
 * Created by UltimateZero on 9/12/2016.
 */
public class EmojiListController {

	@FXML
	private ScrollPane searchScrollPane;
	@FXML
	private FlowPane searchFlowPane;
	@FXML
	private TabPane tabPane;
	@FXML
	private TextField txtSearch;
	@FXML
	private ComboBox<Image> boxTone;

	@FXML
	void initialize() {
		ObservableList<Image> tonesList = FXCollections.observableArrayList();

		for(int i = 1; i <= 5; i++) {
			Emoji emoji = EmojiOne.getInstance().getEmoji(":thumbsup_tone"+i+":");
			Image image = ImageCache.getInstance().getImage(getEmojiImagePath(emoji.getHex()));
			tonesList.add(image);
		}
		Emoji em = EmojiOne.getInstance().getEmoji(":thumbsup:"); //default tone
		Image image = ImageCache.getInstance().getImage(getEmojiImagePath(em.getHex()));
		tonesList.add(image);
		boxTone.setItems(tonesList);
		boxTone.setCellFactory(e->new ToneCell());
		boxTone.setButtonCell(new ToneCell());
		boxTone.getSelectionModel().selectedItemProperty().addListener(e->refreshTabs());


		searchScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		searchFlowPane.prefWidthProperty().bind(searchScrollPane.widthProperty().subtract(5));
		searchFlowPane.setHgap(5);
		searchFlowPane.setVgap(5);

		txtSearch.textProperty().addListener(x-> {
			String text = txtSearch.getText();
			if(text.isEmpty() || text.length() < 2) {
				searchFlowPane.getChildren().clear();
				searchScrollPane.setVisible(false);
			} else {
				searchScrollPane.setVisible(true);
				List<Emoji> results = EmojiOne.getInstance().search(text);
				searchFlowPane.getChildren().clear();
				results.forEach(emoji ->searchFlowPane.getChildren().add(createEmojiNode(emoji)));
			}
		});

		boxTone.getSelectionModel().select(0);
		tabPane.getSelectionModel().select(1);
	}

	private void refreshTabs() {
		Map<String, List<Emoji>> map = EmojiOne.getInstance().getCategorizedEmojis(boxTone.getSelectionModel().getSelectedIndex()+1);
		for(Tab tab : tabPane.getTabs()) {
			ScrollPane scrollPane = (ScrollPane) tab.getContent();
			FlowPane pane = (FlowPane) scrollPane.getContent();
			scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
			pane.prefWidthProperty().unbind();
			pane.prefWidthProperty().bind(scrollPane.widthProperty().subtract(5));
			pane.getChildren().clear();
			pane.setHgap(5);
			pane.setVgap(5);
			String category = tab.getText().toLowerCase();
			if(map.get(category) == null) continue;
			map.get(category).forEach(emoji -> pane.getChildren().add(createEmojiNode(emoji)));
		}
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
			if(txtSearch.getText().isEmpty())
				txtSearch.setPromptText(emoji.getShortname());
		});
		stackPane.setOnMouseExited(e-> {
			stackPane.setStyle("");
		});
		return stackPane;
	}

	private String getEmojiImagePath(String hexStr) {
		return Emoji.class.getResource("png_40/" + hexStr + ".png").toExternalForm();
	}

	class ToneCell extends ListCell<Image> {
		private final ImageView imageView;
		public ToneCell() {
			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			imageView = new ImageView();
			imageView.setFitWidth(20);
			imageView.setFitHeight(20);
		}
		@Override
		protected void updateItem(Image item, boolean empty) {
			super.updateItem(item, empty);

			if(item == null || empty) {
				setText(null);
				setGraphic(null);
			} else {
				imageView.setImage(item);
				setGraphic(imageView);
			}
		}
	}
}
