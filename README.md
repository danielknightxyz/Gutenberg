# The Gutenberg Utility

Gutenberg is a small utility which allows you to create templates and scaffolding for any file asset creation you're doing redundantly.  For example, if you're a Java software developer and you find yourself creating the same Spring-Boot application boiler plate or scaffolding repeatedly, you can use Gutenberg to create your perfect Spring-Boot template and generate your projects with ease.

### Installation

#### Install with Homebrew

To install with Homebrew you'll need to add the tap and install.  The commands are listed below.

```bash
brew tap sourcefoundryus/gutenberg
brew install gutenberg
```

Once its installed you can run ```gutenberg``` to make sure its installed. You should see:

```
Gutenberg is installed! See help for usage.
```

### Principles

Gutenberg is small, which means that everything it does it needs to be principled and thoughtful as to not create confusion in terms of purpose and usage. 

* Templates consist of a Forme file, Mustache templates, and static assets.
* Template instructions / definition files are called Formes and are written in [YAML](http://yaml.org/).  These files must be in the root of the template.
* Templates are written using [Mustache](https://mustache.github.io/).
* Templates should be single purposed.  Meaning a single template should not generate both a Spring-Boot and a Play project.   This would require two templates.

### Audience

The target audience is humans looking to reduce the amount of time spent on redundant/predicable asset creation.  While I'm sure Gutenberg can be used in a continuous deployment / server capacity, this is not directly supported.

### Getting Started

The first thing you need to know to start using Gutenberg is two actions:

* ```add``` - This action will add templates from a Github location.
* ```build``` - This action will build out file assets.

#### Add a Template

To add the getting started template run the following command:

```bash
gutenberg add sourcefoundryus/gbg-getting-started
```

You should see something like the following:

```
> Add from sourcefoundryus/gbg-getting-started
# Downloading https://api.github.com/repos/sourcefoundryus/gbg-getting-started/tarball/master
# 1 formes found: gbg-getting-started
> gbg-getting-started added
```

Notice the last line?  It states that the getting-started template was added to your local inventory.  So now you can build that template anytime you need to.

#### Build

Now lets build.  First we need a directory to hold our built assets.  Make a new directory called 'hello-world' and change directory into it.

```bash
mkdir hello-world && cd hello-world
```

Now run the build process using the template you added before:

```bash
gutenberg build gbg-getting-started
```

When prompted type your name or hit enter for the default.  You should now see the following:

```
Building Forme "gbg-getting-started"
What is your name? [world]: 
+ Creating File... /PATH/TO/FOLDER/hello-world/helloworld.txt
```

When you list the contents of the directory, you'll notice a new file named 'helloworld.txt' and inside that file it says 'hello, ' and the answer your provided at the prompt.  [Here](https://github.com/sourcefoundryus/gbg-getting-started) is the source to the template on Github.

### The Forme File

To use Gutenberg, the first thing you need to do is create a `forme.yml` file.  This file will contain all the instructions which Gutenberg will perform on your behalf.

The Forme file will contains the following sections:

* **Maintainer Information** - The name and contact information of the person responsible for the forme.
* **Forme Variables** - Variables available to all directories and files.
* **Forme Meta Data** - Meta data available to all directories and files.
* **Variable Prompts** - Any variables which will need to supplied by the user at runtime via command line prompts.  These are a great way to allow the user to customize at runtime.
* **Directories** - Any directories which need to be created.
* **Files Templates** - Any files which need to be created.  These can be templated using [Mustache](https://mustache.github.io/).
* **Static Copies** - Any files which need to be copied AS IS into place.

Gutenberg takes advantage of [Mustache](https://mustache.github.io/) templates to produce files.
