package com.shopme.repository;

import java.util.List;

import com.shopme.admin.user.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import com.shopme.repository.CategoryRepository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

//@DataJpaTest
//@AutoConfigureTestDatabase(replace = Replace.NONE)

public class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository repo;

    @Test
    public void testListEnabledCategories() {
        List<Category> categories = repo.findAllEnabled();
        categories.forEach(category -> {
            System.out.println(category.getName() + " (" + category.isEnabled() + ")");
        });
    }

    @Test
    public void testFindCategoryByAlias() {
        String alias = "electronics";
        Category category = repo.findByAliasEnabled(alias);

        assertThat(category).isNotNull();
    }
}
