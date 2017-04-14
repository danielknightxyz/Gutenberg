package us.sourcefoundry.gutenberg.factories;


import us.sourcefoundry.gutenberg.utils.DependencyInjector;

/**
 * Abstract Factory
 */
public abstract class AbstractFactory<T> {

    /**
     * New Instance.
     *
     * @return Instance of T
     */
    public abstract T newInstance();

    /**
     * Get Instance
     *
     * @param classOfT The class type of the object to get.
     * @return T
     */
    protected T getInstance(Class<T> classOfT) {
        return DependencyInjector.getInstance(classOfT);
    }
}
