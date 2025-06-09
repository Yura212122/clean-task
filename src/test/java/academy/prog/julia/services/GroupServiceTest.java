package academy.prog.julia.services;

import academy.prog.julia.model.Group;
import academy.prog.julia.model.User;
import academy.prog.julia.repos.GroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupService groupService;

    @Test
    void findAllNamesOfGroups() {

        when(groupRepository.findAllNames()).thenReturn(Arrays.asList("Group 1", "Group 2"));

        List<String> groupNames = groupService.findAllNamesOfGroups();

        assertEquals(2, groupNames.size());
        assertTrue(groupNames.contains("Group 1"));
        assertTrue(groupNames.contains("Group 2"));
    }

    @Test
    void findGroupByName_Found() {

        //Group found
        Group group = new Group("Group1", new HashSet<>());
        when(groupRepository.findByName("Group1")).thenReturn(Optional.of(group));

        Optional<Group> foundGroup = groupService.findGroupByName("Group1");

        assertTrue(foundGroup.isPresent());
        assertEquals("Group1", foundGroup.get().getName());

        //Group not found
        reset();
        String groupName = "NonexistentGroup";
        when(groupRepository.findByName(groupName)).thenReturn(Optional.empty());


        Optional<Group> notFoundGroup = groupService.findGroupByName(groupName);

        assertFalse(notFoundGroup.isPresent());
    }


    @Test
    void isStudentInGroup_Found() {
        Long userId = 1L;
        Long groupId = 1L;
        User user = new User("Name", "Surname", "00000000000", "Email", "Password");
        user.setId(1L);
        Group group = new Group("Java1", new HashSet<>());
        group.addClient(user);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        boolean isStudentInGroup = groupService.isStudentInGroup(userId, groupId);

        assertTrue(isStudentInGroup);
    }

    @Test
    void isStudentInGroup_NotFound() {
        Long userId = 1L;
        Long groupId = 1L;
        Group group = new Group("Java1", new HashSet<>());

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        boolean isStudentInGroup = groupService.isStudentInGroup(userId, groupId);

        assertFalse(isStudentInGroup);
    }

    @Test
    void isStudentInGroup_GroupNotFound() {
        Long userId = 1L;
        Long groupId = 1L;
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        boolean isStudentInGroup = groupService.isStudentInGroup(userId, groupId);

        assertFalse(isStudentInGroup);
    }
}
