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
});