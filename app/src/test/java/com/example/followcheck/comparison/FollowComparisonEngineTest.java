package com.example.followcheck.comparison;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.followcheck.data.model.InstagramUser;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FollowComparisonEngineTest {

    private InstagramUser createUser(String id) {
        return new InstagramUser(id, id, "Name " + id, null, false, false);
    }

    @Test
    public void testNonFollowers() {
        List<InstagramUser> followers = new ArrayList<>();
        followers.add(createUser("A"));
        followers.add(createUser("B"));

        List<InstagramUser> following = new ArrayList<>();
        following.add(createUser("A"));
        following.add(createUser("B"));
        following.add(createUser("C"));

        List<InstagramUser> result = FollowComparisonEngine.getNonFollowers(followers, following);
        assertEquals(1, result.size());
        assertEquals("c", result.get(0).getId().toLowerCase());
    }

    @Test
    public void testFollowersOnly() {
        List<InstagramUser> followers = new ArrayList<>();
        followers.add(createUser("A"));
        followers.add(createUser("B"));
        followers.add(createUser("C"));

        List<InstagramUser> following = new ArrayList<>();
        following.add(createUser("A"));
        following.add(createUser("B"));

        List<InstagramUser> result = FollowComparisonEngine.getFollowersOnly(followers, following);
        assertEquals(1, result.size());
        assertEquals("c", result.get(0).getId().toLowerCase());
    }

    @Test
    public void testMutuals() {
        List<InstagramUser> followers = new ArrayList<>();
        followers.add(createUser("A"));
        followers.add(createUser("B"));
        followers.add(createUser("C"));

        List<InstagramUser> following = new ArrayList<>();
        following.add(createUser("A"));
        following.add(createUser("B"));
        following.add(createUser("D"));

        List<InstagramUser> result = FollowComparisonEngine.getMutuals(followers, following);
        assertEquals(2, result.size());
    }

    @Test
    public void testLostFollowers() {
        List<InstagramUser> prev = new ArrayList<>();
        prev.add(createUser("A"));
        prev.add(createUser("B"));
        prev.add(createUser("C"));

        List<InstagramUser> curr = new ArrayList<>();
        curr.add(createUser("A"));
        curr.add(createUser("B"));

        List<InstagramUser> result = FollowComparisonEngine.getLostFollowers(prev, curr);
        assertEquals(1, result.size());
        assertEquals("c", result.get(0).getId().toLowerCase());
    }

    @Test
    public void testNewFollowers() {
        List<InstagramUser> prev = new ArrayList<>();
        prev.add(createUser("A"));
        prev.add(createUser("B"));

        List<InstagramUser> curr = new ArrayList<>();
        curr.add(createUser("A"));
        curr.add(createUser("B"));
        curr.add(createUser("C"));

        List<InstagramUser> result = FollowComparisonEngine.getNewFollowers(prev, curr);
        assertEquals(1, result.size());
        assertEquals("c", result.get(0).getId().toLowerCase());
    }

    @Test
    public void testEmptyLists() {
        List<InstagramUser> empty = new ArrayList<>();
        assertTrue(FollowComparisonEngine.getNonFollowers(empty, empty).isEmpty());
        assertTrue(FollowComparisonEngine.getMutuals(empty, empty).isEmpty());
        assertTrue(FollowComparisonEngine.getLostFollowers(empty, empty).isEmpty());
    }

    @Test
    public void testDuplicateUsernames() {
        List<InstagramUser> followers = new ArrayList<>();
        followers.add(createUser("A"));
        followers.add(createUser("A")); // Duplicate

        List<InstagramUser> following = new ArrayList<>();
        following.add(createUser("A"));
        following.add(createUser("B"));

        List<InstagramUser> result = FollowComparisonEngine.getNonFollowers(followers, following);
        assertEquals(1, result.size());
        assertEquals("b", result.get(0).getId().toLowerCase());
    }

    @Test
    public void testCaseInsensitivity() {
        List<InstagramUser> followers = new ArrayList<>();
        followers.add(createUser("john"));

        List<InstagramUser> following = new ArrayList<>();
        following.add(createUser("JOHN"));

        List<InstagramUser> result = FollowComparisonEngine.getNonFollowers(followers, following);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testLargeDataset() {
        List<InstagramUser> followers = new ArrayList<>();
        List<InstagramUser> following = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            followers.add(createUser("user" + i));
            if (i < 9000) {
                following.add(createUser("user" + i));
            }
        }

        long start = System.currentTimeMillis();
        List<InstagramUser> result = FollowComparisonEngine.getFollowersOnly(followers, following);
        long end = System.currentTimeMillis();

        assertEquals(1000, result.size());
        assertTrue("Comparison took too long: " + (end - start) + "ms", (end - start) < 500);
    }
}
