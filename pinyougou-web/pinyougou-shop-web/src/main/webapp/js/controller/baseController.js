/** 定义基础的控制器 */
app.controller("baseController", function ($scope) {
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 10,
        perPageOptions: [10, 15, 20, 30, 50],
        onChange: function () {
            $scope.reload();
        }
    };

    $scope.reload = function () {
        $scope.search("?page=" + $scope.paginationConf.currentPage+"&rows=" + $scope.paginationConf.itemsPerPage)
    };

    $scope.ids = [];
    $scope.setIds = function (event,id) {
        if (event.target.checked) {
            $scope.ids.push(id);
        } else {
            $scope.ids.splice($scope.ids.indexOf(id),1);
        }
    }

});