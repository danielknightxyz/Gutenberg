# Gutenberg Template Utility

#### What can you do with Gutenberg?

You can template-ize any process which you find yourself doing over and over and over again.  

Take for example the case of a Java developer who uses Spring-Boot and finds themselves doing the following tasks repeatably every time there's a new API or project.

1. Create the standard directories.
1. Add a custom repository for his office Artifactory server.  
1. Create standard Gradle build w/ dependencies.
1. Create a main class with the correct annotations.
1. Set project settings.
1. Set proper java versions.
1. Bring in and customize his team's agreed upon Docker setup to the new project.

And so on...

Other types of work you can do with Gutenberg - 

* Template project documentation.
* Template design projects.
* Template document boiler plates.

All this can now be accomplished and shared using Gutenberg!

#### How it works

To use Gutenberg, the first thing you need to do is create a `forme.yml` file.  This file will contain all the instructions which Gutenberg will perform on your behalf.

The Forme file will contains the following sections:

* **Maintainer Information** - The name and contact information of the person responsible for the forme.
* **Forme Variables** - Variables available to all directory and file.
* **Forme Meta Data** - Meta data available to all directory and file.
* **Variable Prompts** - Any variables which will need to supplied by the user at runtime via command line prompts.  These are a great way to allow the user to customize at runtime.
* **Directories** - Any directories which need to be created.
* **Files Templates** - Any files which need to be created based off Mustache templates.
* **Static Copies** - Any files which need to be copied AS IS into place.

Gutenberg takes advantage of [Mustache](https://mustache.github.io/) templates to produce files.
