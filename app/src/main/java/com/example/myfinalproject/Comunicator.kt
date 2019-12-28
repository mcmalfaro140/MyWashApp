package com.example.myfinalproject

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class Communicator : ViewModel(){

    val provName = MutableLiveData<Any>()

    fun setMsgCommunicator(msg:String){
        provName.value = msg
    }

    fun passDataCom(editext_input: String) {}
}