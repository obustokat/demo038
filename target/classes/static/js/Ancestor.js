var key = '';
$(".queryBfInsert").click(function(){
    key = 'insert';
    $("#id").val();
    $("#name").val();
    $("#title").val();
    $("#age").val();
    $("#remark").val();
    $("#startDate").val();
    $("#endDate").val();
})

$(".queryBfUpdate").click(function(){
    key = 'update';
    var id = $(this).prev('.id').val();
    console.log("queryBfDelete = " + id);
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
    var id = $(this).closest('td').find('.id').val();
    $("#id").val(id);
    console.log("queryBfDelete = " + id);
    $.ajax({
        type: "GET",
        url: "/ancestor/queryBfDelete",
        data: {id: id},
        success: function(response) {
            console.log(response);
            $("#hasChild").val(response);
        },
        error: function(xhr, status, error) {
            console.error("Error:", error);
        }
    });
//    console.log("id = " + id);
})

$(".queryBfMove").click(function(){
    key = 'move';
    var id = $(this).closest('td').find('.id').val();
    $("#id").val(id);
    console.log("queryBfMove = " + id);
    $.ajax({
        type: "GET",
        url: "/ancestor/queryBfMove",
        data: {id: id},
        success: function(response) {
            console.log(response);
            var selectElement = document.getElementById("moveSelect");
            selectElement.length = 0;
            var optionDefault = document.createElement("option");
            optionDefault.textContent = '請選擇移动至哪个成员底下';
            selectElement.appendChild(optionDefault);

            response.forEach(function(item) {
                var option = document.createElement("option");
                option.value = item.id;
                option.textContent = item.name;
                selectElement.appendChild(option);
            });

        },
        error: function(xhr, status, error) {
            console.error("Error:", error);
        }
    });
//    console.log("id = " + id);
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
    var url = '/ancestor';
//    console.log("id = " + data.id);

    if (typeof data.parentId !== 'undefined' && data.parentId !== '') {
        console.log("parentId=" + data.parentId);
    } else {
        data.parentId = 0;
    }

    if(typeof data.id !== 'undefined'){
        url = url + '/update';
    }else {
        url = url + '/insert';
    }

//    console.log("url = " + url);

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

$("#moveSelect").change(function(){
    var selectElement = document.getElementById("moveSelect");
    var selectedValue = selectElement.value;
    $("#moveId").val(selectedValue)
//    console.log("所选择的值为: " + selectedValue);
})

$("#move").click(function(){
    console.log("id = " + $("#id").val()  + " , moveId = " + $("#moveId").val())
    $.ajax({
        type: "GET",
        url: "/ancestor/move",
        data: {id: $("#id").val() ,moveId: $("#moveId").val()},
        success: function(response) {
            if(response === 'true'){
                alert("无法移到自己底下");
            }else{
                window.location.reload(response);
            }
        },
        error: function(xhr, status, error) {
            console.error("Error:", error);
        }
    });
})

$("#delete").click(function(){
    if($("#hasChild").val()){
        alert("无法删除，存在下层关系")
    }else {
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
    }
})