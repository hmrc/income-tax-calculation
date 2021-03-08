package connectors

import config.AppConfig
import testUtils.TestSuite
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.logging.Authorization

class DesConnectorSpec extends TestSuite{

  class FakeConnector(override val appConfig: AppConfig) extends DesConnector {
    def headerCarrierTest(hc: HeaderCarrier) = desHeaderCarrier(hc)
  }
  val connector = new FakeConnector(appConfig = mockAppConfig)

  "FakeConnector" should {
    "add the correct authorization" in {
      val hc = HeaderCarrier()
      val result = connector.headerCarrierTest(hc)
      result.authorization mustBe Some(Authorization(s"Bearer ${mockAppConfig.authorisationToken}"))
    }
    "add the correct environment" in {
      val hc = HeaderCarrier()
      val result = connector.headerCarrierTest(hc)
      result.extraHeaders mustBe List("Environment" -> mockAppConfig.environment)
    }
  }

}
