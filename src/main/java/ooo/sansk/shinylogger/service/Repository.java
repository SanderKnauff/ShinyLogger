package ooo.sansk.shinylogger.service;

import java.util.Collection;
import java.util.Optional;

public interface Repository<I, T> {

    /**
     * Load the repository from the file system or database. This method should always be called before using a repository
     */
    void loadAll();

    /**
     * Get all items currently in the repository
     *
     * @return A collection containing all items in the repository
     */
    Collection<T> getAll();

    /**
     * Find a single value in the repository with a given id
     *
     * @param id The id
     * @return An optional that might contain the object if one with the same id was found in the repository
     */
    Optional<T> findOne(I id);

    /**
     * Adds an item to the repository.
     * This method should automatically detect Id collissions and overwrite the old value with the new item
     *
     * @param item
     */
    void addOne(T item);

    /**
     * Save the repository to disk or file system. This should either be called after a mutation or on a timed basis
     */
    void save();

    /**
     * Removes the entry with the provided id
     * @param id the id to search for
     * @return the deleted item
     */
    Optional<T> removeOne(I id);

    /**
     * Removes all data from the repository
     */
    void removeAll();
    /**
     * Adds a collection of items to the repository. By default this uses the addOne implementation of the repository for each item in the collection.
     *
     * @param all
     */
    default void addAll(T... all) {
        for (T item : all) {
            addOne(item);
        }
    }
}
