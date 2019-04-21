app.controller("itemController", function ($scope, $controller, $http, baseService) {
    $controller('baseController', {$scope:$scope});

    $scope.addNum = function (num) {
        $scope.num = parseInt($scope.num);
        $scope.num += num;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    };

    $scope.spec={};
    $scope.sku = {};
    $scope.setSpec = function (specName, specOption) {
        $scope.spec[specName] = specOption;
        $scope.setSku();
    };



    $scope.loadSku = function () {
        $scope.spec = JSON.parse(itemList[0].spec);
        $scope.setSku();
    };

    $scope.setSku = function () {
        for (var i = 0; i < itemList.length; i++) {
            if (itemList[i].spec == JSON.stringify($scope.spec)) {
                $scope.sku = itemList[i];
                break;
            }
        }
    };

    $scope.addToCart = function () {
        $http.get("http://cart.pinyougou.com/cart/addToCarts?itemId="
            + $scope.sku.id + "&num=" + $scope.num,{"withCredentials":true}).then(function (value) {
            if (value.data) {
                alert("添加到购物车成功!");
                location.href = "http://cart.pinyougou.com";
            } else {
                alert("添加失败!");
            }
        });
    }
});