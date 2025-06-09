package academy.prog.julia.services;

import academy.prog.julia.model.Group;
import academy.prog.julia.repos.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for handling operations related to groups.
 * This class provides methods to interact with the GroupRepository for retrieving group information,
 * checking membership, and fetching group names.
 */
@Service
public class GroupService {

    private final GroupRepository groupRepository;

    /**
     * Constructor to inject the GroupRepository dependency.
     *
     * @param groupRepository the repository for group data access.
     */
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    /**
     * Retrieves the names of all groups from the database.
     *
     * @return a list of group names.
     */
    @Transactional(readOnly = true)
    public List<String> findAllNamesOfGroups() {
        return groupRepository.findAllNames();
    }

    /**
     * Finds a group in the database by its name.
     *
     * @param name the name of the group to search for.
     * @return an Optional containing the found Group, or an empty Optional if not found.
     */
    @Transactional(readOnly = true)
    public Optional<Group> findGroupByName(String name) {
        return groupRepository.findByName(name);
    }

    /**
     * Checks if a specific student (identified by userId) is a member of a specific group (identified by groupId).
     *
     * @param userId the ID of the user to check.
     * @param groupId the ID of the group to check.
     * @return true if the user is in the group, false otherwise.
     */
    @Transactional(readOnly = true)
    public boolean isStudentInGroup(Long userId, Long groupId) {
        Optional<Group> groupOptional = groupRepository.findById(groupId);

        return groupOptional.map(group ->
                        group.getClients()
                                .stream()
                                .anyMatch(user -> user.getId().equals(userId)))
                        .orElse(false)
        ;
    }

}
