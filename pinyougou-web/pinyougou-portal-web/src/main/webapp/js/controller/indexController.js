/** 定义首页控制器层 */
app.controller("indexController", function($scope, baseService){
    $scope.findContentByCategoryId = function (categoryId) {
        baseService.sendGet("/findContentByCategoryId?categoryId=" + categoryId).then(function (value) {
            $scope.contentList = value.data;
        })
    };

    $scope.goToSearch = function () {
        $scope.keyword = $scope.keyword?$scope.keyword:'';
        location = "http://search.pinyougou.com?keyword=" + $scope.keyword;
    }
});