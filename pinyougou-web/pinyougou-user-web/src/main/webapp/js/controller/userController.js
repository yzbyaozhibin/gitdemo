/** 定义控制器层 */
app.controller('userController', function($scope,$timeout,baseService){
    $controller('indexController',{$scope:$scope});
    $scope.user = {};
    $scope.confirmPassword = "";
    $scope.code = "";
    $scope.checkTips = 1;
    $scope.tips = "";
    $scope.save = function () {
        if ($scope.confirmPassword && $scope.confirmPassword == $scope.user.password) {
                baseService.sendPost("/user/save?code=" + $scope.code,$scope.user).then(function (value) {
                    if (value.data) {
                        $scope.user = {};
                        $scope.confirmPassword = "";
                        $scope.code = "";
                        $scope.checkTips = 1;
                    } else {
                        alert("注册失败!");
                    }
                })
        } else {
            alert("两次输入密码不一致!");
        }
    };

    $scope.sendSms = function () {
        if ($scope.user.phone && /^1[3|4|5|7|8]\d{9}$/.test($scope.user.phone)) {
            baseService.sendGet("/user/sendSms?phone=" + $scope.user.phone);
            $scope.checkTips = 0;
            // 设置倒计时
            $scope.countDown(60);
        } else {
            alert("手机格式不正确!");
        }
    };

    $scope.countDown =function (count) {
        count --;
        $scope.tips = count +  "秒后重新获取!";
        if (count < 0) {
            $scope.checkTips = 1;
            return;
        }
        $timeout(function () {
            $scope.countDown(count);
        },1000)
    }

    $scope.changePw=function () {
        if($scope.newPassword == $scope.upsure && $scope.newPassword){
            if($scope.pass){
                baseService.sendPost("/user/changePW",$scope.user).then(function (response) {
                    if (response.data) {
                        alert("修改密码成功！！！");
                        $scope.user={};
                        $scope.upsure="";
                    } else {
                        $("#originalPassword").html("原始密码错误");
                    }
                });
            }else{
                $("#originalPassword").html("原始密码不能为空");
            }
        }else{
            $("#newPassword").html("新的密码不能为空");
        }
    }
});