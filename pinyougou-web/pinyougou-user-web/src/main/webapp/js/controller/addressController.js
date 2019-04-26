app.controller("addressController",function ($scope,$controller,baseService) {
    $controller("indexController",{$scope:$scope});

    $scope.getAddress=function () {
        baseService.sendGet("user/getAddress/").then(function (response) {
            $scope.addressList=response.data;
        })
    }

    $scope.show=function (entity) {
        $scope.entity =JSON.parse(JSON.stringify(entity));
    };
    $scope.entity={userId:"",provinceId:"",cityId:"",townId:"",mobile:"",address:"",contact:"",isDefault:"",notes:"",alias:""};
    $scope.saveOrUpdate=function (provinceId,cityId,areaId) {
        $scope.getProvince(provinceId);
        $scope.getCity(cityId);
        $scope.getAreas(areaId);
        $scope.entity.provinceId=$scope.pro;
        $scope.entity.cityId=$scope.ci;
        $scope.entity.townId=$scope.ar;
        alert(JSON.stringify($scope.entity));
        var url = "saveAddress";
        if ($scope.entity.id) {
            url = "updateAddress";
        }
        /*baseService.sendPost("/user/" + url, $scope.entity).then(function (value) {
            if (value.data) {
                $scope.showOrderInfo();
            } else {
                alert("操作失败!");
            }
        })*/

    }


    //查询省份名称
    $scope.getProvince=function (id) {
        baseService.sendGet("/user/getProvince?provinceId="+id).then(function (response) {
            $scope.pro= response.data;
        })
    }
    //查询市的名称
    $scope.getCity=function (id) {
        baseService.sendGet("/user/getCity?cityId="+id).then(function (response) {
            $scope.ci= response.data;
        })
    }
    //查询区域的名称

    $scope.getAreas=function (id) {
        baseService.sendGet("/user/getAreas?areaId="+id).then(function (response) {
            $scope.ar=response.data;
        })
    }
});