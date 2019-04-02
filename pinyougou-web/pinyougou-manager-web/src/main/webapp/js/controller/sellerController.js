app.controller("sellerController",function ($scope, baseService, $controller) {

    $controller("baseController", {$scope:$scope});

    $scope.findByPage = function () {
        baseService.findByPage("/seller/findByPage?page=" + $scope.paginationConf.currentPage +
            "&rows=" + $scope.paginationConf.itemsPerPage + "&status=0",$scope.data).then(function (response) {
            $scope.paginationConf.totalItems = response.data.total;
            $scope.dataList = response.data.rows;
        })
    };

    $scope.updateStatus = function (status) {
        $scope.entity = {
            sellerId:$scope.entity.sellerId,
            status:status
        };
        baseService.doPost("/seller/updateStatus", $scope.entity).then(function (value) {
            if (value.data) {
                $scope.findByPage();
            } else {
                alert("审核失败!");
            }
        })
    }
});