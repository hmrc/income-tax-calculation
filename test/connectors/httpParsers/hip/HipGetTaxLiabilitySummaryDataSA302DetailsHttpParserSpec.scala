/*
 * Copyright 2026 HM Revenue & Customs
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

package connectors.httpParsers.hip

import models.{ErrorBodyModel, ErrorModel}
import play.api.http.Status.*
import testConstants.hip.GetTaxLiabilitySummaryDataSA302TestConstants.{successModelFull, successResponse}
import testUtils.TestSuite
import uk.gov.hmrc.http.HttpResponse

class HipGetTaxLiabilitySummaryDataSA302DetailsHttpParserSpec extends TestSuite {

  val parser: HipGetTaxLiabilitySummaryDataSA302DetailsHttpParser.type = HipGetTaxLiabilitySummaryDataSA302DetailsHttpParser

  "GetTaxLiabilitySummaryDataSA302DetailsHttpReads" should {
    "return a tax liability summary data SA302 model" when {
      "HIP returns 200" in {
        parser.GetTaxLiabilitySummaryDataSA302DetailsHttpReads.read("GET", "url", successResponse) mustBe
          Right(successModelFull)
      }
    }

    "return an error model" when {
      "HIP returns service unavailable" in {
        val response =
          """
            |{
            |  "code": "SERVICE_UNAVAILABLE",
            |  "reason": "Dependent systems are currently not responding."
            |}
            |""".stripMargin

        parser.GetTaxLiabilitySummaryDataSA302DetailsHttpReads.read("GET", "url", HttpResponse(SERVICE_UNAVAILABLE, response)) mustBe
          Left(ErrorModel(SERVICE_UNAVAILABLE, ErrorBodyModel("SERVICE_UNAVAILABLE", "Dependent systems are currently not responding.")))
      }

      "HIP returns server error" in {
        val response =
          """
            |{
            |  "code": "SERVER_ERROR",
            |  "reason": "HIP is currently experiencing problems that require live service intervention."
            |}
            |""".stripMargin

        parser.GetTaxLiabilitySummaryDataSA302DetailsHttpReads.read("GET", "url", HttpResponse(INTERNAL_SERVER_ERROR, response)) mustBe
          Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("SERVER_ERROR", "HIP is currently experiencing problems that require live service intervention.")))
      }

      "HIP returns bad request" in {
        val response =
          """
            |{
            |  "code": "INVALID_TAXABLE_ENTITY_ID",
            |  "reason": "Summary data has not passed validation. Invalid parameter TAXABLE_ENTITY_ID."
            |}
            |""".stripMargin

        parser.GetTaxLiabilitySummaryDataSA302DetailsHttpReads.read("GET", "url", HttpResponse(BAD_REQUEST, response)) mustBe
          Left(ErrorModel(BAD_REQUEST, ErrorBodyModel("INVALID_TAXABLE_ENTITY_ID", "Summary data has not passed validation. Invalid parameter TAXABLE_ENTITY_ID.")))
      }

      "HIP returns conflict" in {
        val response =
          """
            |{
            |  "code": "CONFLICT",
            |  "reason": "The remote endpoint has indicated that final declaration has already been received"
            |}
            |""".stripMargin

        parser.GetTaxLiabilitySummaryDataSA302DetailsHttpReads.read("GET", "url", HttpResponse(CONFLICT, response)) mustBe
          Left(ErrorModel(CONFLICT, ErrorBodyModel("CONFLICT", "The remote endpoint has indicated that final declaration has already been received")))
      }

      "HIP returns UNPROCESSABLE_ENTITY" in {
        val response =
          """
            |{
            |  "code": "UNPROCESSABLE_ENTITY",
            |  "reason": "The remote endpoint has indicated that it can not process the request."
            |}
            |""".stripMargin

        parser.GetTaxLiabilitySummaryDataSA302DetailsHttpReads.read("GET", "url", HttpResponse(UNPROCESSABLE_ENTITY, response)) mustBe
          Left(ErrorModel(UNPROCESSABLE_ENTITY, ErrorBodyModel("UNPROCESSABLE_ENTITY", "The remote endpoint has indicated that it can not process the request.")))
      }

      "HIP returns FORBIDDEN" in {
        val response =
          """
            |{
            |  "code": "FORBIDDEN",
            |  "reason": "The remote endpoint has indicated that no Summary data exist"
            |}
            |""".stripMargin

        parser.GetTaxLiabilitySummaryDataSA302DetailsHttpReads.read("GET", "url", HttpResponse(FORBIDDEN, response)) mustBe
          Left(ErrorModel(FORBIDDEN, ErrorBodyModel("FORBIDDEN", "The remote endpoint has indicated that no Summary data exist")))
      }

      "HIP returns an unexpected error body" in {
        val response =
          """
            |{
            |  "unexpected": "error"
            |}
            |""".stripMargin

        parser.GetTaxLiabilitySummaryDataSA302DetailsHttpReads.read("GET", "url", HttpResponse(INTERNAL_SERVER_ERROR, response)) mustBe
          Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel.parsingError("")))
      }

      "HIP returns invalid Json for 200" in {
        val response =
          """
            |{
            |  "metadata": "invalid_type"
            |}
            |""".stripMargin

        parser.GetTaxLiabilitySummaryDataSA302DetailsHttpReads.read("GET", "url", HttpResponse(OK, response)) mustBe
          Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("PARSING_ERROR",
            "Error parsing response from API - List((/metadata,List(JsonValidationError(List(error.expected.jsobject),ArraySeq()))))")))
      }

      "HIP returns a bad json body" in {
        val response =
          """
            |{
            |""".stripMargin

        parser.GetTaxLiabilitySummaryDataSA302DetailsHttpReads.read("GET", "url", HttpResponse(INTERNAL_SERVER_ERROR, response)) mustBe
          Left(ErrorModel(INTERNAL_SERVER_ERROR, ErrorBodyModel("PARSING_ERROR", "Error parsing response from API")))
      }
    }
  }
}
