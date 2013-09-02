package com.myassets.domain

import com.myassets.db.DBModule

case class Blog(id: Long, title: String, tags: Seq[String], thumbnail: String, content: String, lastUpdated: Long)

trait DomainModule { this: DBModule =>
  val blogService: BlogService

  trait BlogService {
    def get(id: Long): Blog
    def recent(start: Int, size: Int): Seq[Blog]
    def find(tag: String, start: Int, size: Int): Seq[Blog]
    def insert(blog: Blog): Unit
  }
}