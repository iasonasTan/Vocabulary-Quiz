package org.vocab.util;

import com.je.io.configuration.Configuration;
import com.jjfx.utils.MessageWindow;
import org.vocab.App;

public final class MessageWindowShower {
    public static void showThemedMessageWindow(MessageWindow messageWindow) {
        boolean darkTheme = Configuration
                .loadBundle(App.SETTING_THEME_PATH)
                .getBoolean(App.DARK_THEME, false);
        messageWindow.showWindow(darkTheme);
    }

    /* Private constructor to prevent instantiation */
    private MessageWindowShower() {}
}
