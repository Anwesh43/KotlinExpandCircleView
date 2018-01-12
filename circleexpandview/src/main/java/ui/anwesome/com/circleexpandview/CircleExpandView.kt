package ui.anwesome.com.circleexpandview

/**
 * Created by anweshmishra on 12/01/18.
 */
import android.view.*
import android.content.*
import android.graphics.*
import java.util.concurrent.ConcurrentLinkedQueue

class CircleExpandView(ctx:Context):View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas:Canvas) {

    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        return true
    }
    data class CircleExpand(var x:Float,var y:Float,var r:Float,var or:Float) {
        val state = CircleExpandState()
        fun draw(canvas:Canvas,paint:Paint) {
            paint.style = Paint.Style.FILL
            paint.strokeWidth = 8f
            canvas.save()
            canvas.translate(x,y)
            canvas.drawCircle(0f,0f,or+(r-or),paint)
            canvas.restore()
        }
        fun update(stopcb:(Float)->Unit) {
            state.update(stopcb)
        }
        fun startUpdating(startcb:()->Unit) {
            state.startUpdating(startcb)
        }
    }
    data class CircleExpandState(var scale:Float = 0f,var dir:Float = 0f,var prevScale:Float = 0f) {
        fun update(stopcb:(Float)->Unit) {
            scale += 0.1f*dir
            if(Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                stopcb(scale)
            }
        }
        fun startUpdating(startcb:()->Unit) {
            dir = 1-2*scale
        }
    }
    data class CircleExpandContainer(var n:Int,var w:Float,var h:Float) {
        val circleExpands:ConcurrentLinkedQueue<CircleExpand> = ConcurrentLinkedQueue()
        fun draw(canvas:Canvas,paint:Paint) {
            circleExpands.forEach {
                it.draw(canvas,paint)
            }
        }
        fun update(stopcb:(Float,Int)->Unit) {

        }
        fun startUpdating(startcb:()->Unit) {

        }
    }
}