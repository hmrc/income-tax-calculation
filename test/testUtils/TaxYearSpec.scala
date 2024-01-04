/*
 * Copyright 2024 HM Revenue & Customs
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

package testUtils

import utils.TaxYear

class TaxYearSpec extends TestSuite {

  ".convert" should {
    "return a Left(String) if the tax year given does not contain digits exclusively" in {
      TaxYear.convert(Some("20a2")) mustBe Left("Failed to parse Tax year")
    }
    "return a Left(String) if the tax year given is not 4 characters long" in {
      TaxYear.convert(Some("20211")) mustBe Left("Failed to parse Tax year")
    }
    "return a Right(Int) if the tax year given is 4 characters long and contains only digits" in {
      TaxYear.convert(Some("2021")) mustBe Right(2021)
    }
  }

  ".updatedFormat" should {

    "return a tax year of the format" in {
      TaxYear.updatedFormat("2021") mustBe "20-21"
    }
  }

  ".convertSpecificTaxYear" should {

    "return a tax year in yy-yy format" in {
      TaxYear.convertSpecificTaxYear("2024") mustBe "23-24"
    }
  }

}
