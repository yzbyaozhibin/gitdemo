package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.User;

/**
 * UserMapper 数据访问接口
 * @date 2019-03-28 15:41:54
 * @version 1.0
 */
public interface UserMapper extends Mapper<User>{

    @Update("update tb_user set nick_name =#{nickName},sex = #{sex},birthday=#{birthday}, address=#{address} where username=#{username}")
    void saveUserInfo(User user);
    @Select("select * from tb_user where username = #{username}")
    User findByUserName(User user);
}