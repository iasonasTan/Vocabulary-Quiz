package org.vocab.util;

import com.je.core.JeLib;
import com.je.io.configuration.Configuration;
import com.jjfx.context.Context;
import com.jjfx.utils.ExceptionWindow;
import com.jjfx.utils.MessageWindow;
import javafx.application.Platform;
import org.vocab.App;

public final class Utils {
    public static void showThemed(MessageWindow messageWindow) {
        boolean darkTheme = Configuration
                .loadBundle(App.SETTING_THEME_PATH)
                .getBoolean(App.DARK_THEME, false);
        messageWindow.showWindow(darkTheme);
    }

    public static void handleException(Context context, Throwable throwable) {
        JeLib.console().exception(throwable);
        JeLib.console().error("Could not load state. " + throwable);
        ExceptionWindow messageWindow = new ExceptionWindow(
                context.getRootStage(),
                throwable
        );
        messageWindow.addActionOk();
        messageWindow.addAction("Terminate Application", _ -> Platform.exit());
        Utils.showThemed(messageWindow);
    }

    /* Private constructor to prevent instantiation */
    private Utils() {}
}
