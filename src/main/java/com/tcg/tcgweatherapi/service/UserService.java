package com.tcg.tcgweatherapi.service;

import com.tcg.tcgweatherapi.entity.User;
import com.tcg.tcgweatherapi.exceptions.UserAlreadyRegisteredException;
import com.tcg.tcgweatherapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class responsible for managing user-related operations.
 *
 * <p>This class acts as the business layer for user management and provides
 * methods for registering, retrieving, activating, and deactivating users.
 * It uses {@link UserRepository} to interact with the database and ensures
 * transactional integrity.</p>
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    /**
     * Constructor for dependency injection of {@link UserRepository}.
     *
     * @param userRepository the repository for accessing and managing {@link User} entities.
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user with the provided email address.
     *
     * <p>If a user with the given email already exists, a {@link UserAlreadyRegisteredException}
     * is thrown. Otherwise, a new {@link User} entity is created, activated by default,
     * and saved to the database.</p>
     *
     * @param email the email address of the user to be registered.
     * @return the saved {@link User} entity.
     * @throws UserAlreadyRegisteredException if a user with the same email already exists.
     */
    public User registerUser(String email) {
        if (userRepository.findByEmail(email) != null) {
            throw new UserAlreadyRegisteredException("Already user registered");
        }
        User user = new User();
        user.setEmail(email);
        user.setActive(true);
        return userRepository.save(user);
    }

    /**
     * Retrieves a user by their email address.
     *
     * <p>This method queries the database for a {@link User} entity with the specified
     * email address. If no such user exists, it returns {@code null}.</p>
     *
     * @param email the email address of the user to retrieve.
     * @return the {@link User} entity, or {@code null} if not found.
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Activates a user's account based on their email address.
     *
     * <p>If the user does not exist, a {@link RuntimeException} is thrown. Otherwise,
     * the user's account is marked as active and the change is persisted to the database.</p>
     *
     * @param email the email address of the user to activate.
     * @throws RuntimeException if the user does not exist.
     */
    public void activateUser(String email) {
        User user = getUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        user.setActive(true);
        userRepository.save(user);
    }

    /**
     * Deactivates a user's account based on their email address.
     *
     * <p>If the user does not exist, a {@link RuntimeException} is thrown. Otherwise,
     * the user's account is marked as inactive and the change is persisted to the database.</p>
     *
     * @param email the email address of the user to deactivate.
     * @throws RuntimeException if the user does not exist.
     */
    public void deactivateUser(String email) {
        User user = getUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        user.setActive(false);
        userRepository.save(user);
    }
}
