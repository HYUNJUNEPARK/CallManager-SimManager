package com.ex.app.ui

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telecom.Call
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ex.app.R
import com.ex.app.databinding.ActivityCallBinding
import com.module.callmanager.ui.CallViewModel
import com.module.callmanager.ui.CallViewModel.Companion.uiCallState
import com.module.callmanager.util.CallManagerConst.CALL_OUTGOING
import com.module.callmanager.util.CallManagerConst.INTENT_KEY_CALL_STATE

class CallActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCallBinding
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call)
        binding.callViewModel = CallViewModel(application)
        binding.activity = this
        handler = Handler() //TODO Instead, use an java.util.concurrent.Executor

        updateUi()
        callStateObserver()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (CallViewModel.call?.state == Call.STATE_RINGING
                || CallViewModel.call?.state == Call.STATE_RINGING
                ||  CallViewModel.call?.state ==Call.STATE_ACTIVE
            ) {
                CallViewModel.call?.disconnect()
            }
        } else {

        }
    }

    private fun updateUi() {
        val intentValue = intent.getStringExtra(INTENT_KEY_CALL_STATE)
        if (intentValue == CALL_OUTGOING) {
            binding.answerButton.visibility = View.GONE
        }
    }

    private fun callStateObserver() {
        uiCallState.observe(this) { callState ->
            when(callState) {
                Call.STATE_DIALING -> {
                    binding.callState = "전화 거는 중..."
                }
                //전화 올 때
                Call.STATE_RINGING -> {
                    binding.callState = "수신전화"

                }
                Call.STATE_HOLDING -> {
                    binding.callState = "STATE_HOLDING"
                }
                Call.STATE_ACTIVE -> {
                    binding.callState = "통화 중"

                }
                Call.STATE_DISCONNECTED -> {
                    binding.callState = "통화 종료"
                    //TODO 더 깔끔한 표현 방식 필요
                    val task = Runnable { finish() }
                    handler.postDelayed(
                        task,
                        2000
                    )
                }
                Call.STATE_SELECT_PHONE_ACCOUNT -> {
                    binding.callState = "STATE_SELECT_PHONE_ACCOUNT"
                }
                Call.STATE_CONNECTING -> {
                    binding.callState = "STATE_CONNECTING"
                }
                Call.STATE_DISCONNECTING -> {
                    binding.callState = "STATE_DISCONNECTING"
                }
                else -> {
                    binding.callState = "Exception"
                }
            }
        }
    }
}