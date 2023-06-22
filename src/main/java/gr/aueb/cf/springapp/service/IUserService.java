package gr.aueb.cf.springapp.service;

import gr.aueb.cf.springapp.dto.UserDTO;
import gr.aueb.cf.springapp.entity.User;
import gr.aueb.cf.springapp.service.exceptions.EntityNotFoundException;

public interface IUserService {
    User registerUser(UserDTO userToRegister);
    User updateUser(UserDTO userDTO) throws EntityNotFoundException;

    boolean isUserValid(String username, String password);
    void deleteUser(Long id) throws EntityNotFoundException;
    User getUserByUsername(String username) throws EntityNotFoundException;
    User getUserById(Long id) throws EntityNotFoundException;
    boolean usernameAlreadyExists(String username);

    public boolean isPasswordValid(String password);
}
