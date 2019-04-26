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

    //初始化所有check
    $scope.initCheck = function (cartList) {
        $scope.checkAll = false;
        for (var i = 0; i < cartList.length; i++) {
            $scope.checkSellerStatus[$scope.cartList[i].sellerId] = false;
            var orderItems = cartList[i].orderItems;
            for (var j = 0; j < orderItems.length; j++) {
                $scope.checkItemStatus[orderItems[j].itemId] = false;
            }
        }
    };

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
                        $scope.checkItemStatus[orderItem.itemId] = true;
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
            for (var i = 0; i < $scope.choseCart.length; i++) {
                if ($scope.choseCart[i].sellerId == cart.sellerId) {
                    var orderItems = $scope.choseCart[i].orderItems;
                    for (var j = 0; j < orderItems.length; j++) {
                        if (orderItems[j].itemId == orderItem.itemId) {
                            $scope.choseCart[i].orderItems.splice($scope.choseCart[i].orderItems.indexOf(orderItems[j]), 1);
                        }
                    }
                    $scope.checkItemStatus[orderItem.itemId] = false;
                    if (orderItems.length == 0) {
                        $scope.choseCart.splice($scope.choseCart.indexOf($scope.choseCart[i]), 1);
                    }
                }
            }
            $scope.checkSellerStatus[cart.sellerId] = false;
            $scope.checkAll = false;
        }

    };

    $scope.addNewCart = function (cart, orderItem) {
        $scope.newCart = {};
        $scope.newCart.sellerId = cart.sellerId;
        $scope.newCart.seller = cart.seller;
        $scope.newCart.orderItems = [];
        $scope.newCart.orderItems.push(orderItem);
        $scope.choseCart.push($scope.newCart);
        $scope.checkItemStatus[orderItem.itemId] = true;
        for (var i = 0; i < $scope.choseCart.length; i++) {
            if ($scope.choseCart.sellerId == cart.sellerId) {
                if ($scope.choseCart[i].orderItems.length == cart.orderItems.length) {
                    $scope.checkSellerStatus[cart.sellerId] = true;
                }
            }
        }
    };

    $scope.checkItemStatus = {};
    $scope.checkAll = false;
    $scope.checkItem = function (cart, orderItem) {
        return $scope.checkItemStatus[orderItem.itemId] || $scope.checkAll;
    };

    $scope.checkSellerStatus = {};
    $scope.checkSeller = function (cart) {
        return $scope.checkSellerStatus[cart.sellerId] || $scope.checkAll;
    };

    $scope.createSingleCart = function (cart, event) {
        $scope.newCart = JSON.parse(JSON.stringify(cart));
        if ($scope.choseCart.length > 0) {
            for (var i = 0; i < $scope.choseCart.length; i++) {
                if ($scope.choseCart[i].sellerId == cart.sellerId) {
                    if (event.target.checked) {//选中
                        $scope.choseCart.splice($scope.choseCart.indexOf($scope.choseCart[i]), 1);
                        $scope.choseCart.push($scope.newCart);
                        $scope.updateOrderItemsByCart($scope.newCart.orderItems, true);
                        $scope.checkSellerStatus[cart.sellerId] = true;
                        // $scope.checkItemStatus[cart.sellerId] = true;
                    } else {//未选中
                        $scope.choseCart.splice($scope.choseCart.indexOf($scope.newCart), 1);
                        $scope.updateOrderItemsByCart($scope.newCart.orderItems, false);
                        $scope.checkSellerStatus[cart.sellerId] = false;
                        $scope.checkAll = false;
                    }
                } else {
                    $scope.choseCart.push($scope.newCart);
                    $scope.updateOrderItemsByCart($scope.newCart.orderItems, true);
                    $scope.checkSellerStatus[cart.sellerId] = true;
                    $scope.checkAll = false;
                }
            }
        } else {
            if (event.target.checked) {//选中
                $scope.choseCart.push($scope.newCart);
                $scope.updateOrderItemsByCart($scope.newCart.orderItems, true);
                $scope.checkSellerStatus[cart.sellerId] = true;
                $scope.checkAll = false;
            }
        }
        if ($scope.choseCart.length == $scope.cartList.length) {
            $scope.checkAll = true;
        }
    };

    $scope.updateOrderItemsByCart = function (orderItems, boolean) {
        for (var j = 0; j < orderItems.length; j++) {
            $scope.checkItemStatus[orderItems[j].itemId] = boolean;
        }
    };

    $scope.createAll = function (event) {
        if (event.target.checked) {
            $scope.choseCart = JSON.parse(JSON.stringify($scope.cartList));
            for (var i = 0; i < $scope.choseCart.length; i++) {
                var sellerId = $scope.choseCart[i].sellerId;
                $scope.checkSellerStatus[sellerId] = true;
                $scope.updateOrderItemsByCart($scope.choseCart[i].orderItems, true)
            }
            $scope.checkAll = true;
        } else {
            for (var j = 0; j < $scope.cartList.length; j++) {
                var sellerId1 = $scope.cartList[j].sellerId;
                $scope.checkSellerStatus[sellerId1] = false;
                $scope.updateOrderItemsByCart($scope.choseCart[j].orderItems, false);
            }
            $scope.checkAll = false;
            $scope.choseCart = [];
        }
    };

    $scope.findCart = function () {
        baseService.sendGet("/cart/findCart").then(function (value) {
            $scope.res = {total: 0, totalPrice: 0};
            $scope.cartList = value.data;
            $scope.getTotal($scope.cartList);
            $scope.initCheck($scope.cartList);
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