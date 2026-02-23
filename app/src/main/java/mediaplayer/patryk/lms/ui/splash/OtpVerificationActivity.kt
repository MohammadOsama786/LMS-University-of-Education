package mediaplayer.patryk.lms.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import mediaplayer.patryk.lms.databinding.ActivityOtpVerificationBinding

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpVerificationBinding
    private lateinit var otpBoxes: Array<EditText>

    private var receivedOtp: String? = null
    private var userEmail: String? = null
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userEmail = intent.getStringExtra("email")
        receivedOtp = intent.getStringExtra("otp")

        otpBoxes = arrayOf(
            binding.otp1,
            binding.otp2,
            binding.otp3,
            binding.otp4
        )

        setupOtpInputs()
        startResendTimer()

        binding.btnVerify.setOnClickListener {
            val enteredOtp = getEnteredOtp()
            if (enteredOtp.length < 4) {
                Toast.makeText(this, "Enter complete OTP", Toast.LENGTH_SHORT).show()
            } else if (enteredOtp == receivedOtp) {
                Toast.makeText(this, "OTP Verified!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, ChangePasswordActivity ::class.java)
                intent.putExtra("email", userEmail)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvResend.setOnClickListener {
            if (binding.tvTimer.text == "0:00") {
                receivedOtp = (1000..9999).random().toString()
                startResendTimer()
                Toast.makeText(this,
                    "New OTP sent to $userEmail\n(Dev: $receivedOtp)",
                    Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun setupOtpInputs() {
        for (i in otpBoxes.indices) {

            otpBoxes[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < otpBoxes.size - 1) {
                        otpBoxes[i + 1].requestFocus()
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            otpBoxes[i].setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL &&
                    event.action == KeyEvent.ACTION_DOWN &&
                    otpBoxes[i].text.isEmpty() &&
                    i > 0
                ) {
                    otpBoxes[i - 1].requestFocus()
                }
                false
            }
        }
    }

    private fun getEnteredOtp(): String {
        val sb = StringBuilder()
        for (box in otpBoxes) sb.append(box.text.toString())
        return sb.toString()
    }

    private fun startResendTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val sec = millisUntilFinished / 1000
                binding.tvTimer.text = "0:${if (sec < 10) "0$sec" else sec}"
            }

            override fun onFinish() {
                binding.tvTimer.text = "0:00"
            }
        }.start()
    }

    private fun generateOtp(): String {
        return (1000..9999).random().toString()
    }

}
