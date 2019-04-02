app.controller("itemCatController",function ($scope, baseService, $controller) {
    $controller("baseController",{$scope:$scope});

    $scope.findByParentId = function (parentId) {
        baseService.doGet("/itemcat/findByParentId?parentId=" + parentId).then(function (value) {
            $scope.parentId = parentId;
            $scope.dataList = value.data;
        })
    };

    $scope.saveOrUpdate = function () {
        var url = "save";

        if ($scope.entity.id){
            var url = "update";
        }
        baseService.saveOrUpdate("/itemcat/" + url,$scope.entity).then(function (value) {
            if (value) {
                $scope.findByParentId($scope.parentId);
            } else {
                alert("操作失败");
            }
        })
    };

    $scope.deleteByIds = function () {
        var count= $scope.ids.length;
        if (count == 0) {
            alert("请选择要删除的数据");
            return;
        }
        if (confirm("确认要删除这" + count + "条数据吗")) {
            baseService.deleteByIds("/itemcat/deleteByIds",$scope.ids).then(function (value) {
                if (value) {
                    $scope.findByParentId($scope.parentId);
                    $scope.ids=[];
                } else {
                    alert("删除失败");
                }
            })
        }
    };

    $scope.levelList = [];

    $scope.showNexLevel = function (id,name,level) {
        //如果level为空,代表页面点的是查询下一页,如果不为空,代表点击的是面包屑
        if (level != null) {
            $scope.level = level;
            $scope.levelList = $scope.levelList.slice(0,level);
        } else {
            $scope.level += 1;
            $scope.levelList.push({
                name:name,
                id:id,
                level:$scope.level
            });
        }
        $scope.findByParentId(id);
    };

    $scope.changeToArr = function (entityList,key) {
        var resArr = [];
        for(var i=0;i<=entityList.length -1;i++) {
            var entity = entityList[i];
            resArr.push(entity[key]);
        }
        return resArr.join(">>")
    }
});