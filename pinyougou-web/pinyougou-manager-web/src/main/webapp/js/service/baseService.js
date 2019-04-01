app.service("baseService",function ($http) {

    this.doGet = function (url) {
        return $http.get(url);
    };

    this.findByPage = function (url,data) {
        if (data && JSON.stringify(data) != "{}") {
            return $http.get(url, {params: data});
        }
        return this.doGet(url);
    };

    this.saveOrUpdate = function (url,entity) {
        return $http.post(url,entity);
    };

    this.deleteByIds = function (url, ids) {
        var url = url + "?ids=" + ids;
        return $http.get(url);
    }
});