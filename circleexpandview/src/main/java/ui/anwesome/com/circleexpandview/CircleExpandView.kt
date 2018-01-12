package ui.anwesome.com.circleexpandview

/**
 * Created by anweshmishra on 12/01/18.
 */
import android.view.*
import android.content.*
import android.graphics.*
class CircleExpandView(ctx:Context):View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas:Canvas) {

    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        return true
    }
}