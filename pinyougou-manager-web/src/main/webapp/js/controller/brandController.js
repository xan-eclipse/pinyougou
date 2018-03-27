app.controller('brandController',function ($scope,brandService,$controller) {
    $controller('baseController',{$scope:$scope})//传入scope base和brand要公用一个
    $scope.findAll = function () {
        brandService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        )
    }


    $scope.findPage = function(pageNum,pageSize) {
        brandService.findPage(pageNum,pageSize).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        )
    }

    //保存
    $scope.save= function(){
        var serviceObject;
        if($scope.entity.id != null ){
            serviceObject = brandService.update($scope.entity);
        }else{
            serviceObject = brandService.add($scope.entity);
        }
        serviceObject.success(
            function (response) {
                if(response.success){
                    $scope.reloadList();
                }else{
                    alert(response.message)
                }
            }
        )
    }

    //根据id查询
    $scope.findOne = function(id){
        brandService.findOne(id).success(
            function (response){
                $scope.entity=response;
            }
        )
    }

    $scope.dele=function () {
        brandService.dele($scope.selectIds).success(
            function (response) {
                if(response.success){
                    $scope.reloadList();
                } else{
                    alert(response.message);
                }
            }
        )
    }

    $scope.searchEntity={};
    $scope.search=function (pageNum, pageSize) {
        brandService.search(pageNum,pageSize,$scope.searchEntity).success(
            function (response) {
                $scope.paginationConf.totalItems=response.total;//总记录数
                $scope.list=response.rows;//给列表变量赋值
            }
        )
    }
})