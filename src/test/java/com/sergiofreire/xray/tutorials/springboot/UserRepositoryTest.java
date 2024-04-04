package  com.sergiofreire.xray.tutorials.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.sergiofreire.xray.tutorials.springboot.data.User;
import com.sergiofreire.xray.tutorials.springboot.data.UserRepository;

import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * By default, tests annotated with @DataJpaTest are transactional and roll back at the end of each test. They also use an embedded in-memory database (H2).
 * The web environment is *not* loaded. If you want to test the full stack, use @SpringBootTest.
 */
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByIdReturnsUserForValidId() {
        User john = new User("John Doe", "johndoe", "dummypassword");
        entityManager.persistAndFlush(john);

        Optional<User> user = userRepository.findById(john.getId());
        assertThat(user.get()).isEqualTo(john);
    }

    @Test
    void findByIdReturnsNullWhenInvalidId() {
        Optional<User> fromDb = userRepository.findById(-444L);
        assertThat(fromDb).isEmpty();
    }


    @Test
    void findAllReturnsAllUsers() {
        User john = new User("John Doe", "johndoe", "dummypassword");
        User amanda = new User("Amanda James", "amanda", "dummypassword");
        User robert = new User("Robert Junior", "robert", "dummypassword");

        entityManager.persist(john);
        entityManager.persist(amanda);
        entityManager.persist(robert);
        entityManager.flush();

        List<User> allUsers = userRepository.findAll();

        assertThat(allUsers).hasSize(3).extracting(User::getName).containsOnly(john.getName(), amanda.getName(), robert.getName());
        assertThat(allUsers).hasSize(3).extracting(User::getUsername).containsOnly(john.getUsername(), amanda.getUsername(), robert.getUsername());
    }

}