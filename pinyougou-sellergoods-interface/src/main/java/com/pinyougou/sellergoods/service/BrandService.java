package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;

public interface BrandService {
    List<TbBrand> findAll();
    PageResult findPage(int pageNum, int pageSize);
    void add(TbBrand tbBrand);
    void update(TbBrand tbBrand);
    TbBrand findOne(Long id);
    void delete(Long[] ids);
    PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);
}
