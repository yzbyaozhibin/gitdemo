app.controller("indexController",function ($scope,baseService) {
    $scope.username = "";

    $scope.showUser = function () {
        baseService.sendGet("/user/showUser").then(function (value) {
            $scope.username = value.data.username;
        })
    };

    //定义数据结构
    $scope.userInfo = {nickName:'',sex:'',headPic:'', birthday:'',address:{}};
    //查询省
    $scope.findProvinces = function ( name) {
        baseService.sendGet("/user/findProvinces").then(function (response) {
            $scope[name] = response.data;
        })
    };

    //查询市
    $scope.findCitiesByParentId = function (parentId, name) {
        baseService.sendGet("/user/findCitiesByParentId",
            "parentId=" + parentId).then(function(response){
            $scope[name] = response.data;
        });
    };


    $scope.$watch('userInfo.address.province', function(newValue, oldValue){
        if (newValue){
            /** 根据选择的值查询二级分类 */
            $scope.findCitiesByParentId(newValue, "cities");
        }else{
            $scope.cities = [];
        }
    });

    //查询区
    $scope.findAreasByParentId = function (parentId, name) {
        baseService.sendGet("/user/findAreasByParentId",
            "parentId=" + parentId).then(function(response){
            $scope[name] = response.data;
        });
    };


    $scope.$watch('userInfo.address.city', function(newValue, oldValue){
        if (newValue){
            /** 根据选择的值查询三级分类 */
            $scope.findAreasByParentId(newValue, "areas");
        }else{
            $scope.areas = [];
        }
    });

    //保存用户信息
    $scope.saveOrUpdate = function () {
        baseService.sendPost("/user/saveOrUpdate",$scope.userInfo).then(function (response) {
            if(response.data){
                $scope.userInfo={};
            }else {
                alert("保存失败！")
            }
        })
    };



    $scope.findUserInfo = function () {
        baseService.sendGet("/user/findUserInfo").then(function (response) {
            $scope.userInfo = response.data;

        })
    };
   /* $scope.show =function (entity) {
        $scope.userInfo = JSON.parse(JSON.stringify(entity));
    };*/

    /**上传图片 */
    $scope.uploadFile = function(){
        baseService.uploadFile().then(function(response) {
            /** 如果上传成功，取出url */
            if(response.data.status == 200){
                /** 设置图片访问地址 */
                 $scope.userInfo.headPic = response.data.url;
                $scope.addPic();
            }else{
                alert("上传失败！");
            }
        });
    };
        $scope.addPic=function () {
            baseService.sendGet("/user/addPic?headPic="+ $scope.userInfo.headPic).then(function (response) {
                if(response.data){
                    alert("上传成功")
                }
            })
        }


});