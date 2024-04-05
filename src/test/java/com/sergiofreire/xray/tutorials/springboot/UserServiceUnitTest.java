package com.sergiofreire.xray.tutorials.springboot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Mock.Strictness;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sergiofreire.xray.tutorials.springboot.data.User;
import com.sergiofreire.xray.tutorials.springboot.data.UserRepository;
import com.sergiofreire.xray.tutorials.springboot.services.UserServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Verify the service logic by mocking data from the repository
 */
@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    // strictness is set to lenient to avoid warnings about unused stubs in each test, as we setup the same stubs for each test on the setUp() method
    // @Mock( lenient = true)
    @Mock(strictness = Strictness.LENIENT)
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        User john = new User("John Doe", "johndoe", "dummypassword");
        john.setId(999L);
        User amanda = new User("Amanda James", "amanda", "dummypassword");
        User robert = new User("Robert Junior", "robert", "dummypassword");

        List<User> allUsers = Arrays.asList(john, amanda, robert);

 
        Mockito.when(userRepository.findById(john.getId())).thenReturn(Optional.of(john));
        Mockito.when(userRepository.findAll()).thenReturn(allUsers);
        Mockito.when(userRepository.findById(-99L)).thenReturn(Optional.empty());
    }

    @Test
     void getUserDetailsReturnsUserIfExisting() {
        Optional<User> user = userService.getUserDetails(999L);

        Mockito.verify(userRepository, VerificationModeFactory.times(1)).findById(Mockito.anyLong());
        assertThat(user.get().getName()).isEqualTo("John Doe");
        assertThat(user.get().getUsername()).isEqualTo("johndoe");
    }

    @Test
     void getUserDetailsReturnsEmptyIfNonExisting() {
        Optional<User> user = userService.getUserDetails(-99L);

        Mockito.verify(userRepository, VerificationModeFactory.times(1)).findById(Mockito.anyLong());
        assertThat(user).isNotPresent();
    }

    @Test
     void getAllUsersReturnsAllExistingUsers() {
        User john = new User("John Doe", "johndoe", "dummypassword");
        User amanda = new User("Amanda James", "amanda", "dummypassword");
        User robert = new User("Robert Junior", "robert", "dummypassword");

        List<User> allUsers = userService.getAllUsers();
        Mockito.verify(userRepository, VerificationModeFactory.times(1)).findAll();
        assertThat(allUsers).hasSize(3).extracting(User::getName).containsOnly(john.getName(), amanda.getName(), robert.getName());
        assertThat(allUsers).hasSize(3).extracting(User::getUsername).containsOnly(john.getUsername(), amanda.getUsername(), robert.getUsername());
    }

    @Test
    void deleteUserRemovesIt() {
        Optional<User> user = userService.getUserDetails(999L);
        userService.deleteUser(user.get());
        Mockito.verify(userRepository, VerificationModeFactory.times(1)).delete(user.get());
    }

}
