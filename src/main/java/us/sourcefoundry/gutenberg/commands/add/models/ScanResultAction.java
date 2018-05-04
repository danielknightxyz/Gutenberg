package us.sourcefoundry.gutenberg.commands.add.models;

/**
 * This allows the scan and the installation processes know how to handle a forme found in a location.
 */
public enum ScanResultAction {
    INSTALL, INSTALL_WITH_PREEXISTING, ALREADY_INSTALLED, REPLACE
}
