package us.hebi.histogram.visualizer.styles;

import atlantafx.base.theme.*;
import javafx.application.Application;

/**
 * @author Florian Enner
 * @since 24 Aug 2022
 */
public enum Theme {
    PrimerLight(new PrimerLight()),
    PrimerDark(new PrimerDark()),
    NordLight(new NordLight()),
    NordDark(new NordDark()),
    TestTheme(new AbstractTheme() {
        @Override
        public String getName() {
            return "Test Theme";
        }

        @Override
        public String getUserAgentStylesheet() {
            return Theme.class.getResource("test-theme.css").toExternalForm();
        }

        @Override
        public boolean isDarkMode() {
            return false;
        }
    });

    private Theme(AbstractTheme theme) {
        this.theme = theme;
    }

    public void setTheme() {
        Application.setUserAgentStylesheet(theme.getUserAgentStylesheet());
    }

    @Override
    public String toString() {
        return theme.getName();
    }

    AbstractTheme theme;
    public static final Theme DEFAULT = NordLight;

}