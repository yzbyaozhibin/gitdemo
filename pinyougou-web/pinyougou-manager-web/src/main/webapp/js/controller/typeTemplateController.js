app.controller("typeTemplateController", function ($scope, baseService, $controller) {
    $controller("baseController", {$scope: $scope});

    $scope.findByPage = function () {
        baseService.findByPage("/typeTemplate/findByPage?page=" + $scope.paginationConf.currentPage
            + "&rows=" + $scope.paginationConf.itemsPerPage, $scope.data).then(function (value) {
            $scope.paginationConf.totalItems = value.data.total;
            $scope.dataList = value.data.rows;
        })
    };

    $scope.saveOrUpdate = function () {
        var url = "save";
        if ($scope.typeTemplate.id) {
            url = "update";
        }
        baseService.saveOrUpdate("/typeTemplate/" + url, $scope.typeTemplate).then(function (value) {
            if (value.data) {
                $scope.findByPage();
            } else {
                alert("添加失败!");
            }
        })
    };

    $scope.deleteByIds = function () {
        baseService.deleteByIds("/typeTemplate/delete",$scope.ids).then(function (value) {
            if (value.data) {
                $scope.findByPage();
                $scope.ids = [];
            } else {
                alert("删除失败!");
            }
        })
    };

    $scope.getOption = function () {
        baseService.doGet("/brand/findBrandList").then(function (value) {
            $scope.brandList = {};
            $scope.brandList.data = value.data;
        });
        baseService.doGet("/spec/findSpecList").then(function (value) {
            $scope.specList = {};
            $scope.specList.data = value.data;
        })
    };

    $scope.typeTemplate = {customAttributeItems:[]};
    $scope.addExtOption = function () {
        $scope.cai = {};
        $scope.typeTemplate.customAttributeItems.push($scope.cai);
    };

    $scope.removeExtOption = function (index) {
        $scope.typeTemplate.customAttributeItems.splice(index, 1);
    };

    $scope.show = function (entity) {
        $scope.typeTemplate.id = entity.id;
        $scope.typeTemplate.name = JSON.parse(JSON.stringify(entity.name));
        $scope.typeTemplate.specIds = JSON.parse(entity.specIds);
        $scope.typeTemplate.brandIds = JSON.parse(entity.brandIds);
        $scope.typeTemplate.customAttributeItems = JSON.parse(entity.customAttributeItems);
    }


});