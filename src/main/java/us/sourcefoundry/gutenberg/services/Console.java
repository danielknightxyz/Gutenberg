package us.sourcefoundry.gutenberg.services;

import java.text.MessageFormat;
import java.util.HashMap;

public class Console {

    final private HashMap<String, String> colorCodes = new HashMap<String, String>() {{
        put("message", "32m");
        put("info", "36m");
        put("warning", "33m");
        put("error", "31m");
    }};

    public void message(String pattern, Object... args) {
        this.print(
                this.colorCodes.get("message"), pattern, args
        );
    }

    public void info(String pattern, Object... args) {
        this.print(
                this.colorCodes.get("info"), pattern, args
        );
    }

    public void warning(String pattern, Object... args) {
        this.print(
                this.colorCodes.get("warning"), pattern, args
        );
    }

    public void error(String pattern, Object... args) {
        this.print(
                this.colorCodes.get("error"), pattern, args
        );
    }

    public void print(String colorCode, String pattern, Object... args) {
        String preparedMessage = MessageFormat.format(pattern, args);
        String finalMessage = MessageFormat.format("\033[{0}{1}\033[0m", colorCode, preparedMessage);
        System.out.println(finalMessage);
    }
}
