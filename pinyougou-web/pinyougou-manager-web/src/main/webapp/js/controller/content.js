app.controller("content", function ($scope, baseService, $controller) {
    $controller("baseController", {$scope:$scope});

    $scope.findByPage = function () {
        baseService.findByPage("/content/findByPage?page=" + $scope.paginationConf.currentPage +
            "&rows=" + $scope.paginationConf.itemsPerPage).then(function (response) {
            $scope.paginationConf.totalItems = response.data.total;
            $scope.dataList = response.data.rows;
        })
    };

    $scope.saveOrUpdate = function () {
        var url = "save";
        if ($scope.entity.id) {
            url = "update";
        }
        baseService.saveOrUpdate("/content/" + url, $scope.entity).then(function (value) {
            if (value.data) {
                $scope.findByPage();
            } else {
                alert("操作失败!");
            }
        })
    };

    $scope.deleteByIds = function () {
        if ($scope.ids.length != 0) {
            baseService.deleteByIds("/content/delete", $scope.ids).then(function (value) {
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

    $scope.showSelect= function () {
        baseService.doGet("/contentcategory/findAll").then(function (value) {
            $scope.category = value.data;
        })
    };

    $scope.upload = function () {
        baseService.fileUpload().then(function (value) {
            if (value.data.status == 200) {
                $scope.entity.pic = value.data.url;
            } else {
                alert("上传失败");
            }
        })
    };

    $scope.statusFn = function (event) {
        if (event.target.checked) {
            $scope.entity.status = "1";
        } else {
            $scope.entity.status = "0";
        }
    }

});