app.controller("indexController",function ($scope, baseService, $controller) {
    $controller("baseController",{$scope:$scope});

    $scope.getStatus = function () {
        baseService.sendGet("/getStatus").then(function (value) {
            $scope.username = value.data.username;
        })
    }
});