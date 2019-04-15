app.controller("contentCategory", function ($scope, baseService, $controller) {
    $controller("baseController", {$scope:$scope});

    $scope.findByPage = function () {
        baseService.findByPage("/contentcategory/findByPage?page=" + $scope.paginationConf.currentPage +
            "&rows=" + $scope.paginationConf.itemsPerPage).then(function (response) {
            $scope.paginationConf.totalItems = response.data.total;
            $scope.dataList = response.data.rows;
        })
    };

    $scope.deleteByIds = function () {
        if ($scope.ids.length != 0) {
            baseService.deleteByIds("/contentcategory/delete", $scope.ids).then(function (value) {
                if (value.data) {
                    $scope.findByPage();
                } else {
                    alert("删除失败!");
                }
            })
        } else {
            alert("请选择要删除的数据");
        }
    };

    $scope.saveOrUpdate = function () {
        var url = "save";

        if ($scope.entity.id) {
            url = "update";
        }

        baseService.saveOrUpdate("/contentcategory/" + url, $scope.entity).then(function (value) {
            if (value.data) {
                $scope.findByPage();
            } else {
                alert("删除失败!");
            }
        })
    }

});