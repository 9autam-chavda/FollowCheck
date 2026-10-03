package com.example.followcheck.scanner;

import com.example.followcheck.scanner.dom.DomContext;
import java.util.List;

/**
 * Aggregated data captured during a collection session, potentially including
 * both followers and following lists.
 */
public class FollowData {
    private final List<String> followers;
    private final List<String> following;
    private final ScanCompleteness followersCompleteness;
    private final ScanCompleteness followingCompleteness;
    private final long collectedAt;
    private final String diagnosticMessage;

    public FollowData(List<String> followers, List<String> following, 
                      ScanCompleteness followersCompleteness, ScanCompleteness followingCompleteness,
                      long collectedAt, String diagnosticMessage) {
        this.followers = followers;
        this.following = following;
        this.followersCompleteness = followersCompleteness;
        this.followingCompleteness = followingCompleteness;
        this.collectedAt = collectedAt;
        this.diagnosticMessage = diagnosticMessage;
    }

    public List<String> getFollowers() { return followers; }
    public List<String> getFollowing() { return following; }
    public ScanCompleteness getFollowersCompleteness() { return followersCompleteness; }
    public ScanCompleteness getFollowingCompleteness() { return followingCompleteness; }
    public long getCollectedAt() { return collectedAt; }
    public String getDiagnosticMessage() { return diagnosticMessage; }
}
