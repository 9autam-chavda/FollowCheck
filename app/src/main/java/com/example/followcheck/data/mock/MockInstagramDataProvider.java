package com.example.followcheck.data.mock;

import com.example.followcheck.data.model.InstagramUser;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides deterministic mock Instagram data for development and testing.
 */
public class MockInstagramDataProvider {

    /**
     * Generates a deterministic list of followers for Scan 1.
     * Approximately 100 followers: user001 to user100.
     */
    public static List<InstagramUser> getFollowersScan1() {
        List<InstagramUser> users = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            String id = String.format("user%03d", i);
            users.add(new InstagramUser(id, id, "Mock User " + i, null, false, i % 10 == 0));
        }
        return users;
    }

    /**
     * Generates a deterministic list of following for Scan 1.
     * Approximately 120 following:
     * - user001 to user080 (Mutuals)
     * - following001 to following040 (Non-followers)
     */
    public static List<InstagramUser> getFollowingScan1() {
        List<InstagramUser> users = new ArrayList<>();
        // Mutuals: user001 to user080
        for (int i = 1; i <= 80; i++) {
            String id = String.format("user%03d", i);
            users.add(new InstagramUser(id, id, "Mock User " + i, null, false, i % 10 == 0));
        }
        // Non-followers: following001 to following040
        for (int i = 1; i <= 40; i++) {
            String id = String.format("following%03d", i);
            users.add(new InstagramUser(id, id, "Following Only " + i, null, true, false));
        }
        return users;
    }

    /**
     * Generates a deterministic list of followers for Scan 2.
     * Changes from Scan 1:
     * - Lost follower: user003 (removed)
     * - New follower: user101 (added)
     */
    public static List<InstagramUser> getFollowersScan2() {
        List<InstagramUser> users = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            if (i == 3) continue; // user003 is lost
            String id = String.format("user%03d", i);
            users.add(new InstagramUser(id, id, "Mock User " + i, null, false, i % 10 == 0));
        }
        // New follower
        users.add(new InstagramUser("user101", "user101", "Mock User 101", null, false, false));
        return users;
    }

    /**
     * Generates a deterministic list of following for Scan 2.
     * Changes from Scan 1:
     * - Removed following: following001 (removed)
     * - New following: following041 (added)
     */
    public static List<InstagramUser> getFollowingScan2() {
        List<InstagramUser> users = new ArrayList<>();
        // Mutuals: user001 to user080
        for (int i = 1; i <= 80; i++) {
            String id = String.format("user%03d", i);
            users.add(new InstagramUser(id, id, "Mock User " + i, null, false, i % 10 == 0));
        }
        // Non-followers: following002 to following040
        for (int i = 2; i <= 40; i++) {
            String id = String.format("following%03d", i);
            users.add(new InstagramUser(id, id, "Following Only " + i, null, true, false));
        }
        // New following
        users.add(new InstagramUser("following041", "following041", "Following Only 41", null, true, false));
        return users;
    }
}
