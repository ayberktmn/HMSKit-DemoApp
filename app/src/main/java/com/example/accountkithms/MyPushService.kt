import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.huawei.hms.push.HmsMessageService

class MyPushService : HmsMessageService() {
    val TAG = "PushDemoLog"

    override fun onNewToken(token: String?, bundle: Bundle?) {

        // Token'i aldığınızda burada işlem yapabilirsiniz.
        if (!TextUtils.isEmpty(token)) {
            // Token başarıyla alındı, burada token'i kullanabilirsiniz.

            Toast.makeText(this, "Token başarıyla alındı: $token", Toast.LENGTH_SHORT).show()

            // Token'i sunucunuza göndermek veya başka işlemler yapmak için kullanabilirsiniz.
            sendRegTokenToServer(token.toString())
        } else {
            // Token alınamadı
            Toast.makeText(this, "Token alınamadı", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendRegTokenToServer(token: String) {
        // Token'i sunucunuza göndermek veya başka işlemler yapmak için burada kod ekleyebilirsiniz.

    }
}
