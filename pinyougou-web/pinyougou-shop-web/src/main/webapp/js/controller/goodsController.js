/** 定义控制器层 */
app.controller('goodsController', function ($scope, baseService, $controller) {
    $controller('baseController', {$scope: $scope});

    $scope.status = ['未审核','已审核','审核未通过','关闭'];

    $scope.search = function (pageInfo) {
        baseService.sendGet("/goods/findByPage" + pageInfo,$scope.searchEntity).then(function (value) {
            $scope.dataList = value.data.rows;
            $scope.paginationConf.totalItems = value.data.total;
        })
    };

    $scope.saveOrUpdate = function () {
        //封装goodsdesc里的简介
        $scope.goods.goodsDesc.introduction = editor.html();

        //封装goodsdesc里的扩展属性
        // $scope.goods.goodsDesc.customAttributeItems = $scope.extList;

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
            customAttributeItems: [],
            specificationItems: []
        },
        items: []
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
                    $scope.goods.typeTemplateId = $scope.catList3[i].typeId;
                }
            }
        } else {
            $scope.goods.typeTemplateId = null;
        }

    });

    $scope.$watch('goods.typeTemplateId', function (newVal, oldVal) {
        if (newVal) {
            baseService.sendGet("/typeTemplate/findTypeTemplateById?id=" + newVal).then(function (value) {
                $scope.brandList = JSON.parse(value.data.brandIds);
                $scope.goods.goodsDesc.customAttributeItems = JSON.parse(value.data.customAttributeItems);
                $scope.specOptions = value.data.specOptions;
            })
        } else {
            $scope.brandList = [];
            $scope.goods.goodsDesc.customAttributeItems = [];
            $scope.specOptions = [];
        }
    });

    $scope.setSpecItems = function ($event, specName, specOption,specificationItems) {
        var obj = $scope.searchObjByKey(specName, 'attributeName');
        if (obj) {
            if ($event.target.checked) {
                obj.attributeValue.push(specOption);
            } else {
                obj.attributeValue.splice(obj.attributeValue.indexOf(specOption), 1);
                if (obj.attributeValue.length == 0) {
                    specificationItems.splice(specificationItems.indexOf(obj),1);
                }
            }
        } else {
            $scope.goods.goodsDesc.specificationItems.push({attributeValue: [specOption], attributeName: specName});
        }
    };

    $scope.searchObjByKey = function (value, key) {
        var specificationItems = $scope.goods.goodsDesc.specificationItems;
        for (var i = 0; i < specificationItems.length; i++) {
            if (specificationItems[i][key] == value) {
                return specificationItems[i];
            }
        }
    };

    $scope.createItem = function () {
        if($scope.goods.goodsDesc.specificationItems.length == 0) {
            $scope.goods.items = [];
        } else {
            $scope.goods.items = [{spec: {}, price: 0, num: 9999, status: 0, isDefault: 0}];
            var specItems = $scope.goods.goodsDesc.specificationItems;
            for (var i = 0; i < specItems.length; i++) {
                var newItems = new Array();

                for (var j = 0; j < $scope.goods.items.length; j++) {
                    var item = $scope.goods.items[j];
                    var attributeName = specItems[i].attributeName;
                    var attributeValue = specItems[i].attributeValue;

                    for (var k = 0; k< attributeValue.length; k++) {
                        var newItem = JSON.parse(JSON.stringify(item));
                        newItem.spec[attributeName] = attributeValue[k];
                        newItems.push(newItem);
                    }
                }
                $scope.goods.items = newItems;
            }
        }
    };

    $scope.updateIsMarketable = function (status) {
        if ($scope.ids.length != 0) {
            baseService.sendGet("/goods/updateIsMarketable?ids=" + $scope.ids + "&isMarketable=" + status).then(function (value) {
                if (value.data) {
                    $scope.reload();
                } else {
                    alert("操作失败!");
                }
            })
        } else {
            alert("请选择要删除的数据!");
        }
    }

});