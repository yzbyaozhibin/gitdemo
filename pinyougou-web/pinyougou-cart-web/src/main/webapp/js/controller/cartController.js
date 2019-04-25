app.controller("cartController", function ($scope, $controller, baseService) {

    $controller("baseController",{$scope:$scope});

    //测试数据
    $scope.choseCart = [{sellerId:'admin',seller:'品优购',orderItems:[{
                    "goodsId": 149187842867996,
                    "itemId": 1369453,
                    "num": 1,
                    "picPath": "http://image.pinyougou.com/jd/wKgMg1qzw5OAGCT9AAXwJ4Bzfwo451.jpg",
                    "price": 0.01,
                    "sellerId": "admin",
                    "title": "ROZZ性感夜店职业女装连衣裙长袖雪纺棉麻印花蕾丝修身韩版 165",
                    "totalFee": 0.01
                },
                {
                    "goodsId": 149187842867973,
                    "itemId": 1369326,
                    "num": 1,
                    "picPath": "http://image.pinyougou.com/jd/wKgMg1qtKEOATL9nAAFti6upbx4132.jpg",
                    "price": 6688,
                    "sellerId": "admin",
                    "title": "Apple iPhone 8 Plus (A1864) 移动4G 64G",
                    "totalFee": 6688
                }]},
        {sellerId:'xiaomi',seller:'小米',orderItems:[{
                "goodsId": 149187842867984,
                "itemId": 1369382,
                "num": 4,
                "picPath": "http://image.pinyougou.com/jd/wKgMg1quThWAFU-eAAH6-t_lXX0413.jpg",
                "price": 2899,
                "sellerId": "xiaomi",
                "title": "小米6 全网通 移动4G 128G",
                "totalFee": 11596
            }]}];

    $scope.findCart = function () {
        baseService.sendGet("/cart/findCart").then(function (value) {
            $scope.res = {total:0, totalPrice:0};
            $scope.cartList = value.data;
            $scope.getTotal($scope.cartList);
        })
    };

    $scope.getTotal = function (cartList) {
        for (var i = 0; i < cartList.length; i++) {
            var orderItems = cartList[i].orderItems;
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

    //将选择的购物车从redis中复制一份,重新封装存入redis
    $scope.saveChoseCart  = function () {
        // location.href = "/order/getOrderInfo.html";
        baseService.sendPost("/cart/saveChoseCart", $scope.choseCart).then(function (value) {
            if (value.data) {
                location.href = "/order/getOrderInfo.html";
            } else {
                alert("结算失败!");
            }
        })
    }

});