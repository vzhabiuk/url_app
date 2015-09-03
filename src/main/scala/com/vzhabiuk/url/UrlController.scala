package com.vzhabiuk.url

import java.net.URI

import com.twitter.finatra.Controller
import com.vzhabiuk.url.html.{HtmlManager, HtmlProvider}

case class UrlController(htmlManager: HtmlManager, htmlProvider: HtmlProvider) extends Controller {

  get("/") { request =>
    render.static("index.html").toFuture
  }

  post("/tokens.json") { request =>
    try {
      val url = request.request.getParam("url")
      if (url.isEmpty) {
        throw new IllegalStateException("Url shouldn't be empty")
      }


      val tokens = htmlManager.getTagFrequencies(htmlProvider.getHtml(url))
      val jsonMap = Map(
        "iTotalDisplayRecords" -> tokens.size,
        "iTotalRecords" -> tokens.size,
        //"iDisplayLength" -> 50,
        "aaData" -> tokens.map { case (name, frequency) =>
          Map("elementName" -> tagNameToLink(url, name), "frequency" -> frequency)
        }.toList
      )
      render.json(jsonMap).toFuture
    } catch {
      case ex: Throwable =>
        ex.printStackTrace()
        throw ex
    }
  }

  def tagNameToLink(url: String, tagName: String): String = {
    val tagUrl = "/highlight?url=" + new URI(null, url, null).toString + "&tag=" + tagName
    """<a href="%s">%s</a>""".format(tagUrl, tagName)
  }

  get("/highlight") { request =>
    val rawUrl = request.request.getParam("url")
    val url = new URI(rawUrl).toURL.toString
    val tag = request.request.getParam("tag")
    val escapedHtml = htmlManager.escapeHtml(htmlProvider.getHtml(url))
    render.html(htmlManager.highlightTag(escapedHtml, tag)).toFuture
  }
}
