///////////////////////////////////////////////////////////////////////////////
// 
// This file is part of Serleena.
// 
// The MIT License (MIT)
//
// Copyright (C) 2015 Antonio Cavestro, Gabriele Pozzan, Matteo Lisotto, 
//   Nicola Mometto, Filippo Sestini, Tobia Tesan, Sebastiano Valle.    
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to 
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.
//
///////////////////////////////////////////////////////////////////////////////


package com.kyloth.serleena.synchronization.kylothcloud;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Implementa IKylothIdSource per scopi di sviluppo ricavando un id 
 * "sufficientemente" univoco.
 *
 * Ricava un id dall'hash di 
 * (user.name + user.home + user.dir + 
 *  os.name + os.arch + os.version).
 * In produzione deve essere sostituito con un opportuna chiamata all'hardware.
 * 
 */
public class LocalEnvKylothIdSource implements IKylothIdSource {
    @Override
    public String getKylothId() {
        String uname = System.getProperty("user.name");
        String uhome = System.getProperty("user.home");
        String udir  = System.getProperty("user.dir");
        String oname = System.getProperty("os.name");
        String oarch = System.getProperty("os.arch");
        String ov = System.getProperty("os.version");
        String hashable = uname + uhome + udir + oname + oarch + ov;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(hashable.getBytes(), 0, hashable.length());
        String digest = new BigInteger(1, md.digest()).toString(16);
        return digest;
    }
}
