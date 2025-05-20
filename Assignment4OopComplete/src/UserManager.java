import java.util.TreeMap;

public class UserManager {
    private static final UserManager instance = new UserManager();
    private final TreeMap<String, User> users;

    private UserManager() {
        users = new TreeMap<>();
    }

    public static UserManager getInstance() {
        return instance;
    }

    public void init(String[] usersIn) throws DataValidationException, TradingException {
        if (usersIn == null) {
            throw new DataValidationException("User array cannot be null");

        }
        for (String userId : usersIn) {
            users.put(userId, new User(userId));
        }
    }

    public void updateTradable(String userId, TradableDTO o) throws DataValidationException {
        if (userId == null || o == null) {
            throw new DataValidationException("User ID or TradableDTO cannot be null");
        }
        User user = users.get(userId);
        if (user == null) {
            throw new DataValidationException("User does not exist: " + userId);
        }
        user.updateTradable(o);
    }
    public User getUser(String userId) throws DataValidationException {
        User user = users.get(userId);
        if (user == null) {
            throw new DataValidationException("User does not exist: " + userId);
        }
        return user;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (User user : users.values()) {
            sb.append(user.toString());
        }
        return sb.toString();
    }
}