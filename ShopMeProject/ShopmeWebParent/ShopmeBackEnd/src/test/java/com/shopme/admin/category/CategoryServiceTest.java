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
        Long id = null;
        String name = "Computers";
        String alias = "abc";

        Category category = new Category(id, name, alias);
        
        Mockito.when(repo.findByName(name)).thenReturn(category);

        String result = categoryService.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("DuplicateName");
    }

    @Test
    public void testCheckUniqueInNewModeReturnDuplicateAlias(){
        Long id = null;
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
        Long id = null;
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
        Long id = 1L;
        String name = "Computers";
        String alias = "abc";

        Category category = new Category(2L, name, alias);

        Mockito.when(repo.findByName(name)).thenReturn(category);
        Mockito.when(repo.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("DuplicateName");
    }

    @Test
    public void testCheckUniqueInUpdateModeReturnDuplicateAlias(){
        Long id = 1L;
        String name = "NameABc";
        String alias = "computers";

        Category category = new Category(2L, name, alias);

        Mockito.when(repo.findByName(name)).thenReturn(null);
        Mockito.when(repo.findByAlias(alias)).thenReturn(category);

        String result = categoryService.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("DuplicateAlias");
    }


    @Test
    public void testCheckUniqueInUpdateModeReturnOk(){
        Long id = 1L;
        String name = "Computers";
        String alias = "abc";

        Category category = new Category(id, name, alias);

        Mockito.when(repo.findByName(name)).thenReturn(null);
        Mockito.when(repo.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("OK");
    }
}