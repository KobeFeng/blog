package com.myassets.db

import java.sql.Connection

trait DBModule {
  val db: DB

  trait DB {
    def withConnection[T](query: Connection => T): T
    def close(): Unit
  }
}