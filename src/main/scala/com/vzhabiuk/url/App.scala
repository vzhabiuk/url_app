package com.vzhabiuk.url

import com.twitter.finatra._
import com.vzhabiuk.url.html.{CachedHtmlProvider, HtmlManagerImpl, JsoupHtmlProvider}

object App extends FinatraServer {
  register(UrlController(HtmlManagerImpl, CachedHtmlProvider(JsoupHtmlProvider)))
}
