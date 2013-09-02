package com.myassets.test

import com.myassets.db.MysqlModule
import com.myassets.domain.{Blog, AnormDomainModule}

object Test {
  def main(args: Array[String]): Unit = {
    val module = new AnormDomainModule with MysqlModule {
      val db = new MysqlDB("jdbc:mysql://localhost/myassets", "root", "")
    }
    val blogService = module.blogService
//    for (i <- 0 until 1000) {
//      val blog = Blog(-1L, "title " + i, Seq("tag-" + (i%5)), "thumbnail", "conent for " + i, 0L)
//      blogService.insert(blog)
//    }

    blogService.recent(0, 10) foreach println

    module.db.close
  }
}