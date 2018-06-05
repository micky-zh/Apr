package use.demo;

import static org.codehaus.jackson.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.node.ObjectNode;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import sun.security.provider.MD5;
import use.demo.utils.JSONUtils;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        System.out.println(DigestUtils.md5Hex("cmb" + "c%mb_2018"));
        System.out.println(DigestUtils.md5Hex("gddw1" + "gddw_123"));
    }


}
