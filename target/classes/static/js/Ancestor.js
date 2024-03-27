var key = '';
$("#insert").click(function(){
    key = 'insert';
})

$("#age").keyup(function(){
    var inputValue = $(this).val(); // 获取输入框的值
    if (!/^[0-9]+$/.test(inputValue)) {
        alert("请输入数字");
    }
})

$("#submit").click(function(){
    var data = {
        name: $("#name").val(),
        title: $("#title").val(),
        age: $("#age").val(),
        parentId: $("#parentId").val(),
        remark: $("#remark").val(),
        startDate: $("#startDate").val(),
        endDate: $("#endDate").val()
    };

    if(key !== 'insert'){
        data.id = $("#id").val();
    }

    if (!/^[0-9]+$/.test(data.age)) {
        alert("请输入数字");
        return;
    }

    console.log("data = " + data);

    var url = '/ancestor';
    console.log("id = " + data.id);
    if(typeof data.id !== 'undefined'){
        url = url + '/update';
    }else {
        url = url + '/insert';
    }

    console.log("url = " + url);

    $.ajax({
        type: "POST",
        url: url,
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function(response) {
            window.location.reload(response);
        },
        error: function(xhr, status, error) {
            console.error("Error:", error);
        }
    });
})

$(".queryBfUpdate").click(function(){
    key = 'update';
    var id = $(this).prev('.id').val();
//    $("#id").val(id);
    console.log("id = " + id);
    $.ajax({
        type: "GET",
        url: "/ancestor/queryBfUpdate",
        data: {id: id},
        success: function(response) {
            $("#id").val(response.id);
            $("#name").val(response.name);
            $("#title").val(response.title);
            $("#age").val(response.age);
            $("#remark").val(response.remark);
            $("#startDate").val(response.startDate);
            $("#endDate").val(response.endDate);
        },
        error: function(xhr, status, error) {
            console.error("Error:", error);
        }
    });
})

$(".queryBfDelete").click(function(){
    key = 'delete';
    var id = $(this).prev('.id').val();
    $("#id").val(id);
    console.log("id = " + id);
})

$("#delete").click(function(){
    $.ajax({
        type: "POST",
        url: "/ancestor/delete",
        data: {id: $("#id").val()},
        success: function(response) {
            window.location.reload(response);
        },
        error: function(xhr, status, error) {
            console.error("Error:", error);
        }
    });
})