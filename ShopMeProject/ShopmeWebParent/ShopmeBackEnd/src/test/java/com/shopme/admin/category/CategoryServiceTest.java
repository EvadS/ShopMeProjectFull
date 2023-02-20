package com.shopme.admin.category;

import com.shopme.admin.user.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class CategoryServiceTest {

    @MockBean
    private CategoryRepository repo;

    @InjectMocks
    private CategoryService categoryService;
    
    @Test
    public void testCheckUniqueInNewModeReturnDuplicateName(){
        Integer id = null;
        String name = "Computers";
        String alias = "abc";

        Category category = new Category(id, name, alias);
        
        Mockito.when(repo.findByName(name)).thenReturn(category);

        String result = categoryService.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("DuplicateName");
    }

    @Test
    public void testCheckUniqueInNewModeReturnDuplicateAlias(){
        Integer id = null;
        String name = "NameABc";
        String alias = "computers";

        Category category = new Category(id, name, alias);

        Mockito.when(repo.findByName(name)).thenReturn(null);
        Mockito.when(repo.findByAlias(alias)).thenReturn(category);

        String result = categoryService.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("DuplicateAlias");
    }

    @Test
    public void testCheckUniqueInNewModeReturnOk(){
        Integer id = null;
        String name = "NameABc";
        String alias = "computers";

        Category category = new Category(id, name, alias);

        Mockito.when(repo.findByName(name)).thenReturn(null);
        Mockito.when(repo.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("OK");
    }


    @Test
    public void testCheckUniqueInUpdateModeReturnDuplicateName(){
        Integer id = 1;
        String name = "Computers";
        String alias = "abc";

        Category category = new Category(2, name, alias);

        Mockito.when(repo.findByName(name)).thenReturn(category);
        Mockito.when(repo.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("DuplicateName");
    }

    @Test
    public void testCheckUniqueInUpdateModeReturnDuplicateAlias(){
        Integer id = 1;
        String name = "NameABc";
        String alias = "computers";

        Category category = new Category(2, name, alias);

        Mockito.when(repo.findByName(name)).thenReturn(null);
        Mockito.when(repo.findByAlias(alias)).thenReturn(category);

        String result = categoryService.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("DuplicateAlias");
    }


    @Test
    public void testCheckUniqueInUpdateModeReturnOk(){
        Integer id = 1;
        String name = "Computers";
        String alias = "abc";

        Category category = new Category(id, name, alias);

        Mockito.when(repo.findByName(name)).thenReturn(null);
        Mockito.when(repo.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("OK");
    }
}