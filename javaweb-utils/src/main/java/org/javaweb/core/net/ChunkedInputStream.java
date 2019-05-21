/*
 * Copyright yz 2016-01-14  Email:admin@javaweb.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.javaweb.core.net;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 从HttpComponents 摘过来的代码，删除了无用的代码
 * Implements chunked transfer coding. The content is received in small chunks.
 * Entities transferred using this input stream can be of unlimited length.
 * After the stream is read to the end, it provides access to the trailers,
 * if any.
 * <p>
 * Note that this class NEVER closes the underlying stream, even when close
 * gets called.  Instead, it will read until the "end" of its chunking on
 * close, which allows for the seamless execution of subsequent HTTP 1.1
 * requests, while not requiring the client to remember to read the entire
 * contents of the response.
 *
 * @since 4.0
 */
public class ChunkedInputStream extends InputStream {

	private static final int CHUNK_LEN = 1;

	private static final int CHUNK_DATA = 2;

	private static final int CHUNK_CRLF = 3;

	private static final int CHUNK_INVALID = Integer.MAX_VALUE;

	private static final int BUFFER_SIZE = 2048;

	/**
	 * The session input buffer
	 */
	private final DataInputStream in;

	private int state;

	/**
	 * The chunk size
	 */
	private int chunkSize;

	/**
	 * The current position within the current chunk
	 */
	private int pos;

	/**
	 * True if we've reached the end of stream
	 */
	private boolean eof = false;

	/**
	 * True if this stream is closed
	 */
	private boolean closed = false;

	public ChunkedInputStream(final DataInputStream in) {
		super();
		this.in = in;
		this.pos = 0;
		this.state = CHUNK_LEN;
	}

	/**
	 * <p> Returns all the data in a chunked stream in coalesced form. A chunk
	 * is followed by a CRLF. The method returns -1 as soon as a chunksize of 0
	 * is detected.</p>
	 * <p>
	 * <p> Trailer headers are read automatically at the end of the stream and
	 * can be obtained with the getResponseFooters() method.</p>
	 *
	 * @return -1 of the end of the stream has been reached or the next data
	 * byte
	 * @throws IOException in case of an I/O error
	 */
	@Override
	public int read() throws IOException {
		if (this.closed) {
			throw new IOException("Attempted read from closed stream.");
		}

		if (this.eof) {
			return -1;
		}

		if (state != CHUNK_DATA) {
			nextChunk();

			if (this.eof) {
				return -1;
			}
		}

		final int b = in.read();

		if (b != -1) {
			pos++;

			if (pos >= chunkSize) {
				state = CHUNK_CRLF;
			}
		}

		return b;
	}

	/**
	 * Read some bytes from the stream.
	 *
	 * @param b   The byte array that will hold the contents from the stream.
	 * @param off The offset into the byte array at which bytes will start to be
	 *            placed.
	 * @param len the maximum number of bytes that can be returned.
	 * @return The number of bytes returned or -1 if the end of stream has been
	 * reached.
	 * @throws IOException in case of an I/O error
	 */
	@Override
	public int read(final byte[] b, final int off, final int len) throws IOException {

		if (closed) {
			throw new IOException("Attempted read from closed stream.");
		}

		if (eof) {
			return -1;
		}

		if (state != CHUNK_DATA) {
			nextChunk();
			if (eof) {
				return -1;
			}
		}

		final int bytesRead = in.read(b, off, Math.min(len, chunkSize - pos));

		if (bytesRead != -1) {
			pos += bytesRead;

			if (pos >= chunkSize) {
				state = CHUNK_CRLF;
			}

			return bytesRead;
		} else {
			eof = true;
			throw new RuntimeException("Truncated chunk " + "( expected size: " + chunkSize + "; actual size: " + pos + ")");
		}
	}

	/**
	 * Read some bytes from the stream.
	 *
	 * @param b The byte array that will hold the contents from the stream.
	 * @return The number of bytes returned or -1 if the end of stream has been
	 * reached.
	 * @throws IOException in case of an I/O error
	 */
	@Override
	public int read(final byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	/**
	 * Read the next chunk.
	 *
	 * @throws IOException in case of an I/O error
	 */
	private void nextChunk() throws IOException {
		if (state == CHUNK_INVALID) {
			throw new RuntimeException("Corrupt data stream");
		}
		try {
			chunkSize = getChunkSize();

			if (chunkSize < 0) {
				throw new RuntimeException("Negative chunk size");
			}

			state = CHUNK_DATA;
			pos = 0;

			if (chunkSize == 0) {
				eof = true;
			}
		} catch (RuntimeException ex) {
			state = CHUNK_INVALID;
			throw ex;
		}
	}

	/**
	 * Expects the stream to start with a chunksize in hex with optional
	 * comments after a semicolon. The line must end with a CRLF: "a3; some
	 * comment\r\n" Positions the stream at the start of the next line.
	 */
	private int getChunkSize() throws IOException {
		final int st = this.state;
		String    line;
		switch (st) {
			case CHUNK_CRLF:
				line = in.readLine();
				final int bytesRead1 = line.length();

				if (bytesRead1 == -1) {
					throw new RuntimeException("CRLF expected at end of chunk");
				}

				if (!line.isEmpty()) {
					throw new RuntimeException("Unexpected content at the end of chunk");
				}

				state = CHUNK_LEN;
				//$FALL-THROUGH$
			case CHUNK_LEN:
				line = in.readLine();

				final int bytesRead2 = line != null ? line.length() : -1;

				if (bytesRead2 == -1) {
					throw new RuntimeException("Premature end of chunk coded message body: closing chunk expected");
				}

				int separator = line.indexOf(';');

				if (separator < 0) {
					separator = line.length();
				}

				try {
					return Integer.parseInt(line.substring(0, separator).trim(), 16);
				} catch (final NumberFormatException e) {
					throw new RuntimeException("Bad chunk header");
				}
			default:
				throw new IllegalStateException("Inconsistent codec state");
		}
	}

	/**
	 * Upon close, this reads the remainder of the chunked message,
	 * leaving the underlying socket at a position to start reading the
	 * next response without scanning.
	 *
	 * @throws IOException in case of an I/O error
	 */
	@Override
	public void close() throws IOException {
		if (!closed) {
			try {
				if (!eof && state != CHUNK_INVALID) {
					// read and discard the remainder of the message
					final byte[] buff = new byte[BUFFER_SIZE];
					while (read(buff) >= 0) {
					}
				}
			} finally {
				eof = true;
				closed = true;
			}
		}
	}

}