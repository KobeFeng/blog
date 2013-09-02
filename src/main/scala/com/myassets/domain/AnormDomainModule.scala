package com.myassets.domain

import com.myassets.db.anorm._
import java.util.Date
import com.myassets.db.DBModule

trait AnormDomainModule extends DomainModule { this: DBModule =>
  val blogService = new BlogServiceImpl

  class BlogServiceImpl extends BlogService {
    def get(id: Long): Blog = {
      db.withConnection { implicit conn =>
        SQL("select id, title, thumbnail, content, last_updated from blog where id={id}").onParams(id)()
          .collect {
          case Row(id: Long, title: String, thumbnail: Option[String], content: String, last_updated: Date) => Blog(id, title, Seq(), thumbnail.getOrElse(""), content, last_updated.getTime)
        }.head
      }
    }

    def recent(start: Int, size: Int): Seq[Blog] = {
      val blogs = db.withConnection { implicit conn =>
        SQL("select id, title, thumbnail, content, last_updated from blog order by id desc limit {start},{size}").onParams(start, size)()
          .collect {
          case Row(id: Long, title: String, thumbnail: Option[String], content: String, last_updated: Date) => Blog(id, title, Seq(), thumbnail.getOrElse(""), content, last_updated.getTime)
        }.toSeq
      }
      println("======== total : " + blogs.size)
      blogs
    }

    def find(tag: String, start: Int, size: Int): Seq[Blog] = {
      Seq()
    }

    def insert(blog: Blog): Unit = {
      val insertQuery = SQL("insert into blog(title,thumbnail,content) values({title},{thumbnail},{content})")
      val insertTags = SQL("insert ignore into blog_tag(blog_id, tag) values({blogId}, {tag})")
      db.withConnection { implicit conn =>
        val id = insertQuery.onParams(blog.title, blog.thumbnail, blog.content).executeInsert()
        val batchInsert = (insertTags.asBatch /: blog.tags) {
          (sql, tag) => sql.addBatchParams(id, tag)
        }
        batchInsert.execute()
      }
    }
  }
}