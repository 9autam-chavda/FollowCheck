package com.example.followcheck.scanner.dom;

import java.util.Locale;

/**
 * Represents a candidate relationship entry extracted from the DOM.
 */
public class DomCandidate {
    private String username;
    private String href;
    private boolean visible;

    public DomCandidate() {}

    public DomCandidate(String username, String href, boolean visible) {
        this.username = username;
        this.href = href;
        this.visible = visible;
    }

    public String getUsername() {
        return username != null ? username : "";
    }

    public String getHref() {
        return href;
    }

    public boolean isVisible() {
        return visible;
    }

    public String getNormalizedUsername() {
        return getUsername().trim().toLowerCase(Locale.ROOT);
    }
}
