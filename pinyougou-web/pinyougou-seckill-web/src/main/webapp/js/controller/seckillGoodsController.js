app.controller('seckillGoodsController',function ($scope, $controller, $timeout, $location, baseService) {
    $controller('baseController', {$scope:$scope});

    $scope.findSeckillGoods = function () {
        baseService.sendGet("/seckillGoods/findSeckillGoods").then(function (value) {
            $scope.seckillGoodsList = value.data;
        })
    };

    $scope.goToOrder =function (id) {
        location.href="/seckill-item.html?id=" + id;
    };

    $scope.showItems = function () {
        var id = $location.search().id;
        baseService.sendGet("/seckillGoods/showItems?id=" + id).then(function (value) {
            $scope.seckillGoods = value.data;
            $scope.showClock(value.data.endTime);
        })
    };

    $scope.showClock = function (endTime) {
        var second = (endTime - new Date().getTime())/1000;
        if (second > 0) {
            var minute = second/60;
            var hour = minute/60;
            var day = hour/24;
            $scope.leftTime = Math.floor(day) + "天 " +
                $scope.formatCount(Math.floor(hour % 24)) + ":" +
                $scope.formatCount(Math.floor(minute % 60)) + ":" +
                $scope.formatCount(Math.floor(second % 60));
            $timeout(function () {
                $scope.showClock(endTime);
            },1000)
        }
    };

    $scope.formatCount = function (num) {
        return num < 10?'0' + num:num;
    }
});