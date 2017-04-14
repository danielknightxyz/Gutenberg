package us.sourcefoundry.gutenberg.services;

import us.sourcefoundry.gutenberg.models.forme.Forme;
import us.sourcefoundry.gutenberg.models.forme.VarPrompt;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Scanner;

public class UserPromptService {

    private final static String PROMPT_TEMPLATE = "{0} [{1}]: ";

    private Forme forme;

    public UserPromptService(Forme forme) {
        this.forme = forme;
    }

    public HashMap<String, Object> requestAnswers() {
        HashMap<String, Object> userResponses = new HashMap<>();

        for (VarPrompt prompt : this.forme.getPrompts()) {
            Scanner reader = new Scanner(System.in);
            System.out.print(MessageFormat.format(
                    PROMPT_TEMPLATE,
                    prompt.getMessage(),
                    prompt.getDefaultValue()
                    )
            );
            String response = reader.nextLine();

            if (response.equals("") || response == null)
                response = prompt.getDefaultValue();

            userResponses.put(prompt.getName(), response);
        }

        return userResponses;
    }
}
