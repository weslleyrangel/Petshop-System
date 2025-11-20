package com.projetointegrador.petshop.domain.auth;

import com.projetointegrador.petshop.domain.exception.DomainException;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    private final String NOME_VALIDO = "Nome de Usuario";
    private final String USERNAME_VALIDO = "usuario@email.com";
    private final String PASSWORD_VALIDA = "senhaGrande123456";
    private final String ROLE_VALIDA = "USER";

    @Test
    public void givenValidInputs_whenCreatingUser_thenShouldBeCreatedSuccessfully() {
        User user = new User(NOME_VALIDO, USERNAME_VALIDO, PASSWORD_VALIDA, ROLE_VALIDA);

        assertNotNull(user);
        assertEquals(NOME_VALIDO, user.getNome());
        assertEquals(USERNAME_VALIDO, user.getUsername());
        assertEquals(PASSWORD_VALIDA, user.getHashedPassword());
        assertNull("O ID deve ser nulo antes de ser salvo", user.getId());
    }

    @Test(expected = DomainException.class)
    public void givenNullUsername_whenCreatingUser_thenShouldThrowDomainException() {
        new User(NOME_VALIDO, null, PASSWORD_VALIDA, ROLE_VALIDA);
    }

    @Test(expected = DomainException.class)
    public void givenInvalidUsername_whenCreatingUser_thenShouldThrowDomainException() {
        new User(NOME_VALIDO, "username_invalido", PASSWORD_VALIDA, ROLE_VALIDA);
    }

    @Test(expected = DomainException.class)
    public void givenNullPassword_whenCreatingUser_thenShouldThrowDomainException() {
        new User(NOME_VALIDO, USERNAME_VALIDO, null, ROLE_VALIDA);
    }

    @Test(expected = DomainException.class)
    public void givenShortPassword_whenCreatingUser_thenShouldThrowDomainException() {
        new User(NOME_VALIDO, USERNAME_VALIDO, "curta", ROLE_VALIDA);
    }

    // Stub para o PasswordEncoder
    private static class StubPasswordEncoder implements User.PasswordEncoder {
        private final boolean matches;
        public StubPasswordEncoder(boolean matches) {
            this.matches = matches;
        }
        @Override
        public boolean matches(String rawPassword, String encodedPassword) {
            return this.matches;
        }
    }

    @Test
    public void whenPasswordMatches_thenCheckPasswordShouldReturnTrue() {
        User user = new User(NOME_VALIDO, USERNAME_VALIDO, PASSWORD_VALIDA, ROLE_VALIDA);
        User.PasswordEncoder encoder = new StubPasswordEncoder(true);

        assertTrue(user.checkPassword("qualquerSenha", encoder));
    }

    @Test
    public void whenPasswordDoesNotMatch_thenCheckPasswordShouldReturnFalse() {
        User user = new User(NOME_VALIDO, USERNAME_VALIDO, PASSWORD_VALIDA, ROLE_VALIDA);
        User.PasswordEncoder encoder = new StubPasswordEncoder(false);

        assertFalse(user.checkPassword("senhaErrada", encoder));
    }
}
