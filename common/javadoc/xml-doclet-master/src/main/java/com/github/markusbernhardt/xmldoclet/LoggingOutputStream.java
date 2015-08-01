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


package com.github.markusbernhardt.xmldoclet;

import java.io.IOException;

import org.slf4j.Logger;

class LoggingOutputStream extends java.io.OutputStream {

	protected Logger log;
	protected LoggingLevelEnum loggingLevel;

	/**
	 * Used to maintain the contract of {@link #close()}.
	 */
	protected boolean hasBeenClosed = false;

	/**
	 * The internal buffer where data is stored.
	 */
	protected StringBuffer buffer = new StringBuffer();

	/**
	 * Creates the LoggingOutputStream to flush to the given Category.
	 * 
	 * @param log
	 *            the Logger to write to
	 * 
	 * @param isError
	 *            the if true write to error, else info
	 * 
	 * @exception IllegalArgumentException
	 *                if cat == null or priority == null
	 */
	public LoggingOutputStream(Logger log, LoggingLevelEnum loggingLevel) throws IllegalArgumentException {
		if (log == null) {
			throw new IllegalArgumentException("log == null");
		}

		this.loggingLevel = loggingLevel;
		this.log = log;
	}

	/**
	 * Closes this output stream and releases any system resources associated
	 * with this stream. The general contract of <code>close</code> is that it
	 * closes the output stream. A closed stream cannot perform output
	 * operations and cannot be reopened.
	 */
	@Override
	public void close() {
		flush();
		hasBeenClosed = true;
	}

	/**
	 * Writes the specified byte to this output stream. The general contract for
	 * <code>write</code> is that one byte is written to the output stream. The
	 * byte to be written is the eight low-order bits of the argument
	 * <code>b</code>. The 24 high-order bits of <code>b</code> are ignored.
	 * 
	 * @param b
	 *            the <code>byte</code> to write
	 */
	@Override
	public void write(final int b) throws IOException {
		if (hasBeenClosed) {
			throw new IOException("The stream has been closed.");
		}

		byte[] bytes = new byte[1];
		bytes[0] = (byte) (b & 0xff);
		String s = new String(bytes);
		if (s.equals("\n")) {
			flush();
		} else {
			buffer.append(s);
		}
	}

	/**
	 * Flushes this output stream and forces any buffered output bytes to be
	 * written out. The general contract of <code>flush</code> is that calling
	 * it is an indication that, if any bytes previously written have been
	 * buffered by the implementation of the output stream, such bytes should
	 * immediately be written to their intended destination.
	 */
	@Override
	public void flush() {
		String message = buffer.toString().trim();
		if (message.length() > 0) {
			loggingLevel.log(log, message);
		}
		reset();
	}

	private void reset() {
		buffer = new StringBuffer();
	}
}