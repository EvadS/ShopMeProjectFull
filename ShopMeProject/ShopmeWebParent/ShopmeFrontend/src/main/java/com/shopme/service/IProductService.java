package com.shopme.service;

import com.shopme.admin.user.common.entity.Product;
import org.springframework.data.domain.Page;


public interface IProductService {

    public Page<Product> listByCategory(int pageNum, Integer categoryId);
}
