#!/bin/bash
#
# Gutenberg
#

# Set the location of the installation.
APP_HOME="/usr/local/Gutenberg"
# Relay the command to the quikbus script.
java -jar $APP_HOME/bin/gutenberg-*.jar $@
