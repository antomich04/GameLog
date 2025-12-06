package org.gamelog.utils;

import javafx.scene.Parent;
import org.gamelog.model.SessionManager;
import java.util.HashMap;
import java.util.Map;

public class ThemeManager {

    //Maps Page Keys
    private static final Map<String, String[]> THEME_MAP = new HashMap<>();

    static {
        // Settings Page
        THEME_MAP.put("Settings", new String[]{
                "/org/gamelog/Styles/Settings_css/SettingsPage.css",
                "/org/gamelog/Styles/Settings_css/SettingsPage_dark.css"
        });

        // Account Settings Page
        THEME_MAP.put("AccountSettings", new String[]{
                "/org/gamelog/Styles/Account_Settings_css/AccountSettingsPage.css",
                "/org/gamelog/Styles/Account_Settings_css/AccountSettingsPage_dark.css"
        });

        // Home Page
        THEME_MAP.put("Home", new String[]{
                "/org/gamelog/Styles/Home_Page_css/HomePage.css",
                "/org/gamelog/Styles/Home_Page_css/HomePage_dark.css"
        });

        // Backlog Page
        THEME_MAP.put("Backlog", new String[]{
                "/org/gamelog/Styles/Backlog_Page_css/BackLogPage.css",
                "/org/gamelog/Styles/Backlog_Page_css/BackLogPage_dark.css"
        });

        // Favorites Page
        THEME_MAP.put("Favorites", new String[]{
                "/org/gamelog/Styles/Favorites_Page_css/FavoritesPage.css",
                "/org/gamelog/Styles/Favorites_Page_css/FavoritesPage_dark.css"
        });

        // Navigation Bar
        THEME_MAP.put("NavBar", new String[]{
                "/org/gamelog/Styles/Navigation_Bar_css/NavigationBar.css",
                "/org/gamelog/Styles/Navigation_Bar_css/NavigationBar_dark.css"
        });

        THEME_MAP.put("AddGameModal", new String[]{
                "/org/gamelog/Styles/Add_Game_Modal_css/AddGameModal.css",
                "/org/gamelog/Styles/Add_Game_Modal_css/AddGameModal_dark.css"
        });

        THEME_MAP.put("GameCard", new String[]{
                "/org/gamelog/Styles/Game_Cards_css/GameCards.css",
                "/org/gamelog/Styles/Game_Cards_css/GameCards_dark.css"
        });
    }

    //Applies the correct theme to the given root based on SessionManager state.
    public static void applyTheme(Parent root, String pageKey) {
        if (root == null || !THEME_MAP.containsKey(pageKey)) return;

        //Uses default Light Mode if session is null
        boolean isDark = false;
        if (SessionManager.getInstance() != null) {
            isDark = SessionManager.getInstance().isDarkMode();
        }

        String[] paths = THEME_MAP.get(pageKey);
        String cssPath = isDark ? paths[1] : paths[0];

        //Clears existing sheets to avoid conflicts
        root.getStylesheets().clear();

        //Adds the new stylesheet
        if (ThemeManager.class.getResource(cssPath) != null) {
            root.getStylesheets().add(ThemeManager.class.getResource(cssPath).toExternalForm());
        }
    }
}