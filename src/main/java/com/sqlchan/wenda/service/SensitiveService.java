package com.sqlchan.wenda.service;

import com.sqlchan.wenda.controller.IndexController;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/29.
 */
@Service
public class SensitiveService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
    private static final String DEFAULT_REPLACEMENT = "**";

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                lineTxt = lineTxt.trim();
                addword(lineTxt);
            }
            read.close();
        } catch (Exception e) {
            logger.error("duqu mingganci shibai");
        }
    }

    //abc构造一棵树/增加关键词
    private void addword(String linetext) {
        TrieNode tempnode = rootnode;
        for (int i = 0; i < linetext.length(); i++) {
            Character c = linetext.charAt(i);
            if (isSymbol(c)) {
                continue;
            }

            TrieNode node = tempnode.getSubNode(c);

            if (node == null) {
                node = new TrieNode();
                tempnode.addSubNode(c, node);
            }

            tempnode = node;

            if (i == linetext.length() - 1) {
                tempnode.setKeyWordEnd(true);
            }
        }
    }

    //判断是否是一个符号
    private boolean isSymbol(char c) {
        int ic = (int) c;
        // 0x2E80-0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    private class TrieNode {
        //是不是关键词的结尾
        private boolean end = false;
        //当前节点下的所有子节点
        private Map<Character, TrieNode> subNodes = new HashedMap();

        public void addSubNode(Character key, TrieNode node) {
            subNodes.put(key, node);
        }

        //获取下个节点
        TrieNode getSubNode(Character key) {
            return subNodes.get(key);
        }

        boolean isKeyWordEnd() {

            return end;
        }

        void setKeyWordEnd(boolean end) {
            this.end = end;
        }

    }

    private TrieNode rootnode = new TrieNode();

    public String filter(String text) {

        if (StringUtils.isBlank(text)) { return text; }

        String replacement = DEFAULT_REPLACEMENT;
        StringBuilder result = new StringBuilder();

        TrieNode tempNode = rootnode;
        int begin = 0; // 回滚数
        int position = 0; // 当前比较的位置

        while (position < text.length()) {

            char c = text.charAt(position);
            // 空格直接跳过
            if (isSymbol(c)) {
                if (tempNode == rootnode) {
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }

            tempNode = tempNode.getSubNode(c);

            // 当前位置的匹配结束
            if (tempNode == null) {
                // 以begin开始的字符串不存在敏感词
                result.append(text.charAt(begin));
                // 跳到下一个字符开始测试
                position = begin + 1;
                begin = position;
                // 回到树初始节点
                tempNode = rootnode;
            } else if (tempNode.isKeyWordEnd()) {
                // 发现敏感词， 从begin到position的位置用replacement替换掉
                result.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootnode;
            } else {
                ++position;
            }
        }

        result.append(text.substring(begin));

        return result.toString();
    }
//    public static void main(String[] argv) {
//        SensitiveService s = new SensitiveService();
//        s.addword("色情");
//        s.addword("好色");
//        System.out.print(s.filter("你好色情狂"));
//    }
}
