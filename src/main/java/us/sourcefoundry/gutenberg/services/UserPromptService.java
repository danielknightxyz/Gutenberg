package us.sourcefoundry.gutenberg.services;

import us.sourcefoundry.gutenberg.models.forme.Forme;
import us.sourcefoundry.gutenberg.models.forme.VarPrompt;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This service will prompt the user for answers.
 */
public class UserPromptService {

    //The template for the user prompt.
    private final static String PROMPT_TEMPLATE = "{0} [{1}]: ";

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
    public HashMap<String, Object> requestAnswers() {
        //Map containing the user responses.
        HashMap<String, Object> userResponses = new HashMap<>();

        //For each prompt in the forme, prompt the user.
        for (VarPrompt prompt : this.forme.getPrompts()) {
            Scanner reader = new Scanner(System.in);
            System.out.print(MessageFormat.format(
                    PROMPT_TEMPLATE,
                    prompt.getMessage(),
                    prompt.getDefaultValue()
                    )
            );
            String response = reader.nextLine();

            //If the response is empty, then use the default value of the prompt.
            if (response.equals(""))
                response = prompt.getDefaultValue();

            //Add the response to the user responses.
            userResponses.put(prompt.getName(), response);
        }

        return userResponses;
    }
}
