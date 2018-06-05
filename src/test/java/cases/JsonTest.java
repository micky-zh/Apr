package cases;

import org.codehaus.jackson.JsonNode;

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
}
