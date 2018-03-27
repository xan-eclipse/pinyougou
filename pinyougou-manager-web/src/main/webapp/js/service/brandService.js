app.service('brandService',function ($http) {
    //查询所有
    this.findAll=function () {
        return $http.get('../brand/findAll.do');
    }

    //分页
    this.findPage = function(pageNum,pageSize) {
        return $http.get('../brand/findPage.do?pageNum='+pageNum+'&pageSize='+pageSize);
    }

    //添加
    this.add= function(entity){
        return $http.post('../brand/add.do',entity)
    }

    //修改
    this.update=function (entity) {
        return $http.post('../brand/update.do',entity)
    }

    //根据id查询
    this.findOne = function(id){
        return $http.get('../brand/findOne.do?id='+id)
    }

    //删除
    this.dele=function (selectIds) {
        return $http.get('../brand/delete.do?ids='+selectIds)
    }

    this.search=function (page, rows,searchEntity) {
        return $http.post('../brand/search.do?page=' + page + '&rows=' + rows,searchEntity)
    }
})