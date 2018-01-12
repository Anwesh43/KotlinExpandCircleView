package ui.anwesome.com.kotlincircleexpandview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.circleexpandview.CircleExpandView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CircleExpandView.create(this)
    }
}
