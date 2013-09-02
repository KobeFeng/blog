package com.myassets.db

import java.sql.Connection
import com.jolbox.bonecp.BoneCPDataSource

trait MysqlModule extends DBModule {
  val db: DB

  class MysqlDB(url: String, user: String, password: String) extends DB {
    val datasouce = init

    def withConnection[T](query: Connection => T): T = {
      val conn = datasouce.getConnection
      try {
        query(conn)
      } finally {
        conn.close
      }
    }

    private def init(): BoneCPDataSource = {
      Class.forName("com.mysql.jdbc.Driver") 	// load the DB driver
      val ds = new BoneCPDataSource
      ds.setJdbcUrl(url)
      ds.setUser(user)
      ds.setPassword(password)
      ds.setDefaultAutoCommit(true)
      ds
    }

    def close(): Unit = datasouce.close
  }
}