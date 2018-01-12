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
        val state = ContainerState(n)
        fun draw(canvas:Canvas,paint:Paint) {
            circleExpands.forEach {
                it.draw(canvas,paint)
            }
            if(n > 0) {
                val gap = (4*w/5)/n
                state.executeFn{
                    val scale = circleExpands.at(it)?.state?.scale?:0f
                    canvas.drawLine(w/10,4*h/5,w/10+gap*it+gap*scale,4*h/5,paint)
                }
            }
        }
        fun update(stopcb:(Float,Int)->Unit) {
            state.executeFn { j ->
                circleExpands.at(j)?.update {
                    stopcb(it,j)
                    state.incrementCounter()
                }
            }
        }
        fun startUpdating(startcb:()->Unit) {
            state.executeFn {
                circleExpands.at(it)?.startUpdating(startcb)
            }
        }
    }
    data class ContainerState(var n:Int,var j:Int = 0,var dir:Int = 1) {
        fun incrementCounter() {
            j += dir
            if (j >= n || j == -1) {
                dir *= -1
                j += dir
            }
        }
        fun executeFn(cb:(Int)->Unit) {
            cb(j)
        }
    }
 }
fun ConcurrentLinkedQueue<CircleExpandView.CircleExpand>.at(j:Int):CircleExpandView.CircleExpand? {
    var i:Int  = 0
    forEach {
        if(i == j) {
            return it
        }
        i++
    }
    return null
}