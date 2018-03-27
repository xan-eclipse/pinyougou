app.controller('baseController',function ($scope) {
    $scope.reloadList=function(){
        //切换页码
        $scope.findPage( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
        //$scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }

//分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    }

//$event是ng自带的把点击的东西放进去
    $scope.selectIds=[];
    $scope.updateSelection = function($event,id){
        if($event.target.checked){
            $scope.selectIds.push(id);
        }else{
            var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx,1);
        }
    }

})

