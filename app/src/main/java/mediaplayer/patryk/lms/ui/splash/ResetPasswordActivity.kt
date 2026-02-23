package mediaplayer.patryk.lms.ui.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import mediaplayer.patryk.lms.databinding.ActivityResetPasswordBinding
import kotlin.random.Random

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private var userEmail: String? = null
    private var generatedOtp: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back button
        binding.btnBack.setOnClickListener { finish() }

        // Send OTP button
        binding.btnSendCode.setOnClickListener {
            val email = binding.etRecoveryId.text.toString().trim()
            if (validateEmail(email)) {
                userEmail = email
                generatedOtp = generateOtp()
                sendRecoveryCode(email, generatedOtp!!)
                // Navigate to OTP screen
                val intent = Intent(this, OtpVerificationActivity::class.java)
                intent.putExtra("email", email)
                intent.putExtra("otp", generatedOtp) // for testing, normally verify on server
                startActivity(intent)
            }
        }

        // Resend code button
        binding.btnResend.setOnClickListener {
            if (TextUtils.isEmpty(userEmail)) {
                Toast.makeText(this, "Please enter your email first", Toast.LENGTH_SHORT).show()
            } else {
                generatedOtp = generateOtp()
                sendRecoveryCode(userEmail!!, generatedOtp!!)
                // Optionally, update OTP screen if needed
            }
        }

        // Contact support button
        binding.btnContactSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@unilearn.com") // Support email
                putExtra(Intent.EXTRA_SUBJECT, "Password Reset Assistance")
                putExtra(Intent.EXTRA_TEXT, "Hello Support,\nI need help with resetting my password.")
            }
            startActivity(Intent.createChooser(intent, "Contact Support"))
        }
    }

    private fun validateEmail(email: String): Boolean {
        return if (TextUtils.isEmpty(email)) {
            binding.etRecoveryId.error = "Email is required"
            false
        } else {
            true
        }
    }

    private fun generateOtp(): String {
        val otp = Random.nextInt(100000, 999999).toString() // 6-digit OTP
        Toast.makeText(this, "Generated OTP (for testing): $otp", Toast.LENGTH_SHORT).show()
        return otp
    }

    private fun sendRecoveryCode(email: String, otp: String) {
        // TODO: Replace with actual email sending logic (API, Firebase, etc.)
        // TODO: OTP is passed to the next activity only for testing;
        //  in a real app, verification should be done on your backend/Firebase.
        Toast.makeText(this, "OTP sent to $email", Toast.LENGTH_SHORT).show()
    }
}
