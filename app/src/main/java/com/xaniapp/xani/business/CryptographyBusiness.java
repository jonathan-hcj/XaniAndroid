package com.xaniapp.xani.business;

import com.xaniapp.xani.entites.Result;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class CryptographyBusiness {

    public static Result<String> getSHA256 (String unencoded) {

        var result = new Result<String>();
         try {

            var messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(unencoded.getBytes(StandardCharsets.UTF_8));
            var digest = messageDigest.digest();

            result.data = String.format("%064x", new BigInteger(1, digest));
        }
        catch (Exception e) {
            result.setFail(e.getMessage());
        }
        return result;
    }
}
