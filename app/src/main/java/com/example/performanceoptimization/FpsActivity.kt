package com.example.performanceoptimization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Choreographer
import kotlinx.android.synthetic.main.activity_fps.*
import kotlin.math.pow

class FpsActivity : AppCompatActivity() {

    companion object{
        //一般情况下手机硬件的屏幕刷新频率为60HZ
        private const val FRAME_COUNT_PER_SECOND = 60
        private val FRAME_NANO_TIME = 10.0.pow(9.0)/ FRAME_COUNT_PER_SECOND
        private val MAX_SKIP_TIME = FRAME_NANO_TIME*3
    }

    private val mHandler by lazy {
        Handler{
            //主线程做休眠处理，模拟卡顿情况
            Thread.sleep(80)
            sendEmptyMessageDelayed()
            return@Handler true
        }
    }

    private var mLastFrameNanos = System.nanoTime()

    private val mFrameCallback by lazy {
        Choreographer.FrameCallback {curFrameNanos->
            if(curFrameNanos != mLastFrameNanos){
                val diff = curFrameNanos - mLastFrameNanos
                if(diff> MAX_SKIP_TIME){
                    if(diff>0){
                        val skipFrame = (diff/ FRAME_NANO_TIME -1)
                        Log.e("gyq","diff = $diff skipFrame = $skipFrame" )
                        //显示当前的帧率
                        tv_fps.text = "当前FPS：${FRAME_COUNT_PER_SECOND-skipFrame}"
                    }
                }
            }
            mLastFrameNanos = curFrameNanos
            //因为一次Choreographer.getInstance().postFrameCallback()只会订阅一次Vsync信号
            //所以如果要继续监听下一帧到来的时间，则应该继续订阅Vsync信号
            startListenerFrame()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fps)
        startListenerFrame()
        sendEmptyMessageDelayed()
    }

    private fun sendEmptyMessageDelayed(){
        //主线程做休眠处理，模拟卡顿情况
        mHandler.sendEmptyMessageDelayed(11,500)
    }

    private fun startListenerFrame(){
        //会订阅监听 Vsync 信号
        Choreographer.getInstance().postFrameCallback(mFrameCallback)
    }

}