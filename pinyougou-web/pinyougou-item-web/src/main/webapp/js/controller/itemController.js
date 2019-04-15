app.controller("itemController", function ($scope) {

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
        alert("购买数量:" + $scope.num + "商品id:" + $scope.sku.id);
    }
});