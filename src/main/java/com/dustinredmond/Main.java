package com.dustinredmond;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("BCrypt Hash Generator");
        stage.getIcons().add(new Image("icons8-lock-64.png"));
        BorderPane root = new BorderPane();
        root.setTop(this.getMenu());
        GridPane grid = new GridPane();
        root.setCenter(grid);
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setPrefSize(500, 150);

        int rowIndex = 0;

        Text text = new Text("Generate Hash");
        text.setUnderline(true);
        grid.add(text, 0, rowIndex++);

        PasswordField pf = new PasswordField();
        GridPane.setHgrow(pf, Priority.ALWAYS);
        grid.add(new Label("Password:"), 0, rowIndex);
        grid.add(pf, 1, rowIndex++);

        PasswordField pf2 = new PasswordField();
        grid.add(new Label("Confirm Password:"), 0, rowIndex);
        grid.add(pf2, 1, rowIndex++);

        TextField tfRounds = new TextField("10");
        tfRounds.setTooltip(new Tooltip("The number of hashing rounds to apply. " +
                "Additional rounds may take a long time."));
        tfRounds.setMaxWidth(35);
        grid.add(new Label("Hashing Rounds:"), 0, rowIndex);
        grid.add(tfRounds, 1, rowIndex++);

        Button buttonSubmit = new Button("Generate");
        grid.add(buttonSubmit, 0, rowIndex++);

        grid.add(new Label("Encrypted Password:"), 0, rowIndex);
        TextField result = new TextField();
        result.setEditable(false);
        grid.add(result, 1, rowIndex++);
        buttonSubmit.setOnAction(e -> validateAndDisplay(pf.getText(), pf2.getText(), result, tfRounds.getText()));
        grid.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                validateAndDisplay(pf.getText(), pf2.getText(), result, tfRounds.getText());
            }
        });

        Button buttonCopy = new Button("Copy Password");
        grid.add(buttonCopy, 0, rowIndex++);
        buttonCopy.setOnAction(e -> {
            if (!(pf.getText().isEmpty() || pf2.getText().isEmpty())) {
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(result.getText());
                clipboard.setContent(content);
            }
        });

        grid.add(new Separator(Orientation.HORIZONTAL), 0, rowIndex++, 2, 1);

        Text textMatch = new Text("Check Match");
        textMatch.setUnderline(true);
        grid.add(textMatch, 0, rowIndex++);

        TextField textFieldPlainText = new TextField();
        grid.add(new Label("Plain text:"), 0, rowIndex);
        grid.add(textFieldPlainText, 1, rowIndex++);

        TextField textFieldHashed = new TextField();
        grid.add(new Label("Hashed text:"), 0, rowIndex);
        grid.add(textFieldHashed, 1, rowIndex++);

        Button buttonMatch = new Button("Check Match");
        grid.add(buttonMatch, 0, rowIndex++);

        Label labelMatch = new Label("Match: ");
        grid.add(labelMatch, 0, rowIndex);
        Text textMatchResult = new Text();
        grid.add(textMatchResult, 1, rowIndex);

        buttonMatch.setOnAction(e -> {
            if (textFieldPlainText.getText().isEmpty() || textFieldHashed.getText().isEmpty()) {
                showAlert("Please fill out both password fields.");
                return;
            }

            if (BCrypt.checkpw(textFieldPlainText.getText(), textFieldHashed.getText())) {
                textMatchResult.setFill(Paint.valueOf("GREEN"));
                textMatchResult.setText("MATCH");
            } else {
                textMatchResult.setFill(Paint.valueOf("RED"));
                textMatchResult.setText("NO MATCH");

            }
        });

        stage.setScene(new Scene(root));
        stage.show();
    }

    private void validateAndDisplay(String pass, String confirm, TextField result, String rounds) {
        if (pass == null || confirm == null || pass.isEmpty() || confirm.isEmpty()) {
            showAlert("Please fill out all fields.");
            return;
        }
        if (!pass.equals(confirm)){
            showAlert("Passwords do not match.");
            return;
        }
        if (rounds == null || rounds.isEmpty()) {
            showAlert("You must specify a number of hashing rounds, the default is 10.");
            return;
        }

        int numRounds;

        try {
            numRounds = Integer.parseInt(rounds);
        } catch (Exception e) {
            showAlert("Please enter a whole number for the number of hashing rounds.");
            return;
        }

        if (0 >= numRounds || 30 < numRounds) {
            showAlert("The number of hashing rounds must be a value between 1 and 30");
        }

        String hashedPass = BCrypt.hashpw(pass, BCrypt.gensalt(numRounds));
        // Not wrapping in runLater call causes crash on some *nix systems when 
        // large number of hashing rounds are used, not a known bug, so better to
        // be safe than sorry.
        //
        // EDIT: I actually can't reproduce this crash now, maybe it was fault
        // of hardware or faulty Java install? Leaving runLater, it won't hurt
        Platform.runLater(() -> result.setText(hashedPass));
    }


    private void showAlert(String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR, contentText);
        Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image("icons8-lock-64.png"));
        alert.showAndWait();
    }

    private MenuBar getMenu() {
        MenuBar mb = new MenuBar();
        Menu menuAbout = new Menu("About");
        MenuItem miAbout = new MenuItem("About this program");
        menuAbout.getItems().add(miAbout);
        mb.getMenus().add(menuAbout);
        miAbout.setOnAction(e -> {
            Stage stage = new Stage();
            stage.setTitle("BCrypt Generator");
            stage.getIcons().add(new Image("icons8-lock-64.png"));
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10));
            grid.setHgap(5);
            grid.setVgap(10);
            Text textLicense = new Text("    BCrypt Hash Generator: Generate and validate BCrypt hashes" +
                    "\n"+
                    "    Copyright (C) 2020  Dustin K. Redmond\n" +
                    "\n" +
                    "    This program is free software: you can redistribute it and/or modify\n" +
                    "    it under the terms of the GNU General Public License as published by\n" +
                    "    the Free Software Foundation, either version 3 of the License, or\n" +
                    "    (at your option) any later version.\n" +
                    "\n" +
                    "    This program is distributed in the hope that it will be useful,\n" +
                    "    but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
                    "    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
                    "    GNU General Public License for more details.\n" +
                    "\n" +
                    "    You should have received a copy of the GNU General Public License\n" +
                    "    along with this program.  If not, see <https://www.gnu.org/licenses/>.");
            grid.add(textLicense, 0, 0);

            Text textAttributions = new Text("    jBcrypt Copyright (C) 2006 Damien Miller <djm@mindrot.org>\n\n" +
                    "    Icons provided by Icons8 <https://icons8.com/> (https://icons8.com/icons/set/lock-2)");
            grid.add(textAttributions, 0, 1);
            stage.setScene(new Scene(grid));
            stage.show();
        });
        return mb;
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            for (String arg: args) {
                System.out.println(BCrypt.hashpw(arg, BCrypt.gensalt()));
            }
        } else {
            Application.launch(args);
        }
    }
}
