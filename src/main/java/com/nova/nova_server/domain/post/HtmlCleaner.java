package com.nova.nova_server.domain.post;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlCleaner {
    public static String getTextFromHtml(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        Document doc = Jsoup.parse(html);

        // 제거하고 싶은 태그 선택하여 삭제, 일단 <pre>, <code>, <script>, <style> 태그만
        doc.select("pre").remove(); // 긴 코드 블럭
        doc.select("code").remove(); // 인라인 코드
        doc.select("script").remove(); // 자바스크립트
        doc.select("style").remove(); // 스타일 시트
        doc.select("img").remove(); // 이미지

        // 남은 태그 안에서 순수 텍스트만 추출
        return doc.text();
    }
}
