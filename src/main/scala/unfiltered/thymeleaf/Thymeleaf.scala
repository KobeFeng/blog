package unfiltered.thymeleaf

import collection.JavaConverters._
import unfiltered.request.HttpRequest
import unfiltered.response.{ResponseWriter}
import java.io.{File, PrintWriter, OutputStreamWriter}
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.Locale
import org.thymeleaf.templateresolver.TemplateResolver
import org.thymeleaf.resourceresolver.FileResourceResolver
import nz.net.ultraq.thymeleaf.LayoutDialect

case class RenderContext(engine: TemplateEngine, extraAttributes: Seq[(String, Any)], isDevelopmentMode: Boolean)

object Thymeleaf {
  /** Constructs a ResponseWriter for thymeleaf templates.
    *  Note that any parameter in the second, implicit set
    *  can be overriden by specifying an implicit value of the
    *  expected type in a pariticular scope. */
  def apply[A, B](request: HttpRequest[A],
                  template: String,
                  attributes:(String,Any)*)
                 (implicit context: RenderContext = defaultRenderContext) = new ResponseWriter {
    def write(writer: OutputStreamWriter) {
      val printWriter = new PrintWriter(writer)
      try {
        context.engine.process(template, buildContext(attributes ++ context.extraAttributes), printWriter)
      } catch {
        case e if context.isDevelopmentMode =>
          printWriter.println("Exception: " + e.getMessage)
          e.getStackTrace.foreach(printWriter.println)
        case e: Throwable => throw e
      }
    }
  }

  @inline
  private def buildContext(attributes: Seq[(String, Any)]): Context = {
    val attes = attributes.map {
      case (key, value: Map[_, _]) => (key, value.asJava)
      case (key, value: Iterable[_]) => (key, value.asJava)
      case (key, value) => (key, value)
    }
    new Context(Locale.getDefault, attes.toMap.asJava)
  }

  private val defaultRenderContext = RenderContext(buildDefaultEngine("www"), Nil, true)

  def buildDefaultEngine(dir: String): TemplateEngine = {
    val templateResolver = new TemplateResolver
    templateResolver.setTemplateMode("LEGACYHTML5")
    // This will convert "home" to "/WEB-INF/templates/home.html"

    templateResolver.setPrefix(new File(dir).getAbsolutePath + "/")
    templateResolver.setSuffix(".html")
    // Set template cache TTL to 1 hour. If not set, entries would live in cache until expelled by LRU
//    templateResolver.setCacheTTLMs(3600000L)
    templateResolver.setCacheable(false)
    templateResolver.setResourceResolver(new FileResourceResolver)
    val templateEngine = new TemplateEngine
    templateEngine.setTemplateResolver(templateResolver)
    templateEngine.addDialect(new LayoutDialect)
    templateEngine
  }
}