package com.vzhabiuk.url.html

import java.util.concurrent.TimeUnit

import com.google.common.cache.{CacheBuilder, CacheLoader}
import org.jsoup.Jsoup

trait HtmlProvider {
  def getHtml(url: String): String

  def normalize(url: String): String = {
    if (!url.startsWith("http")) {
       "http://" + url
    } else {
      url
    }
  }
}

object JsoupHtmlProvider extends HtmlProvider {
   def getHtml(url: String): String = Jsoup.connect(normalize(url)).execute().body()
}

case class CachedHtmlProvider(delegate: HtmlProvider) extends HtmlProvider {
  val urlCache = CacheBuilder.newBuilder()
      .maximumSize(10000)
      .expireAfterWrite(3, TimeUnit.MINUTES)
      .build(
        new CacheLoader[String, String]() {
          override def load(url: String): String = delegate.getHtml(url)
        })

  def getHtml(url: String): String = {
    urlCache.get(url)
  }
}
