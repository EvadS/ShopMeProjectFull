package com.shopme.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.shopme.admin.user.common.entity.Category;
import com.shopme.admin.user.common.entity.Product;
import com.shopme.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


import com.shopme.repository.CategoryRepository;

@Service
public class CategoryService implements ICategoryService {

    @Autowired
    private CategoryRepository repo;

    public CategoryService(CategoryRepository repo) {
        super();
        this.repo = repo;
    }

    @Override
    public List<Category> listNoChildrenCategories() {

        List<Category> listNoChildrenCategories = new ArrayList<>();

        List<Category> listEnabledCategories = repo.findAllEnabled();

        listEnabledCategories.forEach(category -> {
            Set<Category> children = category.getChildren();
            if (children == null || children.size() == 0) {
                listNoChildrenCategories.add(category);
            }
        });

        return listNoChildrenCategories;
    }

    @Override
    public Category getCategory(String alias) {
        return repo.findByAliasEnabled(alias);
    }

    @Override
    public List<Category> getCategoryParents(Category child) {
        List<Category> listParents = new ArrayList<>();

        Category parent = child.getParent();

        while (parent != null) {
            listParents.add(0, parent);
            parent = parent.getParent();
        }

        listParents.add(child);

        return listParents;
    }


}
