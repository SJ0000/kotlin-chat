package sj.messenger.global.dto

import sj.messenger.global.exception.ErrorCode


data class ErrorResponse(
    val code : Int,
    val message: String,
    val fieldErrors : MutableMap<String, String> = mutableMapOf()
){
    companion object{
        @JvmStatic
        fun of(errorCode: ErrorCode) : ErrorResponse{
            return ErrorResponse(errorCode.code, errorCode.message);
        }
    }

    fun addError(fieldName: String , error: String ){
        fieldErrors[fieldName] = error;
    }
}