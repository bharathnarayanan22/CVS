package coffeevendingsystem;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.bson.Document;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
public class MainPage extends Application {
    private User currentUser; 
    private List<Feedback> feedbackList = new ArrayList<>();
    private MongoCollection<Document> feedbackCollection;
    private MongoCollection<Document> coffeeItemsCollection;
    private MongoDatabase database;
    private MongoDBConnection mongoDBConnection;
    public MainPage(User currentUser, MongoDatabase database) {
    this.currentUser = currentUser;
    this.database = database;
    this.mongoDBConnection = new MongoDBConnection(); 
    this.feedbackCollection = database.getCollection("feedback");
}
    public static void main(String[] args) {
        launch(args);
    }   
    private void showAdminDashboard() {
        AdminDashboard adminDashboard = new AdminDashboard(coffeeItemsCollection);
        adminDashboard.showAndWait();
    }
    private void showAdminLoginDialog() {
        Stage loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.initStyle(StageStyle.UTILITY);
        loginStage.setTitle("Admin Login");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (isAdmin(username, password)) {
                showAdminDashboard();
                loginStage.close();
            } else {
                showAlert("Invalid credentials", "Please enter valid admin credentials.");
            }
        });
        VBox loginLayout = new VBox(20, usernameField, passwordField, loginButton);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setPadding(new Insets(20));
        Scene loginScene = new Scene(loginLayout, 300, 200);
        loginStage.setScene(loginScene);
        loginStage.showAndWait();
    }
    private boolean isAdmin(String username, String password) {
        return username.equals("Admin") && password.equals("admin");
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Main Page");
        Image backgroundImage = new Image("/coffeevendingsystem/image1.jpg");
        BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
        );
        Background backgroundLayout = new Background(background);
        Label titleLabel = new Label("Main Page");
        titleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #1E88E5;");
        Button buyCoffeeButton = new Button("Buy Coffee");
        buyCoffeeButton.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white; -fx-font-size: 18px;"); 
        buyCoffeeButton.setOnAction(event -> showCoffeeItemsDashboard(primaryStage,currentUser,database));
        Button adminLoginButton = new Button("Admin Login");
        adminLoginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px;"); 
        adminLoginButton.setOnAction(event -> showAdminLoginDialog());
        Button settingsButton = new Button("Settings");
        settingsButton.setStyle("-fx-background-color: #607D8B; -fx-text-fill: white; -fx-font-size: 18px;"); 
        settingsButton.setOnAction(event -> showSettings(primaryStage));
        VBox mainPageLayout = new VBox(20, titleLabel, buyCoffeeButton, adminLoginButton, settingsButton);
        mainPageLayout.setAlignment(Pos.CENTER);
        mainPageLayout.setPadding(new Insets(20));
        mainPageLayout.setBackground(backgroundLayout);
        Scene mainPageScene = new Scene(mainPageLayout, 800, 600);
        primaryStage.setScene(mainPageScene);
        primaryStage.show();
    }
    private void showCoffeeItemsDashboard(Stage primaryStage, User currentUser, MongoDatabase database) {
        CoffeeItemsDashboard coffeeItemsDashboard = new CoffeeItemsDashboard(currentUser, database);
        coffeeItemsDashboard.start(primaryStage);
    }
    private void showSettings(Stage primaryStage) {
        Stage settingsStage = new Stage();
        Image backgroundImage = new Image("/coffeevendingsystem/image1.jpg");
        BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
        );
        Background backgroundLayout = new Background(background);
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.initStyle(StageStyle.UTILITY);
        settingsStage.setTitle("Settings");
        Button feedbackButton = new Button("Feedback");
        feedbackButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        feedbackButton.setOnAction(event -> showFeedbackDialog());
        Button historyButton = new Button("History");
        historyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        historyButton.setOnAction(event -> showHistory());
        Button logoutButton = new Button("Log Out");
        logoutButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        logoutButton.setOnAction(event -> {
            showWelcomePage(primaryStage);
            settingsStage.close();
        });
        VBox settingsLayout = new VBox(20, feedbackButton, historyButton, logoutButton);
        settingsLayout.setAlignment(Pos.CENTER);
        settingsLayout.setPadding(new Insets(20));
        settingsLayout.setBackground(backgroundLayout);
        Scene settingsScene = new Scene(settingsLayout, 300, 200);
        settingsStage.setScene(settingsScene);
        settingsStage.showAndWait();
    }
    private void showAlert(String title, String content) {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
}

    private void addFadeTransition(Label welcomeLabel) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void addTranslateTransition(Label systemLabel) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void addScaleTransition(Label journeyLabel) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
  
    private class Feedback {
        private String user;
        private String feedback;
        public Feedback(String user, String feedback, String formattedTimestamp) {
            this.user = user;
            this.feedback = feedback;
        }
        public String getUser() {
            return user;
        }
        public String getFeedback() {
            return feedback;
        }
    }
    private void showFeedbackDialog() {
        Stage feedbackStage = new Stage();
        feedbackStage.initModality(Modality.APPLICATION_MODAL);
        feedbackStage.initStyle(StageStyle.UTILITY);
        feedbackStage.setTitle("Feedback");
        TextArea feedbackTextArea = new TextArea();
        feedbackTextArea.setPromptText("Enter your feedback");
        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        submitButton.setOnAction(event -> {
            String feedbackText = feedbackTextArea.getText();
            if (!feedbackText.isEmpty()) {
                submitFeedback(feedbackText);
                feedbackStage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Empty Feedback");
                alert.setHeaderText(null);
                alert.setContentText("Please enter your feedback before submitting.");
                alert.showAndWait();
            }
        });
        VBox feedbackLayout = new VBox(20, feedbackTextArea, submitButton);
        feedbackLayout.setAlignment(Pos.CENTER);
        feedbackLayout.setPadding(new Insets(20));
        Scene feedbackScene = new Scene(feedbackLayout, 300, 200);
        feedbackStage.setScene(feedbackScene);
        feedbackStage.showAndWait();
    }
    private void submitFeedback(String feedbackText) {
        if (currentUser != null) {
            LocalDateTime timestamp = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTimestamp = timestamp.format(formatter);

            Feedback feedback = new Feedback(currentUser.getUsername(), feedbackText, formattedTimestamp);
            feedbackList.add(feedback);
            Document feedbackDocument = new Document()
                    .append("username", currentUser.getUsername())
                    .append("feedbackText", feedbackText)
                    .append("timestamp", formattedTimestamp);
            feedbackCollection.insertOne(feedbackDocument);

            System.out.println("Feedback submitted by " + currentUser.getUsername() + ": " + feedbackText);
        } else {
            System.out.println("Error: currentUser is null.");
        }
    } 
    private void showHistory() {
    Stage historyStage = new Stage();
    historyStage.initModality(Modality.APPLICATION_MODAL);
    historyStage.initStyle(StageStyle.UTILITY);
    historyStage.setTitle("Payment History");

    String username = currentUser.getUsername();
    List<String> userPurchaseHistory = getPurchaseHistoryFromDatabase(username);
    if (userPurchaseHistory.isEmpty()) {
        showAlert("No Payment History", "There is no payment history available.");
        return;
    }

    VBox historyLayout = new VBox(10);
    historyLayout.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 20px;");
    for (String paymentRecord : userPurchaseHistory) {
        VBox recordBox = new VBox(5);
        recordBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1px; -fx-padding: 10px;");
        String[] details = paymentRecord.split(","); 
        for (String detail : details) {
            Label detailLabel = new Label(detail);
            detailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
            recordBox.getChildren().add(detailLabel);
        }

        historyLayout.getChildren().add(recordBox);
    }

    ScrollPane scrollPane = new ScrollPane(historyLayout);
    scrollPane.setFitToWidth(true);

    Scene historyScene = new Scene(scrollPane, 500, 400);
    historyStage.setScene(historyScene);
    historyStage.show();
}    
    
    public void connectToMongoDB() {
        mongoDBConnection = new MongoDBConnection();
        coffeeItemsCollection = mongoDBConnection.getDatabase().getCollection("coffeeItems");
    }

private List<String> getPurchaseHistoryFromDatabase(String username) {
    List<String> purchaseHistory = new ArrayList<>();
    try {
        MongoCollection<Document> paymentHistoryCollection = mongoDBConnection.getDatabase().getCollection("paymentHistory");
        BasicDBObject query = new BasicDBObject();
        query.put("user", username);
        FindIterable<Document> paymentRecords = paymentHistoryCollection.find(query);
        for (Document paymentRecord : paymentRecords) {
                String user = paymentRecord.getString("user");
                double amount = paymentRecord.getDouble("amount");
                List<String> items = (List<String>) paymentRecord.get("items");
                Object timeObject = paymentRecord.get("time");
            String time;
            if (timeObject instanceof Long) {
                time = ((Long) timeObject).toString();
            } else if (timeObject instanceof String) {
                time = (String) timeObject;
            } else {
                throw new RuntimeException("Unexpected type for 'time' field: " + timeObject.getClass());
            }
                String recordString = String.format("User: %s, Amount: ₹%.2f, Items: %s, Time: %s",
                        user, amount, String.join(", ", items), time);
                purchaseHistory.add(recordString);
            }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return purchaseHistory;
}
private void showWelcomePage(Stage primaryStage) {
WelcomePage welcome = new WelcomePage();
welcome.start(primaryStage);
}
}