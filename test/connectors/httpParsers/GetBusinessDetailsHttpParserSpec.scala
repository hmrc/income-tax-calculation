/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package connectors.httpParsers

import models.{ErrorBodyModel, ErrorModel, ErrorsBodyModel}
import play.api.http.Status._
import testConstants.IncomeSourceDetailsTestConstants.{successResponse, testIncomeSourceDetailsModel}
import testUtils.TestSuite
import uk.gov.hmrc.http.HttpResponse

class GetBusinessDetailsHttpParserSpec extends TestSuite {

  val parser = GetBusinessDetailsHttpParser

  "GetBusinessDetailsHttpReads" should {
    "return a income sources details model" when {
      "DES returns 200" in {
        parser.GetBusinessDetailsHttpReads.read("GET", "url", successResponse) mustBe
          Right(testIncomeSourceDetailsModel)
      }
    }

    "return an error model" when {
      "DES returns service unavailable" in {
        val response =
          """
            |{
            |  "code": "SERVICE_UNAVAILABLE",
            |  "reason": "Dependent systems are currently not responding."
            |}
            |""".stripMargin

        parser.GetBusinessDetailsHttpReads.read("GET", "url", HttpResponse(SERVICE_UNAVAILABLE, response)) mustBe
          Left(ErrorModel(SERVICE_UNAVAILABLE, ErrorBodyModel("SERVICE_UNAVAILABLE", "Dependent systems are currently not responding.")))
      }

      "DES returns server error" in {
        val response =
          """
            |{
            |  "code": "SERVER_ERROR",
            |  "reason": "DES is currently experiencing problems that require live service intervention."
            |}
            |""".stripMargin

        parser.GetBusinessDetailsHttpReads.read("GET", "url", HttpResponse(INTERNAL_SERVER_ERROR, response)) mustBe
          Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("SERVER_ERROR", "DES is currently experiencing problems that require live service intervention.")))
      }

      "DES returns bad request" in {
        val response =
          """
            |{
            |  "code": "INVALID_NINO",
            |  "reason": "Submission has not passed validation. Invalid parameter NINO."
            |}
            |""".stripMargin

        parser.GetBusinessDetailsHttpReads.read("GET", "url", HttpResponse(BAD_REQUEST, response)) mustBe
          Left(ErrorModel(BAD_REQUEST, ErrorBodyModel("INVALID_NINO", "Submission has not passed validation. Invalid parameter NINO.")))
      }

      "DES returns conflict" in {
        val response =
          """
            |{
            |  "code": "CONFLICT",
            |  "reason": "The remote endpoint has indicated that final declaration has already been received"
            |}
            |""".stripMargin

        parser.GetBusinessDetailsHttpReads.read("GET", "url", HttpResponse(CONFLICT, response)) mustBe
          Left(ErrorModel(CONFLICT, ErrorBodyModel("CONFLICT", "The remote endpoint has indicated that final declaration has already been received")))
      }

      "DES returns UNPROCESSABLE_ENTITY" in {
        val response =
          """
            |{
            |  "code": "UNPROCESSABLE_ENTITY",
            |  "reason": "The remote endpoint has indicated that crystallisation can not occur until after the end of tax year."
            |}
            |""".stripMargin

        parser.GetBusinessDetailsHttpReads.read("GET", "url", HttpResponse(UNPROCESSABLE_ENTITY, response)) mustBe
          Left(ErrorModel(UNPROCESSABLE_ENTITY, ErrorBodyModel("UNPROCESSABLE_ENTITY", "The remote endpoint has indicated that crystallisation can not occur until after the end of tax year.")))
      }

      "DES returns FORBIDDEN" in {
        val response =
          """
            |{
            |  "code": "FORBIDDEN",
            |  "reason": "The remote endpoint has indicated that no income submissions exist"
            |}
            |""".stripMargin

        parser.GetBusinessDetailsHttpReads.read("GET", "url", HttpResponse(FORBIDDEN, response)) mustBe
          Left(ErrorModel(FORBIDDEN, ErrorBodyModel("FORBIDDEN", "The remote endpoint has indicated that no income submissions exist")))
      }

      "DES returns an unexpected error response" in {
        val response =
          """
            |{
            |  "code": "IM_A_TEAPOT",
            |  "reason": "The remote endpoint has indicated that I'm a teapot"
            |}
            |""".stripMargin

        parser.GetBusinessDetailsHttpReads.read("GET", "url", HttpResponse(IM_A_TEAPOT, response)) mustBe
          Left(ErrorModel(IM_A_TEAPOT, ErrorBodyModel("IM_A_TEAPOT", "The remote endpoint has indicated that I'm a teapot")))
      }

      "DES returns an unexpected error body" in {
        val response =
          """
            |{
            |  "unexpected": "error"
            |}
            |""".stripMargin

        parser.GetBusinessDetailsHttpReads.read("GET", "url", HttpResponse(INTERNAL_SERVER_ERROR, response)) mustBe
          Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel.parsingError("")))
      }

      "DES returns invalid Json for 200" in {
        val response =
          """
            |{
            |  "bad": "bad"
            |}
            |""".stripMargin

        parser.GetBusinessDetailsHttpReads.read("GET", "url", HttpResponse(OK, response)) mustBe
          Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("PARSING_ERROR",
            "Error parsing response from API - List((/processingDate,List(JsonValidationError(List(error.path.missing),List()))), (/taxPayerDisplayResponse,List(JsonValidationError(List(error.path.missing),List()))))")))
      }

      "DES returns a bad json body" in {
        val response =
          """
            |{
            |""".stripMargin

        parser.GetBusinessDetailsHttpReads.read("GET", "url", HttpResponse(INTERNAL_SERVER_ERROR, response)) mustBe
          Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("PARSING_ERROR", "Error parsing response from API")))
      }
    }
  }
}
