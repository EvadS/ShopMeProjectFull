package com.shopme.service;

import com.shopme.admin.user.common.entity.Category;
import com.shopme.admin.user.common.entity.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ICategoryService {
    public List<Category> listNoChildrenCategories();

    public Category getCategory(String alias);

    public List<Category> getCategoryParents(Category child);

  }

