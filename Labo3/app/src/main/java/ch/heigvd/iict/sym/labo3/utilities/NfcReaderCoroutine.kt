package ch.heigvd.iict.sym.labo3.utilities

import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.util.Log
import ch.heigvd.iict.sym.labo3.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.UnsupportedEncodingException
import java.lang.RuntimeException
import java.util.*

class NfcReaderCoroutine {
    suspend fun execute(tag: Tag) : Result<List<String>>{

        return withContext(Dispatchers.IO) {
            val ndef = Ndef.get(tag)?: return@withContext Result.failure(RuntimeException("NDEF is not supported by this Tag."))
            val ndefMessage = ndef.cachedNdefMessage
            val records = ndefMessage.records

            var results = mutableListOf<String>()

            for (ndefRecord in records) {
                if (ndefRecord.tnf == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(
                        ndefRecord.type,
                        NdefRecord.RTD_TEXT
                    )
                ) {
                    try {
                        results.add(readText(ndefRecord))
                    } catch (e: UnsupportedEncodingException) {
                        Log.e("NdefReaderTask", "Unsupported Encoding", e)
                    }
                }
            }

            return@withContext Result.success(results)
        }
    }

    private fun readText(record: NdefRecord): String {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */
        val payload = record.payload

        // Get the Text Encoding
        val textEncoding = if ((payload[0].toInt() and 128) == 0) "UTF-8" else "UTF-16"

        // Get the Language Code
        val languageCodeLength: Int = payload[0].toInt() and 51

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        return String(
            payload,
            languageCodeLength + 1,
            payload.size - languageCodeLength - 1,
            charset(textEncoding)
        )
    }
}