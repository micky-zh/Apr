package cases;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import use.demo.utils.JSONUtils;

public class JsonTest {

    public void toStringJson() {
        String json = "\"{\\\"answer\\\":{\\\"text\\\":\\\"<p>资料库 <span style=\\\\\\\"background-color: rgb(255, 255,"
                + " 0);\\\\\\\"><b>资料wj</b></span>, "
                + "更多精彩反问<a href=\\\\\\\"http://aicp.baidu.com\\\\\\\" target=\\\\\\\"_blank\\\\\\\"> aicp.baidu"
                + ".com</a></p><p><br></p><p>列表</p><ul><li>AAAA</li><li>BBBB</li><li>CCCC</li></ul><p><br></p><p"
                + ">表格</p><p><br></p><table class=\\\\\\\"table "
                + "table-bordered\\\\\\\"><tbody><tr><td>AAA</td><td>BBB</td><td>CCC</td><td>DDD</td></tr><tr><td>WWW"
                + "</td><td><br></td><td><br></td><td><br></td></tr><tr><td>EEE</td><td><br></td><td><br></td><td><br"
                + "></td></tr></tbody></table><p><br></p><p><br></p><p></p>\\\"},\\\"type\\\":3}\"";

        if (json.startsWith("\"") && json.endsWith("\"")) {
            json = json.substring(1, json.length() - 1);
        }
        json = json.replaceAll("\\\\\"", "\"").replaceAll("\\\\\\\\", "\\\\");
        JsonNode jsonNode = JSONUtils.toObject(json);
        System.out.println(json);
    }

    private ObjectNode createNode(String key, Object v) {
        return setNodeValue(JsonNodeFactory.instance.objectNode(), key, v);
    }

    private ObjectNode setNodeValue(ObjectNode root, String key, Object v) {
        String arr[] = key.split("\\.");
        Validate.notNull(key);
        int counter = 0;
        ObjectNode node = null;
        for (String k : arr) {
            counter++;
            if (node == null) {
                node = root;
            }
            if (node.path(k).isMissingNode()) {
                node.set(k, JsonNodeFactory.instance.objectNode());
            }

            if (counter != arr.length) {
                node = (ObjectNode) node.path(k);
                continue;
            }

            if (v == null) {
                node.set(k, null);
            }

            if (v instanceof ObjectNode) {
                node.set(k, (ObjectNode) v);
            }

            if (v instanceof ArrayNode) {
                node.set(k, (ArrayNode) v);
            }

            if (v instanceof String) {
                node.put(k, v.toString());
            }

            if (v instanceof Integer) {
                node.put(k, (Integer) v);
            }
        }
        return root;
    }

    protected ArrayNode newArrayNode() {
        return JsonNodeFactory.instance.arrayNode();
    }

    @Test
    public void print() throws IOException {
        ObjectNode root = JsonNodeFactory.instance.objectNode();
        //        System.out.println(createNode("a.b.c", 2));
        //        System.out.println(createNode("a.b.c", ""));
        //        System.out.println(createNode("a.b.c", "a"));
        //        System.out.println(createNode("a.b.c", null));
        //        System.out.println(createNode("a.b.c", null));
        //        System.out.println(setNodeValue(root,"a.b.c.d", 1));
        //        System.out.println(setNodeValue(root,"a.b.c.e", 2));
        //        System.out.println(createNode("a.b.c", newArrayNode()));
        setNodeValue(root, "aggs.top.aggs.submax.max.field", "created");
        System.out.println(setNodeValue(root, "aggs.top.terms.field", "dbId"));
        System.out.println(setNodeValue(root, "aggs.top.aggs.submax.max.field", "created"));

    }
}
