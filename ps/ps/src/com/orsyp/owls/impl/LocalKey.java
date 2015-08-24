/*
 * LocalKey.java
 *
 * Copyright: Copyright (c) 2010
 *
 * Company: Orsyp Logiciels Inc.
 */

package com.orsyp.owls.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import com.orsyp.log.Log;
import com.orsyp.log.LogFactory;

/**
 * Implements the local.key (key for local authentication).
 *
 * @author jjt
 * @version $Revision: 1.3 $
 */
public class LocalKey {

    private static final String CHARSET = "ISO-8859-1";

    private static final int LG_LEN = 5;
    private static final int LG_BUFF = 256;
    private static final int LG_CHECKSUM = 1;
    private static final int LG_FILL = 256;

    private static Log log = LogFactory.getLog(LocalKey.class);


    private String filename;
    private byte[] mask;


    public LocalKey(String filename, String host)
            throws UnsupportedEncodingException {

        super();
        this.filename = filename;
        initMask(host);
    }


    public static CharsetDecoder getCharsetDecoder() {

        return Charset.forName(CHARSET).newDecoder();
    }


    public byte[] read() {

       byte[] len = new byte[LG_LEN];
       byte[] buff = new byte[LG_BUFF];
       byte[] checksum = new byte[LG_CHECKSUM];
       byte[] fill = new byte[LG_FILL];

       InputStream stream = null;

       try {
           File file = new File(this.filename);
           stream = new BufferedInputStream(new FileInputStream(file));
           if (stream.read(len) != len.length) {
               if ((log != null) && (log.isErrorEnabled())) {
                   log.error("cannot read len");
               }
               return null;
           }

           if (stream.read(buff) != buff.length) {
               if ((log != null) && (log.isErrorEnabled())) {
                   log.error("cannot read buff");
               }
               return null;
           }

           if (stream.read(checksum) != checksum.length) {
               if ((log != null) && (log.isErrorEnabled())) {
                   log.error("cannot read checksum");
               }
               return null;
           }

           if (stream.read(fill) != fill.length) {
               if ((log != null) && (log.isErrorEnabled())) {
                   log.error("cannot read fill");
               }
               return null;
           }
       } catch (IOException e) {
           if ((log != null) && (log.isErrorEnabled())) {
               log.error("error reading local key", e);
           }
       } finally {
           if (stream != null) {
               try {
                   stream.close();
               } catch (IOException ignored) {
                   if ((log != null) && (log.isErrorEnabled())) {
                       log.error("error closing " + this.filename, ignored);
                   }
               }
           }
       }

       byte[] key = null;
       CharsetDecoder decoder = getCharsetDecoder();
       try {
           CharBuffer cbuff = decoder.decode(ByteBuffer.wrap(len));
           int keyLen = Integer.parseInt(cbuff.toString());

           key = new byte[keyLen];

           for (int i = 0; i < keyLen; i++) {
               key[i] = (byte) (this.mask[i % this.mask.length] ^ buff[i]);
           }

           int check = keyLen;
           for (int i = 0; i < keyLen; i++) {
               check += key[i];
           }

           if ((byte) (check % 256) != checksum[0]) {
               if ((log != null) && (log.isErrorEnabled())) {
                   log.error("checksum error");
               }
           }
       } catch (CharacterCodingException e) {
           if ((log != null) && (log.isErrorEnabled())) {
               log.error("error reading local key", e);
           }
       }
       return key;
   }


   private void initMask(String host) throws UnsupportedEncodingException {

       this.mask = host.getBytes(CHARSET);
   }
}
