package com.vzhabiuk.url.html

import org.apache.commons.lang3.StringEscapeUtils
import org.jsoup.Jsoup

trait HtmlManager {
  def getTagFrequencies(html: String): Seq[(String, Int)]
  def escapeHtml(html: String): String
  def highlightTag(escapedHtml: String, tagName: String): String
  def wrapWithHtmlTags(content: String): String
}

object HtmlManagerImpl extends HtmlManager {

  def getTagFrequencies(html: String): Seq[(String, Int)] = {
    val iterator = Jsoup.parse(html).getAllElements.iterator()
    val frequencyMap = collection.mutable.Map[String, Int]()
    while (iterator.hasNext) {
      val tagName = iterator.next().tagName()
      if (!tagName.contains("#")) {
        frequencyMap.put(tagName, frequencyMap.getOrElse(tagName, 0) + 1)
      }
    }
    frequencyMap.toSeq.sortBy(-1 * _._2)
  }

  def escapeHtml(html: String): String = StringEscapeUtils.escapeHtml4(html)

  def wrapWithHtmlTags(content: String): String = {
    "<html><body>" +
        content +
    "</html></body>"
  }

  def highlightTag(escapedHtml: String, tagName: String): String = {
    escapedHtml.replaceAll(s"&lt;${tagName}&lt; ", s"<mark>&lt;${tagName}&lt;</mark> ")
        .replaceAll(s"&lt;/${tagName}&gt;", s"<mark>&lt;/${tagName}&gt;</mark>")
        .replaceAll(s"&lt;${tagName}/&gt;", s"<mark>&lt;${tagName}/&gt;</mark>")
  }
}
