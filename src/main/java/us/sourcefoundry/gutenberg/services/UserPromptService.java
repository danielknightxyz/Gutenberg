package us.sourcefoundry.gutenberg.services;

import us.sourcefoundry.gutenberg.models.forme.Forme;
import us.sourcefoundry.gutenberg.models.forme.VarPrompt;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This service will prompt the user for answers.
 */
public class UserPromptService {

    //The template for the user prompt.
    private final static String PROMPT_TEMPLATE = "{0} [{1}]: ";

    private boolean first = true;

    //The forme, which will supply the prompts.
    private Forme forme;

    /**
     * Constructor
     *
     * @param forme The forme.
     */
    public UserPromptService(Forme forme) {
        this.forme = forme;
    }

    /**
     * Requests the user answers, using the forme for prompts.
     *
     * @return Map
     */
    public HashMap<String, Object> requestAnswers(Map<String, Object> providedAnswers) {
        //Map containing the user responses.
        HashMap<String, Object> userResponses = new HashMap<>();

        //For each prompt in the forme, prompt the user.
        for (VarPrompt prompt : this.forme.getPrompts()) {
            //If the answer was in the provided answers, don't prompt.
            String answer;

            if (!providedAnswers.containsKey(prompt.getName()))
                //Gets the answer from the user.
                answer = this.promptForAnswer(prompt);
            else
                //Gets the answer from the provided answers.
                answer = providedAnswers.get(prompt.getName()).toString();

            //If the response is empty, then use the default value of the prompt.
            if (answer == null || answer.equals(""))
                answer = prompt.getDefaultValue();

            //Add the response to the user responses.
            userResponses.put(prompt.getName(), answer);
        }

        return userResponses;
    }

    /**
     * Prompt the user for an answer.
     *
     * @param prompt The prompt.
     * @return String
     */
    private String promptForAnswer(VarPrompt prompt) {

        if(this.first) {
            (new Console()).message("To build, please answer the following questions:");
            this.first = false;
        }

        //Get the input scanner.
        Scanner reader = new Scanner(System.in);
        //Build the message and prompt.
        System.out.print(MessageFormat.format(
                PROMPT_TEMPLATE,
                prompt.getMessage(),
                prompt.getDefaultValue()
                )
        );
        //Return the response.
        return reader.nextLine();
    }
}
