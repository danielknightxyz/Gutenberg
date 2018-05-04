package us.sourcefoundry.gutenberg.commands.add.models;

/**
 * This is an interface for locations.
 */
public interface ILocationReference {

    //The user name.
    String getUser();

    //The data repository.
    String getRepository();

    //The version tag.
    String getTag();

    //The fully qualified name for the reference.
    String toFQN();
}
