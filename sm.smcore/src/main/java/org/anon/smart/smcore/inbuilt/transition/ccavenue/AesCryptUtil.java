/**
 * SMART - State Machine ARchiTecture
 *
 * Copyright (C) 2012 Individual contributors as indicated by
 * the @authors tag
 *
 * This file is a part of SMART.
 *
 * SMART is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SMART is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * */
 
/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.anon.smart.smcore.inbuilt.transition.ccavenue.AesCryptUtil
 * Author:              rsankar
 * Revision:            1.0
 * Date:                14-08-2014
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A utility class provided by ccavenue for encrypting requests
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.inbuilt.transition.ccavenue;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class AesCryptUtil 
{
    Cipher ecipher;
    Cipher dcipher;

    /**
    * Input a string that will be md5 hashed to create the key.
    * @return void, cipher initialized
    */

    public AesCryptUtil(){
        try{
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            this.setupCrypto(kgen.generateKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public AesCryptUtil(String key){
        SecretKeySpec skey = new SecretKeySpec(getMD5(key), "AES");
        this.setupCrypto(skey);
    }

    private void setupCrypto(SecretKey key){
        // Create an 8-byte initialization vector
        byte[] iv = new byte[]
        {
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
        };

        AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
        try
        {
            ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            // CBC requires an initialization vector
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // Buffer used to transport the bytes from one stream to another
    byte[] buf = new byte[1024];

    public void encrypt(InputStream in, OutputStream out){
        try {
            // Bytes written to out will be encrypted
            out = new CipherOutputStream(out, ecipher);

            // Read in the cleartext bytes and write to out to encrypt
            int numRead = 0;
            while ((numRead = in.read(buf)) >= 0){
                out.write(buf, 0, numRead);
            }
            out.close();
        }
        catch (java.io.IOException e){
            e.printStackTrace();
        }
    }

    /**
    * Input is a string to encrypt.
    * @return a Hex string of the byte array
    */
    public String encrypt(String plaintext){
        try{
            byte[] ciphertext = ecipher.doFinal(plaintext.getBytes("UTF-8"));
            return this.byteToHex(ciphertext);
        } catch (Exception e){
             e.printStackTrace();
            return null;
        }

    }

    public void decrypt(InputStream in, OutputStream out){
        try {
            // Bytes read from in will be decrypted
            in = new CipherInputStream(in, dcipher);

            // Read in the decrypted bytes and write the cleartext to out
            int numRead = 0;
            while ((numRead = in.read(buf)) >= 0) {
                out.write(buf, 0, numRead);
            }
            out.close();
        } catch (java.io.IOException e) {
             e.printStackTrace();
        }
    }

    /**
    * Input encrypted String represented in HEX
    * @return a string decrypted in plain text
    */
    public String decrypt(String hexCipherText){
        try{
            String plaintext = new String(dcipher.doFinal(this.hexToByte(hexCipherText)), "UTF-8");
            return  plaintext;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String decrypt(byte[] ciphertext){
        try{
            String plaintext = new String(dcipher.doFinal(ciphertext), "UTF-8");
            return  plaintext;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] getMD5(String input){
        try{
            byte[] bytesOfMessage = input.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(bytesOfMessage);
        }  catch (Exception e){
             return null;
        }
    }

    static final String HEXES = "0123456789ABCDEF";

    public static String byteToHex( byte [] raw ) {
        if ( raw == null ) {
          return null;
        }
		String result = "";
		for (int i=0; i < raw.length; i++) {
			result +=
			Integer.toString( ( raw[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		return result;
	}

    public static byte[] hexToByte( String hexString){
        int len = hexString.length();
        byte[] ba = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            ba[i/2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i+1), 16));
        }
        return ba;
    }
}

