app.controller("sellerController",function ($scope, baseService, $controller) {

    $controller("baseController", {$scope:$scope});

    $scope.data = {status : '0'};
    $scope.findByPage = function () {
        baseService.findByPage("/seller/findByPage?page=" + $scope.paginationConf.currentPage +
            "&rows=" + $scope.paginationConf.itemsPerPage, $scope.data).then(function (response) {
            $scope.paginationConf.totalItems = response.data.total;
            $scope.dataList = response.data.rows;
        });
    };

    /** 商家管理 */
    $scope.SelectSeller = function (status) {
        $scope.data.status = status;
        $scope.findByPage();
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
        });
    };



});