app.controller("addressController",function ($scope,$controller,baseService) {
    $controller("indexController",{$scope:$scope});

    $scope.getAddress=function () {
        baseService.sendGet("user/getAddress/").then(function (response) {
            $scope.mapList=response.data;
        })
    }

    $scope.show=function (entity) {
         $scope.entity =entity;
         $scope.entity[provinceId]=entity[provinceId];
         $scope.entity[cityId]=entity[cityId];
         $scope.entity[townId]=entity[townId];

    };
    $scope.entity={userId:"",provinceId:"",cityId:"",townId:"",mobile:"",address:"",contact:"",isDefault:"",notes:"",alias:""};

    $scope.saveOrUpdate=function () {
        var url = "saveAddress";
        if ($scope.entity.id) {
            url = "updateAddress";
        }
        baseService.sendPost("/user/" + url, $scope.entity).then(function (value) {
            if (value.data) {
                 location.href="/home-setting-address.html";
            } else {
                alert("操作失败!");
            }
        })

    }

    //删除地址
    $scope.deleteAddress=function (id) {
        baseService.sendGet("/user/deleteAddress?id="+id).then(function (response) {
            if (response.data){
                location.href="/home-setting-address.html";
            }else {
                alert("删除失败")
            }
        })
    }

    //设置默认地址
    $scope.setDefaultAddress=function (id) {
        baseService.sendGet("/user/setDefaultAddress?id="+id).then(function (response) {
            if (response.data){
                location.href="/home-setting-address.html";
            }else {
                alert("删除失败")
            }
        })
    }

    $scope.$watch('entity.provinceId', function(newValue, oldValue){
        if (newValue){
            /** 根据选择的值查询二级分类 */
            $scope.findCitiesByParentId(newValue, "cities");
        }else{
            $scope.cities = [];
        }
    });

    $scope.$watch('entity.cityId', function(newValue, oldValue){
        if (newValue){
            /** 根据选择的值查询三级分类 */
            $scope.findAreasByParentId(newValue, "areas");
        }else{
            $scope.areas = [];
        }
    });



});