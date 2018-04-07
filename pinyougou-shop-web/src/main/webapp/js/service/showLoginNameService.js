app.service('showLoginNameService',function ($http) {
    this.getLoginName=function () {
        return $http.post('../getLoginName.do');
    }
})