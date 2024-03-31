package coffeevendingsystem;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
public class MongoDBConnection {
    private MongoClient mongoClient;
    private MongoDatabase database;
    public MongoDBConnection() {
        String mongoHost = "localhost";
        int mongoPort = 27017;
        String dbName = "cvs";
        mongoClient = new MongoClient(mongoHost, mongoPort);
        database = mongoClient.getDatabase(dbName);
    }
    public MongoDatabase getDatabase() {
        return database;
    }
    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}