app.controller("goodsController", function ($scope, baseService, $controller) {
    $controller("baseController", {$scope: $scope});

    $scope.status = ["未审核","已通过","审核不通过","关闭"];

    $scope.findByPage = function () {
        baseService.findByPage("/goods/findByPage?page=" + $scope.paginationConf.currentPage +
            "&rows=" + $scope.paginationConf.itemsPerPage, $scope.data).then(function (value) {
                $scope.dataList = value.data.rows;
                $scope.paginationConf.totalItems = value.data.total;
        })
    };

    $scope.updateStatus = function (status) {
        if ($scope.ids.length != 0) {
            baseService.doGet("/goods/updateStatus?ids="+ $scope.ids +"&status=" + status).then(function (value) {
                if (value.data) {
                    $scope.findByPage();
                    $scope.ids = [];
                } else {
                    alert("审核失败!");
                }
            })
        } else {
            alert("请选择要删除的数据!");
        }
    };

});