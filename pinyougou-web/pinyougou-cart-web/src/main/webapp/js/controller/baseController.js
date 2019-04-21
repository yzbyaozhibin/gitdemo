app.controller("baseController",function ($scope, baseService, $filter) {
    $scope.showName = function () {
        $scope.serverUrl = window.encodeURIComponent(location.href);
        baseService.sendGet("/user/showName").then(function (value) {
            $scope.username = value.data.username;
        })
    };

    $scope.show =function (entity) {
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };


});