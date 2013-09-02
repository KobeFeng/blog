package com.myassets

import unfiltered.request._
import unfiltered.response._
import com.myassets.domain.AnormDomainModule
import com.myassets.db.MysqlModule
import unfiltered.thymeleaf.Thymeleaf
import unfiltered.netty.async.Plan._
import unfiltered.thymeleaf.RenderContext
import unfiltered.kit.GZip
import com.myassets.intents.BlogIntent
import unfiltered.netty.Resources
import java.io.File

object Server {
  def main(args: Array[String]): Unit = {
    val config = ConfigUtil.load("app.conf", "app.test.conf", "app.local.conf")

    val module = new AnormDomainModule with MysqlModule {
      val db = new MysqlDB(config.getString("db.url"), config.getString("db.user"), config.getString("db.password"))
    }

    implicit val renderContext = RenderContext(Thymeleaf.buildDefaultEngine(config.getString("assets.templateDir")), Seq("resouceBase" -> config.getString("assets.resouceBase")), config.getBoolean("developmentMode"))

    val blogIntents = new BlogIntent(module)
    val server = unfiltered.netty.Http(8080)
      .resources(new File(config.getString("assets.wwwDir")).toURL)
      .chunked(5242880).plan(planify(blogIntents.all))
    server.run
  }

  // TODO: Make async plan with directive DSL
  def planify(head: Intent, tail: Intent*) = unfiltered.netty.async.Planify {
    GZip.async {
      tail.fold(head) { _ onPass _ }
    }
  }
}