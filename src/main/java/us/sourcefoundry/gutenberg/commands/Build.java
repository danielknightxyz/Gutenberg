package us.sourcefoundry.gutenberg.commands;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import us.sourcefoundry.gutenberg.factories.FormeFactory;
import us.sourcefoundry.gutenberg.factories.InventoryFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.FormeInventoryItem;
import us.sourcefoundry.gutenberg.models.forme.Forme;
import us.sourcefoundry.gutenberg.models.templates.AnswersFileTemplate;
import us.sourcefoundry.gutenberg.services.Cli;
import us.sourcefoundry.gutenberg.services.Console;
import us.sourcefoundry.gutenberg.services.FileSystemService;
import us.sourcefoundry.gutenberg.services.UserPromptService;
import us.sourcefoundry.gutenberg.utils.Pair;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class will execute the BUILD action for Gutenberg.
 */
public class Build implements Command {

    //The application context.
    private ApplicationContext applicationContext;
    //The CLI.
    private Cli cli;

    /**
     * Constructor.
     *
     * @param applicationContext The application context.
     * @param cli                The cli service.
     */
    @Inject
    public Build(ApplicationContext applicationContext, Cli cli) {
        this.applicationContext = applicationContext;
        this.cli = cli;
    }

    /**
     * Executes the BUILD action.
     */
    @Override
    public void execute() {
        try {

            (new Console()).message("\nBuilding...\n");

            //Get the forme location.
            String formeLocation = this.determineFormeLocation();

            //If forme was not found, then just bounce out.
            if (formeLocation == null)
                return;

            //Get the forme file and make sure it exists.
            Forme forme = this.getFormeFile(formeLocation);

            //Only continue if the forme was found.
            if (forme == null)
                return;

            //Get the build path for the generated files.
            String buildPath = this.getBuildPath();

            //Check to newInstance sure the output directory is available.
            if (!this.checkBuildPath((new FileSystemService()).getByLocation(buildPath), this.cli.hasOption("f")))
                return;

            //Run any prompts the forme may require.
            HashMap<String, Object> userResponses = this.getUserResponseToPrompts(forme, cli);

            /*
             * Run the process bellow.
             */
            //Show the message that the build is starting.
            (new Console()).message("Building Forme \"{0}\"", forme.getName());
            //Make the directories.
            forme.getDirectories().forEach(d -> d.create(buildPath, forme, userResponses));
            //Make the files from the templates.
            forme.getFiles().forEach(f -> f.create(formeLocation, buildPath, forme, userResponses));
            //Perform the static directory copy.  This is the same data but we do the directory entries first, since
            // they will need to exist in case a file copy needs it.
            forme.getCopy().stream().filter(c -> c.getType().equals("directory")).forEach(c -> c.copy(formeLocation, buildPath, forme, userResponses));
            //Perform the static file copy.
            forme.getCopy().stream().filter(c -> c.getType().equals("file")).forEach(c -> c.copy(formeLocation, buildPath, forme, userResponses));

            //If the user wants their answers saved, then this will save those answers for use in later runs.
            if (cli.hasOption("s"))
                this.saveUserAnswersFromPrompts(cli, userResponses);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This will find the forme file given a location on the file system.
     *
     * @param formeLocation The system path to the file.
     * @return Forme
     */
    private Forme getFormeFile(String formeLocation) throws FileNotFoundException {
        //Get the forme file and make sure it exists.
        File formeFile = (new FileSystemService()).getByLocation("{0}/forme.yml", formeLocation);
        if (!formeFile.exists()) {
            (new Console()).error("! Could not locate a forme file in source location.  Does it needs to be initialized?");
            return null;
        }

        //Parse it into a forme object.
        return (new FormeFactory()).newInstance(
                (new FileSystemService()).streamFile(formeFile)
        );
    }

    /**
     * Get the build path from the cli.  If build path is not specified, then it uses the current working directory.
     *
     * @return String
     */
    private String getBuildPath() {
        return (this.cli.hasOption("o") ?
                this.cli.getOptionValue("o") :
                this.applicationContext.getWorkingDirectory()
        );
    }

    /**
     * Determines the location of the Forme file.  First by seeing if the forme location is being supplied by the user
     * via the local option.  If not, its going to check and make sure there's a forme name provided as an argument.  If
     * the forme name is given as a argument, this will be looked up in the inventory.
     *
     * @return String
     */
    private String determineFormeLocation() {
        //If the forme is local, then look some where other than the inventory; which will be supplied by the user.
        if (this.cli.hasOption("local"))
            //Get local forme.
            return this.cli.getOptionValue("local").equals(".") ? this.applicationContext.getWorkingDirectory() : this.cli.getOptionValue("local");

        //Else, lets check out the inventory.
        String formeName = (this.cli.getArgList().get(1) != null ? this.cli.getArgList().get(1).toString() : null);

        //If the forme name is not provided, return null.
        if (formeName == null)
            return null;

        return this.determineInventoryFormeLocation(formeName);
    }

    /**
     * This will look up the installation path of a forme from the installed inventory.  If the inventory doesn't exist,
     * then null will be returned.
     *
     * @param formeName The name of the forme to look up in inventory.
     * @return
     */
    private String determineInventoryFormeLocation(String formeName) {
        //Get the install directory.
        String installDir = this.applicationContext.getInstallDirectory();
        //Get the inventory.
        Map<String, FormeInventoryItem> inventory = (new InventoryFactory()).newInstance(installDir + "/inventory.json");

        //Check to make sure the inventory is valid.  If the user is trying to run a local forme, then we don't care
        //if the inventory is valid.
        if (inventory == null) {
            (new Console()).info("! No inventory found.");
            return null;
        }

        //If the forme is not in inventory, return null.
        if (!inventory.containsKey(formeName)) {
            (new Console()).info("! {0} not found in inventory.");
            return null;
        }

        //Get inventory forme.
        return installDir + "/formes/" + inventory.get(formeName).getInstallPath();
    }

    /**
     * This will use the user prompt service to get any required information from the user.  This also will use an answer
     * file if its provided in the answersfile option.
     *
     * @param forme The forme file.
     * @param cli   The command line.
     * @return Map of variable name to response.
     */
    private HashMap<String, Object> getUserResponseToPrompts(Forme forme, Cli cli) throws FileNotFoundException {
        Map<String, Object> answers = new HashMap<>();

        if (cli.hasOption("a")) {
            String answersFile = cli.getOptionValue("a");
            InputStream answerFileIS = new FileInputStream((new FileSystemService()).getByLocation(answersFile));
            Yaml parser = new Yaml(new Constructor(HashMap.class));
            answers = (HashMap<String, Object>) parser.load(answerFileIS);
        }

        //TODO: NOT RETURNING VALUES.
        return (new UserPromptService(forme).requestAnswers(answers));

    }

    /**
     * This will save user's answers to the anwser file.
     *
     * @param cli           The command line.
     * @param userResponses The user responses to prompts.
     */
    private void saveUserAnswersFromPrompts(Cli cli, HashMap<String, Object> userResponses) throws FileNotFoundException {
        List<Pair<Object, Object>> answers = new ArrayList<>();
        String answersFilePath = cli.getOptionValue("s");

        for (Map.Entry entry : userResponses.entrySet())
            answers.add(new Pair<>(entry.getKey(), entry.getValue()));

        (new Console()).message("\nCreating Answer File... {0}", answersFilePath);
        (new AnswersFileTemplate()).create(answersFilePath, answers);
    }

    /**
     * This function will check the build path and make sure its available for use as a build location.
     *
     * @param buildPath The location of the build location.
     * @param force     Should the build location be used regardless of readiness.
     * @return boolean
     */
    private boolean checkBuildPath(File buildPath, boolean force) {
        boolean outputDirectoryExists = buildPath.exists();
        boolean isDirectory = buildPath.isDirectory();

        if (outputDirectoryExists && !force) {
            boolean isEmptyDirectory = buildPath.list().length == 0;

            if (isDirectory && !isEmptyDirectory) {
                (new Console()).error("! Could not build. {0} exists and is not empty.\n", buildPath.getAbsolutePath());
                return false;
            }

            if (!isDirectory) {
                (new Console()).error("! Could not build. {0} exists and is not a directory.\n", buildPath.getAbsolutePath());
                return false;
            }
        }

        if (outputDirectoryExists && force && !isDirectory) {
            (new Console()).error("! Could not force build. {0} exists and is not a directory.\n", buildPath.getAbsolutePath());
            return false;
        }

        if (outputDirectoryExists && force)
            (new Console()).warning("# {0} already exists. Building anyways.\n", buildPath.getAbsolutePath());

        if(!outputDirectoryExists && !buildPath.mkdir()) {
            (new Console()).warning("# {0} did not exist and could not be created.\n", buildPath.getAbsolutePath());
            return false;
        }

        return true;
    }
}
