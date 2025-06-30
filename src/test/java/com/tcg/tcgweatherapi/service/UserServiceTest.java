package com.tcg.tcgweatherapi.service;

import com.tcg.tcgweatherapi.entity.User;
import com.tcg.tcgweatherapi.exceptions.UserAlreadyRegisteredException;
import com.tcg.tcgweatherapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        User savedUser = new User();
        savedUser.setEmail(email);
        savedUser.setActive(true);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertTrue(result.isActive());
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_AlreadyRegistered() {
        String email = "test@example.com";
        User existingUser = new User();
        existingUser.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(existingUser);

        Exception exception = assertThrows(UserAlreadyRegisteredException.class, () -> {
            userService.registerUser(email);
        });

        assertEquals("User already registered", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserByEmail_UserExists() {
        String email = "test@example.com";
        User existingUser = new User();
        existingUser.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(existingUser);

        User result = userService.getUserByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testGetUserByEmail_UserDoesNotExist() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        User result = userService.getUserByEmail(email);

        assertNull(result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testActivateUser_Success() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setActive(false);

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        userService.activateUser(email);

        assertTrue(user.isActive());
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testActivateUser_UserDoesNotExist() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.activateUser(email);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeactivateUser_Success() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setActive(true);

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        userService.deactivateUser(email);

        assertFalse(user.isActive());
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeactivateUser_UserDoesNotExist() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.deactivateUser(email);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }
}
