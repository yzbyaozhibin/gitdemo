app.controller("cartController", function ($scope, $controller, baseService) {

    $controller("baseController", {$scope: $scope});

    //测试数据
    // $scope.choseCart = [{sellerId:'admin',seller:'品优购',orderItems:[{
    //                 "goodsId": 149187842867996,
    //                 "itemId": 1369453,
    //                 "num": 1,
    //                 "picPath": "http://image.pinyougou.com/jd/wKgMg1qzw5OAGCT9AAXwJ4Bzfwo451.jpg",
    //                 "price": 0.01,
    //                 "sellerId": "admin",
    //                 "title": "ROZZ性感夜店职业女装连衣裙长袖雪纺棉麻印花蕾丝修身韩版 165",
    //                 "totalFee": 0.01
    //             },
    //             {
    //                 "goodsId": 149187842867973,
    //                 "itemId": 1369326,
    //                 "num": 1,
    //                 "picPath": "http://image.pinyougou.com/jd/wKgMg1qtKEOATL9nAAFti6upbx4132.jpg",
    //                 "price": 6688,
    //                 "sellerId": "admin",
    //                 "title": "Apple iPhone 8 Plus (A1864) 移动4G 64G",
    //                 "totalFee": 6688
    //             }]},
    //     {sellerId:'xiaomi',seller:'小米',orderItems:[{
    //             "goodsId": 149187842867984,
    //             "itemId": 1369382,
    //             "num": 4,
    //             "picPath": "http://image.pinyougou.com/jd/wKgMg1quThWAFU-eAAH6-t_lXX0413.jpg",
    //             "price": 2899,
    //             "sellerId": "xiaomi",
    //             "title": "小米6 全网通 移动4G 128G",
    //             "totalFee": 11596
    //         }]}];
    $scope.choseCart = [];
    $scope.updateChoseCart = function (cart, orderItem, event) {
        if (event.target.checked) {//选中
            if ($scope.choseCart.length > 0) {
                var check = 0;
                for (var i = 0; i < $scope.choseCart.length; i++) {
                    var sellerId = $scope.choseCart[i].sellerId;
                    if (sellerId == cart.sellerId) {
                        check = 1;
                        $scope.choseCart[i].orderItems.push(orderItem);
                        if (cart.orderItems.length == $scope.choseCart[i].orderItems.length) {
                            $scope.checkSellerStatus[cart.sellerId] = true;
                        }
                    }
                }
                if (check == 0) {
                    $scope.addNewCart(cart, orderItem);
                }
            } else {
                $scope.addNewCart(cart, orderItem);
            }
        } else {//未选中
            if ($scope.choseCart.length > 0) {
                for (var i = 0; i < $scope.choseCart.length; i++) {
                    var sellerId = $scope.choseCart[i].sellerId;
                    if (sellerId == cart.sellerId) {
                        var orderItems = $scope.choseCart[i].orderItems;
                        for (var j = 0; j < orderItems.length; j++) {
                            if (orderItems[j].itemId == orderItem.itemId) {
                                $scope.choseCart[i].orderItems.splice($scope.choseCart[i].orderItems.indexOf(orderItems[j]), 1);
                            }
                        }
                        if (orderItems.length == 0) {
                            $scope.choseCart.splice($scope.choseCart.indexOf($scope.choseCart[i]), 1);
                        }
                    }
                    $scope.checkSellerStatus[cart.sellerId] = false;
                }
            }
        }

    };

    $scope.addNewCart = function (cart, orderItem) {
        $scope.newCart = {};
        $scope.newCart.sellerId = cart.sellerId;
        $scope.newCart.seller = cart.seller;
        $scope.newCart.orderItems = [];
        $scope.newCart.orderItems.push(orderItem);
        $scope.choseCart.push($scope.newCart);
    };

    $scope.checkItemStatus = {};
    $scope.checkItem = function (cart, orderItem) {
        return $scope.checkItemStatus[cart.sellerId];
    };

    $scope.checkSellerStatus = {};
    $scope.checkSeller = function (cart) {
        return $scope.checkSellerStatus[cart.sellerId];
    };

    $scope.createSingleCart = function (cart, event) {
        $scope.newCart = JSON.parse(JSON.stringify(cart));
        if ($scope.choseCart.length > 0) {
            for (var i = 0; i < $scope.choseCart.length; i++) {
                if ($scope.choseCart[i].sellerId == cart.sellerId) {
                    $scope.choseCart.splice($scope.choseCart.indexOf($scope.choseCart[i]),1);
                    if (event.target.checked) {
                        $scope.choseCart.push($scope.newCart);
                        $scope.checkItemStatus[cart.sellerId] = true;
                    } else {
                        $scope.choseCart.splice($scope.choseCart.indexOf($scope.newCart),1);
                        $scope.checkItemStatus[cart.sellerId] = false;
                    }
                }
            }
        } else {
            $scope.choseCart.push($scope.newCart);
            $scope.checkItemStatus[cart.sellerId] = true;
        }

    };

    $scope.findCart = function () {
        baseService.sendGet("/cart/findCart").then(function (value) {
            $scope.res = {total: 0, totalPrice: 0};
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

    $scope.addToCarts = function (itemId, value) {
        baseService.sendGet("/cart/addToCarts?itemId=" + itemId + "&num=" + value).then(function (value) {
            if (value.data) {
                $scope.findCart();
            } else {
                alert("操作失败");
            }
        })
    };

    //将选择的购物车从redis中复制一份,重新封装存入redis
    $scope.saveChoseCart = function () {
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