app.controller("cartController", function ($scope, $controller, baseService) {
    $controller("baseController",{$scope:$scope});


    $scope.findCart = function () {
        baseService.sendGet("/cart/findCart").then(function (value) {
            $scope.res = {total:0, totalPrice:0};
            $scope.cartList = value.data;
            $scope.getTotal();
        })
    };

    $scope.getTotal = function () {
        for (var i = 0; i < $scope.cartList.length; i++) {
            var orderItems = $scope.cartList[i].orderItems;
            for (var j = 0; j < orderItems.length; j++) {
                $scope.res.total += orderItems[j].num;
                $scope.res.totalPrice += orderItems[j].totalFee;
            }
        }
    };

    $scope.addToCarts = function (itemId,value) {
        baseService.sendGet("/cart/addToCarts?itemId=" + itemId + "&num=" + value).then(function (value) {
            if (value.data) {
                $scope.findCart();
            } else {
                alert("操作失败");
            }
        })
    };

});