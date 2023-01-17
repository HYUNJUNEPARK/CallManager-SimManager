package com.module.callex

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.module.callex.data.model.log.LogType
import com.module.callex.databinding.ActivityMainBinding
import com.module.callex.ui.CallLogViewModel
import com.module.callex.ui.CallViewModel.Companion.uiCallState
import com.module.callex.ui.ContactViewModel
import com.module.callex.util.CallUtil
import com.module.callex.util.Permission

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var permission: Permission
    private lateinit var callUtil: CallUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permission = Permission(this)
        callUtil = CallUtil(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.callLogViewModel = CallLogViewModel(application)
        binding.contactViewModel = ContactViewModel(application)
        binding.callUtil = callUtil
        binding.main = this
        binding.logType = LogType.OUTGOING //테스트 파라미터

        permission.checkPermissions()

        //TODO check owner
        uiCallState.observe(this) { callState ->
            Log.d("testLog", "$callState")

            if (callState == Call.STATE_DIALING) {
                val intent = Intent(this, CallActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.all { it ==  PackageManager.PERMISSION_GRANTED}) {
            permission.permissionGranted()
        } else {
            permission.permissionDenied()
        }
    }

    fun changeDefaultDialer() {
        val intent = callUtil.createDefaultDialerIntent()
        changeDefaultDialerResultLauncher.launch(intent)
    }

    //기본 전화 앱으로 변경 되었는지 결과를 확인하기 위한 ActivityResultLauncher
    private val changeDefaultDialerResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (callUtil.isDefaultDialer) {
                //기본 앱으로 설정되었을 때
            } else {
                //기본 앱으로 설정되지 않았을 때
            }
        }
}