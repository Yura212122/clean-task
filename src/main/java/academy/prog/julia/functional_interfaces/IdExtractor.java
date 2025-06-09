package academy.prog.julia.functional_interfaces;
/**
 * Functional interface for extracting an ID from an entity.
 *
 * This interface defines a method for retrieving the ID from an entity.
 * It can be used to extract IDs in various contexts, such as comparing entities or processing them based on their IDs.
 *
 * @param <T> the type of the entity
 */
@FunctionalInterface
public interface IdExtractor<T> {
    /**
     * Extracts the ID from the given entity.
     *
     * @param entity the entity from which to extract the ID
     * @return the ID of the entity
     */
    Long extractId(T entity);
}
