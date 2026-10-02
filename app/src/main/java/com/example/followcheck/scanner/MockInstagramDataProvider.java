package com.example.followcheck.scanner;

import com.example.followcheck.data.model.InstagramUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @deprecated Use com.example.followcheck.data.mock.MockInstagramDataProvider instead.
 * This class is kept to fix compilation errors until it can be safely removed.
 */
@Deprecated
public class MockInstagramDataProvider {
    private static final String[] USERNAMES = {
            "tech_guru", "nature_lover", "fitness_junkie", "foodie_delight", "travel_bug"
    };

    public static List<InstagramUser> generateMockUsers(int count, long seed) {
        List<InstagramUser> users = new ArrayList<>();
        Random random = new Random(seed);
        for (int i = 0; i < count; i++) {
            String id = "user_" + (1000 + i);
            String username = USERNAMES[random.nextInt(USERNAMES.length)] + "_" + i;
            users.add(new InstagramUser(id, username, "Mock " + i, null, false, false));
        }
        return users;
    }
}
