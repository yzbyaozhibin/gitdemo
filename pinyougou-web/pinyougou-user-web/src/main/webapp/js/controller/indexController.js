app.controller("indexController",function ($scope,baseService) {
    $scope.username = "";

    $scope.showUser = function () {
        baseService.sendGet("/user/showUser").then(function (value) {
            $scope.username = value.data.username;
        })
    }
});