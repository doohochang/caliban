package caliban.interop.play

import caliban._
import caliban.Macros.gqldoc
import caliban.schema.Schema.auto._
import zio.test._

object ExecutionSpec extends ZIOSpecDefault {

  override def spec =
    suite("Play ExecutionSpec")(
      test("Play Json scalar") {
        import caliban.interop.play.json._
        import play.api.libs.json._
        case class Queries(test: JsValue)

        val interpreter = graphQL(RootResolver(Queries(Json.obj(("a", JsNumber(333)))))).interpreter
        val query       = gqldoc("""
             {
               test
             }""")

        interpreter.flatMap(_.execute(query)).map { response =>
          assertTrue(response.data.toString == """{"test":{"a":333}}""")
        }
      }
    )
}
