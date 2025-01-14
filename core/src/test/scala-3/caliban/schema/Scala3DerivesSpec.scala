package caliban.schema

import caliban._
import caliban.RootResolver
import zio.test.{ assertTrue, ZIOSpecDefault }

object Scala3DerivesSpec extends ZIOSpecDefault {

  override def spec = suite("Scala3DerivesSpec") {

    val expected =
      """schema {
        |  query: Bar
        |}

        |type Bar {
        |  foo: Foo!
        |}

        |type Foo {
        |  value: String!
        |}""".stripMargin

    List(
      test("SemiAuto derivation - default") {
        final case class Foo(value: String) derives Schema.SemiAuto
        final case class Bar(foo: Foo) derives Schema.SemiAuto

        val gql = graphQL(RootResolver(Bar(Foo("foo"))))

        assertTrue(gql.render == expected)
      },
      test("Auto derivation - default") {
        final case class Foo(value: String)
        final case class Bar(foo: Foo) derives Schema.Auto

        val gql = graphQL(RootResolver(Bar(Foo("foo"))))

        assertTrue(gql.render == expected)
      },
      test("Auto derivation - custom R") {
        class Env
        object CustomSchema extends SchemaDerivation[Env]
        final case class Foo(value: String)
        final case class Bar(foo: Foo) derives CustomSchema.Auto

        val gql = graphQL(RootResolver(Bar(Foo("foo"))))

        assertTrue(gql.render == expected)
      },
      test("SemiAuto derivation - custom R") {
        class Env
        object CustomSchema extends SchemaDerivation[Env]
        final case class Foo(value: String) derives CustomSchema.SemiAuto
        final case class Bar(foo: Foo) derives CustomSchema.SemiAuto

        val gql = graphQL(RootResolver(Bar(Foo("foo"))))

        assertTrue(gql.render == expected)
      },
      suite("ArgBuilder derivation") {
        val expected =
          """schema {
            |  query: Query
            |}

            |type Bar {
            |  foo: Foo!
            |}

            |type Foo {
            |  s: String!
            |}

            |type Query {
            |  f(s: String!): Bar!
            |}""".stripMargin

        List(
          test("SemiAuto") {
            final case class Foo(s: String) derives Schema.SemiAuto, ArgBuilder
            final case class Bar(foo: Foo) derives Schema.SemiAuto
            final case class Query(f: Foo => Bar) derives Schema.SemiAuto

            val gql = graphQL(RootResolver(Query(Bar(_))))

            assertTrue(gql.render == expected)
          },
          test("Auto") {
            final case class Foo(s: String) derives Schema.Auto, ArgBuilder.GenAuto
            final case class Bar(foo: Foo) derives Schema.SemiAuto
            final case class Query(f: Foo => Bar) derives Schema.SemiAuto

            val gql = graphQL(RootResolver(Query(Bar(_))))

            assertTrue(gql.render == expected)
          }
        )
      }
    )
  }
}
