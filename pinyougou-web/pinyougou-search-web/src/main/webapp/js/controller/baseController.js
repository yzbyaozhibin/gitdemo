app.controller("baseController",function ($scope, baseService) {
    $scope.showName = function () {
        $scope.serverUrl = window.encodeURIComponent(location.href);
        baseService.sendGet("/user/showName").then(function (value) {
            $scope.username = value.data.username;
        })
    }
});