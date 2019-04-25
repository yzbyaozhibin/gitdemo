package com.pinyougou.service;

import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Order;
import java.util.List;
import java.io.Serializable;
import java.util.Map;

/**
 * OrderService 服务接口
 * @date 2019-03-28 15:42:09
 * @version 1.0
 */
public interface OrderService {

	/** 添加方法 */
	void save(Order order);

	/** 修改方法 */
	void update(Order order);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	Order findOne(Serializable id);

	/** 查询全部 */
	List<Order> findAll();

	/** 多条件分页查询 */
	List<Order> findByPage(Order order, int page, int rows);

    /* 获取用户订单信息*/
    PageResult getUserOrder(int page,int rows,String userId);
}