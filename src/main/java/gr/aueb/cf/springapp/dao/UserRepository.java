package gr.aueb.cf.springapp.dao;

import gr.aueb.cf.springapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsernameEquals(String username);

    @Query("SELECT count(*) > 0 FROM User U WHERE U.username = ?1 AND U.password = ?2")
    boolean isUserValid(String username, String password);

    @Query("SELECT count(*) > 0 FROM User U WHERE U.username = ?1")
    boolean usernameExists(String username);
}
