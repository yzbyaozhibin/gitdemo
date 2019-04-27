package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.AddressMapper;
import com.pinyougou.mapper.AreasMapper;
import com.pinyougou.mapper.CitiesMapper;
import com.pinyougou.mapper.ProvincesMapper;
import com.pinyougou.pojo.Address;
import com.pinyougou.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private ProvincesMapper provincesMapper;
    @Autowired
    private CitiesMapper citiesMapper;
    @Autowired
    private AreasMapper areasMapper;

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
    public List<Map<String,Object>> findByUserName(String userId) {
        try{
            Example example = new Example(Address.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId", userId);
            example.orderBy("isDefault").desc();
            List<Address> addressList = addressMapper.selectByExample(example);
            List<Map<String,Object>> dataList=new ArrayList<>();
            for (Address address : addressList) {
                Map<String,Object> map=new HashMap<>();
                map.put("address",address);
                String provinceName = provincesMapper.findProvinceName(address.getProvinceId());
                String cityName = citiesMapper.findCityName(address.getCityId());
                String areaName = areasMapper.findAreaName(address.getTownId());
                map.put("provinceName",provinceName);
                map.put("cityName",cityName);
                map.put("areaName",areaName);
                dataList.add(map);
            }
            return dataList;
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void setDefaultAddress(Long id,String userId) {
        try{
            List<Map<String, Object>> dataList = findByUserName(userId);
            Map<String, Object> dataMap = dataList.get(0);
            Address address1 = (Address) dataMap.get("address");
            address1.setIsDefault("0");
            addressMapper.updateByPrimaryKey(address1);
            Address address = addressMapper.selectByPrimaryKey(id);
            address.setIsDefault("1");
            addressMapper.updateByPrimaryKey(address);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }

    }

    @Override
    public List<Address> findByUserId(String userId) {
        Example example = new Example(Address.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        example.orderBy("isDefault").desc();
        List<Address> addressList = addressMapper.selectByExample(example);
        return addressList;
    }
}
