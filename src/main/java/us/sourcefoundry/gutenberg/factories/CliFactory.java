package us.sourcefoundry.gutenberg.factories;

import us.sourcefoundry.gutenberg.services.Cli;

public class CliFactory extends AbstractFactory<Cli> {

    /**
     * New Instance
     *
     * @return Cli
     */
    @Override
    public Cli newInstance() {
        return this.getInstance(Cli.class);
    }

    /**
     * New Instance w/ arguments.
     *
     * @param args The application arguments.
     * @return Cli
     */
    public Cli newInstance(String[] args) {
        Cli newObj = this.getInstance(Cli.class);
        newObj.load(args);
        return newObj;
    }
}
