/** 定义控制器层 */
app.controller('sellerController', function($scope, $controller, baseService){
    $controller("baseController",{$scope:$scope});

    //判断是否同意协议
    $scope.isAgree = function (event) {
        if (event.target.checked) {
            $scope.letGo = true;
        } else {
            $scope.letGo = false;
        }
    };

    $scope.entity = {};
    $scope.saveOrUpdate = function () {
        if ($scope.letGo == true) {
            baseService.sendPost("/seller/save",$scope.entity).then(function (value) {
                if(value.data) {
                    location.href = "/shoplogin.html";
                } else {
                    alert("注册失败!");
                }
            })
        } else {
            alert("需要同意协议才能注册哦!")
        }
    }
});