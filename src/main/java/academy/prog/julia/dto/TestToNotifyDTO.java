package academy.prog.julia.dto;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) for representing a test to notify users about.
 *
 * 09/09/2024 - current DTO is not used.
 */
public class TestToNotifyDTO {

    private String nameTest;
    private String nameLesson;
    private List<String> groupNames = new ArrayList<>();

    /**
     * Default constructor for creating an empty TestToNotifyDTO.
     */
    public TestToNotifyDTO() {
    }

    /**
     * Constructs a TestToNotifyDTO with the specified values.
     *
     * @param nameTest the name of the test
     * @param nameLesson the name of the lesson associated with the test
     * @param groupNames the list of group names related to the test
     */
    public TestToNotifyDTO(
            String nameTest,
            String nameLesson,
            List<String> groupNames
    ) {
        this.nameTest = nameTest;
        this.nameLesson = nameLesson;
        this.groupNames = groupNames;
    }

    /**
     * Gets the name of the test.
     *
     * @return the name of the test
     */
    public String getNameTest() {
        return nameTest;
    }

    /**
     * Sets the name of the test.
     *
     * @param nameTest the new name to be set for the test
     */
    public void setNameTest(String nameTest) {
        this.nameTest = nameTest;
    }

    /**
     * Gets the name of the lesson associated with the test.
     *
     * @return the name of the lesson
     */
    public String getNameLesson() {
        return nameLesson;
    }

    /**
     * Sets the name of the lesson associated with the test.
     *
     * @param nameLesson the new name to be set for the lesson
     */
    public void setNameLesson(String nameLesson) {
        this.nameLesson = nameLesson;
    }

    /**
     * Gets the list of group names related to the test.
     *
     * @return the list of group names
     */
    public List<String> getGroupNames() {
        return groupNames;
    }

    /**
     * Sets the list of group names related to the test.
     *
     * @param groupNames the new list of group names to be set
     */
    public void setGroupNames(List<String> groupNames) {
        this.groupNames = groupNames;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * Two TestToNotifyDTO objects are considered equal if all their fields are equal.
     *
     * @param o the object to compare with
     * @return true if this object is equal to the other object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestToNotifyDTO that = (TestToNotifyDTO) o;

        return Objects.equals(nameTest, that.nameTest) &&
                Objects.equals(nameLesson, that.nameLesson) &&
                Objects.equals(groupNames, that.groupNames)
        ;
    }

    /**
     * Returns a hash code value for the object.
     *
     * The hash code is computed based on the values of the fields that are used in equals().
     *
     * @return the hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                nameTest,
                nameLesson,
                groupNames
        );
    }

}
