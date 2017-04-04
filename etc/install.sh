#!/bin/bash
#
# Gutenberg Installation
#
# Make the application directory
sudo mkdir -p /usr/local/gutenberg
# Copy the jar file into the application directory.
sudo cp build/libs/gutenberg-*.jar /usr/local/gutenberg/
# Copy the runner script into place.
sudo cp etc/gutenberg.sh /usr/local/bin/gutenberg
# Make the runner script executable.
sudo chmod +x /usr/local/bin/gutenberg