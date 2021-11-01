import md5 from 'js-md5';

/**
 * 获取当前时间
 * */
function getCurrentTime() {
    //获取当前时间并打印
    let yy = new Date().getFullYear();
    let mm = new Date().getMonth()+1;
    let dd = new Date().getDate();
    let hh = new Date().getHours();
    let mf = new Date().getMinutes()<10 ? '0'+new Date().getMinutes() : new Date().getMinutes();
    let ss = new Date().getSeconds()<10 ? '0'+new Date().getSeconds() : new Date().getSeconds();
    return yy+'-'+mm+'-'+dd+' '+hh+':'+mf+':'+ss;

}

/**
 * 百度翻译 中文 -> 英文
 * */
function transform(word) {
    let appid = '20210810000912778';
    let salt = '1435660288';
    let key = 'kyvsbJKaF9X5RNXGcgWe';
    let sign = md5(appid+word+salt+key);
    return {
        q: word,
        from: 'zh',
        to: 'en',
        appid: appid,
        salt: salt, //任意均可以
        sign: sign //appid+q+salt+密钥
    }
}

/**
 * 将json对象格式化为可读性字符串
 * */
function formatJsonToStr(jsonObj) {
    return JSON.stringify(jsonObj, null, 4);
}

/**
 * 将json对象格式化为初始格式
 * */
function formatJsonToStr1(jsonObj) {
    return JSON.stringify(jsonObj, null, 4);
}


export {getCurrentTime, transform, formatJsonToStr, formatJsonToStr1}