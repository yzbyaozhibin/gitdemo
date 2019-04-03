/** 定义控制器层 */
app.controller('goodsController', function ($scope, baseService, $controller) {
    $controller('baseController', {$scope: $scope});

    $scope.saveOrUpdate = function () {
        //封装goodsdesc里的简介
        $scope.goods.goodsDesc.introduction = editor.html();

        //封装goodsdesc里的扩展属性
        $scope.goods.goodsDesc.customAttributeItems = $scope.extList;

        baseService.sendPost("/goods/save", $scope.goods).then(function (value) {
            if (value.data) {
                $scope.goods = {goodsDesc: {itemImages: []}};
                editor.html('');
                alert("保存成功!");
            } else {
                alert("操作失败!");
            }
        })

    };

    $scope.goods = {
        goodsDesc: {
            itemImages: [],
            customAttributeItems: []
        }
    };
    $scope.fileUpload = function () {
        baseService.fileUpload().then(function (value) {
            if (value.data.status == 200) {
                $scope.entityImg.url = value.data.url;
            } else {
                alert("上传失败!");
            }
        })
    };

    $scope.addPic = function () {
        $scope.goods.goodsDesc.itemImages.push($scope.entityImg);
    };

    $scope.removePic = function (index) {
        $scope.goods.goodsDesc.itemImages.splice(index, 1);
    };


    $scope.findItemCatList = function (parentId, list) {
        baseService.sendGet("/itemcat/findByParentId?parentId=" + parentId).then(function (value) {
            $scope[list] = value.data;
        })
    };

    $scope.$watch('goods.category1Id', function (newVal, oldVal) {
        if (newVal) {
            $scope.findItemCatList(newVal, 'catList2');
        } else {
            $scope.catList2 = [];
        }
    });

    $scope.$watch('goods.category2Id', function (newVal, oldVal) {
        if (newVal) {
            $scope.findItemCatList(newVal, 'catList3');
        } else {
            $scope.catList3 = [];
        }
    });

    $scope.$watch('goods.category3Id', function (newVal, oldVal) {
        if (newVal) {
            for (var i = 0; i < $scope.catList3.length; i++) {
                if (newVal == $scope.catList3[i].id) {
                    $scope.typeId = $scope.catList3[i].typeId;
                }
            }
        } else {
            $scope.typeId = null;
        }

    });

    $scope.$watch('typeId', function (newVal, oldVal) {
        if (newVal) {
            baseService.sendGet("/typeTemplate/findOne?id=" + newVal).then(function (value) {
                $scope.typeTemplate = value.data;
                $scope.extList = JSON.parse($scope.typeTemplate.customAttributeItems);
                $scope.brandList = JSON.parse($scope.typeTemplate.brandIds);
            })
        } else {

        }
    })

});