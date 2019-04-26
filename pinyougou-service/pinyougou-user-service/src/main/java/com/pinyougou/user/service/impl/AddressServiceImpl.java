package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.AddressMapper;
import com.pinyougou.pojo.Address;
import com.pinyougou.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.List;

/**
 * AddressServiceImpl
 *
 * @version 1.0
 * @date 2019/4/20
 */
@Service(interfaceName = "com.pinyougou.service.AddressService")
@Transactional
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public void save(Address address) {
        addressMapper.insertSelective(address);
    }

    @Override
    public void update(Address address) {
        addressMapper.updateByPrimaryKeySelective(address);
    }

    @Override
    public void delete(Serializable id) {
            try{
                addressMapper.deleteByPrimaryKey(id);
            }catch(Exception ex){
                throw new RuntimeException(ex);
            }
    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Address findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Address> findAll() {
        return null;
    }

    @Override
    public List<Address> findByPage(Address address, int page, int rows) {
        return null;
    }

    @Override
    public List<Address> findByUserId(String userId) {
        Example example = new Example(Address.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        example.orderBy("isDefault").desc();
        return addressMapper.selectByExample(example);

    }

    @Override
    public void setDefaultAddress(Long id,String userId) {
        try{
            Address address = addressMapper.selectByPrimaryKey(id);
            address.setIsDefault("1");
            addressMapper.updateByPrimaryKey(address);
            List<Address> addressList = findByUserId(userId);
            Address address1 = addressList.get(0);
            address1.setIsDefault("0");
            addressMapper.updateByPrimaryKey(address1);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }

    }
}
