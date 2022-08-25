package us.hebi.histogram.visualizer.themes;

import javafx.application.Application;

/**
 * @author Florian Enner
 * @since 24 Aug 2022
 */
public enum Theme {
    Light("primer-light.css"),
    Dark("primer-dark.css");

    private Theme(String resource) {
        userAgentStyleSheet = Theme.class.getResource(resource).toExternalForm();
    }

    public void setTheme() {
        Application.setUserAgentStylesheet(userAgentStyleSheet);
    }

    private final String userAgentStyleSheet;
    public static final Theme DEFAULT = Dark;

}