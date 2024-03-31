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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import javafx.scene.control.Hyperlink;
import org.bson.Document;
public class LoginPage extends Application {
    private MongoDBConnection mongoDBConnection;
    private MongoCollection<Document> usersCollection;
    @Override
    public void start(Stage primaryStage) {
        connectToMongoDB();
        primaryStage.setTitle("Coffee Vending System - Login");
        Label titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #8B4513;");
        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #4CAF50;");
        TextField usernameField = new TextField();
        usernameField.setStyle("-fx-font-size: 16px; -fx-background-color: rgba(255, 255, 255, 0.5);");
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #4CAF50;");
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-font-size: 16px; -fx-background-color: rgba(255, 255, 255, 0.5);");
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        loginButton.setOnAction(event -> handleLogin(primaryStage,usernameField.getText(), passwordField.getText()));       
        Hyperlink goToSignUpLink = new Hyperlink("Don't have an account? Sign Up");
        goToSignUpLink.setStyle("-fx-font-size: 14;");
        goToSignUpLink.setOnAction(event -> showSignupPage(primaryStage));
        VBox loginLayout = new VBox(10, new Label("Username:"), usernameField, new Label("Password:"), passwordField, loginButton,goToSignUpLink);
        loginLayout.setPadding(new Insets(20));
        loginLayout.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-padding: 20; -fx-background-radius: 10;");
        VBox fullLoginPage = new VBox(20, titleLabel, loginLayout);
        fullLoginPage.setAlignment(Pos.CENTER);
        fullLoginPage.getStyleClass().add("main-background");
        Image backgroundImage = new Image("/coffeevendingsystem/image1.jpg");
        StackPane stackPane = new StackPane(new javafx.scene.image.ImageView(backgroundImage), fullLoginPage);
        Scene scene = new Scene(stackPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }  
    private void showSignupPage(Stage primaryStage) {
        SignupPage signupPage = new SignupPage();
        signupPage.start(primaryStage);
    }
    private void connectToMongoDB() {
        mongoDBConnection = new MongoDBConnection();
        usersCollection = mongoDBConnection.getDatabase().getCollection("users");
    }  
    private void handleLogin(Stage primaryStage,String username, String password) {
        Document query = new Document("username", username).append("password", password);
        MongoCursor<Document> cursor = usersCollection.find(query).iterator();

        if (cursor.hasNext()) {
        System.out.println("Login successful for user: " + username);
        User currentUser = new User(username);
        showMainPage(primaryStage, currentUser, mongoDBConnection.getDatabase());
    } else {
        System.out.println("Login failed. Invalid credentials.");
    }

        cursor.close();
    }    
private void showMainPage(Stage primaryStage, User currentUser,MongoDatabase database) {
    MainPage dashboard = new MainPage(currentUser, database);
    dashboard.start(primaryStage);
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