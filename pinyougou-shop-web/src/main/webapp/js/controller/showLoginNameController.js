app.controller('showLoginNameController',function ($scope,$controller,showLoginNameService) {
    $scope.showLoginName=function () {
        indexService.getLoginName().success(
            function (reponse) {
                $scope.loginName = reponse.name
            }
        )
    }
})