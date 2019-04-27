app.controller("orderController", function ($scope, $controller, $interval, $location, baseService) {
    $controller('cartController', {$scope: $scope});

    $scope.order = {paymentType: '1'};

    $scope.showOrderInfo = function () {
        baseService.sendGet("/order/getAddress").then(function (value) {
            $scope.findTempCart();
            $scope.address = value.data;
            $scope.order.receiverAreaName = $scope.address[0].address;
            $scope.order.receiverMobile = $scope.address[0].mobile;
            $scope.order.receiver = $scope.address[0].contact;
        })
    };

    $scope.findTempCart = function () {
        baseService.sendGet("/cart/findTempCart").then(function (value) {
            $scope.res = {total: 0, totalPrice: 0};
            $scope.choseCart = value.data;
            $scope.getTotal();
        })
    };

    $scope.isSelect = function (address) {
        return address.address == $scope.order.receiverAreaName;
    };

    $scope.selectAddress = function (address) {
        $scope.order.receiverAreaName = address.address;
        $scope.order.receiverMobile = address.mobile;
        $scope.order.receiver = address.contact;
    };

    $scope.saveOrder = function () {
        $scope.orderInfo = {'order': $scope.order, 'tempCartList': $scope.choseCart};
        baseService.sendPost("/order/saveOrder", $scope.orderInfo).then(function (value) {
            if ($scope.order.paymentType == '2') {
                location.href = "/order/paysuccess.html";
            } else {
                location.href = "/order/pay.html?outTradeNo=" + value.data.outTradeNo;
            }
        })
    };

    $scope.entity = {};
    $scope.saveOrUpdateAddress = function () {
        var url = "save";
        if ($scope.entity.id) {
            url = "update";
        }
        baseService.sendPost("/order/" + url, $scope.entity).then(function (value) {
            if (value.data) {
                $scope.showOrderInfo();
            } else {
                alert("操作失败!");
            }
        })
    };

    $scope.genPayCode = function () {
        var outTradeNo = $location.search().outTradeNo;
        baseService.sendGet("/order/genPayCode?outTradeNo=" + outTradeNo).then(function (value) {
            $scope.outTradeNo = value.data.outTradeNo;
            $scope.money = value.data.totalFee / 100;
            $scope.codeUrl = value.data.codeUrl;
        });

        var timer = $interval(function () {
            baseService.sendGet("/order/getStatus?outTradeNo=" + $scope.outTradeNo).then(function (value) {
                $scope.status = value.data.status;
                if ($scope.status == "SUCCESS") {
                    baseService.sendGet("/order/updatePayLog?outTradeNo=" + $scope.outTradeNo + "&transactionId=" + value.data.transactionId);
                    $interval.cancel(timer);
                    location.href = "/order/paysuccess.html?totalFee=" + $scope.money;
                }
                if ($scope.status == "PAYERROR") {
                    location.href = "/order/payfail.html";
                }
            })
        }, 10000, 200);

        timer.then(function () {
            $scope.tips = "二维码已过期，刷新页面重新获取二维码。";
        })
    };

    $scope.getTotalFee = function () {
        return $location.search().totalFee;
    }

});