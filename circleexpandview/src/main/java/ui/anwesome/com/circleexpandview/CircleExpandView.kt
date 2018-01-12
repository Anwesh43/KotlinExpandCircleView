package ui.anwesome.com.circleexpandview

/**
 * Created by anweshmishra on 12/01/18.
 */
import android.app.Activity
import android.view.*
import android.content.*
import android.graphics.*
import java.util.concurrent.ConcurrentLinkedQueue

class CircleExpandView(ctx:Context,var n:Int = 4):View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val renderer = CircleExpandRenderer(this)
    override fun onDraw(canvas:Canvas) {
        renderer.render(canvas,paint)
    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
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
            if(dir == 0f) {
                dir = 1 - 2 * scale
            }
        }
    }
    data class CircleExpandContainer(var n:Int,var w:Float,var h:Float) {
        val circleExpands:ConcurrentLinkedQueue<CircleExpand> = ConcurrentLinkedQueue()
        val state = ContainerState(n)
        init {
            if(n > 0) {
                var r = w/(2*n+1)
                for (i in 0..n - 1) {
                    circleExpands.add(CircleExpand(w/2,h/2,r*i,r*(i+1)))
                }
            }
        }
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
    data class CircleExpandRenderer(var view:CircleExpandView,var time:Int = 0){
        var container:CircleExpandContainer?=null
        val animator = Animator(view)
        fun render(canvas:Canvas,paint:Paint) {
            if(time == 0) {
                val w = canvas.width.toFloat()
                val h = canvas.height.toFloat()
                container = CircleExpandContainer(view.n,w,h)
            }
            canvas.drawColor(Color.parseColor("#212121"))
            paint.color = Color.parseColor("#FF9800")
            container?.draw(canvas,paint)
            animator.animate {
                container?.update {scale,j ->
                    animator.stop()
                }
            }
            time++
        }
        fun handleTap() {
            container?.startUpdating {
                animator.start()
            }
        }
    }
    data class Animator(var view:CircleExpandView,var animated:Boolean = false) {
        fun animate(updatecb:()->Unit) {
            if(animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch(ex:Exception) {

                }
            }
        }
        fun start() {
            if(!animated) {
                animated = true
                view.postInvalidate()
            }
        }
        fun stop() {
            if(animated) {
                animated = false
            }
         }
    }
    companion object {
        fun create(activity:Activity):CircleExpandView {
            val view = CircleExpandView(activity)
            activity.setContentView(view)
            return view
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