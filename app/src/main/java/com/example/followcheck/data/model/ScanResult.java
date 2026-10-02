package com.example.followcheck.data.model;

import java.util.List;

/**
 * Domain model representing the results of a follow relationship analysis.
 */
public class ScanResult {
    private final FollowSnapshot snapshot;
    private final List<InstagramUser> followers;
    private final List<InstagramUser> following;
    
    private List<InstagramUser> nonFollowers;
    private List<InstagramUser> followersOnly;
    private List<InstagramUser> mutuals;
    
    private List<InstagramUser> newFollowers;
    private List<InstagramUser> lostFollowers;
    private List<InstagramUser> newFollowing;
    private List<InstagramUser> removedFollowing;

    public ScanResult(FollowSnapshot snapshot, List<InstagramUser> followers, List<InstagramUser> following) {
        this.snapshot = snapshot;
        this.followers = followers;
        this.following = following;
    }

    public FollowSnapshot getSnapshot() {
        return snapshot;
    }

    public List<InstagramUser> getFollowers() {
        return followers;
    }

    public List<InstagramUser> getFollowing() {
        return following;
    }

    public List<InstagramUser> getNonFollowers() {
        return nonFollowers;
    }

    public void setNonFollowers(List<InstagramUser> nonFollowers) {
        this.nonFollowers = nonFollowers;
    }

    public List<InstagramUser> getFollowersOnly() {
        return followersOnly;
    }

    public void setFollowersOnly(List<InstagramUser> followersOnly) {
        this.followersOnly = followersOnly;
    }

    public List<InstagramUser> getMutuals() {
        return mutuals;
    }

    public void setMutuals(List<InstagramUser> mutuals) {
        this.mutuals = mutuals;
    }

    public List<InstagramUser> getNewFollowers() {
        return newFollowers;
    }

    public void setNewFollowers(List<InstagramUser> newFollowers) {
        this.newFollowers = newFollowers;
    }

    public List<InstagramUser> getLostFollowers() {
        return lostFollowers;
    }

    public void setLostFollowers(List<InstagramUser> lostFollowers) {
        this.lostFollowers = lostFollowers;
    }

    public List<InstagramUser> getNewFollowing() {
        return newFollowing;
    }

    public void setNewFollowing(List<InstagramUser> newFollowing) {
        this.newFollowing = newFollowing;
    }

    public List<InstagramUser> getRemovedFollowing() {
        return removedFollowing;
    }

    public void setRemovedFollowing(List<InstagramUser> removedFollowing) {
        this.removedFollowing = removedFollowing;
    }
}
