#!/bin/bash
#
# Gutenberg Installation
#
# Make the application directory
sudo mkdir -p /usr/local/gutenberg/bin
sudo mkdir -p /usr/local/gutenberg/scripts
# Copy the jar file into the application directory.
sudo cp build/libs/gutenberg-*.jar /usr/local/gutenberg/bin
# Copy the runner script into place.
sudo cp etc/gutenberg.sh /usr/local/gutenberg/scripts
# Make the runner script executable.
sudo chmod +x /usr/local/gutenberg/scripts/gutenberg.sh
# Symlink the scripts.
ln -sf /usr/local/gutenberg/scripts/gutenberg.sh /usr/local/bin/gutenberg
# They short hand.
ln -sf /usr/local/gutenberg/scripts/gutenberg.sh /usr/local/bin/gbg
