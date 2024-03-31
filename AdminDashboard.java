package coffeevendingsystem;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.bson.Document;
import org.bson.conversions.Bson;
public class AdminDashboard extends Stage {
    private MongoDBConnection mongoDBConnection;
    private MongoCollection<Document> coffeeItemsCollection;
    public AdminDashboard(MongoCollection<Document> coffeeItemsCollection) {
        connectToMongoDB();
        this.mongoDBConnection = new MongoDBConnection();
        this.coffeeItemsCollection = coffeeItemsCollection;
        Image backgroundImage = new Image("/coffeevendingsystem/image1.jpg");
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        StackPane rootLayout = new StackPane();
        rootLayout.setBackground(new Background(background));
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UTILITY);
        setTitle("Admin Dashboard");
        Button addCoffeeButton = new Button("Add New Coffee");
        addCoffeeButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addCoffeeButton.setOnAction(event -> showAddCoffeeItemDialog());
        Button deleteCoffeeButton = new Button("Delete Coffee");
        deleteCoffeeButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        deleteCoffeeButton.setOnAction(event -> showDeleteCoffeeItemDialog());
        Button updateCoffeeButton = new Button("Update Coffee");
        updateCoffeeButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        updateCoffeeButton.setOnAction(event -> showUpdateCoffeeItemDialog());
        Button purchaseHistoryButton = new Button("Purchase History");
        purchaseHistoryButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        purchaseHistoryButton.setOnAction(event -> showPurchaseHistory());       
        Button feedbackHistoryButton = new Button("Feedback History");
        feedbackHistoryButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        feedbackHistoryButton.setOnAction(event -> showFeedbackHistory());
        VBox adminDashboardLayout = new VBox(20, addCoffeeButton, deleteCoffeeButton, updateCoffeeButton, purchaseHistoryButton, feedbackHistoryButton);
        adminDashboardLayout.setAlignment(Pos.CENTER);
        adminDashboardLayout.setPadding(new Insets(20));
        rootLayout.getChildren().add(adminDashboardLayout);
        Scene adminDashboardScene = new Scene(rootLayout, 600, 400);
        setScene(adminDashboardScene);
    }
    public void connectToMongoDB() {
        mongoDBConnection = new MongoDBConnection();
        coffeeItemsCollection = mongoDBConnection.getDatabase().getCollection("coffeeItems");
    }
    private void showAddCoffeeItemDialog() {
        connectToMongoDB();
        Stage addCoffeeItemDialog = new Stage();
        addCoffeeItemDialog.initModality(Modality.APPLICATION_MODAL);
        addCoffeeItemDialog.initStyle(StageStyle.UTILITY);
        addCoffeeItemDialog.setTitle("Add Coffee Item");
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        TextField stockField = new TextField();
        stockField.setPromptText("Stock");
        Button addButton = new Button("Add Coffee Item");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addButton.setOnAction(event -> addCoffeeItem(nameField.getText(), descriptionField.getText(),
                priceField.getText(), stockField.getText()));
        VBox addCoffeeLayout = new VBox(20, nameField, descriptionField, priceField, stockField, addButton);
        addCoffeeLayout.setAlignment(Pos.CENTER);
        addCoffeeLayout.setPadding(new Insets(20));
        Scene addCoffeeScene = new Scene(addCoffeeLayout, 400, 300);
        addCoffeeItemDialog.setScene(addCoffeeScene);
        addCoffeeItemDialog.showAndWait();
    }
    private void showDeleteCoffeeItemDialog() {
     connectToMongoDB();
     List<String> coffeeItems = getCoffeeItemNamesFromDatabase();
        if (coffeeItems.isEmpty()) {
            showAlert("No Coffee Items", "There are no coffee items to delete.");
            return;
        }
        Stage deleteCoffeeItemDialog = new Stage();
        deleteCoffeeItemDialog.initModality(Modality.APPLICATION_MODAL);
        deleteCoffeeItemDialog.initStyle(StageStyle.UTILITY);
        deleteCoffeeItemDialog.setTitle("Delete Coffee Item");
        VBox coffeeItemsLayout = new VBox(10);
        for (String coffeeItem : coffeeItems) {
            String coffeeItemNameText = coffeeItem;
    Button deleteButton = new Button("Delete");
    deleteButton.setOnAction(event -> {
        System.out.println("Delete button clicked for item: " + coffeeItemNameText);
        handleDeleteCoffeeItem(coffeeItemNameText);
        deleteCoffeeItem(coffeeItemNameText);
    });
    Label coffeeItemName = new Label(coffeeItemNameText);
    HBox coffeeItemBox = new HBox(5, deleteButton, coffeeItemName);
    coffeeItemsLayout.getChildren().add(coffeeItemBox);
        }
        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> deleteCoffeeItemDialog.close());
        VBox dialogLayout = new VBox(20, coffeeItemsLayout, closeButton);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(dialogLayout);
        Scene dialogScene = new Scene(scrollPane, 200, 500);
        deleteCoffeeItemDialog.setScene(dialogScene);
        deleteCoffeeItemDialog.showAndWait();
}
    private List<String> getCoffeeItemNamesFromDatabase() {
        FindIterable<Document> coffeeItems = coffeeItemsCollection.find();
        List<String> coffeeItemNames = new ArrayList<>();
        for (Document coffeeItem : coffeeItems) {
            coffeeItemNames.add(coffeeItem.getString("name"));
        }
        return coffeeItemNames;
    }
    private void handleDeleteCoffeeItem(String coffeeItemName) {
        System.out.println("Handling delete for item: " + coffeeItemName);
    }
    private void deleteCoffeeItem(String coffeeItemName) {
        Bson filter = Filters.eq("name", coffeeItemName);
    coffeeItemsCollection.deleteOne(filter);
    showDeleteCoffeeItemDialog();
    }
    private void showUpdateCoffeeItemDialog() {
    connectToMongoDB();
    List<String> coffeeItems = getCoffeeItemNamesFromDatabase();
    if (coffeeItems.isEmpty()) {
        showAlert("No Coffee Items", "There are no coffee items to update.");
        return;
    }
    Stage updateCoffeeItemDialog = new Stage();
    updateCoffeeItemDialog.initModality(Modality.APPLICATION_MODAL);
    updateCoffeeItemDialog.initStyle(StageStyle.UTILITY);
    updateCoffeeItemDialog.setTitle("Update Coffee Item");
    VBox coffeeItemsLayout = new VBox(10);
    for (String coffeeItem : coffeeItems) {
        String coffeeItemNameText = coffeeItem;
        Button updateButton = new Button("Update");
        updateButton.setOnAction(event -> {
            System.out.println("Update button clicked for item: " + coffeeItemNameText);
            showEditCoffeeItemDialog(coffeeItemNameText);
        });
        Label coffeeItemName = new Label(coffeeItemNameText);
        HBox coffeeItemBox = new HBox(5, updateButton, coffeeItemName);
        coffeeItemsLayout.getChildren().add(coffeeItemBox);
    }
    Button closeButton = new Button("Close");
    closeButton.setOnAction(event -> updateCoffeeItemDialog.close());
    VBox dialogLayout = new VBox(20, coffeeItemsLayout, closeButton);
    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setContent(dialogLayout);
    scrollPane.setFitToWidth(true);
    Scene dialogScene = new Scene(scrollPane, 300, 500);
    updateCoffeeItemDialog.setScene(dialogScene);
    updateCoffeeItemDialog.showAndWait();
}
    private void showEditCoffeeItemDialog(String coffeeItemName) {
    Document coffeeItem = getCoffeeItemDetails(coffeeItemName);
    if (coffeeItem != null) {
        Stage editCoffeeItemDialog = new Stage();
        editCoffeeItemDialog.initModality(Modality.APPLICATION_MODAL);
        editCoffeeItemDialog.initStyle(StageStyle.UTILITY);
        editCoffeeItemDialog.setTitle("Edit Coffee Item");
        TextField nameField = new TextField(coffeeItem.getString("name"));
        nameField.setPromptText("Name");
        TextField descriptionField = new TextField(coffeeItem.getString("description"));
        descriptionField.setPromptText("Description");
        TextField priceField = new TextField(String.valueOf(coffeeItem.getDouble("price")));
        priceField.setPromptText("Price");
        TextField stockField = new TextField(String.valueOf(coffeeItem.getInteger("stock")));
        stockField.setPromptText("Stock");
        Button updateButton = new Button("Update Coffee Item");
        updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        updateButton.setOnAction(event -> updateCoffeeItem(coffeeItem, nameField.getText(), descriptionField.getText(),
                priceField.getText(), stockField.getText(), editCoffeeItemDialog));
        VBox editCoffeeLayout = new VBox(20, nameField, descriptionField, priceField, stockField, updateButton);
        editCoffeeLayout.setAlignment(Pos.CENTER);
        editCoffeeLayout.setPadding(new Insets(20));
        Scene editCoffeeScene = new Scene(editCoffeeLayout, 400, 300);
        editCoffeeItemDialog.setScene(editCoffeeScene);
        editCoffeeItemDialog.showAndWait();
    } else {
        showAlert("Coffee Item Not Found", "The selected coffee item was not found in the database.");
    }
}
private Document getCoffeeItemDetails(String coffeeItemName) {
    Bson filter = Filters.eq("name", coffeeItemName);
    Document coffeeItem = coffeeItemsCollection.find(filter).first();
    if (coffeeItem != null) {
        Object priceObject = coffeeItem.get("price");
        if (priceObject instanceof Integer) {
            coffeeItem.put("price", ((Integer) priceObject).doubleValue());
        }
    }
    return coffeeItem;
}
private void updateCoffeeItem(Document originalCoffeeItem, String name, String description, String priceText, String stockText, Stage editCoffeeItemDialog) {
    if (!name.isEmpty() && !description.isEmpty() && !priceText.isEmpty() && !stockText.isEmpty()) {
        try {
            double price = Double.parseDouble(priceText);
            int stock = Integer.parseInt(stockText);
            Bson filter = Filters.eq("_id", originalCoffeeItem.getObjectId("_id"));
            Bson update = new Document("$set", new Document()
                    .append("name", name)
                    .append("description", description)
                    .append("price", price)
                    .append("stock", stock));
            coffeeItemsCollection.updateOne(filter, update);
            System.out.println("Coffee item updated: " + name);
            editCoffeeItemDialog.close();
        } catch (NumberFormatException e) {
            showAlert("Invalid input", "Please enter valid numeric values for Price and Stock.");
        }
    } else {
        showAlert("Incomplete input", "Please fill in all fields.");
    }
}
    private void addCoffeeItem(String name, String description, String priceText, String stockText) {
        if (!name.isEmpty() && !description.isEmpty() && !priceText.isEmpty() && !stockText.isEmpty()) {
            try {
                double price = Double.parseDouble(priceText);
                int stock = Integer.parseInt(stockText);
                Document coffeeItemDocument = new Document()
                        .append("name", name)
                        .append("description", description)
                        .append("price", price)
                        .append("stock", stock);
                coffeeItemsCollection.insertOne(coffeeItemDocument);
                System.out.println("New coffee item added: " + name);
            } catch (NumberFormatException e) {
                showAlert("Invalid input", "Please enter valid numeric values for Price and Stock.");
            }
        } else {
            showAlert("Incomplete input", "Please fill in all fields.");
        }
    }
    private void showPurchaseHistory() {
    Stage purchaseHistoryStage = new Stage();
    purchaseHistoryStage.setTitle("Purchase History");

    List<String> purchaseHistory = getPurchaseHistoryFromDatabase();
    if (purchaseHistory.isEmpty()) {
        showAlert("No Purchase History", "There is no purchase history available.");
        return;
    }
    VBox purchaseHistoryLayout = new VBox(10);
    purchaseHistoryLayout.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 20px;"); 

    for (String purchaseRecord : purchaseHistory) {
        VBox recordBox = new VBox(5);
        recordBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1px; -fx-padding: 10px;");

        String[] details = purchaseRecord.split(",");
        for (String detail : details) {
            Label detailLabel = new Label(detail);
            detailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;"); 
            recordBox.getChildren().add(detailLabel);
        }

        purchaseHistoryLayout.getChildren().add(recordBox);
    }

    ScrollPane scrollPane = new ScrollPane(purchaseHistoryLayout);
    scrollPane.setFitToWidth(true);

    Scene purchaseHistoryScene = new Scene(scrollPane, 500, 400);
    purchaseHistoryStage.setScene(purchaseHistoryScene);
    purchaseHistoryStage.show();
}
    private List<String> getPurchaseHistoryFromDatabase() {
        List<String> purchaseHistory = new ArrayList<>();
        try {
            MongoCollection<Document> paymentHistoryCollection = mongoDBConnection.getDatabase().getCollection("paymentHistory");
            FindIterable<Document> paymentRecords = paymentHistoryCollection.find();
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
    private void showFeedbackHistory() {
        Stage feedbackHistoryStage = new Stage();
        feedbackHistoryStage.setTitle("Feedback History");
        List<String> feedbackHistory = getFeedbackHistoryFromDatabase();
        if (feedbackHistory.isEmpty()) {
            showAlert("No Feedback History", "There is no feedback history available.");
            return;
        }
        TextArea feedbackTextArea = new TextArea();
        feedbackTextArea.setEditable(false);
        feedbackTextArea.setStyle("-fx-font-size: 14px;");
        for (String feedbackRecord : feedbackHistory) {
            feedbackTextArea.appendText(feedbackRecord + "\n\n");
        }
        Scene feedbackHistoryScene = new Scene(new StackPane(feedbackTextArea), 600, 400);
        feedbackHistoryStage.setScene(feedbackHistoryScene);
        feedbackHistoryStage.show();
    }
    private List<String> getFeedbackHistoryFromDatabase() {
    List<String> feedbackHistory = new ArrayList<>();
    try {
        MongoCollection<Document> feedbackCollection = mongoDBConnection.getDatabase().getCollection("feedback");
        FindIterable<Document> feedbackRecords = feedbackCollection.find();
        for (Document feedbackRecord : feedbackRecords) {
            System.out.println("Feedback Record: " + feedbackRecord.toJson());

            String user = feedbackRecord.getString("username"); // Adjusted field name
            String feedbackText = feedbackRecord.getString("feedbackText"); // Adjusted field name
            String time = feedbackRecord.getString("timestamp");

            String recordString = String.format("User: %s, Feedback: %s, Time: %s",
                    user, feedbackText, time);
            feedbackHistory.add(recordString);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return feedbackHistory;
}



    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}