import md5 from 'js-md5'
/**
 *
 * @param md5加密
 */
function encryptWebPassword(password) {
    let salt = "1a2b3c"
    return md5(salt.charAt(0) + password + salt.charAt(1) + salt.charAt(salt.length-1)) //后端盐长度取6  取出3粒盐
}

export {
    encryptWebPassword
}
