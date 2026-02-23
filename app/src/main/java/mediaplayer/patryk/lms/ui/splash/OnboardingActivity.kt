package mediaplayer.patryk.lms.ui.splash

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.ViewFlipper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import mediaplayer.patryk.lms.R

class OnboardingActivity : AppCompatActivity() {

    private lateinit var flipper: ViewFlipper
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var dots: Array<View>
    private var page = 0
    private val PREFS = "onboarding_prefs"
    private val KEY_DONE = "done"
    private lateinit var btnBack: ImageView
    private lateinit var btnSkip: View
    private lateinit var btnNext: com.google.android.material.button.MaterialButton
    private lateinit var tvTitle: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        if (prefs.getBoolean(KEY_DONE, false)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // INIT
        flipper = findViewById(R.id.viewFlipper)

        btnBack = findViewById(R.id.btnBack)
        btnSkip = findViewById(R.id.btnSkip)
        btnNext = findViewById(R.id.btnNext)
        tvTitle = findViewById(R.id.tvTitle)


        flipper.inAnimation = AlphaAnimation(0f, 1f).apply { duration = 300 }
        flipper.outAnimation = AlphaAnimation(1f, 0f).apply { duration = 300 }

        dots = arrayOf(
            findViewById(R.id.dot1),
            findViewById(R.id.dot2),
            findViewById(R.id.dot3)
        )

        gestureDetector = GestureDetectorCompat(this, SwipeListener())

        flipper.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        btnNext.setOnClickListener {
        if (page == 2) finishOnboarding()
            else nextPage()
        }

        btnSkip.setOnClickListener {
        finishOnboarding()
        }

        btnBack.setOnClickListener {
            if (page > 0) previousPage()
        }


        animateDots()
    }

    private fun finishOnboarding() {
        val image = flipper.getChildAt(page).findViewById<ImageView>(R.id.heroImage)

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this, image, "heroImage"
        )

        getSharedPreferences(PREFS, MODE_PRIVATE)
            .edit().putBoolean(KEY_DONE, true).apply()

        startActivity(Intent(this, LoginActivity::class.java), options.toBundle())
        finish()
    }

    private fun nextPage() {
        val img = flipper.getChildAt(page).findViewById<ImageView>(R.id.heroImage)
        applyParallax(img, 1)

        if (page < flipper.childCount - 1) {
            page++
            flipper.showNext()
            animateDots()
            updateTopBar()   // ADD THIS
        }
    }

    private fun previousPage() {
        val img = flipper.getChildAt(page).findViewById<ImageView>(R.id.heroImage)
        applyParallax(img, -1)

        if (page > 0) {
            page--
            flipper.showPrevious()
            animateDots()
            updateTopBar()   // ADD THIS
        }
    }

    private fun applyParallax(image: ImageView, direction: Int) {
        image.animate()
            .translationX(if (direction > 0) -40f else 40f)
            .setDuration(250)
            .withEndAction {
                image.animate().translationX(0f).setDuration(250).start()
            }.start()
    }

    private fun animateDots() {
        for (i in dots.indices) {
            val isActive = i == page
            val width = if (isActive) 60 else 20

            val anim = ValueAnimator.ofInt(dots[i].width, width)
            anim.addUpdateListener {
                val params = dots[i].layoutParams
                params.width = it.animatedValue as Int
                dots[i].layoutParams = params
            }
            anim.duration = 250
            anim.start()

            dots[i].setBackgroundResource(
                if (isActive) R.drawable.dot_active else R.drawable.dot_inactive
            )
        }
    }


    private fun updateTopBar() {

        when (page) {

            0 -> { // PAGE 1
                btnBack.setImageResource(R.drawable.ic_logo_text)
                btnBack.visibility = View.VISIBLE
                btnSkip.visibility = View.VISIBLE
                tvTitle.visibility = View.GONE

                btnNext.text = "Next"
                btnNext.setIconResource(R.drawable.ic_arrow_forward)
            }

            1 -> { // PAGE 2
                btnBack.visibility = View.GONE
                btnSkip.visibility = View.VISIBLE
                tvTitle.visibility = View.GONE

                btnNext.text = "Next"
                btnNext.setIconResource(R.drawable.ic_arrow_forward)
            }

            2 -> { // PAGE 3
                btnBack.visibility = View.VISIBLE
                btnBack.setImageResource(R.drawable.ic_arrow_back)

                btnSkip.visibility = View.GONE
                tvTitle.visibility = View.VISIBLE

                btnNext.text = "Get Started"
                btnNext.icon = null
            }
        }
    }


    inner class SwipeListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY = 100

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val diffX = e2.x - (e1?.x ?: 0f)

            if (kotlin.math.abs(diffX) > SWIPE_THRESHOLD &&
                kotlin.math.abs(velocityX) > SWIPE_VELOCITY
            ) {
                if (diffX > 0) previousPage() else nextPage()
                return true
            }
            return false
        }
    }
}
