package coffeevendingsystem;
import coffeevendingsystem.MongoDBConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.bson.Document;
import java.io.InputStream;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextInputDialog;
public class CoffeeItemsDashboard extends Application {
    private MongoDBConnection mongoDBConnection;
    private MongoCollection<Document> coffeeItemsCollection;
    private List<String> cart = new ArrayList<>();
    private VBox fullLayout; 
    private MongoDBHandler mongoDBHandler;
    private User currentUser; 
    private MongoDatabase database;  
    public CoffeeItemsDashboard(User currentUser, MongoDatabase database) {
        this.currentUser = currentUser;
        this.database = database;
    }
    @Override
    public void start(Stage primaryStage) {
        connectToMongoDB();
        mongoDBHandler = new MongoDBHandler();

        primaryStage.setTitle("Coffee Vending System - Coffee Items Dashboard");

        Label titleLabel = new Label("Coffee Items Dashboard");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            VBox updatedCoffeeItemsLayout = createCoffeeItemsLayout(newValue);
            fullLayout.getChildren().set(2, updatedCoffeeItemsLayout);
        });     
        VBox coffeeItemsLayout = createCoffeeItemsLayout("");
        Button cartButton = new Button("My Cart");
        cartButton.setOnAction(event -> viewCart());
        cartButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");       
        Button BackButton = new Button("Back to Main Page");
        BackButton.setOnAction(event -> showMainPage(primaryStage,currentUser,database));
        BackButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        fullLayout = new VBox(20, titleLabel, searchField, coffeeItemsLayout,(new HBox( 50,cartButton,BackButton)));
        fullLayout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(fullLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    } 
    private void showMainPage(Stage primaryStage, User currentUser,MongoDatabase database) {
        MainPage dashboard = new MainPage(currentUser, database);
        dashboard.start(primaryStage);
    }
    public void connectToMongoDB() {
        mongoDBConnection = new MongoDBConnection();
        coffeeItemsCollection = mongoDBConnection.getDatabase().getCollection("coffeeItems");
    }  
    private VBox cartLayout = new VBox(20);
    private VBox createCoffeeItemsLayout(String searchQuery) {
        VBox coffeeItemsLayout = new VBox(20);
        coffeeItemsLayout.setAlignment(Pos.CENTER);
        coffeeItemsLayout.setPadding(new Insets(20));
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER);
        MongoCursor<Document> cursor = coffeeItemsCollection.find().iterator();
        int itemsInRow = 0;
        while (cursor.hasNext()) {
            Document coffeeItem = cursor.next();
            String itemName = coffeeItem.getString("name");
            String itemDescription = coffeeItem.getString("description");
            Number priceNumber = (Number) coffeeItem.get("price");
            double itemPrice = priceNumber.doubleValue();
            int itemStock = coffeeItem.getInteger("stock");
            if (!itemName.toLowerCase().contains(searchQuery.toLowerCase())) {
                continue; 
            }
try {
    String itemNameLowerCase = itemName.toLowerCase();
    String imagePath = "/coffeevendingsystem/" + itemNameLowerCase + ".jpg";

    InputStream inputStream = getClass().getResourceAsStream(imagePath);
    ImageView imageView;

    if (inputStream != null) {
        imageView = new ImageView(new Image(inputStream));
    } else {
        System.out.println("Image not found: " + imagePath);

        String defaultImagePath = "/coffeevendingsystem/noimage.jpg";
        InputStream defaultImageStream = getClass().getResourceAsStream(defaultImagePath);

        if (defaultImageStream != null) {
            imageView = new ImageView(new Image(defaultImageStream));
        } else {
            System.out.println("Default image not found: " + defaultImagePath);

            imageView = new ImageView();
        }
    }
    imageView.setFitWidth(100);
    imageView.setFitHeight(100);
    Label nameLabel = new Label(itemName);
    nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
    Label descriptionLabel = new Label(itemDescription);
    Label priceLabel = new Label("Price: ₹" + itemPrice);
    Label stockLabel = new Label("Stock: " + itemStock + " units");
    Label quantityLabel = new Label("Quantity:");
    Spinner<Integer> quantitySpinner = new Spinner<>(0, coffeeItem.getInteger("stock"), 0);
    Button addToCartButton = new Button("Add to Cart");
    addToCartButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
    addToCartButton.setOnAction(event -> addToCart(itemName, quantitySpinner.getValue(), cartLayout));
    VBox itemBox = new VBox(5, nameLabel, imageView, descriptionLabel, priceLabel, stockLabel, quantityLabel, quantitySpinner, addToCartButton);
    itemBox.setAlignment(Pos.CENTER_LEFT);
    itemBox.setStyle("-fx-border-color: #ddd; -fx-border-width: 1px; -fx-padding: 10px;");
    itemBox.setPrefSize(200, 250);
    row.getChildren().add(itemBox);
    itemsInRow++;
    if (itemsInRow == 2) {
        coffeeItemsLayout.getChildren().add(row);
        row = new HBox(20);
        row.setAlignment(Pos.CENTER);
        itemsInRow = 0;
    }
} catch (Exception e) {
    e.printStackTrace();
}
        }
        cursor.close();
        if (itemsInRow > 0) {
            coffeeItemsLayout.getChildren().add(row);
        }
        ScrollPane scrollPane = new ScrollPane(coffeeItemsLayout);
        scrollPane.setFitToWidth(true);

        return new VBox(scrollPane);
    }
private void viewCart() {
    Stage cartStage = new Stage();
    cartStage.setTitle("My Cart");
    VBox cartLayout = new VBox(20);
    cartLayout.setAlignment(Pos.CENTER);
    cartLayout.setPadding(new Insets(20));
     if (!cart.isEmpty()) {
        for (String itemName : cart) {
            HBox cartItemBox = createCartItemBox(itemName, cartLayout);
            cartLayout.getChildren().add(cartItemBox);
        }
    } else {
        Label emptyCartLabel = new Label("Cart is empty.");
        cartLayout.getChildren().add(emptyCartLabel);
    }
    ScrollPane scrollPane = new ScrollPane(cartLayout);
    scrollPane.setFitToWidth(true);
    Button checkoutButton = new Button("Checkout");
    checkoutButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
    checkoutButton.setOnAction(event -> showPaymentPage(cartLayout, cartStage));
    Button closeButton = new Button("Close Cart");
    closeButton.setStyle("-fx-background-color: #ff0000; -fx-text-fill: white;");
    closeButton.setOnAction(event -> {
        cart.clear();
        cartStage.close();
    });
    VBox cartPageLayout = new VBox(20, scrollPane, checkoutButton, closeButton);
    cartPageLayout.setAlignment(Pos.CENTER);
    Scene cartScene = new Scene(cartPageLayout, 800, 600);
    cartStage.setScene(cartScene);
    cartStage.show();
}
private HBox createCartItemBox(String itemName, VBox cartLayout) {
    Label itemNameLabel = new Label(itemName.split(" \\(Qty: ")[0]);
    itemNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
    TextField quantityTextField = createQuantityTextField(itemName, cartLayout);
    quantityTextField.setPrefWidth(50);
    Button okButton = new Button("OK");
    okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
    okButton.setOnAction(e -> {
        String quantityText = quantityTextField.getText().trim();
        if (!quantityText.isEmpty()) {
            try {
                int newQuantity = Integer.parseInt(quantityText);
                updateCartItem(itemName, newQuantity, cartLayout);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid quantity format: " + quantityText);
            }
        }
    });
    Button removeButton = new Button("Remove");
    removeButton.setStyle("-fx-background-color: #ff0000; -fx-text-fill: white;");
    removeButton.setOnAction(event -> removeFromCart(itemName, cartLayout));
    HBox cartItemBox = new HBox(10, itemNameLabel, quantityTextField, okButton, removeButton);
    cartItemBox.setAlignment(Pos.CENTER_LEFT);
    cartItemBox.setStyle("-fx-border-color: #ddd; -fx-border-width: 1px; -fx-padding: 10px;");
    cartItemBox.setPrefSize(300, 50);
    return cartItemBox;
}
private TextField createQuantityTextField(String itemName, VBox cartLayout) {
    TextField quantityTextField = new TextField();
    int currentQuantity = findCartItemQuantity(itemName);
    quantityTextField.setText(String.valueOf(currentQuantity));
    quantityTextField.setOnAction(e -> {
        String quantityText = quantityTextField.getText().trim();
        if (!quantityText.isEmpty()) {
            try {
                int newQuantity = Integer.parseInt(quantityText);
                updateCartItem(itemName, newQuantity, cartLayout);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid quantity format: " + quantityText);
            }
        }
    });

    return quantityTextField;
}
private int findCartItemQuantity(String itemName) {
    for (String cartItem : cart) {
        if (cartItem.startsWith(itemName)) {
            String quantityString = cartItem.replaceAll("\\D+", "");
            try {
                return Integer.parseInt(quantityString);
            } catch (NumberFormatException e) {
                System.out.println("Error parsing quantity: " + quantityString);
            }
        }
    }
    return 0;
}
private void addToCart(String itemName, int quantity, VBox cartLayout) {
   if (quantity > 1) { 
        int index = findCartItemIndex(itemName);
        if (index != -1) {
            int existingQuantity = Integer.parseInt(cart.get(index).replaceAll("\\D+", ""));
            quantity += existingQuantity;
            cart.remove(index);
        }

        String cartItem = itemName + " (Qty: " + quantity + ")";
        cart.add(cartItem);
        System.out.println("Added to Cart: " + cartItem);

        updateCartLayout(cartLayout);
    }
}
private void updateCartItem(String itemName, int newQuantity, VBox cartLayout) {
    if (itemName != null) {
        // Iterate over the cart and replace the quantity if the item is found
        for (int i = 0; i < cart.size(); i++) {
            String cartItem = cart.get(i);
            if (cartItem.startsWith(itemName)) {
                // Replace the existing quantity with the new one
                cart.set(i, cartItem.replaceFirst("\\(Qty: \\d+\\)", "(Qty: " + newQuantity + ")"));
                break;
            }
        }

        updateCartLayout(cartLayout);
    }
}
private int findCartItemIndex(String itemName) {
    for (int i = 0; i < cart.size(); i++) {
        if (cart.get(i).startsWith(itemName)) {
            return i;
        }
    }
    return -1;
}
private void removeFromCart(String itemName, VBox cartLayout) {
    cart.remove(itemName);
    System.out.println("Removed from Cart: " + itemName);
    updateCartLayout(cartLayout);
}
private void updateCartLayout(VBox cartLayout) {
    cartLayout.getChildren().clear();
    if (!cart.isEmpty()) {
        for (String item : cart) {
            HBox cartItemBox = createCartItemBox(item, cartLayout);
            cartLayout.getChildren().add(cartItemBox);
        }
    } else {
        Label emptyCartLabel = new Label("Cart is empty.");
        cartLayout.getChildren().add(emptyCartLabel);
    }
}
private void showPaymentPage(VBox cartLayout, Stage cartStage) {
    Stage paymentStage = new Stage();
    paymentStage.setTitle("Payment Page");
    Label titleLabel = new Label("Payment Details");
    titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
    VBox paymentLayout = new VBox(20, titleLabel);
    paymentLayout.setAlignment(Pos.CENTER);
    paymentLayout.setPadding(new Insets(20));
    double totalAmount = 0;
    if (!cart.isEmpty()) {
        for (String item : cart) {
            String itemName = item.split(" \\(Qty: ")[0];
            int quantity = Integer.parseInt(item.replaceAll("\\D+", ""));
            double itemPrice = getItemPrice(itemName);
            double itemTotal = quantity * itemPrice;
            totalAmount += itemTotal;
            Label itemLabel = new Label(itemName + " (Qty: " + quantity + ") - ₹" + itemTotal);
            paymentLayout.getChildren().add(itemLabel);
        }
    } else {
        Label emptyCartLabel = new Label("Cart is empty.");
        paymentLayout.getChildren().add(emptyCartLabel);
    }
    Label amountLabel = new Label("Total Amount: ₹" + totalAmount);
    TextField amountField = new TextField();
    Button payButton = new Button("Pay Now");
    payButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
    payButton.setOnAction(event -> handlePayment(amountField.getText(), paymentStage));
    paymentLayout.getChildren().addAll(amountLabel, amountField, payButton);
    Scene scene = new Scene(paymentLayout, 400, 300);
    paymentStage.setScene(scene);
    paymentStage.show();
    cartStage.close();
}
private double getItemPrice(String itemName) {
    return 10.0; 
}
private void handlePayment(String enteredAmount, Stage paymentStage) {
    try {
        double enteredAmountValue = Double.parseDouble(enteredAmount);
        double totalAmount = calculateTotalAmount();
        

        if (enteredAmountValue == totalAmount) {
            storePaymentHistory();
            deductStockFromDatabase();
            showPaymentSuccess(paymentStage);
        } else {
            showPaymentFailure(paymentStage);
        }
    } catch (NumberFormatException e) {
        showPaymentFailure(paymentStage);
    }
}
private void deductStockFromDatabase() {
    MongoDBHandler mongoDBHandler = new MongoDBHandler();
    for (String item : cart) {
        String itemName = item.split(" \\(Qty: ")[0];
        int quantity = Integer.parseInt(item.replaceAll("\\D+", ""));
        mongoDBHandler.updateStock(itemName, quantity);
    }
    mongoDBHandler.close();
}
private double calculateTotalAmount() {
    double totalAmount = 0;
    if (!cart.isEmpty()) {
        for (String item : cart) {
            String itemName = item.split(" \\(Qty: ")[0];
            int quantity = Integer.parseInt(item.replaceAll("\\D+", ""));
            double itemPrice = getItemPrice(itemName);
            double itemTotal = quantity * itemPrice;
            totalAmount += itemTotal;
        }
    }
    return totalAmount;
}
private void showPaymentSuccess(Stage paymentStage) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Payment Successful");
    alert.setHeaderText(null);
    alert.setContentText("Payment was successful!");
    alert.showAndWait();
    paymentStage.close();
}
private void showPaymentFailure(Stage paymentStage) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Payment Not Valid");
    alert.setHeaderText(null);
    alert.setContentText("Payment is not valid. Please enter the correct amount.");
    alert.showAndWait();
}
private void storePaymentHistory() {
    try {
        // Connect to the payment history collection in the database
        MongoCollection<Document> paymentHistoryCollection = database.getCollection("paymentHistory");

        // Create a new document for the payment history
        Document paymentDocument = new Document();
        paymentDocument.append("user", currentUser.getUsername());
        paymentDocument.append("amount", calculateTotalAmount());
        paymentDocument.append("items", cart);
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);

        paymentDocument.append("time", formattedTime);

        paymentHistoryCollection.insertOne(paymentDocument);

        System.out.println("Payment history stored in the database.");

    } catch (Exception e) {
        e.printStackTrace();
    }
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