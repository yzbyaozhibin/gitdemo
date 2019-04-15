app.controller("baseController",function ($scope) {
    //配置分页插件初始化数据
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 10,
        perPageOptions: [10, 15, 20, 30, 50],
        onChange: function () {
            $scope.findByPage();
        }
    };

    // 讲方法传过来的数据绑定到entity,回显
    $scope.show = function (entity) {
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };


    // 保存要删除的id
    $scope.ids = [];

    $scope.setIds = function (event,id) {
        if (event.target.checked) {
            $scope.ids.push(id);
        } else {
            $scope.ids.splice($scope.ids.indexOf(id),1);
        }
    };

    $scope.jsonToStrArr = function (jsonArr,key) {
        var strArr = JSON.parse(jsonArr);
        var resArr = [];
        for(var i = 0;i <= strArr.length - 1;i++) {
            var json = strArr[i];
            resArr.push(json[key]);
        }
        return resArr.join(",")
    };

    $scope.setAllIds = function (event) {
        if (event.target.checked) {
            $scope.ids = [];
            for (var i = 0; i < $scope.dataList.length; i++) {
                $scope.ids.push($scope.dataList[i].id);
            }
            $scope.checkStatus = 1;
        } else {
            $scope.ids=[];
            $scope.checkStatus = 0;
        }
    }
});