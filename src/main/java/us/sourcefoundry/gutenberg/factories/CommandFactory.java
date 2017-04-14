package us.sourcefoundry.gutenberg.factories;

import us.sourcefoundry.gutenberg.commands.*;
import us.sourcefoundry.gutenberg.utils.DependencyInjector;

public class CommandFactory extends AbstractFactory<Command> {

    @Override
    public Command newInstance() {
        return null;
    }

    public Command newInstance(String cliCommand) {
        switch (cliCommand.toLowerCase()) {
            case "add":
                return this.getCommandInstance(Add.class);
            case "list":
                return this.getCommandInstance(ListInventory.class);
            case "init":
                return this.getCommandInstance(Init.class);
            case "build":
                return this.getCommandInstance(Build.class);
            default:
                return this.getCommandInstance(Unknown.class);
        }
    }

    private <T extends Command> T getCommandInstance(Class<T> classOfT) {
        return DependencyInjector.getInstance(classOfT);
    }
}
