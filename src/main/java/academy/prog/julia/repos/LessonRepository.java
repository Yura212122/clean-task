package academy.prog.julia.repos;

import academy.prog.julia.model.Lesson;
import academy.prog.julia.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    /**
     * Finds lessons by the spreadsheet ID and sheet number.
     *
     * @param spreadsheetID the ID of the spreadsheet where the lessons are stored
     * @param sheetNumber   the sheet number within the spreadsheet
     * @return a list of lessons associated with the specified spreadsheet ID and sheet number
     */
    List<Lesson> findBySpreadsheetIDAndSheetNumber(String spreadsheetID, Integer sheetNumber);
    /**
     * Finds all distinct lessons associated with a specific group by group ID.
     * <p>
     * This method uses a JPQL query to join the {@code Lesson} and {@code Group} entities and
     * returns a list of distinct lessons for a given group.
     *
     * @param groupId the ID of the group to find lessons for
     * @return a list of distinct lessons associated with the specified group
     */
    @Query("SELECT DISTINCT l FROM Lesson l JOIN l.groups g WHERE g.id = :groupId")
    List<Lesson> findCourseLessons(@Param("groupId") Long groupId);
}
