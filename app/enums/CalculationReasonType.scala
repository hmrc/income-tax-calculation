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

package enums

import play.api.libs.json.{JsError, JsString, JsSuccess, Reads, Writes}

enum CalculationReasonType(val name: String):
  case CustomerRequest extends CalculationReasonType("customerRequest")
  case Class2NICEvent extends CalculationReasonType("class2NICEvent")
  case HMRCRevenueAmendment extends CalculationReasonType("HMRCrevenueAmendment")
  case NewAnnualAdjustmentEvent extends CalculationReasonType("newAnnualAdjustmentEvent")
  case OperatorRequest extends CalculationReasonType("operatorRequest")
  case UpdatedAnnualAdjustmentEvent extends CalculationReasonType("updatedAnnualAdjustmentEvent")
  case UnattendedCalculation extends CalculationReasonType("unattendedCalculation")
  case HMRCAutoCorrection extends CalculationReasonType("HMRCautoCorrection")
  case MarriageAllowance extends CalculationReasonType("marriageAllowance")
  case NationalInsurance extends CalculationReasonType("nationalInsurance")
  case DevolvedResidency extends CalculationReasonType("devolvedResidency")
  case CustomerRejectionOfaCorrection extends CalculationReasonType("customerRejectionOfaCorrection")

object CalculationReasonType:
  private val byString: Map[String, CalculationReasonType] =
    CalculationReasonType.values.map(ct => ct.name -> ct).toMap

  given reads: Reads[CalculationReasonType] =
    Reads {
      case JsString(value) =>
        byString.get(value)
          .map(JsSuccess(_))
          .getOrElse(JsError(s"Unknown CalculationReasonType: $value"))

      case _ =>
        JsError("calculationReasonType must be a string")
    }

  given writes: Writes[CalculationReasonType] =
    Writes(t => JsString(t.name))
  
end CalculationReasonType