app.controller("indexController",function ($scope,baseService) {
    $scope.username = "";

    $scope.showUser = function () {
        baseService.sendGet("/user/showUser").then(function (value) {
            $scope.username = value.data.username;
        })
    };

    $scope.userInfo = {userInfo.}
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


    $scope.$watch('provinces.provinceId', function(newValue, oldValue){
        if (newValue){
            /** 根据选择的值查询二级分类 */
            $scope.findCitiesByParentId(newValue, "cities");
        }else{
            $scope.cities = [];
        }
    });


});