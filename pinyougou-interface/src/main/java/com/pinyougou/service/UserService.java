package com.pinyougou.service;

import com.pinyougou.pojo.User;
import java.util.List;
import java.io.Serializable;
import java.util.Map;

/**
 * UserService 服务接口
 * @date 2019-03-28 15:42:10
 * @version 1.0
 */
public interface UserService {

	/** 添加方法 */
	void save(User user);

	/** 修改方法 */
	void update(User user);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	User findOne(Serializable id);

	/** 查询全部 */
	List<User> findAll();

	/** 多条件分页查询 */
	List<User> findByPage(User user, int page, int rows);

	Boolean sendSms(String phone);

	boolean checkSmsCode(String code);
	//根据用户名得到用户
    User selectUser(String username);
	//根据用户名更改密码
	void updatePassWord(String newPassword, String username);
	/**保存用户信息*/
	void saveUserInfo(User user);

    User findByUserName(User user);

    Boolean addPicUrl(String username, String headPic);
}