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

package repositories

import java.util.concurrent.TimeUnit
import config.AppConfig
import org.mongodb.scala.model.Indexes.{ascending}
import org.mongodb.scala.model.{IndexModel, IndexOptions}

private[repositories] object TaxYearsDataIndexes {
  def indexes(appConfig: AppConfig): Seq[IndexModel] = {
    Seq(
      IndexModel(ascending("nino"), IndexOptions().unique(true).name("TaxYearDataLookupIndex")),
      IndexModel(ascending("lastUpdated"), IndexOptions().expireAfter(appConfig.mongoTTL, TimeUnit.MINUTES).name("TaxYearDataTTL"))
    )
  }
}
