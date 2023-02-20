package com.shopme.admin.user;

import com.shopme.admin.category.CategoryRepository;
import com.shopme.admin.user.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(true)
public class CategoryTest {
    @Autowired
    private CategoryRepository repo;

    @Test
    public void createNewCategoryShouldWorkCorrect(){

        Category category = new Category();
        category.setName("category name");
        category.setAlias("category alias");
        category.setImage("");
        category.setEnabled(true);


        Category save = repo.save(category);
        assertThat(save.getId()).isGreaterThan(0);
    }
}
