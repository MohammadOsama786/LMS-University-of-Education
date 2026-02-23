package mediaplayer.patryk.lms.ui.splash

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import mediaplayer.patryk.lms.data.AppDatabase
import mediaplayer.patryk.lms.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mediaplayer.patryk.lms.adminDashboard.AdminMainActivity
import mediaplayer.patryk.lms.data.User
import mediaplayer.patryk.lms.studentDashboard.StudentMainActivity
import mediaplayer.patryk.lms.teacherDashboard.TeacherMainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: AppDatabase
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        // Edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListeners()
        seedTestUsers() // Optional: create sample users
        setupGoogleSignIn()
    }

    // -------------------------------
    // Setup Click Listeners
    // -------------------------------
    private fun setupListeners() {
        // Back button
        binding.btnBack.setOnClickListener { finish() }

        // Login button
        binding.btnLogin.setOnClickListener {
            val email = binding.textInputEditTextEmail.text.toString().trim()
            val password = binding.textInputEditTextPassword.text.toString().trim()
            val roleId = binding.roleSegment.checkedRadioButtonId
            val role = findViewById<RadioButton>(roleId)
                .text.toString().lowercase()

            // Validation
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isValidEmail(email)) {
                Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Authenticate with Room DB
            lifecycleScope.launch {
                val user = withContext(Dispatchers.IO) {
                    db.userDao().login(email, password, role)
                }

                if (user != null) {
                    Toast.makeText(this@LoginActivity, "Login successful as $role", Toast.LENGTH_SHORT).show()

                    val intent = when (role) {
                        "admin" -> Intent(this@LoginActivity, AdminMainActivity::class.java)
                        "teacher" -> Intent(this@LoginActivity, TeacherMainActivity::class.java)
                        else -> Intent(this@LoginActivity, StudentMainActivity::class.java)
                    }

                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Forgot password
        binding.textViewForgotPassword.setOnClickListener {

            val email = binding.textInputEditTextEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your email first", Toast.LENGTH_SHORT).show()
            } else {
                val otp = (1000..9999).random().toString() // Generate OTP manually
                val intent = Intent(this, OtpVerificationActivity::class.java)
                intent.putExtra("email", email)
                intent.putExtra("otp", otp)
                startActivity(intent)
            }

            Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show()
        }

        // Apple Sign-In (placeholder)
        binding.imageViewIOS.setOnClickListener {
            val email = "appleuser@domain.com" // Replace with actual from Apple SDK
            lifecycleScope.launch(Dispatchers.IO) {
                val existing = db.userDao().getUsersByRole("Student")
                if (existing.none { it.email == email }) {
                    db.userDao().insertUser(User(
                        email = email, password = "", role = "Student",
                        id = TODO(),
                        fullName = TODO()
                    ))
                }
            }
            Toast.makeText(this, "iOS login: $email", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@LoginActivity, StudentMainActivity::class.java)
            startActivity(intent)
            finish()

        }
    }


    private fun isValidEmail(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    // Optional: Seed some users
    private fun seedTestUsers() {
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = db.userDao()
            dao.insertUser(User(
                email = "student@uni.edu", password = "123456", role = "Student",
                id = TODO(),
                fullName = TODO()
            ))
            dao.insertUser(User(
                email = "teacher@uni.edu", password = "123456", role = "Teacher",
                id = TODO(),
                fullName = TODO()
            ))
            dao.insertUser(User(
                email = "admin@uni.edu", password = "123456", role = "Admin",
                id = TODO(),
                fullName = TODO()
            ))
        }
    }

    // -------------------------------
    // Setup Google Sign-In
    // -------------------------------
    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.imageViewGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    // -------------------------------
    // Handle Activity Result for Google
    // -------------------------------
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val email = account?.email

                if (email != null) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        // Add to Room DB if not exists
                        val existing = db.userDao().getUsersByRole("Student")
                        if (existing.none { it.email == email }) {
                            db.userDao().insertUser(User(
                                email = email, password = "", role = "Student",
                                id = TODO(),
                                fullName = TODO()
                            ))
                        }
                    }

                    Toast.makeText(this, "Google login: $email", Toast.LENGTH_SHORT).show()
                    // TODO: Navigate to dashboard
                }

            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
