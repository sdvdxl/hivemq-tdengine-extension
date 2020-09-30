package coder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * PayloadCodecType <br>
 *
 * @author sdvdxl <杜龙少> <br>
 * @date 2020/9/30 5:21 下午 <br>
 */
public enum PayloadCoder {
  /** to text (string) */
  RAW {
    @Override
    public String encode(byte[] payload) {
      return new String(payload, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] decode(String encodedPayload) {
      return encodedPayload == null ? null : encodedPayload.getBytes(StandardCharsets.UTF_8);
    }
  },
  /** to base64 string */
  BASE64 {
    @Override
    public String encode(byte[] payload) {
      return Base64.getEncoder().encodeToString(payload);
    }

    @Override
    public byte[] decode(String encodedPayload) {
      return encodedPayload == null ? null : Base64.getDecoder().decode(encodedPayload);
    }
  },
  HEX {
    @Override
    public String encode(byte[] payload) {
      return Hex.encodeHexString(payload);
    }

    @Override
    public byte[] decode(String encodedPayload) {
      try {
        return Hex.decodeHex(encodedPayload);
      } catch (DecoderException e) {
        throw new RuntimeException(e);
      }
    }
  };

  /**
   * encode the payload
   *
   * @param payload
   * @return encoded content
   */
  public abstract String encode(byte[] payload);

  /**
   * decode the content
   *
   * @param encodedPayload
   * @return decoded bytes
   */
  public abstract byte[] decode(String encodedPayload);

  /**
   * encode the payload
   *
   * @param payload
   * @return encoded content
   */
}
