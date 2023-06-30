package gr.aueb.cf.springapp.service.impl;

import gr.aueb.cf.springapp.dao.UserRepository;
import gr.aueb.cf.springapp.dto.UserDTO;
import gr.aueb.cf.springapp.entity.User;
import gr.aueb.cf.springapp.service.IUserService;
import gr.aueb.cf.springapp.service.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user based on the provided UserDTO and hashes the password
     * using BCrypt.
     * @param userToRegister the UserDTO containing user registration data
     * @return the registered User entity
     */
    @Override
    public User registerUser(UserDTO userToRegister) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(userToRegister.getPassword());

        userToRegister.setPassword(hashedPassword);

        User user = userRepository.save(mapUser(userToRegister));
        return user;
    }

    /**
     * Updates an existing user based on the provided UserDTO.
     * @param userDTO the UserDTO containing updated user data
     * @return the updated User entity
     * @throws EntityNotFoundException if the user with the specified ID is not found
     */
    @Override
    public User updateUser(UserDTO userDTO) throws EntityNotFoundException {
        Optional<User> user = userRepository.findById(userDTO.getId());
        if (user.isEmpty()){
            throw new EntityNotFoundException(User.class, userDTO.getId());
        }
        return  userRepository.save(mapUser(userDTO));
    }

    /**
     *
     * Checks if the provided username and password combination is valid.
     * @param username the username to validate
     * @param password the password to validate
     * @return true if the username and password are valid, false otherwise
     */
    @Override
    public boolean isUserValid(String username, String password) {
        return userRepository.isUserValid(username,password);
    }

    /**
     *
     * Deletes the user with the specified ID.
     * @param id the ID of the user to delete
     * @throws EntityNotFoundException if the user with the specified ID is not found
     */
    @Override
    public void deleteUser(Long id) throws EntityNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()){
            throw new EntityNotFoundException(User.class, id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Retrieves a user by their username.
     * @param username the username of the user to retrieve
     * @return the User entity with the specified username
     * @throws EntityNotFoundException if the user with the specified username is not found
     */
    @Override
    public User getUserByUsername(String username) throws EntityNotFoundException {
        User user = userRepository.findByUsernameEquals(username);
        if (user == null) {
            throw new EntityNotFoundException(User.class, 1L);
        }
        return user;
    }

    /**
     * Retrieves a user by their ID.
     * @param id the ID of the user to retrieve
     * @return the User entity with the specified ID
     * @throws EntityNotFoundException if the user with the specified ID is not found
     */
    @Override
    public User getUserById(Long id) throws EntityNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()){
            throw new EntityNotFoundException(User.class, id);
        }
        return user.get();
    }

    /**
     * Checks if a username already exists.
     * @param username the username to check
     * @return true if the username already exists, false otherwise
     */
    @Override
    public boolean usernameAlreadyExists(String username) {
        return userRepository.usernameExists(username);
    }

    /**
     * Checks if a password is valid based on a specified pattern.
     * @param password the password to validate
     * @return true if the password is valid, false otherwise
     */
    @Override
    public boolean isPasswordValid(String password){
        Pattern pattern = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }


    /**
     * Maps a UserDTO to a User entity.
     * @param userDTO the UserDTO to map
     * @return the mapped User entity
     */
    private User mapUser(UserDTO userDTO){
        return new User(userDTO.getId(),userDTO.getUsername(),userDTO.getPassword());
    }
}
