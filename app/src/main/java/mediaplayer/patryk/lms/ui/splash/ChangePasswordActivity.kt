package mediaplayer.patryk.lms.ui.splash

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mediaplayer.patryk.lms.data.AppDatabase
import mediaplayer.patryk.lms.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private var email: String? = null
    private lateinit var db: AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        email = intent.getStringExtra("email")

        // Back
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnChangePassword.setOnClickListener {

            val newPassword = binding.etNewPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            when {
                newPassword.isEmpty() -> {
                    binding.etNewPassword.error = "Enter new password"
                }
                newPassword.length < 6 -> {
                    binding.etNewPassword.error = "Password must be at least 6 characters"
                }
                confirmPassword.isEmpty() -> {
                    binding.etConfirmPassword.error = "Confirm your password"
                }
                newPassword != confirmPassword -> {
                    binding.etConfirmPassword.error = "Passwords do not match"
                }
                else -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        db.userDao().updatePassword(email!!, newPassword)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@ChangePasswordActivity,
                                "Password updated successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    }
                }

            }
        }
    }
}
