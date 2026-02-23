package mediaplayer.patryk.lms.ui.splash

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import mediaplayer.patryk.lms.MainActivity
import mediaplayer.patryk.lms.R
import mediaplayer.patryk.lms.customView.BlurCircleView
import kotlin.jvm.java

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val blurCircleTop = findViewById<BlurCircleView>(R.id.blurCircleTopRight)
        val blurCircleBottom = findViewById<BlurCircleView>(R.id.blurCircleBottomLeft)
        val logo = findViewById<ImageView>(R.id.logoIcon)

        // Blur circles & Set colors and blur radius
        blurCircleTop.setCircleColor(ContextCompat.getColor(this, R.color.primary))
        blurCircleTop.setBlurRadius(120f)

        blurCircleBottom.setCircleColor(0x2200FF00.toInt())
        blurCircleBottom.setBlurRadius(120f)

        animateBlurCircles(blurCircleTop, blurCircleBottom)
        animateLogo(logo)
        simulateLoading(progressBar)



    }

    private fun animateBlurCircles(top: BlurCircleView, bottom: BlurCircleView) {
        // Top-right circle floats up and down diagonally
        val topAnimator = ObjectAnimator.ofPropertyValuesHolder(
            top,
            PropertyValuesHolder.ofFloat("translationX", top.translationX, top.translationX + 20f, top.translationX),
            PropertyValuesHolder.ofFloat("translationY", top.translationY, top.translationY + 20f, top.translationY)
        )
        topAnimator.duration = 4000
        topAnimator.repeatCount = ValueAnimator.INFINITE
        topAnimator.repeatMode = ValueAnimator.REVERSE
        topAnimator.interpolator = LinearInterpolator()
        topAnimator.start()

        // Bottom-left circle floats up and down diagonally
        val bottomAnimator = ObjectAnimator.ofPropertyValuesHolder(
            bottom,
            PropertyValuesHolder.ofFloat("translationX", bottom.translationX, bottom.translationX - 20f, bottom.translationX),
            PropertyValuesHolder.ofFloat("translationY", bottom.translationY, bottom.translationY - 20f, bottom.translationY)
        )
        bottomAnimator.duration = 4000
        bottomAnimator.repeatCount = ValueAnimator.INFINITE
        bottomAnimator.repeatMode = ValueAnimator.REVERSE
        bottomAnimator.interpolator = LinearInterpolator()
        bottomAnimator.start()
    }

    private fun simulateLoading(progressBar: ProgressBar) {
        val handler = Handler(Looper.getMainLooper())
        var progress = 0

        val runnable = object : Runnable {
            override fun run() {
                progress += 1
                progressBar.progress = progress

                if (progress < 100) {
                    handler.postDelayed(this, 30)
                } else {
                    // Navigate to Main Activity
                    startActivity(Intent(this@SplashActivity, OnboardingActivity::class.java))
                    finish()
                }
            }
        }

        handler.post(runnable)
    }

    private fun animateLogo(logo: ImageView) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.08f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.08f, 1f)

        val animator = ObjectAnimator.ofPropertyValuesHolder(logo, scaleX, scaleY)
        animator.duration = 1200
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

}
