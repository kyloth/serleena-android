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


/**
 * Name: CloudJSONOutboundStream.java
 * Package: com.kyloth.serleena.synchronization.kylothcloud.outbound
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer        Changes
 * 1.0.0    Tobia Tesan       Creazione file
 */
package com.kyloth.serleena.synchronization.kylothcloud.outbound;

import com.kyloth.serleena.synchronization.JSONOutboundStream;

import java.io.BufferedOutputStream;
import java.io.OutputStream;

/**
 * Concretizza OutboundStream in uno stream JSON idoneo in particolare per l'invio a
 * KylothCloud.
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0.0
 */
public class CloudJSONOutboundStream extends BufferedOutputStream implements JSONOutboundStream {
    /**
     * Costruisce un CloudJSONOutboundStream da un OutputStream
     *
     * @param out L'OutputStream da cui creare il CloudJSONOutboundStream
     */
    public CloudJSONOutboundStream(OutputStream out) {
        super(out);
    }
}
