app.controller("orderController", function ($scope, $controller, $interval, $location, baseService) {
    $controller('cartController', {$scope:$scope});

    $scope.order = {paymentType:'1'};

    $scope.showOrderInfo = function () {
        baseService.sendGet("/order/getAddress").then(function (value) {
            $scope.findCart();
            $scope.address = value.data;
            $scope.order.receiverAreaName=$scope.address[0].address;
            $scope.order.receiverMobile=$scope.address[0].mobile;
            $scope.order.receiver=$scope.address[0].contact;
        })
    };

    $scope.isSelect = function (address) {
        return address.address == $scope.order.receiverAreaName;
    };

    $scope.selectAddress = function (address) {
        $scope.order.receiverAreaName=address.address;
        $scope.order.receiverMobile=address.mobile;
        $scope.order.receiver=address.contact;
    };

    $scope.saveOrder = function () {
        baseService.sendPost("/order/saveOrder",$scope.order).then(function (value) {
            if ($scope.order.paymentType == '2') {
                location.href = "/order/paysuccess.html";
            } else {
                if (value.data) {
                    location.href = "/order/pay.html";
                } else {
                    alert("提交失败!");
                }
            }
        })
    };

    $scope.entity = {};
    $scope.saveOrUpdateAddress = function () {
        var url = "save";
        if ($scope.entity.id) {
            url = "update";
        }
        baseService.sendPost("/order/" + url,$scope.entity).then(function (value) {
            if (value.data) {
                $scope.showOrderInfo();
            } else {
                alert("操作失败!");
            }
        })
    };

    $scope.genPayCode = function () {
        baseService.sendGet("/order/genPayCode").then(function (value) {
            $scope.outTradeNo = value.data.outTradeNo;
            $scope.money = value.data.totalFee/100;
            $scope.codeUrl = value.data.codeUrl;
        });

        var timer = $interval(function () {
            baseService.sendGet("/order/getStatus?outTradeNo=" + $scope.outTradeNo).then(function (value) {
                $scope.status = value.data.status;
                if ($scope.status == "SUCCESS") {
                    baseService.sendGet("/order/updatePayLog?transactionId=" + value.data.transactionId);
                    $interval.cancel(timer);
                    location.href = "/order/paysuccess.html?totalFee=" + $scope.money;
                }
                if ($scope.status == "PAYERROR") {
                    location.href="/order/payfail.html";
                }
            })
        },10000,200);

        timer.then(function () {
            $scope.tips = "二维码已过期，刷新页面重新获取二维码。";
        })
    };

    $scope.getTotalFee = function () {
        return $location.search().totalFee;
    }

});