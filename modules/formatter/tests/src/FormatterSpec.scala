/* Copyright 2022 Disney Streaming
 *
 * Licensed under the Tomorrow Open Source Technology License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://disneystreaming.github.io/TOST-1.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package smithytranslate
package formatter

import smithytranslate.formatter.parsers.IdlParser
import smithytranslate.formatter.writers.IdlWriter.idlWriter
import smithytranslate.formatter.writers.Writer._
import munit.Location

final class FormatterSpec extends munit.FunSuite {
  private def formatTest(src: String, expected: String)(implicit
      loc: Location
  ) = {
    IdlParser.idlParser
      .parseAll(src)
      .fold(
        err => fail(s"Parsing failed with error: $err"),
        idl => {
          assertEquals(
            idl.write,
            expected
          )
        }
      )
  }

  test("w/ trailing newline") {
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("w/o trailing newline w/o shape".ignore) {
    val src = """|$version: "2.0"
                 |
                 |namespace test""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("w/o trailing newline w/ shape") {
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |
                 |string Value""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |string Value
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("w/o trailing newline w/ shape with whitespace") {
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |
                 |string Value  """.stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |string Value
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("control - 0") {
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("control - 1") {
    val src = """|$version: "2.0" //comment
                 |
                 |namespace test
                 |""".stripMargin
    val expected = """|$version: "2.0" // comment
                      |
                      |namespace test
                      |
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("control - 2") {
    val src = """|$version: "2.0" /// comment
                 |
                 |namespace test
                 |""".stripMargin
    val expected = """|$version: "2.0" /// comment
                      |
                      |namespace test
                      |
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("control - 3") {
    val src = """|/// triple comment
                 |/// on multiline
                 |$version: "2.0"
                 |
                 |namespace test
                 |""".stripMargin
    val expected = """|/// triple comment
                      |/// on multiline
                      |$version: "2.0"
                      |
                      |namespace test
                      |
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("control - 4") {
    val src = """|// double comment
                 |// on multiline
                 |$version: "2.0"
                 |
                 |namespace test
                 |
                 |""".stripMargin
    val expected = """|// double comment
                      |// on multiline
                      |$version: "2.0"
                      |
                      |namespace test
                      |
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("control - 5") {
    val src = """|// double comment
                 |/// and triple
                 |// on multiline
                 |$version: "2.0"
                 |
                 |namespace test
                 |""".stripMargin
    val expected = """|// double comment
                      |/// and triple
                      |// on multiline
                      |$version: "2.0"
                      |
                      |namespace test
                      |
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("format test - keep new line after control section") {
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |
                 |use alloy#uuidFormat
                 |
                 |long TargetingRuleId
                 |""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |use alloy#uuidFormat
                      |
                      |long TargetingRuleId
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("format test - handles triple comments") {
    // this test is intended to catch instances where
    // the line after a triple comment is moved onto the comment
    // line. this example produces the following:
    //  `/// Used to identify the disney platformstring PartnerName`
    // this only happes if `long TargetingRuleId` is there
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |
                 |use dslib#uuidFormat
                 |
                 |long TargetingRuleId
                 |
                 |/// Used to identify the disney platform
                 |string PartnerName
                 |
                 |integer FeatureVersion
                 |""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |use dslib#uuidFormat
                      |
                      |long TargetingRuleId
                      |
                      |/// Used to identify the disney platform
                      |string PartnerName
                      |
                      |integer FeatureVersion
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("format test - newlines between each shape") {
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |
                 |use dslib#uuidFormat
                 |
                 |long TargetingRuleId
                 |
                 |string PartnerName
                 |
                 |integer FeatureVersion
                 |""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |use dslib#uuidFormat
                      |
                      |long TargetingRuleId
                      |
                      |string PartnerName
                      |
                      |integer FeatureVersion
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("format test - comment on traits") {
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |
                 |/// comment between trait and shape
                 |@documentation("this is a test")
                 |string PartnerName
                 |
                 |integer FeatureVersion
                 |""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |/// comment between trait and shape
                      |@documentation("this is a test")
                      |string PartnerName
                      |
                      |integer FeatureVersion
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("format test - comment between trait and shape") {
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |
                 |@documentation("this is a test")
                 |/// comment between trait and shape
                 |string PartnerName
                 |
                 |integer FeatureVersion
                 |""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |@documentation("this is a test")
                      |/// comment between trait and shape
                      |string PartnerName
                      |
                      |integer FeatureVersion
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("format test - comments between trait and shape") {
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |
                 |@documentation("this is a test")
                 |/// comment between trait and shape
                 |///two comments?
                 |string PartnerName
                 |
                 |integer FeatureVersion
                 |""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |@documentation("this is a test")
                      |/// comment between trait and shape
                      |/// two comments?
                      |string PartnerName
                      |
                      |integer FeatureVersion
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("format test - enum") {
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |
                 |enum MyEnum {
                 |  // comment 1
                 |  VALUE1,
                 |  /// comment 2
                 |    VALUE2
                 |}
                 |enum OtherEnum {
                 |  /// comment 1
                 |  V1 = "v1"
                 |//comment 2
                 |V2 = "v2"
                 |}
                 |
                 |@enum([
                 |    /// Invalid!
                 |    { name: "X", value: "X"}
                 |]) string Features
                 |""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |enum MyEnum {
                      |    // comment 1
                      |    VALUE1
                      |    /// comment 2
                      |    VALUE2
                      |}
                      |
                      |enum OtherEnum {
                      |    /// comment 1
                      |    V1 = "v1"
                      |    // comment 2
                      |    V2 = "v2"
                      |}
                      |
                      |@enum([
                      |    /// Invalid!
                      |    {
                      |        name: "X"
                      |        value: "X"
                      |    }
                      |])
                      |string Features
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("format test - structure with commented members") {
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |
                 |structure MyStruct {
                 |  @doc("data")
                 |  /// comment 1
                 |  this: String,
                 |  /// comment 2
                 |  that: Integer
                 |}
                 |
                 |structure MyStructDefault {
                 |  // some comment
                 |  other: Integer = 1
                 |}
                 |
                 |union MyUnion {
                 |    // some comment
                 | this: String,
                 |    /// some comment
                 |orThat: Integer
                 |}
                 |""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |structure MyStruct {
                      |    @doc("data")
                      |    /// comment 1
                      |    this: String
                      |    /// comment 2
                      |    that: Integer
                      |}
                      |
                      |structure MyStructDefault {
                      |    // some comment
                      |    other: Integer = 1
                      |}
                      |
                      |union MyUnion {
                      |    // some comment
                      |    this: String
                      |    /// some comment
                      |    orThat: Integer
                      |}
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("format test - identation on shape with body") {
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |
                 |structure MyStruct {
                 |this: String,
                 |  that: Integer
                 |}
                 |
                 |structure MyStructDefault {
                 |  other: Integer = 1
                 |}
                 |
                 |union MyUnion {
                 | this: String,
                 |orThat: Integer
                 |}
                 |""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |structure MyStruct {
                      |    this: String
                      |    that: Integer
                      |}
                      |
                      |structure MyStructDefault {
                      |    other: Integer = 1
                      |}
                      |
                      |union MyUnion {
                      |    this: String
                      |    orThat: Integer
                      |}
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("format test - map key/value") {
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |
                 |map Milestone {
                 |  // some doc
                 |  key: String,
                 |  // other doc
                 |  value: Milestone
                 |}
                 |""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |map Milestone {
                      |    // some doc
                      |    key: String
                      |    // other doc
                      |    value: Milestone
                      |}
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("format test - multiple mixins") {
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |
                 |structure Struct with [
                 |    Mixin
                 |    Mixin
                 |    Mixin
                 |    Mixin
                 |    Mixin
                 |    Mixin
                 |    Mixin
                 |] {}
                 |
                 |structure Struct with [
                 |    Mixin
                 |    Mixin
                 |] {}
                 |
                 |structure Struct with [
                 |    // Just one but w/ comment
                 |    Mixin
                 |] {}
                 |""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |structure Struct with [
                      |    Mixin
                      |    Mixin
                      |    Mixin
                      |    Mixin
                      |    Mixin
                      |    Mixin
                      |    Mixin
                      |] {}
                      |
                      |structure Struct with [Mixin, Mixin] {}
                      |
                      |structure Struct with [
                      |    // Just one but w/ comment
                      |    Mixin
                      |] {}
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("format test - regular triple dquote") {
    val tq = "\"\"\""
    val src = s"""|$$version: "2.0"
                  |
                  |namespace test
                  |
                  |@documentation(${tq}
                  |  a
                  |  "b
                  |  ""c
                  |${tq})
                  |string Value
                  |""".stripMargin
    val expected = s"""|$$version: "2.0"
                       |
                       |namespace test
                       |
                       |@documentation(${tq}
                       |  a
                       |  "b
                       |  ""c
                       |${tq})
                       |string Value
                       |""".stripMargin
    formatTest(src, expected)
  }

  test("format test - large documentation trait") {
    val tq = "\"\"\""
    val src = s"""|$$version: "2.0"
                  |
                  |namespace test
                  |
                  |@documentation(${tq}
                  |  value
                  |${tq})
                  |string Value
                  |
                  |@other(${tq}
                  |  value
                  |  is
                  |  multiline${tq})
                  |string Value
                  |""".stripMargin
    val expected = s"""|$$version: "2.0"
                       |
                       |namespace test
                       |
                       |@documentation(${tq}
                       |  value
                       |${tq})
                       |string Value
                       |
                       |@other(${tq}
                       |  value
                       |  is
                       |  multiline${tq})
                       |string Value
                       |""".stripMargin
    formatTest(src, expected)
  }

  test("format test - support empty structure") {
    val src = s"""|$$version: "2.0"
                  |
                  |namespace test
                  |
                  |operation Op {
                  |    input := { }
                  |    output := {
                  |
                  |    }
                  |    output := {
                  |      // empty for X reason
                  |    }
                  |}
                  |
                  |structure Empty {}
                  |""".stripMargin
    val expected = s"""|$$version: "2.0"
                       |
                       |namespace test
                       |
                       |operation Op {
                       |    input := {}
                       |    output := {}
                       |    output := {
                       |        // empty for X reason
                       |    }
                       |}
                       |
                       |structure Empty {}
                       |""".stripMargin
    formatTest(src, expected)
  }

  test("#121 - comment in empty structure") {
    val src = """|$version: "2"
                 |
                 |namespace demo
                 |
                 |structure MyOpOutput {
                 |    //hello
                 |}
                 |""".stripMargin
    val expected = """|$version: "2"
                      |
                      |namespace demo
                      |
                      |structure MyOpOutput {
                      |    // hello
                      |}
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("format test - support inline structure") {
    val src = s"""|$$version: "2.0"
                  |
                  |namespace test
                  |
                  |operation Op {
                  |  input: ShapeId
                  |  output: ShapeId
                  |}
                  |
                  |operation Op {
                  |  input := {
                  |    value: String
                  |  }
                  |  output := {
                  |    value: String
                  |  }
                  |}
                  |
                  |operation Op {
                  |  input := @someTrait {
                  |    value: String
                  |  }
                  |
                  |  output := with [Mixin] {
                  |    value: String
                  |  }
                  |}
                  |
                  |operation Op {
                  |    input := @someTrait with [Mixin] {
                  |        value: String
                  |    }
                  |}
                  |""".stripMargin
    val expected = s"""|$$version: "2.0"
                       |
                       |namespace test
                       |
                       |operation Op {
                       |    input: ShapeId
                       |    output: ShapeId
                       |}
                       |
                       |operation Op {
                       |    input := {
                       |        value: String
                       |    }
                       |    output := {
                       |        value: String
                       |    }
                       |}
                       |
                       |operation Op {
                       |    input := @someTrait
                       |     {
                       |        value: String
                       |    }
                       |    output := with [Mixin] {
                       |        value: String
                       |    }
                       |}
                       |
                       |operation Op {
                       |    input := @someTrait
                       |     with [Mixin] {
                       |        value: String
                       |    }
                       |}
                       |""".stripMargin
    formatTest(src, expected)
  }

  test("format test - support node object in service/resource") {
    val src = """|$version: "2.0"
                |
                |namespace test
                |
                |resource SubscriberResource {
                |    read: GetSubscriber
                |}
                |service Service {
                |  version: "2"
                |}
                |service Service { }
                |""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |resource SubscriberResource {
                      |    read: GetSubscriber
                      |}
                      |
                      |service Service {
                      |    version: "2"
                      |}
                      |
                      |service Service {
                      |
                      |}
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("format test - number") {
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |
                 |structure Value {
                 |  @range(a: -1.0, b: 2, c: 1E10, d: -1.12E-10)
                 |  a: Int
                 |}
                 |""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |structure Value {
                      |    @range(a: -1.0, b: 2, c: 1E10, d: -1.12E-10)
                      |    a: Int
                      |}
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("format test - node value formatting") {
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |
                 |service MyService {
                 |  operations: [A, B, C]
                 |  operations: [
                 |    // comment 1
                 |    A
                 |    /// comment 2
                 |    B
                 |  ]
                 |  errors: [E1, E2]
                 |    errors: [E3]
                 |}
                 |
                 |operation SomeOp {
                 |    errors: [E1, E2, E3]
                 |}
                 |
                 |operation SomeOp {
                 |    errors: [
                 |      // one comment
                 |      E1,
                 |      E2
                 |    ]
                 |}
                 |operation SomeOp {
                 |    errors: [E1, E2]
                 |}
                 |operation SomeOp {
                 |    errors: []
                 |}
                 |""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |service MyService {
                      |    operations: [
                      |        A
                      |        B
                      |        C
                      |    ]
                      |    operations: [
                      |        // comment 1
                      |        A
                      |        /// comment 2
                      |        B
                      |    ]
                      |    errors: [E1, E2]
                      |    errors: [E3]
                      |}
                      |
                      |operation SomeOp {
                      |    errors: [
                      |        E1
                      |        E2
                      |        E3
                      |    ]
                      |}
                      |
                      |operation SomeOp {
                      |    errors: [
                      |        // one comment
                      |        E1
                      |        E2
                      |    ]
                      |}
                      |
                      |operation SomeOp {
                      |    errors: [E1, E2]
                      |}
                      |
                      |operation SomeOp {
                      |    errors: []
                      |}
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("format test - apply formatting") {
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |
                 |apply Foo$baz {
                 |    @documentation("Hi")
                 |    @internal
                 |    @deprecated
                 |}
                 |""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |apply Foo$baz {
                      |    @documentation("Hi")
                      |    @internal
                      |    @deprecated
                      |}
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("structure with for resource") {
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |
                 |resource MyResource {
                 |    identifiers: {
                 |        id: String
                 |    }
                 |}
                 |
                 |structure MyResourceIdentifiers for MyResource {
                 |    $id
                 |    value: SomeMember$list
                 |}
                 |""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |resource MyResource {
                      |    identifiers: {
                      |        id: String
                      |    }
                      |}
                      |
                      |structure MyResourceIdentifiers for MyResource {
                      |    $id
                      |    value: SomeMember$list
                      |}
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("elided member") {
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |
                 |list MixedList with [MixinList] {
                 |  $member
                 |}
                 |
                 |map MixedMap with [MixinMap] {
                 |  $key
                 |  $value
                 |}
                 |
                 |map MixedMap with [MixinMap] {
                 |  value: String
                 |}""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |list MixedList with [MixinList] {
                      |    $member
                      |}
                      |
                      |map MixedMap with [MixinMap] {
                      |    $key
                      |    $value
                      |}
                      |
                      |map MixedMap with [MixinMap] {
                      |    value: String
                      |}
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("sort use statements") {
    val src = """|$version: "2.0"
                 |
                 |namespace test
                 |
                 |use a#a
                 |use b#c
                 |use b#a
                 |use a#b
                 |use a#b
                 |""".stripMargin
    val expected = """|$version: "2.0"
                      |
                      |namespace test
                      |
                      |use a#a
                      |use a#b
                      |use a#b
                      |use b#a
                      |use b#c
                      |
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("#119 no trailing - metadata w/ multiple comments") {
    val src = """|$version: "2"
                 |
                 |metadata foo = ["bar"] // comm1
                 |metadata f_o_o = ["baz"]//comm2
                 |""".stripMargin
    val expected = """|$version: "2"
                      |
                      |metadata foo = ["bar"] // comm1
                      |metadata f_o_o = ["baz"] // comm2
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("#119 no trailing - metadata w/ comment") {
    val src = """|$version: "2"
                 |
                 |metadata foo = ["bar"] // some test
                 |""".stripMargin
    val expected = """|$version: "2"
                      |
                      |metadata foo = ["bar"] // some test
                      |""".stripMargin
    formatTest(src, expected)
  }

  test("#119 no trailing - metadata w/o comment") {
    val src = """|$version: "2"
                 |
                 |metadata foo = ["bar"]""".stripMargin
    val expected = """|$version: "2"
                      |
                      |metadata foo = ["bar"]""".stripMargin
    formatTest(src, expected)
  }

  test("#119 no trailing - metadata w/ trailing newline") {
    val src = """|$version: "2"
                 |
                 |metadata foo = ["bar"]
                 |""".stripMargin
    val expected = """|$version: "2"
                      |
                      |metadata foo = ["bar"]
                      |""".stripMargin
    formatTest(src, expected)
  }
}
