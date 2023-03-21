package cn.ctrlcv.im.common.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Class Name: Base64URL
 * Class Description:
 *
 * @author liujm
 * @date 2023-03-21
 */
public class Base64Url {

    /**
     * 将base64编码后的字符串中的+、/、=替换成*、-、_
     * @param input
     * @return
     */
    public static byte[] base64EncodeUrl(byte[] input) {
        byte[] base64 = new BASE64Encoder().encode(input).getBytes();
        encodeReplace(base64);
        return base64;
    }

    public static byte[] base64EncodeUrlNotReplace(byte[] input) {
        byte[] base64 = new BASE64Encoder().encode(input).getBytes(StandardCharsets.UTF_8);
        encodeReplace(base64);
        return base64;
    }

    private static void encodeReplace(byte[] base64) {
        for (int i = 0; i < base64.length; ++i) {
            switch (base64[i]) {
                case '+':
                    base64[i] = '*';
                    break;
                case '/':
                    base64[i] = '-';
                    break;
                case '=':
                    base64[i] = '_';
                    break;
                default:
                    break;
            }
        }
    }

    
    public static byte[] base64DecodeUrlNotReplace(byte[] input) throws IOException {
        encodeNotReplace(input);
        return new BASE64Decoder().decodeBuffer(new String(input, StandardCharsets.UTF_8));
    }

    public static byte[] base64DecodeUrl(byte[] input) throws IOException {
        byte[] base64 = input.clone();
        encodeNotReplace(base64);
        return new BASE64Decoder().decodeBuffer(Arrays.toString(base64));
    }

    private static void encodeNotReplace(byte[] input) {
        for (int i = 0; i < input.length; ++i) {
            switch (input[i]) {
                case '*':
                    input[i] = '+';
                    break;
                case '-':
                    input[i] = '/';
                    break;
                case '_':
                    input[i] = '=';
                    break;
                default:
                    break;
            }
        }
    }



}
