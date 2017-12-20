package us.sourcefoundry.gutenberg.services.console.types;

/**
 * This interface allows "formattable" console messages.
 */
interface IConsoleOut {

    /**
     * Run the formatting of a console message.
     *
     * @return String
     */
    String format();
}
