package sj.messenger.global.dto


data class ErrorResponse(
    val code : Int,
    val message: String,
    val fieldErrors : MutableMap<String, String> = mutableMapOf()
){
    fun addError(fieldName: String , error: String ){
        fieldErrors[fieldName] = error;
    }
}