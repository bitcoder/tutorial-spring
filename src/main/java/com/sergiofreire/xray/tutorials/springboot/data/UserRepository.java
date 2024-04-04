package com.sergiofreire.xray.tutorials.springboot.data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public Optional<User> findById(Long id);
    public User findByUsername(String username);
    public List<User> findAll();
    public void delete(User user);

}
