/** 定义基础的控制器 */
app.controller("baseController", function ($scope) {
    $scope.paginationCof = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 10,
        perPageOptions: [10, 15, 20, 30, 50],
        onChange: function () {
            $scope.reload();
        }
    };

    $scope.reload = function () {
        $scope.search("?page=" + $scope.paginationCof.currentPage,
            "&rows=" + $scope.paginationCof.itemsPerPage)
    };

    $scope.search = function (url,data) {
        baseService.sendGet(url,data).then(function (value) {
            $scope.paginationCof.totalItems = value.data.total;
            $scope.dataList = value.data.rows;
        })
    }

});