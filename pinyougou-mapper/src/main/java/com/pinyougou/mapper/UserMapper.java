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
    //根据用户名查询用户
    @Select("select * from tb_user  where username=#{username}")
    User findUsernameByUser(String username);
    //根据用户名更改密码
    @Update("update tb_user set password=#{password} where username=#{username}")
    void updatePassword(User user);
    @Update("update tb_user set phone=#{phone} where username=#{username}")
    void updateUserPhone(User user);
}