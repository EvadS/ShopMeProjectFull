package com.shopme.admin.brands;

import com.shopme.admin.error.BrandNotFoundException;
import com.shopme.admin.user.common.entity.Brand;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IBrandService {
    List<Brand> listAll();

    Brand save(Brand brand);

    Brand get(Integer id) throws BrandNotFoundException;

    void delete(Integer id) throws BrandNotFoundException;

     String checkUnique(Integer id, String name);
    Page<Brand> listByPage(int pageNum, String sortField, String sortDir, String keyword);
}
