/**
 * 将json对象格式化为可读性字符串
 * */
function formatJsonToStr(jsonObj) {
    return JSON.stringify(jsonObj, null, 4);
}

//校验模板是否合法 (格式上校验)
function checkTemplate(template){
    let res = null;
    let dataType = typeof template;
    if(dataType == "string"){
        //字符串类型
        try{
            res = JSON.parse(template);
        }catch (e) {
            console.log("字符串转换JSON异常，格式有误!")
        }
    }else{
        //object类型
        res = template;
    }
    //这里预留校验数据内容是否合法
    return res;
}



export {formatJsonToStr, checkTemplate}
