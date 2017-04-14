package us.sourcefoundry.gutenberg.utils;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Dependency Injector
 */
public class DependencyInjector {

    //The injector to use.
    private static Injector injector;

    /**
     * Init
     */
    public static void init() {
        injector = Guice.createInjector();
    }

    /**
     * Get Instance
     *
     * @param classOfT The class to create an instance for.
     * @param <T>      The class type.
     * @return Instance of type T.
     */
    public static <T> T getInstance(Class<T> classOfT) {
        return injector.getInstance(classOfT);
    }

    /**
     * Inject Memebers
     *
     * @param object The object to on which to perform injection.
     * @param <T>    Object Type
     */
    public static <T> void injectMember(T object) {
        injector.injectMembers(object);
    }
}
