package geek.gui;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Displays one chat message with the speaker's avatar.
 */
public class DialogBox extends HBox {
    private static final String DIALOG_BOX_FXML =
            "/view/DialogBox.fxml";

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        FXMLLoader loader = new FXMLLoader(
                DialogBox.class.getResource(DIALOG_BOX_FXML)
        );
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to load the dialog-box layout.",
                    e
            );
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Creates a right-aligned dialog for a user message.
     *
     * @param text Message text.
     * @param image User avatar.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(
            String text,
            Image image
    ) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for a Geek response.
     *
     * @param text Response text.
     * @param image Geek avatar.
     * @return Geek dialog box.
     */
    public static DialogBox getGeekDialog(
            String text,
            Image image
    ) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.getStyleClass().add("geek-dialog");
        return dialogBox;
    }

    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> children =
                FXCollections.observableArrayList(getChildren());
        FXCollections.reverse(children);
        getChildren().setAll(children);
    }
}
