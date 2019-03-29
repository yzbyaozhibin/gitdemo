app.service("baseService",function ($http) {

    this.findByPage = function (url,data) {
        return $http.get(url, {params: data});
    };

    this.saveOrUpdate = function (url,entity) {
        return $http.post(url,entity);
    };

    this.deleteByIds = function (url, ids) {
        var url = url + "?ids=" + ids;
        return $http.get(url);
    }
});