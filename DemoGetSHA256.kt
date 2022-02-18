import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URL
import java.security.MessageDigest
import java.security.cert.Certificate
import javax.net.ssl.HttpsURLConnection
import javax.security.cert.CertificateEncodingException
import javax.xml.bind.DatatypeConverter




/**
 * You can edit, run, and share this code.
 * play.kotlinlang.org
 */

fun main() {
    println("Hello, world!!!")
    val fingerprint = getFingerprint("https://api.uat.finos.asia", 30000, emptyMap(), "SHA-256")
//    val ans = fingerprint.chunked(2).joinToString(":")
//    println(ans)
    println(fingerprint)
}

private fun getFingerprint(
    httpsURL: String,
    connectTimeout: Int,
    httpHeaderArgs: Map<String, String>,
    type: String
): String {

    val url = URL(httpsURL)
    val httpClient: HttpsURLConnection = url.openConnection() as HttpsURLConnection
    if (connectTimeout > 0)
        httpClient.connectTimeout = connectTimeout * 1000
    httpHeaderArgs.forEach { (key, value) -> httpClient.setRequestProperty(key, value) }

    try {
        httpClient.connect()
    } catch (socket: SocketTimeoutException) {
        return ""
    } catch (io: IOException) {
        return ""
    }

    val cert: Certificate = httpClient.serverCertificates[0] as Certificate
    try {
        println("-----BEGIN CERTIFICATE-----")
//        println(DatatypeConverter.printBase64Binary(cert.getEncoded()))
        println(DatatypeConverter.printBase64Binary(cert.getEncoded()).replace(Regex("(.{64})"), "$1\n"))
        println("-----END CERTIFICATE-----")
    } catch (e: CertificateEncodingException) {
        e.printStackTrace()
    }
    return hashString(type, cert.encoded)
}

private fun hashString(type: String, input: ByteArray) =
    MessageDigest
        .getInstance(type)
        .digest(input).joinToString(separator = ":") { String.format("%02X", it) }