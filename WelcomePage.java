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
public class WelcomePage extends Application {  
    @Override
    public void start(Stage primaryStage) {     
    primaryStage.setTitle("Welcome to Coffee Vending Bliss");
        Image welcomeImage = new Image("/coffeevendingsystem/image1.jpg");
        ImageView welcomeImageView = new ImageView(welcomeImage);
        Label welcomeLabel = new Label("Ready to indulge in the world of coffee delights?");
        welcomeLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #8B4513;");
        Label introLabel = new Label("Join us and discover a universe of coffee creations waiting for you to explore. ☕");
        introLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: normal; -fx-text-fill: #696969;");
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-font-size: 18px; -fx-background-color: #8B4513; -fx-text-fill: #FFFFFF; -fx-padding: 10px 20px; -fx-border-radius: 5px;");
        loginButton.setOnAction(event -> showLoginPage(primaryStage));
        Button signUpButton = new Button("Sign Up");
        signUpButton.setStyle("-fx-font-size: 18px; -fx-background-color: #8B4513; -fx-text-fill: #FFFFFF; -fx-padding: 10px 20px; -fx-border-radius: 5px;");
        signUpButton.setOnAction(event -> showSignupPage(primaryStage));
        VBox welcomeLayout = new VBox(20, welcomeLabel, introLabel, loginButton, signUpButton);
        welcomeLayout.setAlignment(Pos.CENTER);
        welcomeLayout.setStyle("-fx-background-color: transparent; -fx-padding: 40px;");
        addFadeTransition(welcomeLabel);
        addTranslateTransition(introLabel);
        addScaleTransition(loginButton);
        addTranslateTransition(signUpButton);
    StackPane stackPane = new StackPane(welcomeImageView, welcomeLayout);
    Scene scene = new Scene(stackPane, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.show();
    }
    private void showLoginPage(Stage primaryStage) {
        LoginPage loginPage = new LoginPage();
        loginPage.start(primaryStage);
    }   
    private void showSignupPage(Stage primaryStage) {
        SignupPage signupPage = new SignupPage();
        signupPage.start(primaryStage);
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