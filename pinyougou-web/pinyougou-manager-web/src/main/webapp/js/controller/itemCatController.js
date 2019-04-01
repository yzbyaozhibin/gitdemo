app.controller("itemCatController",function ($scope, baseService, $controller) {
    $controller("baseController",{$scope:$scope});

    $scope.findByParentId = function (parentId) {
        baseService.doGet("/itemcat/findByParentId?parentId=" + parentId).then(function (value) {
            $scope.dataList = value.data;
        })
    };

    $scope.showNexLevel = function (id) {
        $scope.level += 1;

        $scope.findByParentId(id);
    }
});