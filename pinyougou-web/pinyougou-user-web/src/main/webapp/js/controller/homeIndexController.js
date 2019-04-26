app.controller("homeIndexController",function ($scope, $controller,$location,$interval,baseService) {
    $controller("indexController",{$scope:$scope});
    $scope.page="1";
    $scope.rows="5";
    $scope.search = function () {
        baseService.sendGet("/user/getUserOrder?page="+$scope.page+"&rows="+$scope.rows).then(function (response) {
            //数据
            $scope.dataList = response.data.rows;
            //总记录数
            $scope.totalItems=response.data.total;
            //总页数
           $scope.totalPages= Math.ceil($scope.totalItems/$scope.rows);
            $scope.showPage($scope.page);
        })
    };

    //时间转换
    Date.prototype.Format = function (fmt) { //author: meizz
        var o = {
            "M+": this.getMonth() + 1, //月份
            "d+": this.getDate(), //日
            "h+": this.getHours(), //小时
            "m+": this.getMinutes(), //分
            "s+": this.getSeconds(), //秒
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度
            "S": this.getMilliseconds() //毫秒
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    };

    $scope.changeTime=function (time) {
       var date=new Date(time);
       return date.Format("yyyy-MM-dd hh:mm:ss");

    };
    $scope.showPage = function (page) {
        $scope.page = page;
        $scope.pageNum = [];
        $scope.firstDotted = true;
        $scope.lastDotted = true;
        var totalPages = $scope.totalPages;
        var lastPage = totalPages;
        var firstPage = 1;
        if (totalPages >= 5) {
            if (page < 3) {
                firstPage = 1;
                lastPage = 5;
                $scope.firstDotted = false;
            } else if (page > totalPages - 3) {
                lastPage = totalPages;
                firstPage = totalPages - 4;
                $scope.lastDotted = false;
            } else {
                firstPage = page - 2;
                lastPage = page + 2;
            }
        } else {
            $scope.firstDotted = false;
            $scope.lastDotted = false;
        }
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageNum.push(i);
        }
    };


    $scope.searchPage = function (page) {
        page = parseInt(page);
        if (page >= 1 && page <= $scope.totalPages&& page != $scope.page) {
            $scope.page = page;
            $scope.search();
        }
    };

    //商品点击事件跳转至详情页
    $scope.jumpToOrderItem=function (goodsId) {
        location.href="http://item.pinyougou.com/"+goodsId+".html";
    }

    //付款点击事件
    $scope.goToPay=function (id,total) {
       location.href="pay.html?orderId="+id+"&money="+total*100;
    };

    //支付页面获取二维码事件
    $scope.genPayCode = function () {
        //获取从个人订单点击事件传过来的参数
        var outTradeNo= $location.search().orderId;
        var totalFee= $location.search().money;
        baseService.sendGet("/user/genPayCode?outTradeNo="+outTradeNo+"&totalFee="+totalFee).then(function (value) {
            $scope.outTradeNo = value.data.outTradeNo;
            $scope.money = value.data.totalFee/100;
            var code = value.data.codeUrl;
            document.getElementById("qrious").src="/barcode?url="+code;
        });

        var timer = $interval(function () {
            baseService.sendGet("/user/getStatus?outTradeNo=" + outTradeNo).then(function (value) {
                $scope.status = value.data.status;
                if ($scope.status == "SUCCESS") {
                    baseService.sendGet("/user/updatePayLog?transactionId=" + value.data.transactionId);
                    $interval.cancel(timer);
                    location.href = "/paysuccess.html?totalFee=" + $scope.money;
                }
                if ($scope.status == "PAYERROR") {
                    location.href="/payfail.html";
                }
            })
        },10000,200);

        timer.then(function () {
            $scope.tips = "二维码已过期，刷新页面重新获取二维码。";
        });
    };
});