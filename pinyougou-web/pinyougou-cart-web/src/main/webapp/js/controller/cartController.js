app.controller("cartController", function ($scope, $controller, baseService) {

    $controller("baseController", {$scope: $scope});
    // --------------------------------------------------复选框开始---------------------------------------------
    //初始化所有check
    $scope.checkItemStatus = {};
    $scope.checkSellerStatus = {};
    $scope.checkAll = false;
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
    //点击子选框的方法
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
            //判断是否需要选中全部
            $scope.checkAllFn();

        } else {//未选中
            for (var i = 0; i < $scope.choseCart.length; i++) {
                if ($scope.choseCart[i].sellerId == cart.sellerId) {
                    var orderItems = $scope.choseCart[i].orderItems;
                    for (var j = 0; j < orderItems.length; j++) {
                        if (orderItems[j].itemId == orderItem.itemId) {
                            $scope.choseCart[i].orderItems.splice(j, 1);
                        }
                    }
                    $scope.checkItemStatus[orderItem.itemId] = false;
                    if (orderItems.length == 0) {
                        $scope.choseCart.splice(i, 1);
                    }
                }
            }
            $scope.checkSellerStatus[cart.sellerId] = false;
            $scope.checkAll = false;
        }
        $scope.getTotal();
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
            if ($scope.choseCart[i].sellerId == cart.sellerId) {
                if ($scope.choseCart[i].orderItems.length == cart.orderItems.length) {
                    $scope.checkSellerStatus[cart.sellerId] = true;
                }
            }
        }
    };


    $scope.checkItem = function (cart, orderItem) {
        return $scope.checkItemStatus[orderItem.itemId] || $scope.checkAll;
    };

    $scope.checkSeller = function (cart) {
        return $scope.checkSellerStatus[cart.sellerId] || $scope.checkAll;
    };

    //点击父选框处理方法
    $scope.createSingleCart = function (cart, event) {
        $scope.newCart = JSON.parse(JSON.stringify(cart));
        if ($scope.choseCart.length > 0) {
            for (var i = 0; i < $scope.choseCart.length; i++) {
                if ($scope.choseCart[i].sellerId == cart.sellerId) {
                    if (event.target.checked) {//选中
                        $scope.choseCart.splice(i, 1);
                        $scope.choseCart.push($scope.newCart);
                        $scope.updateOrderItemsByCart($scope.newCart.orderItems, true);
                        $scope.checkSellerStatus[cart.sellerId] = true;
                    } else {//未选中
                        //有问题
                        $scope.choseCart.splice(i, 1);
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
            }
        }
        $scope.checkAllFn();
        $scope.getTotal();
    };

    $scope.checkAllFn = function () {
        if ($scope.choseCart.length == $scope.cartList.length) {
            var checkAll = 1;
            for (var k = 0; k < $scope.choseCart.length; k++) {
                if (!$scope.checkSellerStatus[$scope.choseCart[k].sellerId]) {
                    checkAll = 0;
                }
            }
            if (checkAll == 1) {
                $scope.checkAll=true;
            }
        }
    };

    $scope.updateOrderItemsByCart = function (orderItems, boolean) {
        for (var j = 0; j < orderItems.length; j++) {
            $scope.checkItemStatus[orderItems[j].itemId] = boolean;
        }
    };


    //点击总选框的方法
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
        $scope.getTotal();
    };
// --------------------------------------------------复选框结束---------------------------------------------
    $scope.findCart = function () {
        baseService.sendGet("/cart/findCart").then(function (value) {
            $scope.choseCart = [];
            $scope.res = {total: 0, totalPrice: 0};
            $scope.cartList = value.data;

            if ($scope.cartList.length > 0) {
                $scope.totalShop = 0;
                for (var i = 0; i < $scope.cartList.length; i++) {
                    var orderItems = $scope.cartList[i].orderItems;
                    for (var j = 0; j < orderItems.length; j++) {
                        $scope.totalShop += orderItems[j].num;
                    }
                }
            }

            // $scope.getTotal($scope.cartList);
            $scope.initCheck($scope.cartList);
        })
    };

    $scope.res = {total:0, totalPrice:0};
    $scope.getTotal = function () {
        $scope.res = {total:0, totalPrice:0};
        if ($scope.choseCart.length > 0) {
            for (var i = 0; i < $scope.choseCart.length; i++) {
                var orderItems = $scope.choseCart[i].orderItems;
                for (var j = 0; j < orderItems.length; j++) {
                    $scope.res.total += orderItems[j].num;
                    $scope.res.totalPrice += orderItems[j].totalFee;
                }
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