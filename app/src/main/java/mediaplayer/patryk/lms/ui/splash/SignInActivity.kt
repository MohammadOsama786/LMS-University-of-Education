package mediaplayer.patryk.lms.ui.splash

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mediaplayer.patryk.lms.R
import mediaplayer.patryk.lms.data.AppDatabase
import mediaplayer.patryk.lms.data.User
import mediaplayer.patryk.lms.Database.UserDao
import mediaplayer.patryk.lms.databinding.ActivitySignInBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var userDao: UserDao

    private var selectedRole: String = "Student"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Room DAO
        userDao = AppDatabase.getDatabase(this).userDao()

        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Role selection
        binding.btnStudent.setOnClickListener { selectRole("Student") }
        binding.btnTeacher.setOnClickListener { selectRole("Teacher") }

        // Sign Up button
        binding.btnSignUp.setOnClickListener {
            if (validateInput()) {
                signUpUser()
            }
        }

        // Footer login
        binding.tvLoginFooter.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun selectRole(role: String) {
        selectedRole = role
        if (role == "Student") {
            binding.btnStudent.setBackgroundResource(R.drawable.bg_role_selected)
//            binding.btnStudent.setTextColor(resources.getColor(R.color.primary))
            binding.btnTeacher.setBackgroundResource(R.drawable.bg_role_unselected)
//            binding.btnTeacher.setTextColor(resources.getColor(R.color.text_secondary))
        } else {
            binding.btnTeacher.setBackgroundResource(R.drawable.bg_role_selected)
//            binding.btnTeacher.setTextColor(resources.getColor(R.color.primary))
            binding.btnStudent.setBackgroundResource(R.drawable.bg_role_unselected)
//            binding.btnStudent.setTextColor(resources.getColor(R.color.text_secondary))
        }
    }

    private fun validateInput(): Boolean {
        val fullName = binding.textInputFullName.text.toString().trim()
        val email = binding.textInputEmail.text.toString().trim()
        val password = binding.textInputPassword.text.toString().trim()
        val confirmPassword = binding.textInputConfirmPassword.text.toString().trim()

        if (TextUtils.isEmpty(fullName)) {
            binding.textInputFullName.error = "Full Name is required"
            return false
        }

        if (TextUtils.isEmpty(email)) {
            binding.textInputEmail.error = "Email is required"
            return false
        }

        if (TextUtils.isEmpty(password)) {
            binding.textInputPassword.error = "Password is required"
            return false
        }

        if (password.length < 6) {
            binding.textInputPassword.error = "Password must be at least 6 characters"
            return false
        }

        if (password != confirmPassword) {
            binding.textInputConfirmPassword.error = "Passwords do not match"
            return false
        }

        return true
    }

    private fun signUpUser() {
        val fullName = binding.textInputFullName.text.toString().trim()
        val email = binding.textInputEmail.text.toString().trim()
        val password = binding.textInputPassword.text.toString().trim()

        // Run Room operations in background
        lifecycleScope.launch(Dispatchers.IO) {
            // Check if user already exists
            val existingUser = userDao.login(email, password, selectedRole)
            if (existingUser != null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SignUpActivity,
                        "User already exists with this email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                // Insert new user
                val user = User(
                    fullName = fullName,
                    email = email,
                    password = password,
                    role = selectedRole
                )
                userDao.insertUser(user)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Sign Up successful! Welcome $selectedRole",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Navigate to dashboard
                    if (selectedRole == "Student") {
//                        startActivity(Intent(this@SignUpActivity, StudentDashboardActivity::class.java))
                        Toast.makeText(
                            this@SignUpActivity,
                            "Sign Up successful! Welcome $selectedRole",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Sign Up successful! Welcome $selectedRole",
                            Toast.LENGTH_SHORT
                        ).show()
//                        startActivity(Intent(this@SignUpActivity, TeacherDashboardActivity::class.java))
                    }
                    finish()
                }
            }
        }
    }
}
