package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import com.github.javafaker.Faker;
import com.shopme.admin.user.common.entity.Role;
import com.shopme.admin.user.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTests {



    private UserRepository repo;

    private TestEntityManager entityManager;

    @Autowired
    public UserRepositoryTests(UserRepository repo, TestEntityManager entityManager) {
        super();
        this.repo = repo;
        this.entityManager = entityManager;
    }


    @Test
    public void testCreateNewUserWithOneRole() {
        Role roleAdmin = entityManager.find(Role.class, 1);
        User userWithOneRole = new User("y@a.net", "ya2020", "Yağmur", "Akşaç");
        userWithOneRole.addRole(roleAdmin);

        User savedUser = repo.save(userWithOneRole);

        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateNewUserWithTwoRoles() {
        User userWithTwoRole = new User("r@g.com", "rg2020", "Remzi", "Güloğlu");
        Role roleEditor = entityManager.find(Role.class, 2);
        Role roleAssistant = entityManager.find(Role.class, 4);

        userWithTwoRole.addRole(roleEditor);
        userWithTwoRole.addRole(roleAssistant);

        User savedUser = repo.save(userWithTwoRole);

        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllUsers() {
        Iterable<User> listUsers = repo.findAll();
        listUsers.forEach(user -> System.out.println(user));
    }

    @Test
    public void testGetUserById() {

        User userWithTwoRole = new User("r@g.com", "rg2020", "Remzi", "Güloğlu");
        Role roleEditor = new Role(3);
        Role roleAssistant = new Role(5);

        userWithTwoRole.addRole(roleEditor);
        userWithTwoRole.addRole(roleAssistant);

        User savedUser = repo.save(userWithTwoRole);

        User userById = repo.findById(1).get();
        System.out.println(userById);
        assertThat(userById).isNotNull();
    }

    //@Test
    public void testUpdateUserDetails() {
        User userUpdateUserDetails = repo.findById(1).get();
        userUpdateUserDetails.setEnabled(true);
        userUpdateUserDetails.setEmail("ya@a.com");

        repo.save(userUpdateUserDetails);
    }


   // @Test
    public void testDeleteUser() {
        Integer userDeleteUser = 2;
        repo.deleteById(userDeleteUser);

    }

    @Test
    public void testGetUserByEmail(){
        String email = "r@g.com";
        User user = repo.getUserByEmail(email);

        assertThat(user).isNotNull();
    }

    @Test
    public void countbyId(){
        Integer id = 100;

        final Long countby = repo.countById(id);
        //// assertThat(countby).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testListFirstPage(){
        int pageNumber = 1;
        int pageSize = 4;

        List<User> user = createUsersList(10);
        user.forEach(entityManager::persist);
        entityManager.flush();

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = repo.findAll(pageable);

        final List<User> userList = page.getContent();

        assertThat(userList.size()).isEqualTo(pageSize);
    }

    private List<User> createUsersList(int num ) {
        List<User> userList = new ArrayList<>();
        Faker faker = new Faker();

        for(int i= 0; i< num; i++) {
            User user = new User();
            user.setPassword(faker.internet().password());
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            user.setEmail(faker.internet().emailAddress());

            userList.add(user);
        }

        return userList;
    }
}
