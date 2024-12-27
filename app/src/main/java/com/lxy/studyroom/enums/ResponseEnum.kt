package com.lxy.studyroom.enums

enum class ResponseEnum(val code: Int,val msg: String) {

    SUCCESS(0,"调用成功"),
    FAIL(-1,"调用失败"),
    REQUEST_PARAM_MISS(400,"请求参数错误！"),
    METHOD_NOT_SUPPORTED(405,"请求方法不支持！"),
    SERVER_ERROR(500,"无法与服务器建立安全连接！"),
    NEED_LOGIN(1000,"账号未登录，请进行登录操作！"),
    DATA_ERROR(1077,"数据异常");

    companion object {
        @JvmStatic
        fun getMsgByCode(code: Int) = when (code){
            FAIL.code -> FAIL.msg
            REQUEST_PARAM_MISS.code -> REQUEST_PARAM_MISS.msg
            METHOD_NOT_SUPPORTED.code -> METHOD_NOT_SUPPORTED.msg
            SERVER_ERROR.code -> SERVER_ERROR.msg
            NEED_LOGIN.code -> NEED_LOGIN.msg
            else -> DATA_ERROR.msg
        }
    }


}