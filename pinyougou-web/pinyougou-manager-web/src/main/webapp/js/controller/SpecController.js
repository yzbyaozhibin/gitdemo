app.controller("SpecController", function ($scope, baseService, $controller) {
    $controller("baseController", {$scope: $scope});

    $scope.findByPage = function () {
        baseService.findByPage("/spec/findByPage?page=" + $scope.paginationConf.currentPage +
            "&rows=" + $scope.paginationConf.itemsPerPage, $scope.data).then(function (value) {
            $scope.paginationConf.totalItems = value.data.total;
            $scope.dataList = value.data.rows;
        })
    };

    $scope.saveOrUpdate = function () {
        baseService.saveOrUpdate("/spec/save", $scope.specification).then(function (value) {
            if (value.data) {
                $scope.findByPage();
            } else {
                alert("添加失败!");
            }
        });
    };

    $scope.specification = {specificationOptions: []};

    $scope.addOption = function () {
        $scope.entity = {};
        $scope.specification.specificationOptions.push($scope.entity);
    };

    $scope.removeOption = function (entity) {
        $scope.specification.specificationOptions.splice($scope.specification.specificationOptions.indexOf(entity), 1);
    }
});