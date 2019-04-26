/** 定义控制器层 */
app.controller('sellerController', function($scope, $controller, baseService){
    $controller("baseController",{$scope:$scope});

    //判断是否同意协议
    $scope.isAgree = function (event) {
        if (event.target.checked) {
            $scope.letGo = true;
        } else {
            $scope.letGo = false;
        }
    };

    $scope.entity = {};
    $scope.saveOrUpdate = function () {
        if ($scope.letGo == true) {
            baseService.sendPost("/seller/save",$scope.entity).then(function (value) {
                if(value.data) {
                    location.href = "/shoplogin.html";
                } else {
                    alert("注册失败!");
                }
            })
        } else {
            alert("需要同意协议才能注册哦!")
        }
    }


    /** 显示修改 */
    $scope.show = function () {
        baseService.sendGet("/seller/findOne").then(function (response) {
            $scope.seller = response.data;
            /** 把json对象转化成一个新的json对象 */
            $scope.seller = JSON.parse(JSON.stringify(seller));
        })
    };


    $scope.update = function () {
        /** 发送post请求 */
        baseService.sendPost("/seller/update", $scope.seller).then(function (response) {
            if (response.data) {
                alert("修改成功！");
                $scope.show();
            } else {
                alert("操作失败！");
            }
        });
    };

    //查询原密码
    $scope.findOldPassword = function () {
        baseService.sendPost("/seller/findOldPassword?oldPassword=" + $scope.oldPassword ).then(function (response) {
            if(!response.data){
                alert("原密码不正确");
            }
        });
    };

    //保存密码
    $scope.updatePassword = function () {
        //确认密码
        if ($scope.confirmNewPassword && $scope.newPassword == $scope.confirmNewPassword){
            baseService.sendPost("/seller/updatePassword?newPassword=" + $scope.newPassword).then(function (response) {
                if(response.data){
                    location.href = ("/shoplogin.html");
                }else {
                    alert("修改失败");
                }
            });
        }else {
            alert("密码不一致，请重新输入！");
            return;
        }
    };

    //清空
    $scope.clear = function () {
        $scope.oldPassword = "";
        $scope.newPassword = "";
        $scope.confirmNewPassword = "";

    };


});