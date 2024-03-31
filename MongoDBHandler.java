package coffeevendingsystem;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;
public class MongoDBHandler {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> coffeeCollection;
    public MongoDBHandler() {
        mongoClient = new MongoClient("localhost", 27017); 
        database = mongoClient.getDatabase("cvs"); 
        coffeeCollection = database.getCollection("coffeeItems");
    }
    public void updateStock(String itemName, int quantity) {
        coffeeCollection.updateOne(eq("name", itemName), inc("stock", -quantity));
    }
    public void insertPaymentHistory(Document paymentHistoryDoc) {
    try {
        MongoCollection<Document> paymentHistoryCollection = database.getCollection("paymentHistory");
        paymentHistoryCollection.insertOne(paymentHistoryDoc);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
