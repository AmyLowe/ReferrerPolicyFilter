package com.tutuka.cg.processor
import java.util.*
import kotlin.collections.*


open class RefGenerator {
  val encodeMap: Array<Char> = arrayOf( 
          '0','1','2','3','4','5','6','7','8','9',  // 10
          'A','B','C','D','E','F','G','H','I','J',  // 20
          'K','L','M','N','O','P','Q','R','S','T',  // 30
          'U','V','W','X','Y','Z','|','}','~','{',  // 40
          '!','"','#','$','%','&','\'','(',')','`', // 50
          '*','+',',','-','.','/','[','\\',']','^', // 60
          ':',';','<','=','>','?','@','_','¼','½',  // 70
          '¾','ß','Ç','Ð','€','«','»','¿','•','Ø',  // 80
          '£','†','‡','§','¥'                       // 85
      )

  private val decodeMap: MutableMap<Char,Int> = mutableMapOf<Char,Int>()

  init {
    var index = 0
    encodeMap.forEach { c:Char ->
       decodeMap.put(c, index)
       index++
     }
  }

    fun getUnsignedLong(b : Byte) : Long {
        val longVal = b.toLong()
        return if(longVal > 0) longVal else longVal + 9223372036854775807

    }

  fun encode(guid: UUID): String {
        // Convert the 128-bit Guid into two 64-bit parts.
        var byteArray = guid.toString().toByteArray();
        val higher =
                getUnsignedLong(byteArray[0]).shl( 56) or getUnsignedLong(byteArray[1]).shl(48) or
                        getUnsignedLong(byteArray[2]).shl(40)  or getUnsignedLong(byteArray[3]).shl(32) or
                        getUnsignedLong(byteArray[4]).shl(24) or getUnsignedLong(byteArray[5]).shl(16) or
                        getUnsignedLong(byteArray[6]).shl(8)  or getUnsignedLong(byteArray[7])

        var lower =
                getUnsignedLong(byteArray[8]).shl( 56) or getUnsignedLong(byteArray[9]).shl(48) or
                        getUnsignedLong(byteArray[10]).shl(40)  or getUnsignedLong(byteArray[11]).shl(32) or
                        getUnsignedLong(byteArray[12]).shl(24) or getUnsignedLong(byteArray[13]).shl(16) or
                        getUnsignedLong(byteArray[14]).shl(8)  or getUnsignedLong(byteArray[15])

        var encodedStringBuilder = StringBuilder()

        // Encode each part into an ascii-85 encoded string.
        asciiEncode(encodedStringBuilder, higher)
        asciiEncode(encodedStringBuilder, lower)

        return encodedStringBuilder.toString()
    }

   fun asciiEncode(encodedStringBuilder : StringBuilder, part: Long) {
        var lPart = part;
        // Nb, the most significant digits in our encoded character will
        // be the right-most characters.
        var charCount = encodeMap.size;

        // Ascii-85 can encode 4 bytes of binary data into 5 bytes of Ascii.
        // Since a UInt64 is 8 bytes long, the Ascii-85 encoding should be
        // 10 characters long.
        for (i in 0..9)
        {
            // Get the remainder when dividing by the base.
            var remainder = (lPart % charCount).toInt();

            // Divide by the base.
            lPart /= charCount;

            // Add the appropriate character for the current value (0-84).
            encodedStringBuilder.append(encodeMap[remainder]);
        }
    }

    fun decode(ascii85Encoding: String) : UUID
    {
        // Ascii-85 can encode 4 bytes of binary data into 5 bytes of Ascii.
        // Since a Guid is 16 bytes long, the Ascii-85 encoding should be 20
        // characters long.
//        if(ascii85Encoding.length != 20)
//        {
//            throw ArgumentException(
//                    "An encoded Guid should be 20 characters long.",
//            "ascii85Encoding");
//        }

        // We only support upper case characters.
        var l_ascii85Encoding = ascii85Encoding.toUpperCase();

        // Split the string in half and decode each substring separately.
        var part1 = l_ascii85Encoding.substring(0,10)
        var higher = asciiDecode(part1)
        var part2 = l_ascii85Encoding.substring(10)

        var lower = asciiDecode(part2);
        // Convert the decoded substrings into an array of 16-bytes.
        var gbytes : ByteArray  = byteArrayOf(
                (higher and (0x0F00000000000000 + 256)).shr(56).toByte(),
                (higher and 0x00FF000000000000).shr(48).toByte(),
                (higher and 0x0000FF0000000000).shr(40).toByte(),
                (higher and 0x000000FF00000000).shr(32).toByte(),
                (higher and 0x00000000FF000000).shr(24).toByte(),
                (higher and 0x0000000000FF0000).shr(16).toByte(),
                (higher and 0x000000000000FF00).shr(8).toByte(),
                (higher and 0x00000000000000FF).toByte(),
                (lower and (0x0F00000000000000 + 256)).shr(56).toByte(),
                (lower and 0x00FF000000000000).shr(48).toByte(),
                (lower and 0x0000FF0000000000).shr(40).toByte(),
                (lower and 0x000000FF00000000).shr(32).toByte(),
                (lower and 0x00000000FF000000).shr(24).toByte(),
                (lower and 0x0000000000FF0000).shr(16).toByte(),
                (lower and 0x000000000000FF00).shr(8).toByte(),
                (lower and 0x00000000000000FF).toByte())


//        {
//            (byte)((higher & 0xFF00000000000000) >> 56),
//            (byte)((higher & 0x00FF000000000000) >> 48),
//            (byte)((higher & 0x0000FF0000000000) >> 40),
//            (byte)((higher & 0x000000FF00000000) >> 32),
//            (byte)((higher & 0x00000000FF000000) >> 24),
//            (byte)((higher & 0x0000000000FF0000) >> 16),
//            (byte)((higher & 0x000000000000FF00) >> 8),
//            (byte)((higher & 0x00000000000000FF)),
//            (byte)((lower  & 0xFF00000000000000) >> 56),
//            (byte)((lower  & 0x00FF000000000000) >> 48),
//            (byte)((lower  & 0x0000FF0000000000) >> 40),
//            (byte)((lower  & 0x000000FF00000000) >> 32),
//            (byte)((lower  & 0x00000000FF000000) >> 24),
//            (byte)((lower  & 0x0000000000FF0000) >> 16),
//            (byte)((lower  & 0x000000000000FF00) >> 8),
//            (byte)((lower  & 0x00000000000000FF)),
//        }

        return UUID.nameUUIDFromBytes(gbytes)
    }

    fun asciiDecode(ascii85EncodedString: String) : Long
    {
//        if (ascii85EncodedString.length != 10)
//        {
//            throw ArgumentException(
//                    "An Ascii-85 encoded Uint64 should be 10 characters long.",
//            "ascii85EncodedString");
//        }

        // Nb, the most significant digits in our encoded character
        // will be the right-most characters.
        var charCount = encodeMap.size;
        var result :Long = 0;

        // Starting with the right-most (most-significant) character,
        // iterate through the encoded string and decode.
        var i = ascii85EncodedString.length-1
        while (i != -1){
            // Multiply the current decoded value by the base.
            result *= charCount;

            // Add the integer value for that encoded character.
            val x = ascii85EncodedString[i]
            val c = decodeMap[x]
            c?.let {
                result += c;
            }
            i--
        }

        return result.toLong();
    }



}

