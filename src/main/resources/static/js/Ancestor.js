var key = '';
$(".queryBfInsert").click(function(){
    key = 'insert';
//    alert("key="+key);
    $("#id").val('');
    $("#name").val('');
    $("#title").val('');
    $("#age").val('');
    $("input[name='custom-radio-3'][value='0']").prop('checked', true);
    $(".spouseNameDiv").hide();
    $("#spouseName").val('');
    $("#remark").val('');
    $("#startDate").val('');
    $("#endDate").val('');
})


$(".queryBfUpdate").click(function(){
    key = 'update';
//    alert("key="+key);
    var id = $(this).prev('.id').val();
    console.log("queryBfDelete = " + id);
    $.ajax({
        type: "GET",
        url: "/ancestor/queryBfUpdate",
        data: {id: id},
        success: function(response) {
        console.log("response.marry = " + response.marry);
        console.log("response.spouseName = " + response.spouseName);
            $("#id").val(response.id);
            $("#name").val(response.name);
            $("#title").val(response.title);
            $("#age").val(response.age);
            $("input[name='custom-radio-3'][value='" + response.marry + "']").prop('checked', true);
            isMarry();
            $("#spouseName").val(response.spouseName);
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
        alert("年龄请输入数字");
    }
})

$("input[name='custom-radio-3']").change(isMarry);

// 分开检查
function toCheckData(){
    var inputValue = $("#name").val();
    if (inputValue == '') {
        alert("请输入姓名");
        return false;
    }
    var selectedValue = $("input[name='custom-radio-3']:checked").val();
//    var spouseName = $("#spouseName").val();
    if(selectedValue == 1){
//        alert("selectedValue = " + selectedValue + " spouseName = " + spouseName);
        if(typeof spouseName == 'undefined' || spouseName == ''){
            alert("请输入配偶姓名");
            return false;
        }
    }
    return true;
}

function toCheckSpouseName(spouseName){
    var selectedValue = $("input[name='custom-radio-3']:checked").val();
//    var spouseName = $("#spouseName").val();
    if(selectedValue == 1){
//        alert("selectedValue = " + selectedValue + " spouseName = " + spouseName);
        if(typeof spouseName == 'undefined' || spouseName == ''){
            alert("请输入配偶姓名");
            return ;
        }else {
            if (spouseName.includes(',')) {
                return spouseName.split(',');
            } else {
                return [spouseName];
            }
        }
    }else {
        spouseName = "";
    }
}

$("#startDate").change(function(){
    calculateAge();
})

$("#endDate").change(function(){
    calculateAge();
})


function calculateAge() {
    var age = 0;
    var sDate = $("#startDate").val();
    var startDate = new Date(sDate);
    var sBool = isNaN(startDate.getTime());

    var eDate = $("#endDate").val();
    var endDate = new Date(eDate);
    var eBool = isNaN(endDate.getTime());

    var today = new Date();

    if (!sBool && !eBool) {
        age = endDate.getFullYear() - startDate.getFullYear();
        if (endDate.getMonth() < startDate.getMonth() ||
            (endDate.getMonth() === startDate.getMonth() &&
                endDate.getDate() < startDate.getDate())) {
            age--;
        }
//        console.log("age1 = " + age);
    } else if (!sBool) {
        age = today.getFullYear() - startDate.getFullYear();
        if (today.getMonth() < startDate.getMonth() ||
            (today.getMonth() === startDate.getMonth() &&
                today.getDate() < startDate.getDate())) {
            age--;
        }
//        console.log("age2 = " + age);
    }

    $("#age").val(age == 0 ? '' : age);
}

function isMarry(){
    var selectedValue = $("input[name='custom-radio-3']:checked").val();
    selectedValue == 1 ? $(".spouseNameDiv").show() : $(".spouseNameDiv").hide();
}

$("#submit").click(function(){
    var spouseName = $("#spouseName").val();
    var array = toCheckSpouseName(spouseName);
    var selectedValue = $("input[name='custom-radio-3']:checked").val();
    var data = {
        name: $("#name").val(),
        title: $("#title").val(),
        age: $("#age").val(),
        marry: selectedValue,
        spouseName: array,
        parentId: $("#parentId").val(),
        remark: $("#remark").val(),
        startDate: $("#startDate").val(),
        endDate: $("#endDate").val()
    };

    if(key !== 'insert'){
        data.id = $("#id").val();
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

    console.log("url = " + url);
    var bool = toCheckData();
    console.log(bool);

    if(bool){
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
    }
})

$("#moveSelect").change(function(){
    var selectElement = document.getElementById("moveSelect");
    var selectedValue = selectElement.value;
    $("#moveId").val(selectedValue)
//    console.log("所选择的值为: " + selectedValue);
})

$("#move").click(function(){
//    console.log("id = " + $("#id").val()  + " , moveId = " + $("#moveId").val())
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
    if($("#hasChild").val() == 'false'){
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