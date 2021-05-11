import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESCrypt {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final String KEY = "1Hbfh667adfDEJ78";

    public static String encrypt(String value) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedByteValue = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
    }

    public static String decrypt(String value) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedValue64 = Base64.decode(value, Base64.DEFAULT);
        byte[] decryptedByteValue = cipher.doFinal(decryptedValue64);
        return new String(decryptedByteValue, StandardCharsets.UTF_8);
    }

    String getHashKey(Context context) {
        Context mAppContext = context.getApplicationContext();
        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = mAppContext.getPackageManager().getPackageInfo(mAppContext.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            Signature signature = info.signatures[0];
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(signature.toByteArray());
            String sig = Base64.encodeToString(md.digest(), Base64.DEFAULT);
            Toast.makeText(context, sig, Toast.LENGTH_LONG).show();
            return sig;
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Key generateKey() {
        return new SecretKeySpec(AESCrypt.KEY.getBytes(), AESCrypt.ALGORITHM);
    }
}
