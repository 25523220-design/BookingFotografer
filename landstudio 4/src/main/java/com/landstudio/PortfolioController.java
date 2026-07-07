package com.landstudio;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PortfolioController {

    @FXML private ScrollPane rootScroll;
    @FXML private VBox pageBox;
    @FXML private Label userNameLabel;
    @FXML private StackPane cardPernikahan;
    @FXML private StackPane cardPreWedding;
    @FXML private StackPane cardWisuda;
    @FXML private HBox cardFasilitas;

    @FXML
    private void initialize() {
        HeaderUtil.bindUserLabel(userNameLabel);
        pageBox.minHeightProperty().bind(rootScroll.heightProperty());
        rootScroll.addEventFilter(ScrollEvent.SCROLL, this::handlePageScroll);
    }

    private void handlePageScroll(ScrollEvent event) {
        double contentHeight = pageBox.getBoundsInLocal().getHeight();
        double viewportHeight = rootScroll.getViewportBounds().getHeight();
        double overflow = contentHeight - viewportHeight;
        if (overflow <= 0) {
            return;
        }
        double newVvalue = rootScroll.getVvalue() - event.getDeltaY() / overflow;
        rootScroll.setVvalue(Math.max(0, Math.min(1, newVvalue)));
        event.consume();
    }

    @FXML
    private void goToBeranda() {
        SceneManager.switchTo("MainView.fxml");
    }

    @FXML
    private void goToBooking() {
        if (!Session.requireLogin("BookingView.fxml")) return;
        SceneManager.switchTo("BookingView.fxml");
    }

    @FXML
    private void goToPaket() {
        SceneManager.switchTo("PaketView.fxml");
    }

    public void bindResponsiveSizing(Scene scene) {
        NumberBinding topRowHeight = Bindings.createDoubleBinding(
                () -> clamp(scene.getHeight() * 0.34, 240, 480),
                scene.heightProperty());
        cardPernikahan.prefHeightProperty().bind(topRowHeight);
        cardPreWedding.prefHeightProperty().bind(topRowHeight);

        NumberBinding bottomRowHeight = Bindings.createDoubleBinding(
                () -> clamp(scene.getHeight() * 0.30, 220, 420),
                scene.heightProperty());
        cardWisuda.prefHeightProperty().bind(bottomRowHeight);
        cardFasilitas.prefHeightProperty().bind(bottomRowHeight);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
