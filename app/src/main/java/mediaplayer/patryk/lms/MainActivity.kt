package mediaplayer.patryk.lms

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import mediaplayer.patryk.lms.adminDashboard.AdminMainActivity
import mediaplayer.patryk.lms.studentDashboard.StudentMainActivity
import mediaplayer.patryk.lms.teacherDashboard.TeacherMainActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val role = intent.getStringExtra("role")

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        when (role) {
            "admin" -> startActivity(Intent(this, AdminMainActivity::class.java))
            "teacher" -> startActivity(Intent(this, TeacherMainActivity::class.java))
            "student" -> startActivity(Intent(this, StudentMainActivity::class.java))
        }
        finish()
    }
}
