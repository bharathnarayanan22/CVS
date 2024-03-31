package coffeevendingsystem;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import com.mongodb.client.MongoCollection;
import javafx.scene.control.Hyperlink;
import org.bson.Document;
public class SignupPage extends Application {
    private MongoDBConnection mongoDBConnection;
    private MongoCollection<Document> usersCollection;
    @Override
    public void start(Stage primaryStage) {
        connectToMongoDB();
        primaryStage.setTitle("Coffee Vending System - Signup");
        Label titleLabel = new Label("Sign Up");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #8B4513;");
        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #4CAF50;");
        TextField usernameField = new TextField();
        usernameField.setStyle("-fx-font-size: 16px; -fx-background-color: rgba(255, 255, 255, 0.5);");
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #4CAF50;");
        TextField emailField = new TextField();
        emailField.setStyle("-fx-font-size: 16px; -fx-background-color: rgba(255, 255, 255, 0.5);");
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #4CAF50;");
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-font-size: 16px; -fx-background-color: rgba(255, 255, 255, 0.5);");
        Button signupButton = new Button("Sign Up");
        signupButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        signupButton.setOnAction(event -> handleSignup(primaryStage, usernameField.getText(), emailField.getText(), passwordField.getText()));
        Hyperlink goToLoginLink = new Hyperlink("Already have an account? Login");
        goToLoginLink.setStyle("-fx-font-size: 14;");
        goToLoginLink.setOnAction(event -> showLoginPage(primaryStage));
        VBox SignupLayout = new VBox(10,titleLabel, usernameLabel,usernameField,new Label("Email_ID:"), emailField, new Label("Password:"), passwordField,signupButton,goToLoginLink);
        SignupLayout.setPadding(new Insets(20));
        SignupLayout.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-padding: 20; -fx-background-radius: 10;");
        VBox fullsignupPage = new VBox(20, SignupLayout);
        fullsignupPage.setAlignment(Pos.CENTER);
        fullsignupPage.getStyleClass().add("main-background");   
        Image backgroundImage = new Image("/coffeevendingsystem/image1.jpg");
        StackPane stackPane = new StackPane(new javafx.scene.image.ImageView(backgroundImage), fullsignupPage);
        Scene scene = new Scene(stackPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }  
    private void showLoginPage(Stage primaryStage) {
        LoginPage loginPage = new LoginPage();
        loginPage.start(primaryStage);
    }
    private void connectToMongoDB() {
        mongoDBConnection = new MongoDBConnection();
        usersCollection = mongoDBConnection.getDatabase().getCollection("users");
    }
    private void handleSignup(Stage primaryStage, String username, String email, String password) {
        Document userDocument = new Document("username", username).append("email", email).append("password", password);
        usersCollection.insertOne(userDocument);
        System.out.println("Signup: Username - " + username + ", Email - " + email + ", Password - " + password);
        showLoginPage(primaryStage);
    }
    @Override
    public void stop() {
        if (mongoDBConnection != null) {
            mongoDBConnection.closeConnection();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}