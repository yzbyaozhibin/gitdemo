package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Seller;

import java.util.List;

/**
 * SellerMapper 数据访问接口
 * @date 2019-03-28 15:41:54
 * @version 1.0
 */
public interface SellerMapper extends Mapper<Seller>{


    List<Seller> findAll(Seller seller);

    /** 查询原密码 */
    @Select("select password from tb_seller where seller_id = #{sellerId}")
    String findOldPassword(String sellerId);
    /** 修改密码 */


    @Update("update tb_seller set password = #{newPassword} where seller_id = #{sellerId}")
    void updatePassword(@Param("sellerId") String sellerId, @Param("newPassword") String newPassword);
}