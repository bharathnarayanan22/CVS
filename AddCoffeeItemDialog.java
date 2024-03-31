package coffeevendingsystem;
import com.mongodb.client.MongoCollection;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.bson.Document;

public class AddCoffeeItemDialog extends Stage {

    
    private MongoCollection<Document> coffeeItemsCollection;

    public AddCoffeeItemDialog(MongoCollection<Document> coffeeItemsCollection) {
        this.coffeeItemsCollection = coffeeItemsCollection;
        connectToMongoDB();

        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UTILITY);
        setTitle("Add Coffee Item");

        // Input fields for the new coffee item
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
        setScene(addCoffeeScene);
    }

    private void addCoffeeItem(String name, String description, String priceText, String stockText) {
        if (!name.isEmpty() && !description.isEmpty() && !priceText.isEmpty() && !stockText.isEmpty()) {
            try {
                double price = Double.parseDouble(priceText);
                int stock = Integer.parseInt(stockText);

                // Store the new coffee item in MongoDB
                Document coffeeItemDocument = new Document()
                        .append("name", name)
                        .append("description", description)
                        .append("price", price)
                        .append("stock", stock);

                coffeeItemsCollection.insertOne(coffeeItemDocument);

                System.out.println("New coffee item added: " + name);
                close();
            } catch (NumberFormatException e) {
                showAlert("Invalid input", "Please enter valid numeric values for Price and Stock.");
            }
        } else {
            showAlert("Incomplete input", "Please fill in all fields.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void connectToMongoDB() {
        MongoDBConnection mongoDBConnection = new MongoDBConnection();
        coffeeItemsCollection = mongoDBConnection.getDatabase().getCollection("coffeeItems");
    }
}
