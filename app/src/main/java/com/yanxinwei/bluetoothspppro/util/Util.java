package com.yanxinwei.bluetoothspppro.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by yanxinwei on 16/3/28.
 */
public class Util {

    public static String getInfo(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    F.P, PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            return parseSignature(sign.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parseSignature(byte[] signature) {
        try {
            CertificateFactory certFactory;
                certFactory = CertificateFactory
                        .getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory
                    .generateCertificate(new ByteArrayInputStream(signature));
//            String pubKey = cert.getPublicKey().toString();
            return cert.getSerialNumber().toString();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }



}
