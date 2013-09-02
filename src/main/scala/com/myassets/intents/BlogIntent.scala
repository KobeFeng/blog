package com.myassets.intents

import unfiltered.request._
import unfiltered.response._
import com.myassets.domain.DomainModule
import unfiltered.thymeleaf.Thymeleaf
import unfiltered.netty._
import unfiltered.response.Redirect
import unfiltered.thymeleaf.RenderContext
import com.myassets.domain.Blog

class BlogIntent(module: DomainModule)(implicit renderContext: RenderContext) {
  val all = async.Intent {
    case req @ GET(Path("/blog")) =>
      println("#### in list")
      req.respond(HtmlContent ~> Thymeleaf(req, "views/list", "blogs" -> module.blogService.recent(0, 10)))

    case req @ GET(Path("/blog/edit")) =>
      println("##### in edit")
      req.respond(HtmlContent ~> Thymeleaf(req, "views/edit"))

    case req @ POST(Path("/blog/edit")) => {
      object Title extends Params.Extract("title", Params.first)
      object Tags extends Params.Extract("tags", Params.first)
      object Content extends Params.Extract("content", Params.first)
      req match {
        case Params(Title(title) & Tags(tags) & Content(content)) => {
          println("###################### post")
          println(title)
          println(tags)
          println(content)
          module.blogService.insert(Blog(-1L, title, tags.split(","), "", content, 0L))
          req.respond(Redirect("/blog"))
        }
        case _ => req.respond(Redirect("/blog"))
      }
    }

    case req @ GET(Path(Seg("blog" :: id :: Nil))) =>
      println("###### in view")
      req.respond(HtmlContent ~> Thymeleaf(req, "views/view", "blog" -> module.blogService.get(id.toLong)))
  }
}