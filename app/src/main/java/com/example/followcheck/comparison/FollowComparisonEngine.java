package com.example.followcheck.comparison;

import com.example.followcheck.data.model.InstagramUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Engine for comparing Instagram follow relationships.
 * Normalization Rule: All user identifiers are converted to lowercase for comparison.
 */
public class FollowComparisonEngine {

    private static String normalize(String identifier) {
        if (identifier == null) return "";
        return identifier.toLowerCase(Locale.ROOT).trim();
    }

    /**
     * People the user follows but who do NOT follow the user.
     * Formula: Following - Followers
     */
    public static List<InstagramUser> getNonFollowers(List<InstagramUser> followers, List<InstagramUser> following) {
        Set<String> followerIds = new HashSet<>();
        for (InstagramUser user : followers) {
            followerIds.add(normalize(user.getId()));
        }

        List<InstagramUser> result = new ArrayList<>();
        for (InstagramUser user : following) {
            if (!followerIds.contains(normalize(user.getId()))) {
                result.add(user);
            }
        }
        return result;
    }

    /**
     * People who follow the user but the user does not follow back.
     * Formula: Followers - Following
     */
    public static List<InstagramUser> getFollowersOnly(List<InstagramUser> followers, List<InstagramUser> following) {
        Set<String> followingIds = new HashSet<>();
        for (InstagramUser user : following) {
            followingIds.add(normalize(user.getId()));
        }

        List<InstagramUser> result = new ArrayList<>();
        for (InstagramUser user : followers) {
            if (!followingIds.contains(normalize(user.getId()))) {
                result.add(user);
            }
        }
        return result;
    }

    /**
     * People who follow the user AND are followed by the user.
     * Formula: Followers ∩ Following
     */
    public static List<InstagramUser> getMutuals(List<InstagramUser> followers, List<InstagramUser> following) {
        Set<String> followingIds = new HashSet<>();
        for (InstagramUser user : following) {
            followingIds.add(normalize(user.getId()));
        }

        List<InstagramUser> result = new ArrayList<>();
        for (InstagramUser user : followers) {
            if (followingIds.contains(normalize(user.getId()))) {
                result.add(user);
            }
        }
        return result;
    }

    /**
     * People who followed the user since the last scan.
     * Formula: Current Followers - Previous Followers
     */
    public static List<InstagramUser> getNewFollowers(List<InstagramUser> previousFollowers, List<InstagramUser> currentFollowers) {
        Set<String> prevIds = new HashSet<>();
        for (InstagramUser user : previousFollowers) {
            prevIds.add(normalize(user.getId()));
        }

        List<InstagramUser> result = new ArrayList<>();
        for (InstagramUser user : currentFollowers) {
            if (!prevIds.contains(normalize(user.getId()))) {
                result.add(user);
            }
        }
        return result;
    }

    /**
     * People who stopped following the user since the last scan.
     * Formula: Previous Followers - Current Followers
     */
    public static List<InstagramUser> getLostFollowers(List<InstagramUser> previousFollowers, List<InstagramUser> currentFollowers) {
        Set<String> currentIds = new HashSet<>();
        for (InstagramUser user : currentFollowers) {
            currentIds.add(normalize(user.getId()));
        }

        List<InstagramUser> result = new ArrayList<>();
        for (InstagramUser user : previousFollowers) {
            if (!currentIds.contains(normalize(user.getId()))) {
                result.add(user);
            }
        }
        return result;
    }

    /**
     * People the user started following since the last scan.
     */
    public static List<InstagramUser> getNewFollowing(List<InstagramUser> previousFollowing, List<InstagramUser> currentFollowing) {
        Set<String> prevIds = new HashSet<>();
        for (InstagramUser user : previousFollowing) {
            prevIds.add(normalize(user.getId()));
        }

        List<InstagramUser> result = new ArrayList<>();
        for (InstagramUser user : currentFollowing) {
            if (!prevIds.contains(normalize(user.getId()))) {
                result.add(user);
            }
        }
        return result;
    }

    /**
     * People the user stopped following since the last scan.
     */
    public static List<InstagramUser> getRemovedFollowing(List<InstagramUser> previousFollowing, List<InstagramUser> currentFollowing) {
        Set<String> currentIds = new HashSet<>();
        for (InstagramUser user : currentFollowing) {
            currentIds.add(normalize(user.getId()));
        }

        List<InstagramUser> result = new ArrayList<>();
        for (InstagramUser user : previousFollowing) {
            if (!currentIds.contains(normalize(user.getId()))) {
                result.add(user);
            }
        }
        return result;
    }
}
