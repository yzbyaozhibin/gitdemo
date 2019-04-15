app.controller("specController", function ($scope, baseService, $controller) {
    $controller("baseController", {$scope: $scope});

    $scope.findByPage = function () {
        baseService.findByPage("/spec/findByPage?page=" + $scope.paginationConf.currentPage +
            "&rows=" + $scope.paginationConf.itemsPerPage, $scope.data).then(function (value) {
            $scope.paginationConf.totalItems = value.data.total;
            $scope.dataList = value.data.rows;
        })
    };

    $scope.saveOrUpdate = function () {
        baseService.saveOrUpdate("/spec/save", $scope.entity).then(function (value) {
            if (value.data) {
                $scope.findByPage();
            } else {
                alert("添加失败!");
            }
        });
    };

    $scope.deleteByIds = function () {
        if ($scope.ids.length == 0) {
            alert("请选择要删除的数据");
        } else {
            baseService.deleteByIds("/spec/delete",$scope.ids).then(function (value) {
                if (value.data) {
                    $scope.findByPage();
                } else {
                    alert("删除失败");
                }
            })
        }
    };

    $scope.showSpec = function (entity) {
        $scope.entity = JSON.parse(JSON.stringify(entity));
        baseService.doGet("/specOption/findBySpecId?specId="+$scope.entity.id).then(function (value) {
            $scope.entity.specificationOptions = value.data;
        })
    };


    $scope.entity = {specificationOptions: []};

    $scope.addOption = function () {
        $scope.entity.specificationOptions.push({});
    };

    $scope.removeOption = function (entity) {
        $scope.entity.specificationOptions.splice($scope.entity.specificationOptions.indexOf(entity), 1);
    }
});