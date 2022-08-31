package us.hebi.histogram.visualizer.themes;

import javafx.application.Application;

/**
 * @author Florian Enner
 * @since 24 Aug 2022
 */
public enum Theme {
    PrimerLight("primer-light.css"),
    PrimerDark("primer-dark.css"),
    NordLight("nord-light.css"),
    NordDark("nord-dark.css");

    private Theme(String resource) {
        userAgentStyleSheet = Theme.class.getResource(resource).toExternalForm();
    }

    public void setTheme() {
        Application.setUserAgentStylesheet(userAgentStyleSheet);
    }

    private final String userAgentStyleSheet;
    public static final Theme DEFAULT = NordLight;

}