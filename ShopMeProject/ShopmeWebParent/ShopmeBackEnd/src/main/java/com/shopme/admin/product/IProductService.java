package com.shopme.admin.product;

import com.shopme.admin.error.ProductNotFoundException;
import com.shopme.admin.user.common.entity.Product;

import java.util.List;

public interface IProductService {

    public List<Product> listAll();

    public Product save(Product product);

    public String checkUnique(Integer id, String name);

    public void updateProductEnabledStatus(Integer id, boolean enabled);

    public void delete(Integer id) throws ProductNotFoundException;
}
