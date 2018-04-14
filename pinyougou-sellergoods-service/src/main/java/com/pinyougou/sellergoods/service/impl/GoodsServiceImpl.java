package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbSellerMapper sellerMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbGoods goods) {
		goodsMapper.insert(goods);		
	}

	
	/**
	 * 修改
	 */
	/*
	public void update(TbGoods goods){
		goodsMapper.updateByPrimaryKey(goods);
	}	
	*/
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(tbGoodsDesc);

        //查询SKU商品列表
        TbItemExample example=new TbItemExample();
        com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);//查询条件：商品ID
        List<TbItem>itemList = itemMapper.selectByExample(example);
        goods.setItemList(itemList);

		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			goodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
							criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	private void saveItemList(Goods goods){
		if("1".equals(goods.getGoods().getIsEnableSpec())){
			List<TbItem> itemList = goods.getItemList();
			for(TbItem item :itemList) {
				//标题
				String title = goods.getGoods().getGoodsName();
				Map<String, Object> specMap = JSON.parseObject(item.getSpec());
				for (String key : specMap.keySet()) {
					title += "" + specMap.get(key);
				}
				item.setTitle(title);

				if (goods.getGoodsDesc().getItemImages() != null && goods.getGoodsDesc().getItemImages().length() > 0) {
					String itemImages = goods.getGoodsDesc().getItemImages();
					List<Map> images = JSON.parseArray(itemImages, Map.class);
					item.setImage((String) images.get(0).get("url"));
				}


				item.setGoodsId(goods.getGoods().getId());//商品SPU编号
				item.setSellerId(goods.getGoods().getSellerId());//商家编号
				item.setCategoryid(goods.getGoods().getCategory3Id());//商品分类编号（3级）
				item.setCreateTime(new Date());//创建日期
				item.setUpdateTime(new Date());//修改日期
				//品牌名称
				TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
				item.setBrand(brand.getName());
				//分类名称
				TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
				item.setCategory(itemCat.getName());

				//插入
				itemMapper.insert(item);
			}
		}else{
			//插入一个单品
			TbItem item =new TbItem();

			item.setSellerId(goods.getGoods().getSellerId());
			//商家名称
			TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
			item.setSeller(seller.getNickName());

			item.setPrice(goods.getGoods().getPrice());
			item.setNum(9999);
			//图片地址（取spu的第一个图片）
			List<Map>imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class) ;
			if(imageList.size()>0){
				item.setImage ( (String)imageList.get(0).get("url"));
			}

			item.setCategoryid(goods.getGoods().getCategory3Id());
			TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
			item.setCategory(itemCat.getName());//三级分类的名称

			item.setCreateTime(new Date());
			item.setUpdateTime(item.getCreateTime());

			item.setStatus("1");//启用
			item.setIsDefault("1");
			item.setGoodsId(goods.getGoods().getId());

			TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
			item.setBrand(brand.getName());//品牌名称
			item.setSpec("{}");

			itemMapper.insert(item);
		}
	}

    @Override
    public void add(Goods goods) {
        goods.getGoods().setAuditStatus("0");
        goodsMapper.insert(goods.getGoods());
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insert(goods.getGoodsDesc());
        saveItemList(goods);

	}

	@Override
	public void update(Goods goods) {
		//1.更新spu
		TbGoods tbGoods = goods.getGoods();
		goodsMapper.updateByPrimaryKey(tbGoods);
		//2.更新商品的描述
		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
		System.out.println("desc的商品的ID"+goodsDesc.getGoodsId());
		goodsDesc.setGoodsId(tbGoods.getId());
		goodsDescMapper.updateByPrimaryKey(goodsDesc);
		//3.更新SKU列表
		//根据spu商品的ID查询该SPU的SKU的列表  删除
		TbItemExample example = new TbItemExample();
		example.createCriteria().andGoodsIdEqualTo(tbGoods.getId());// from tbitem where goodsid=1

		itemMapper.deleteByExample(example);
		//插入最新的SKU的列表（页面传递过来的）
		List<TbItem> itemList = goods.getItemList();
		if("1".equals(tbGoods.getIsEnableSpec())) {
			for (TbItem item : itemList) {
				//设置标题 spuname+" "+规格
				String itemSpec = item.getSpec();
				Map<String, Object> specObject = JSON.parseObject(itemSpec, Map.class);
				String title = goods.getGoods().getGoodsName();
				for (String key : specObject.keySet()) {
					String specOptionName = (String) specObject.get(key);
					title += " " + specOptionName;
				}

				item.setTitle(title);

				//设置图片Wie一张

				if (goods.getGoodsDesc().getItemImages() != null && goods.getGoodsDesc().getItemImages().length() > 0) {
					String itemImages = goods.getGoodsDesc().getItemImages();
					List<Map> images = JSON.parseArray(itemImages, Map.class);
					item.setImage((String) images.get(0).get("url"));
				}

				//设置三级分类
				item.setCategoryid(tbGoods.getCategory3Id());
				TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
				item.setCategory(itemCat.getName());//三级分类的名称

				item.setCreateTime(new Date());
				item.setUpdateTime(item.getCreateTime());


				item.setGoodsId(tbGoods.getId());

				item.setSellerId(tbGoods.getSellerId());
				TbSeller seller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
				item.setSeller(seller.getNickName());//店铺名称
				TbBrand brand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
				item.setBrand(brand.getName());//品牌名称
				//插入
				itemMapper.insert(item);
			}
		}else{
			//插入一个单品
			TbItem item =new TbItem();

			item.setSellerId(tbGoods.getSellerId());
			TbSeller seller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
			item.setSeller(seller.getNickName());//店铺名称

			item.setTitle(tbGoods.getGoodsName());//SPU mingch

			item.setPrice(tbGoods.getPrice());
			item.setNum(9999);



			if (goods.getGoodsDesc().getItemImages() != null && goods.getGoodsDesc().getItemImages().length() > 0) {
				String itemImages = goods.getGoodsDesc().getItemImages();
				List<Map> images = JSON.parseArray(itemImages, Map.class);
				item.setImage((String) images.get(0).get("url"));
			}

			item.setCategoryid(tbGoods.getCategory3Id());
			TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
			item.setCategory(itemCat.getName());//三级分类的名称

			item.setCreateTime(new Date());
			item.setUpdateTime(item.getCreateTime());

			item.setStatus("1");//启用
			item.setIsDefault("1");
			item.setGoodsId(tbGoods.getId());

			TbBrand brand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
			item.setBrand(brand.getName());//品牌名称
			item.setSpec("{}");

			itemMapper.insert(item);

		}
	}

	@Override
	public void updateStatus(Long[] ids, String status) {
		for (Long id : ids) {
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(goods);
        }
	}

}
