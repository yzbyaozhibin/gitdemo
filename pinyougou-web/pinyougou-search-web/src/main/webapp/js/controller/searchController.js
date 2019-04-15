/** 定义搜索控制器 */
app.controller("searchController", function ($scope, $sce, $location ,baseService) {
    $scope.searchParams = {keywords: '', category: '', brand: '', spec: {}, price: '', page : 1, rows : 10, sortField:'', sortValue:''};
    $scope.search = function () {
        baseService.sendPost("/Search", $scope.searchParams).then(function (value) {
            $scope.resMap = value.data;
            $scope.showPage($scope.searchParams.page);
        })
    };

    $scope.trustHtml = function (value) {
        return $sce.trustAsHtml(value);
    };

    $scope.addParams = function (key, value) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchParams[key] = value;
        } else {
            $scope.searchParams.spec[key] = value;
        }
        $scope.search();
    };

    $scope.removeParam = function (key) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchParams[key] = "";
        } else {
            delete $scope.searchParams.spec[key];
        }
        $scope.search();
    };

    $scope.showPage = function (page) {
        $scope.searchParams.page = page;
        $scope.pageNum = [];
        $scope.firstDotted = true;
        $scope.lastDotted = true;
        var totalPages = $scope.resMap.totalPages;
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
        if (page >= 1 && page <= $scope.resMap.totalPages&& page != $scope.searchParams.page) {
            $scope.searchParams.page = page;
            $scope.search();
        }
    };

    $scope.sortSearch = function (sortValue, sortField) {
        $scope.searchParams.sortValue = sortValue;
        $scope.searchParams.sortField = sortField;
        $scope.search();
    };

    $scope.getKeywords = function () {
        $scope.searchParams.keywords = $location.search().keyword;
        $scope.search();
    }
});
