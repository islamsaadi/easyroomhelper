# EasyRoomHelper

A lightweight, generic helper library for Android Room. It provides:

- **EasyDao<T>**: A base DAO interface for CRUD and raw operations.
- **EasyRepository<T>**: A generic repository wrapping EasyDao<T> with a comprehensive set of methods:
    - CRUD: insert, insertAll, update, delete, deleteAll
    - Basic queries: findAll(), count()
    - Dynamic queries: findByField(), deleteByField(), updateField()
    - Pagination & sorting: findWithLimit(), findWithLimitOffset(), findAllSorted()
    - Custom SQL: customQueryList(), customQuerySingle(), customUpdate(), customDelete()

- **Result<T>**: A wrapper for success/error results, with `isSuccess()`, `getData()`, and `getError()`.

---

## 🔧 Setup

1. **Add the library module** to your project (settings.gradle):
   ```groovy
   include ':easyroomhelper'
   ```

2. **Add dependencies** in your app module `build.gradle`:
   ```groovy
   dependencies {
     implementation project(':easyroomhelper')
     implementation "androidx.room:room-runtime:2.6.1"
     annotationProcessor "androidx.room:room-compiler:2.6.1"
   }
   ```

---

## 🚀 Usage

### 1. Define your DAO

Create one DAO per entity by extending EasyDao<T>:

```java
@Dao
public interface UserDao extends EasyDao<User> {
    // Optional: add custom @Query methods here
}
```

### 2. Define your RoomDatabase

Declare your entities and DAOs in a single @Database:

```java
@Database(entities = { User.class }, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
```

### 3. Create a Repository

Extend EasyRepository<T> and supply your DAO:

```java
public class UserRepository extends EasyRepository<User> {
    private final UserDao userDao;

    public UserRepository(UserDao userDao) {
        // Pass the entity class and true to use lowercase table names
        super(User.class, true);
        this.userDao = userDao;
    }

    @Override
    protected EasyDao<User> getDao() {
        return userDao;
    }
}
```

### 4. Initialize Room and use the repository

Instantiate Room and your repository, then call methods:

```java
AppDatabase db = Room.databaseBuilder(
    context,
    AppDatabase.class,
    "my_app_db"
)
.fallbackToDestructiveMigration()
.build();

UserRepository  userRepository = new UserRepository(db.userDao());

// Example Usage:

// Insert users
userRepository.insert(new User("John Doe", "john@example.com"));
userRepository.insert(new User("Jane Smith", "jane@example.com"));

// Find user by email
Result<List<User>> result = userRepository.findByField("email", "john@example.com");

if (result.isSuccess()) {
    List<User> users = result.getData();
    if (users != null && !users.isEmpty()) {
        Log.d("EasyRoomDemo", "User Found: " + users.get(0).name);
    }
} else {
    Log.e("EasyRoomDemo", "Error: " + result.getError().getMessage());
}

// Custom query
String query = "SELECT * FROM user WHERE email LIKE ?";
Result<List<User>> users = userRepository.customQueryList(query, "%@example.com");

if (users.isSuccess()) {
    for (User u : users.getData()) {
        Log.d("EasyRoomDemo", u.name + " - " + u.email);
    }
}

// Count
Result<Integer> total = userRepository.count();
if(total.isSuccess()) {
    Log.d("EasyRoomDemo", "Users count: " + total.getData());
}

// Delete user by name
Result<Integer> deleteResult = userRepository.deleteByField("name", "Jane Smith");
if (deleteResult.isSuccess()) {
    Log.d("EasyRoomDemo", "User has been deleted");
}

Result<Integer> deletedAllResult = userRepository.deleteAll();
if (deletedAllResult.isSuccess()) {
    Log.d("EasyRoomDemo", "All users were been deleted");
}
```

---

## 📖 API Reference

### EasyDao<T>
```java
@Insert void insert(T entity);
@Insert void insertAll(List<T> entities);
@Update void update(T entity);
@Delete void delete(T entity);
@RawQuery List<T> rawQuery(SupportSQLiteQuery query);
@RawQuery int rawUpdate(SupportSQLiteQuery query);
@RawQuery int rawDelete(SupportSQLiteQuery query);
@RawQuery int rawCount(SupportSQLiteQuery query);
```

### EasyRepository<T>
- **insert**, **insertAll**, **update**, **delete**, **deleteAll**
- **findAll**, **count**, **countByField**, **existsByField**
- **findByField**, **deleteByField**, **updateField**
- **findWithLimit**, **findWithLimitOffset**, **findAllSorted**
- **customQueryList**, **customQuerySingle**, **customUpdate**, **customDelete**

### Result<T>
```java
static <T> Result<T> success(T data);
static <T> Result<T> error(Exception error);

boolean isSuccess();
T getData();
Exception getError();
```

---

## 📜 License

[MIT](LICENSE)
