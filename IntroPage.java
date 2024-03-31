package coffeevendingsystem;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
public class IntroPage extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Coffee Vending System ");
        Image introImage = new Image("/coffeevendingsystem/image1.jpg");
        ImageView introImageView = new ImageView(introImage);
        Label welcomeLabel = new Label("Experience the Aroma of");
        welcomeLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #8B4513;"); 
        Label systemLabel = new Label("Coffee Vending Bliss");
        systemLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #008080;");
        Label journeyLabel = new Label("Embark on a Journey of Flavorful Delight");
        journeyLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FF4500;");
        Label explorationLabel = new Label("Where your coffee cravings meet endless possibilities. ☕");
        explorationLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: violet"); 
        Button exploreButton = new Button("Explore Coffee Menu");
        exploreButton.setStyle("-fx-font-size: 18px; -fx-background-color: #4682B4; -fx-text-fill: #FFFFFF;");
        VBox introLayout = new VBox(20, welcomeLabel, systemLabel, journeyLabel, explorationLabel, exploreButton);
        introLayout.setAlignment(Pos.CENTER);
        introLayout.setStyle("-fx-background-color: transparent; -fx-padding: 40px;");
        addFadeTransition(welcomeLabel);
        addTranslateTransition(systemLabel);
        addScaleTransition(journeyLabel);
        addTranslateTransition(explorationLabel);
        addFadeTransition(exploreButton);
        exploreButton.setOnAction(event -> showWelcomePage(primaryStage));
        StackPane stackPane = new StackPane(introImageView, introLayout);
        Scene introScene = new Scene(stackPane, 800, 600);
        primaryStage.setScene(introScene);
        primaryStage.show();
    }
    private void showWelcomePage(Stage primaryStage) {
        WelcomePage welcome = new WelcomePage();
        welcome.start(primaryStage);
    }   
   private void addFadeTransition(javafx.scene.Node node) {
    FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), node);
    fadeTransition.setFromValue(0);
    fadeTransition.setToValue(1);
    fadeTransition.play();
}
private void addTranslateTransition(javafx.scene.Node node) {
    TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1), node);
    translateTransition.setFromY(-50);
    translateTransition.setToY(0);
    translateTransition.play();
}
private void addScaleTransition(javafx.scene.Node node) {
    ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), node);
    scaleTransition.setFromX(0);
    scaleTransition.setToX(1);
    scaleTransition.setFromY(0);
    scaleTransition.setToY(1);
    scaleTransition.play();
}
    public static void main(String[] args) {
        launch(args);
    }
}