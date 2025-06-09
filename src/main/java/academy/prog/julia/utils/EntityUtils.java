package academy.prog.julia.utils;

import academy.prog.julia.functional_interfaces.ComparableEntity;
import academy.prog.julia.functional_interfaces.IdExtractor;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utility class for working with objects and their ID.
 */
public class EntityUtils {

    /**
     * Universal method for extracting an ID from an object.
     *
     * @param entity the object from which to extract the ID
     * @param extractor functional interface for extracting IDs
     * @param <T> the object type
     * @return The ID of the object or null if the object is null
     */
    public static <T> Long extractId(
            T entity,
            IdExtractor<T> extractor
    ) {
        return entity != null ? extractor.extractId(entity) : null;
    }

    /**
     * Extracts IDs from a collection of entities.
     *
     * @param collection the collection of entities
     * @param idExtractor a functional interface for extracting IDs
     * @param <T> the type of the entities
     * @return A list of IDs extracted from the collection
     */
    public static <T> List<Long> extractIds(
            Collection<T> collection,
            IdExtractor<T> idExtractor
    ) {
        return collection.stream()
                .map(entity -> extractId(entity, idExtractor))
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
        ;
    }

    /**
     * Compares two entities for equality based on their IDs.
     *
     * This method checks if both entities are null, in which case they are considered equal.
     * If one of the entities is null and the other is not, they are considered not equal.
     * If both entities are not null, their IDs are compared to determine equality.
     *
     * @param entity1 the first entity to compare
     * @param entity2 the second entity to compare
     * @param comparator a functional interface that provides a method to extract IDs from entities
     * @param <T> the type of the entities
     * @return true if both entities are equal based on their IDs, or if both are null; false otherwise
     */
    public static  <T> boolean areEntitiesEqualById(
            T entity1,
            T entity2,
            ComparableEntity<T> comparator
    ) {
        if (entity1 == null && entity2 == null) {
            return true;
        }

        if (entity1 == null || entity2 == null) {
            return false;
        }

        return Objects.equals(comparator.getId(entity1), comparator.getId(entity2));
    }

    /**
     * Compares two collections of entities for equality based on their IDs.
     *
     * @param collection1 the first collection to compare
     * @param collection2 the second collection to compare
     * @param idExtractor a functional interface that provides a method to extract IDs from entities
     * @param <T> the type of the entities in the collections
     * @return true if both collections are equal based on their IDs, or if both are null; false otherwise
     */
    public static <T> boolean areCollectionsEqualById(
            Collection<T> collection1,
            Collection<T> collection2,
            IdExtractor<T> idExtractor
    ) {
        if (collection1 == null && collection2 == null) {
            return true;
        }

        if (collection1 == null || collection2 == null) {
            return false;
        }

        if (collection1.size() != collection2.size()) {
            return false;
        }

        for (T entity1 : collection1) {
            boolean found = collection2.stream()
                    .anyMatch(entity2 -> Objects.equals(
                            idExtractor.extractId(entity1),
                            idExtractor.extractId(entity2)
                    ))
            ;

            if (!found) {
                return false;
            }
        }

        return true;
    }

}
