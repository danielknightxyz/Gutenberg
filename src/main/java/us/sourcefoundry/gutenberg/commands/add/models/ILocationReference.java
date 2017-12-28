package us.sourcefoundry.gutenberg.commands.add.models;

public interface ILocationReference<T> {

    String getUser();

    String getRepository();

    String getTag();

    String toFQN();
}
