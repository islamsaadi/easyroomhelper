package com.islamsaadi.easyroomhelperdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;

import com.islamsaadi.easyroomhelper.result.Result;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "easyroom-db").allowMainThreadQueries().build(); // Just for demo

        userRepository = new UserRepository(db.userDao());

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
    }
}
