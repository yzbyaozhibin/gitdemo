app.service('baseService', function ($http) {

    this.sendGet = function (url, data) {
        if (data) {
            return $http.get(url,{params:data});
        } else {
            return $http.get(url);
        }
    };

    this.sendPost = function (url, data) {
        if (data) {
            return $http.post(url, data);
        } else {
            return $http.post(url, data);
        }
    };

    this.fileUpload = function () {
        var formData = new FormData();
        /** 追加需要上传的文件 */
        formData.append("file", file.files[0]);
        /** 发送异步请求上传文件 */
        return $http({
            method : 'post', // 请求方式
            url : "/upload", // 请求URL
            data : formData, // 表单数据
            headers : {'Content-Type' : undefined}, // 请求头
            transformRequest : angular.identity  // 转换对象
        });
    }
});