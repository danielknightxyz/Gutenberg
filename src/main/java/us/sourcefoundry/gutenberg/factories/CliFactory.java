package us.sourcefoundry.gutenberg.factories;

import us.sourcefoundry.gutenberg.services.CliService;

/**
 * Creates a CliService object for the application.
 */
public class CliFactory extends AbstractFactory<CliService> {

    /**
     * New Instance
     *
     * @return CliService
     */
    @Override
    public CliService newInstance() {
        return this.getInstance(CliService.class);
    }

    /**
     * New Instance w/ arguments.
     *
     * @param args The application arguments.
     * @return CliService
     */
    public CliService newInstance(String[] args) {
        CliService newObj = this.getInstance(CliService.class);
        newObj.load(args);
        return newObj;
    }
}
