package com.example.accountkithms

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.huawei.hms.push.HmsMessageService

class MyPushService : HmsMessageService() {
    val TAG = "PushDemoLog"
    override fun onNewToken(token: String?, bundle: Bundle?) {
        Log.i(TAG, "tokenXX:$token")
        // C:\Users\awx1288614\AndroidStudioProjects\accountkitproject\app\release
    }
}