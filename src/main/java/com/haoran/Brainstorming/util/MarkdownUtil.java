package com.haoran.Brainstorming.util;

import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Image;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class MarkdownUtil {

    public static String render(String content) {
        List<Extension> extensions = Arrays.asList(AutolinkExtension.create(), TablesExtension.create());

        Parser parser = Parser.builder().extensions(extensions).build();
        HtmlRenderer renderer = HtmlRenderer.builder().softbreak("<br/>").attributeProviderFactory(context -> new
                MyAttributeProvider()).extensions(extensions).build();
        Node document = parser.parse(content == null ? "" : content);
        return renderer.render(document);
    }

    static class MyAttributeProvider implements AttributeProvider {

        @Override
        public void setAttributes(Node node, String s, Map<String, String> map) {
            if (node instanceof Image) {
                map.put("class", "md-image");
            }
            if (node instanceof TableBlock) {
                map.put("class", "table table-bordered");
            }
        }
    }
}
