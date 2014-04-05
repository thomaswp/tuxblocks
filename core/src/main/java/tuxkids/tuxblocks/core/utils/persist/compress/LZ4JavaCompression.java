// Auto-generated: DO NOT EDIT

package tuxkids.tuxblocks.core.utils.persist.compress;

import static tuxkids.tuxblocks.core.utils.persist.compress.LZ4Constants.*;
import static tuxkids.tuxblocks.core.utils.persist.compress.LZ4Utils.*;
import static tuxkids.tuxblocks.core.utils.persist.compress.Utils.*;

/**
 * Compressor. 
 */
public final class LZ4JavaCompression {

	public static final LZ4JavaCompression INSTANCE = new LZ4JavaCompression();

	public final int compress(byte[] src, int srcOff, int srcLen, byte[] dest, int destOff) {
		return compress(src, srcOff, srcLen, dest, destOff, dest.length - destOff);
	}

	/**
	 * Convenience method, equivalent to calling
	 * {@link #compress(byte[], int, int, byte[], int) compress(src, 0, src.length, dest, 0)}.
	 */
	public final int compress(byte[] src, byte[] dest) {
		return compress(src, 0, src.length, dest, 0);
	}

	private static byte[] copyOf(byte[] array, int length) {
		byte[] copy = new byte[length];
		int min = Math.min(array.length, length);
		for (int i = 0; i < min; i++) {
			copy[i] = array[i];
		}
		return copy;
	}
	
	private static void fill(int[] array, int value) {
		int length = array.length;
		for (int i = 0; i < length; i++) {
			array[i] = value;
		}
	}
	
	/**
	 * Convenience method which returns <code>src[srcOff:srcOff+srcLen]</code>
	 * compressed.
	 * <p><b><span style="color:red">Warning</span></b>: this method has an
	 * important overhead due to the fact that it needs to allocate a buffer to
	 * compress into, and then needs to resize this buffer to the actual
	 * compressed length.</p>
	 * <p>Here is how this method is implemented:</p>
	 * <pre>
	 * final int maxCompressedLength = maxCompressedLength(srcLen);
	 * final byte[] compressed = new byte[maxCompressedLength];
	 * final int compressedLength = compress(src, srcOff, srcLen, compressed, 0);
	 * return Arrays.copyOf(compressed, compressedLength);
	 * </pre>
	 */
	public final byte[] compress(byte[] src, int srcOff, int srcLen) {
		final int maxCompressedLength = maxCompressedLength(srcLen);
		final byte[] compressed = new byte[maxCompressedLength];
		final int compressedLength = compress(src, srcOff, srcLen, compressed, 0);
		return copyOf(compressed, compressedLength);
	}

	/**
	 * Convenience method, equivalent to calling
	 * {@link #compress(byte[], int, int) compress(src, 0, src.length)}.
	 */
	public final byte[] compress(byte[] src) {
		return compress(src, 0, src.length);
	}


	static int compress64k(byte[] src, int srcOff, int srcLen, byte[] dest, int destOff, int destEnd) {
		final int srcEnd = srcOff + srcLen;
		final int srcLimit = srcEnd - LAST_LITERALS;
		final int mflimit = srcEnd - MF_LIMIT;

		int sOff = srcOff, dOff = destOff;

		int anchor = sOff;

		if (srcLen >= MIN_LENGTH) {

			final short[] hashTable = new short[HASH_TABLE_SIZE_64K];

			++sOff;

			main:
				while (true) {

					// find a match
					int forwardOff = sOff;

					int ref;
					int findMatchAttempts = (1 << SKIP_STRENGTH) + 3;
					do {
						sOff = forwardOff;
						forwardOff += findMatchAttempts++ >>> SKIP_STRENGTH;

					if (forwardOff > mflimit) {
						break main;
					}

					final int h = hash64k(readInt(src, sOff));
					ref = srcOff + readShort(hashTable, h);
					writeShort(hashTable, h, sOff - srcOff);
					} while (!readIntEquals(src, ref, sOff));

					// catch up
					final int excess = commonBytesBackward(src, ref, sOff, srcOff, anchor);
					sOff -= excess;
					ref -= excess;

					// sequence == refsequence
					final int runLen = sOff - anchor;

					// encode literal length
					int tokenOff = dOff++;

					if (dOff + runLen + (2 + 1 + LAST_LITERALS) + (runLen >>> 8) > destEnd) {
						throw new LZ4Exception("maxDestLen is too small");
					}

					if (runLen >= RUN_MASK) {
						writeByte(dest, tokenOff, RUN_MASK << ML_BITS);
						dOff = writeLen(runLen - RUN_MASK, dest, dOff);
					} else {
						writeByte(dest, tokenOff, runLen << ML_BITS);
					}

					// copy literals
					wildArraycopy(src, anchor, dest, dOff, runLen);
					dOff += runLen;

					while (true) {
						// encode offset
						writeShortLittleEndian(dest, dOff, (short) (sOff - ref));
						dOff += 2;

						// count nb matches
						sOff += MIN_MATCH;
						ref += MIN_MATCH;
						final int matchLen = commonBytes(src, ref, sOff, srcLimit);
						if (dOff + (1 + LAST_LITERALS) + (matchLen >>> 8) > destEnd) {
							throw new LZ4Exception("maxDestLen is too small");
						}
						sOff += matchLen;

						// encode match len
						if (matchLen >= ML_MASK) {
							writeByte(dest, tokenOff, readByte(dest, tokenOff) | ML_MASK);
							dOff = writeLen(matchLen - ML_MASK, dest, dOff);
						} else {
							writeByte(dest, tokenOff, readByte(dest, tokenOff) | matchLen);
						}

						// test end of chunk
						if (sOff > mflimit) {
							anchor = sOff;
							break main;
						}

						// fill table
						writeShort(hashTable, hash64k(readInt(src, sOff - 2)), sOff - 2 - srcOff);

						// test next position
						final int h = hash64k(readInt(src, sOff));
						ref = srcOff + readShort(hashTable, h);
						writeShort(hashTable, h, sOff - srcOff);

						if (!readIntEquals(src, sOff, ref)) {
							break;
						}

						tokenOff = dOff++;
						dest[tokenOff] = 0;
					}

					// prepare next loop
					anchor = sOff++;
				}
		}

		dOff = lastLiterals(src, anchor, srcEnd - anchor, dest, dOff, destEnd);
		return dOff - destOff;
	}

	public int compress(byte[] src, final int srcOff, int srcLen, byte[] dest, final int destOff, int maxDestLen) {
		checkRange(src, srcOff, srcLen);
		checkRange(dest, destOff, maxDestLen);
		final int destEnd = destOff + maxDestLen;

		if (srcLen < LZ4_64K_LIMIT) {
			return compress64k(src, srcOff, srcLen, dest, destOff, destEnd);
		}

		final int srcEnd = srcOff + srcLen;
		final int srcLimit = srcEnd - LAST_LITERALS;
		final int mflimit = srcEnd - MF_LIMIT;

		int sOff = srcOff, dOff = destOff;
		int anchor = sOff++;

		final int[] hashTable = new int[HASH_TABLE_SIZE];
		fill(hashTable, anchor);

		main:
			while (true) {

				// find a match
				int forwardOff = sOff;

				int ref;
				int findMatchAttempts = (1 << SKIP_STRENGTH) + 3;
				int back;
				do {
					sOff = forwardOff;
					forwardOff += findMatchAttempts++ >>> SKIP_STRENGTH;

				if (forwardOff > mflimit) {
					break main;
				}

				final int h = hash(readInt(src, sOff));
				ref = readInt(hashTable, h);
				back = sOff - ref;
				writeInt(hashTable, h, sOff);
				} while (back >= MAX_DISTANCE || !readIntEquals(src, ref, sOff));


				final int excess = commonBytesBackward(src, ref, sOff, srcOff, anchor);
				sOff -= excess;
				ref -= excess;

				// sequence == refsequence
						final int runLen = sOff - anchor;

						// encode literal length
						int tokenOff = dOff++;

						if (dOff + runLen + (2 + 1 + LAST_LITERALS) + (runLen >>> 8) > destEnd) {
							throw new LZ4Exception("maxDestLen is too small");
						}

						if (runLen >= RUN_MASK) {
							writeByte(dest, tokenOff, RUN_MASK << ML_BITS);
							dOff = writeLen(runLen - RUN_MASK, dest, dOff);
						} else {
							writeByte(dest, tokenOff, runLen << ML_BITS);
						}

						// copy literals
						wildArraycopy(src, anchor, dest, dOff, runLen);
						dOff += runLen;

						while (true) {
							// encode offset
							writeShortLittleEndian(dest, dOff, back);
							dOff += 2;

							// count nb matches
							sOff += MIN_MATCH;
							final int matchLen = commonBytes(src, ref + MIN_MATCH, sOff, srcLimit);
							if (dOff + (1 + LAST_LITERALS) + (matchLen >>> 8) > destEnd) {
								throw new LZ4Exception("maxDestLen is too small");
							}
							sOff += matchLen;

							// encode match len
							if (matchLen >= ML_MASK) {
								writeByte(dest, tokenOff, readByte(dest, tokenOff) | ML_MASK);
								dOff = writeLen(matchLen - ML_MASK, dest, dOff);
							} else {
								writeByte(dest, tokenOff, readByte(dest, tokenOff) | matchLen);
							}

							// test end of chunk
							if (sOff > mflimit) {
								anchor = sOff;
								break main;
							}

							// fill table
							writeInt(hashTable, hash(readInt(src, sOff - 2)), sOff - 2);

							// test next position
							final int h = hash(readInt(src, sOff));
							ref = readInt(hashTable, h);
							writeInt(hashTable, h, sOff);
							back = sOff - ref;

							if (back >= MAX_DISTANCE || !readIntEquals(src, ref, sOff)) {
								break;
							}

							tokenOff = dOff++;
							writeByte(dest, tokenOff, 0);
						}

						// prepare next loop
						anchor = sOff++;
			}

		dOff = lastLiterals(src, anchor, srcEnd - anchor, dest, dOff, destEnd);
		return dOff - destOff;
	}

	public final int decompress(byte[] src, int srcOff, int srcLen, byte[] dest, int destOff) {
		return decompress(src, srcOff, srcLen, dest, destOff, dest.length - destOff);
	}

	/**
	 * Convenience method, equivalent to calling
	 * {@link #decompress(byte[], int, int, byte[], int) decompress(src, 0, src.length, dest, 0)}
	 */
	public final int decompress(byte[] src, byte[] dest) {
		return decompress(src, 0, src.length, dest, 0);
	}

	/**
	 * Convenience method which returns <code>src[srcOff:srcOff+srcLen]</code>
	 * decompressed.
	 * <p><b><span style="color:red">Warning</span></b>: this method has an
	 * important overhead due to the fact that it needs to allocate a buffer to
	 * decompress into, and then needs to resize this buffer to the actual
	 * decompressed length.</p>
	 * <p>Here is how this method is implemented:</p>
	 * <pre>
	 * byte[] decompressed = new byte[maxDestLen];
	 * final int decompressedLength = decompress(src, srcOff, srcLen, decompressed, 0, maxDestLen);
	 * if (decompressedLength != decompressed.length) {
	 * decompressed = Arrays.copyOf(decompressed, decompressedLength);
	 * }
	 * return decompressed;
	 * </pre>
	 */
	public final byte[] decompress(byte[] src, int srcOff, int srcLen, int maxDestLen) {
		byte[] decompressed = new byte[maxDestLen];
		final int decompressedLength = decompress(src, srcOff, srcLen, decompressed, 0, maxDestLen);
		if (decompressedLength != decompressed.length) {
			decompressed = copyOf(decompressed, decompressedLength);
		}
		return decompressed;
	}

	/**
	 * Convenience method, equivalent to calling
	 * {@link #decompress(byte[], int, int, int) decompress(src, 0, src.length, maxDestLen)}.
	 */
	public final byte[] decompress(byte[] src, int maxDestLen) {
		return decompress(src, 0, src.length, maxDestLen);
	}

	public int decompress(byte[] src, final int srcOff, final int srcLen, byte[] dest, final int destOff, int destLen) {

		checkRange(src, srcOff, srcLen);
		checkRange(dest, destOff, destLen);

		if (destLen == 0) {
			if (srcLen != 1 || src[srcOff] != 0) {
				throw new LZ4Exception("Output buffer too small");
			}
			return 0;
		}

		final int srcEnd = srcOff + srcLen;


		final int destEnd = destOff + destLen;

		int sOff = srcOff;
		int dOff = destOff;

		while (true) {
			final int token = readByte(src, sOff) & 0xFF;
			++sOff;

			// literals
			int literalLen = token >>> ML_BITS;
			if (literalLen == RUN_MASK) {
				byte len = (byte) 0xFF;
				while (sOff < srcEnd && (len = src[sOff++]) == (byte) 0xFF) {
					literalLen += 0xFF;
				}
				literalLen += len & 0xFF;
			}

			final int literalCopyEnd = dOff + literalLen;
			if (literalCopyEnd > destEnd - COPY_LENGTH) {
				if (literalCopyEnd != destEnd) {
					throw new LZ4Exception("Malformed input at " + sOff);
				} else {
					safeArraycopy(src, sOff, dest, dOff, literalLen);
					sOff += literalLen;
					dOff = literalCopyEnd;
					break; // EOF
				}
			}

			wildArraycopy(src, sOff, dest, dOff, literalLen);
			sOff += literalLen;
			dOff = literalCopyEnd;

			// matchs
			final int matchDec = readShortLittleEndian(src, sOff);
			sOff += 2;
			int matchOff = dOff - matchDec;

			if (matchOff < destOff) {
				throw new LZ4Exception("Malformed input at " + sOff);
			}

			int matchLen = token & ML_MASK;
			if (matchLen == ML_MASK) {
				byte len = (byte) 0xFF;
				while (sOff < srcEnd && (len = src[sOff++]) == (byte) 0xFF) {
					matchLen += 0xFF;
				}
				matchLen += len & 0xFF;
			}
			matchLen += MIN_MATCH;

			final int matchCopyEnd = dOff + matchLen;

			if (matchCopyEnd > destEnd - COPY_LENGTH) {
				if (matchCopyEnd > destEnd) {
					throw new LZ4Exception("Malformed input at " + sOff);
				}
				safeIncrementalCopy(dest, matchOff, dOff, matchLen);
			} else {
				wildIncrementalCopy(dest, matchOff, dOff, matchCopyEnd);
			}
			dOff = matchCopyEnd;
		}

		return sOff - srcOff;
	}
}

