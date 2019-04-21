app.controller("baseController",function ($scope, baseService) {
    $scope.showName = function () {
        baseService.sendGet("/user/showName").then(function (value) {
            $scope.serverUrl = window.encodeURIComponent(location.href);
            $scope.username = value.data.username;
        })
    }
});