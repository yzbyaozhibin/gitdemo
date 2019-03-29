app.controller("brandController", function ($scope, baseService,$controller) {

    $controller("baseController",{$scope:$scope});

    // 查询分页数据
    $scope.findByPage = function () {
        baseService.findByPage("/brand/findByPage?page=" + $scope.paginationConf.currentPage +
            "&rows=" + $scope.paginationConf.itemsPerPage,$scope.data).then(function (response) {
            $scope.paginationConf.totalItems = response.data.total;
            $scope.dataList = response.data.rows;
        })
    };

    // 添加或者更新
    $scope.saveOrUpdate = function () {

        var url = "save";

        if ($scope.entity.id) {
            url = "update";
        }

        baseService.saveOrUpdate("/brand/" + url, $scope.entity).then(function (value) {
            if (value.data) {
                $scope.findByPage();
            } else {
                alert("添加失败");
            }
        })
    };

    // 通过id数组删除数据
    $scope.deleteByIds = function () {

        if($scope.ids.length == 0) {
            alert("请选择要删除的数据!");
            return;
        }

        if(confirm("确定要删除这些数据吗?")) {
            baseService.deleteByIds("/brand/delete",$scope.ids).then(function (value) {
                if(value.data) {
                    $scope.findByPage();
                    $scope.ids=[];
                } else {
                    alert("删除失败");
                }
            })
        }
    }
});