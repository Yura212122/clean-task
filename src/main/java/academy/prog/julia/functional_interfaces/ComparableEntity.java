package academy.prog.julia.functional_interfaces;

/**
 * Functional interface for extracting the ID from an entity.
 *
 * This interface defines a method for retrieving the unique identifier (ID) of an entity.
 * Implementations of this interface can be used to compare entities based on their IDs.
 *
 * @param <T> the type of the entity
 */
@FunctionalInterface
public interface ComparableEntity<T> {
    /**
     * Extracts the ID from the given entity.
     *
     * @param entity the entity from which to extract the ID
     * @return the ID of the entity
     */
    Long getId(T entity);
}
