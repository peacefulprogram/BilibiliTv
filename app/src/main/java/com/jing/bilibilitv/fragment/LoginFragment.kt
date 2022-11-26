package com.jing.bilibilitv.fragment

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.jing.bilibilitv.databinding.LoginLayoutBinding
import com.jing.bilibilitv.http.api.BilibiliApi
import com.jing.bilibilitv.http.api.PassportApi
import com.jing.bilibilitv.http.data.QrCodeStatus
import com.jing.bilibilitv.model.LoginUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    @Inject
    lateinit var passportApi: PassportApi

     val userViewModel: LoginUserViewModel by activityViewModels()

    private var fetchStatusJob: Job? = null

    private var qrCodeStatusChannel = Channel<QrCodeStatus> { }

    private var qrCodeStatus = QrCodeStatus.NOT_SCAN

    private lateinit var viewBinding: LoginLayoutBinding

    private val qrCodeClickListener = OnClickListener {
        viewBinding.qrCodeContainer.setOnClickListener(null)
        refreshQrCodeKey()
        viewBinding.qrCodeStatus.visibility = View.INVISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        refreshQrCodeKey()
    }

    fun showQrCodeStatusText(textContent: String) {
        viewBinding.qrCodeStatus.apply {
            visibility = View.VISIBLE
            text = textContent
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = LoginLayoutBinding.inflate(inflater, container, false)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                qrCodeStatusChannel.receiveAsFlow().collectLatest {
                    qrCodeStatus = it
                    when (qrCodeStatus) {
                        QrCodeStatus.NOT_SCAN -> {}
                        QrCodeStatus.SCANNED -> {
                            showQrCodeStatusText("已扫码,请确认")
                        }
                        QrCodeStatus.EXPIRED -> {
                            showQrCodeStatusText("二维码已过期,按OK刷新")
                            Log.d("登录", "二维码过期")
                            fetchStatusJob?.cancel()
                            launch(Dispatchers.Main) {
                                viewBinding.qrCodeContainer.setOnClickListener(qrCodeClickListener)
                            }
                        }
                        QrCodeStatus.LOGIN_SUCCESS -> {
                            fetchStatusJob?.cancel()
                            showQrCodeStatusText("登录成功,即将跳转到主页")
                            launch(Dispatchers.IO) {
                                val user = userViewModel.fetchUser()
                                if (user == null || !user.isLogin) {
                                    showQrCodeStatusText("获取用户信息失败")
                                } else {
                                    navigateToHome()
                                }
                            }
                            Log.d("登录", "登录成功")
                        }
                    }
                }
            }
        }
        viewBinding.qrCodeContainer.requestFocus()
        return viewBinding.root
    }

    private suspend fun navigateToHome() {
        lifecycleScope.launch(Dispatchers.Main) {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
        }
    }

    private fun refreshQrCodeKey() {
        lifecycleScope.launch(Dispatchers.IO) {
            passportApi.applyQrCode().data?.let {
                loopFetchQrCodeStatus(it.qrCodeKey)
                val bitMatrix = QRCodeWriter().encode(it.url, BarcodeFormat.QR_CODE, 512, 512)
                val bitmap = Bitmap.createBitmap(
                    bitMatrix.width,
                    bitMatrix.height,
                    Bitmap.Config.RGB_565
                )
                for (x in 0 until bitMatrix.width) {
                    for (y in 0 until bitMatrix.height) {
                        bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                    }
                }
                launch(Dispatchers.Main) {
                    viewBinding.qrCode.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun loopFetchQrCodeStatus(qrCodeKey: String, interval: Long = 2000L) {
        fetchStatusJob?.cancel()
        fetchStatusJob = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    delay(interval)
                    passportApi.pollQrCodeStatus(qrCodeKey).data?.code?.let { code ->
                        qrCodeStatusChannel.trySend(QrCodeStatus.fromCodeValue(code))
                    }
                }
            }
        }
    }

}