package com.kensoftph.javafxmedia;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MediaPlayerController {

    @FXML
    private BorderPane mainPane;

    @FXML
    private Button btnPlay;

    @FXML
    private Button copyTimeBtn;

    @FXML
    private Label lblDuration;

    @FXML
    private Label fileNameLabel;

    @FXML
    private MediaView mediaView;
    @FXML
    private Slider slider;

    private Media media;
    private MediaPlayer mediaPlayer;

    private boolean isPlayed = false;

    private Duration sliderDuration;

    @FXML
    void onPress(KeyEvent ev) {
        final KeyCombination keyComboCtrlC = new KeyCodeCombination(
                KeyCode.C, KeyCombination.CONTROL_DOWN);
        KeyCode kc = ev.getCode();
        if (keyComboCtrlC.match(ev)) {
            copyTime();
        }
        else if (kc == KeyCode.C) {
            slider.setBlockIncrement(10);
            slider.increment();
            mediaPlayer.seek(Duration.seconds(slider.getValue()));
        }
        else if (kc == KeyCode.Z) {
            slider.setBlockIncrement(5);
            slider.decrement();
            mediaPlayer.seek(Duration.seconds(slider.getValue()));
        }
        else if (kc == KeyCode.D) {
            slider.setBlockIncrement(60);
            slider.increment();
            mediaPlayer.seek(Duration.seconds(slider.getValue()));
        }
        else if (kc == KeyCode.A) {
            slider.setBlockIncrement(30);
            slider.decrement();
            mediaPlayer.seek(Duration.seconds(slider.getValue()));
        }
    }

    @FXML
    void btnPlay(MouseEvent event) {
        if(!isPlayed){
            btnPlay.setText("Pause");
            mediaPlayer.play();
            isPlayed = true;
        }else {
            btnPlay.setText("Play");
            mediaPlayer.pause();
            isPlayed = false;
        }
    }

    @FXML
    void btnStop(MouseEvent event) {
        btnPlay.setText("Play");
        mediaPlayer.stop();
        isPlayed = false;
    }

    @FXML
    private void sliderPressed(MouseEvent event){
        mediaPlayer.seek(Duration.seconds(slider.getValue()));
    }

    @FXML
    void fileDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        String url = db.getFiles().get(0).toURI().toString();
        fileNameLabel.setText(url);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        media = new Media(url);
        mediaPlayer = new MediaPlayer(media);

        mediaView.setMediaPlayer(mediaPlayer);
        slider.setValue(0);

        mediaPlayer.currentTimeProperty().addListener(((observableValue, oldValue, newValue) -> {
            sliderDuration = newValue;
            slider.setValue(newValue.toSeconds());
            lblDuration.setText("Duration: " + formatDuration(newValue) + " / " + formatDuration(media.getDuration()));
        }));

        mediaPlayer.setOnReady(() ->{
            Duration totalDuration = media.getDuration();
            slider.setMax(totalDuration.toSeconds());
            lblDuration.setText("Duration: 00 / " + (int)media.getDuration().toSeconds());
        });

        Scene scene = mediaView.getScene();
        mediaView.fitWidthProperty().bind(scene.widthProperty());
        mediaView.fitHeightProperty().bind(scene.heightProperty());

        mediaPlayer.setAutoPlay(true);
    }

    @FXML
    void fileDragOver(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if(dragboard.hasImage() || dragboard.hasFiles()){
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    @FXML
    void copyTime(MouseEvent event) {
        copyTime();
    }

    private void copyTime() {
        String theTime = String.valueOf(formatDurationAsInt(sliderDuration));
        System.out.println("CopyTime: " + theTime);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(theTime);
        clipboard.setContents(selection, null);
    }

    public static String formatDuration(Duration duration) {
        LocalTime time = LocalTime.MIDNIGHT.plus(convertToJavaTimeDuration(duration));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return formatter.format(time);
    }

    public static int formatDurationAsInt(Duration duration) {
        LocalTime time = LocalTime.MIDNIGHT.plus(convertToJavaTimeDuration(duration));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");
        return Integer.parseInt(formatter.format(time));
    }

    public static java.time.Duration convertToJavaTimeDuration(javafx.util.Duration fxDuration) {
        return java.time.Duration.ofMillis((long) fxDuration.toMillis());
    }

}
