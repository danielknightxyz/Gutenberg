package us.sourcefoundry.gutenberg.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.sourcefoundry.gutenberg.models.FormeContext;
import us.sourcefoundry.gutenberg.models.VarPrompt;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Scanner;

public class UserPromptService {

    private final static String PROMPT_TEMPLATE = "{0} [{1}]: ";

    private FormeContext formeContext;

    public UserPromptService(FormeContext formeContext) {
        this.formeContext = formeContext;
    }

    public HashMap<String, Object> requestAnswers() {
        HashMap<String, Object> userResponses = new HashMap<>();

        for (VarPrompt prompt : this.formeContext.getPrompts()) {
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
