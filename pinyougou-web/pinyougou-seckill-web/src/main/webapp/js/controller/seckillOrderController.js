app.controller('seckillOrderController', function ($scope, $controller, $location,$interval,baseService) {
    $controller('baseController', {$scope:$scope});

    $scope.genPayCode = function () {
        var id = $location.search().id;
        baseService.sendGet("/order/getPayInfo?id=" + id).then(function (value) {
            $scope.outTradeNo = value.data.outTradeNo;
            $scope.money = value.data.totalFee/100;
            $scope.codeUrl = value.data.codeUrl;
        });

        var timer = $interval(function () {
                baseService.sendGet("/order/getStatus?outTradeNo=" + $scope.outTradeNo).then(function (value) {
                    $scope.status = value.data.status;
                    $scope.transactionId = value.data.transactionId;
                    if ($scope.status == "SUCCESS") {
                        $interval.cancel(timer);
                        //保存订单到数据库,并删除索引库中的订单
                        baseService.sendGet("/order/save?transactionId=" + $scope.transactionId + "&outTradeNo=" + $scope.outTradeNo);
                        location.href = "/order/paysuccess.html?money=" + $scope.money;
                    }
                    if ($scope.status == "PAYERROR") {
                        location.href = "/order/paysuccess.html";
                    }
                    if ($scope.status == "CLOSED") {
                        $scope.codeStr = "二维码已过期,请刷新重新获取!";
                    }
                })
        },3000,200)
    };

    $scope.getTotalFee = function () {
        return $location.search().money;
    }

});